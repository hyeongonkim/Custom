import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import requests
import json
import time
import xml.etree.ElementTree as ET
from collections import OrderedDict
from pyfcm import FCMNotification

push_service = FCMNotification(api_key="<ServerKey>") #FCM비밀키

cred = credentials.Certificate("serviceAccountKey.json") #비밀키
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://custom-f54bb.firebaseio.com'
})

ref = db.reference('users')
snapshot = ref.get()


def parseUnipass(traceNum, traceYear, nowStatus):
    parsedData = OrderedDict()
    parsedData.clear()
    newStatus = nowStatus
    parseURL = 'https://unipass.customs.go.kr:38010/ext/rest/cargCsclPrgsInfoQry/retrieveCargCsclPrgsInfo?crkyCn=<APIkey>&hblNo=' + str(traceNum) + '&blYy=' + str(traceYear)
    req = requests.get(parseURL)
    xml = req.text
    tree = ET.fromstring(xml)
    statuses = tree.findall('cargCsclPrgsInfoDtlQryVo')
    cnt = len(statuses)
    
    for i in statuses:
        parsedData[str(cnt)] = {'statusLocation':i.find('shedNm').text, 'statusMessage':i.find('cargTrcnRelaBsopTpcd').text, 'statusTime':i.find('prcsDttm').text}
        cnt = cnt - 1
        
    if(len(parsedData) != 0):
        newStatus = statuses[0].find('cargTrcnRelaBsopTpcd').text
        
    return parsedData, newStatus


def parseEMS(traceNum, nowStatus):
    parsedData = OrderedDict()
    parsedData.clear()
    newStatus = nowStatus
    parseURL = 'http://openapi.epost.go.kr/trace/retrieveLongitudinalEMSService/retrieveLongitudinalEMSService/getLongitudinalEMSList?rgist=' + str(traceNum) + '&serviceKey=<APIkey>'
    req = requests.get(parseURL)
    xml = req.text
    tree = ET.fromstring(xml)
    statuses = tree.findall('longitudinalEMSList')
    cnt = 1
    
    for i in statuses:
        rawTime = i.find('processDe').text
        inputTime = rawTime[0:4] + rawTime[5:7] + rawTime[8:10] + rawTime[11:13] + rawTime[14:16] + '00'
        parsedData[str(cnt)] = {'statusLocation': i.find('nowLc').text,
                                'statusMessage': i.find('processSttus').text,
                                'statusTime': inputTime}
        cnt = cnt + 1
        
    if (len(parsedData) != 0):
        newStatus = statuses[cnt - 2].find('processSttus').text
        
    return parsedData, newStatus


def getDevicesFCM(cu):
    fcm = db.reference('devices/' + cu)
    fcm_snapshot = fcm.get()
    list = []
    
    if(str(type(fcm_snapshot)) != "<class 'NoneType'>"):
        
        for key, val in fcm_snapshot.items():
            list.append(key)
            
    return list


error_cnt = 0
error_treated_cnt = 0
total_delivery = 0
completed_delivery = 0

for key, val in snapshot.items():
    if None in val:
        val.remove(None)
        val = {i['productName']:i for i in val}
        error_treated_cnt += 1
        print("숫자로 된 상품명을 전처리해 오류를 제거했습니다.")
    package_name_list = [k for k, v in val.items()]
    
    for curr_name in package_name_list:
        if '?' in curr_name:
            nval = {}
            for nk, nv in val.items():
                if '?' in nk:
                    nv.update({'productName': nk.replace('?', '!')})
                    nval.update({nk.replace('?', '!'): nv})
                else:
                    nval.update({nk: nv})
            ref.child(key).delete()
            ref.child(key).set(nval)
            val = nval
            error_treated_cnt += 1
            print("물음표가 포함된 상품명을 전처리해 오류를 제거했습니다.")
            break
            
    try:
        for nkey, nval in val.items():
            total_delivery += 1
            prevStatus = nval.get('nowStatus')
            
            if(prevStatus == "배달완료"):
                completed_delivery += 1
                continue
                
            nowStatus = prevStatus
            traceCompany = nval.get('traceCompany')
            
            if(traceCompany != 'EMS'):
                newData, newStatus = parseUnipass(nval.get('traceNumber'), nval.get('traceYear'), prevStatus)
                
            else:
                newData, newStatus = parseEMS(nval.get('traceNumber'), prevStatus)
                
            if(prevStatus != newStatus):
                ref.child(key).child(nkey).child("customStatus").set(newData)
                ref.child(key).child(nkey).update({
                            'nowTime': {'.sv': 'timestamp'},
                            'nowStatus': newStatus
                        })

                registration_id = getDevicesFCM(key)
                message_title = nkey + "의 통관상태가 업데이트 되었어요!"
                message_body = newStatus
                result = push_service.notify_multiple_devices(registration_ids=registration_id, message_title=message_title, message_body=message_body)
                
            elif prevStatus == "송장번호등록":
                if(str(time.localtime().tm_year) != str(nval.get('traceYear'))):
                    ref.child(key).child(nkey).update({'traceYear': str(time.localtime().tm_year)})
                    print("연도가 잘못 입력된 상품을 발견해 올해 연도로 바꿨습니다.")
                    error_treated_cnt += 1
                    
            else:
                continue
                
    except ValueError:
        error_cnt += 1
        print("Value 에러 발생, 문제 유저 Uid: " + key)
        
    except AttributeError:
        error_cnt += 1
        print("Attribute 에러 발생, 문제 유저 Uid: " + key)
        
print("\n- - - - - - - - - - - - - - - - - - - - - - - - - -\n")
print(str(len(snapshot.items())) + "명의 유저가 사용 중입니다.")
print(str(total_delivery) + "개 아이템을 처리하였습니다.")
print(str(completed_delivery) + "개 아이템이 배달완료로 처리되어 있습니다.")
print(str(error_cnt) + "개 아이템에서 오류가 발생했습니다.")
print(str(error_treated_cnt) + "개 아이템의 오류를 사전에 감지하고 해결했습니다.")
