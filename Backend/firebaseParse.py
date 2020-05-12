import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
import requests
import json
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


for key, val in snapshot.items():
    if(str(type(val)) == "<class 'list'>"):
        continue
    for nkey, nval in val.items():
        prevStatus = nval.get('nowStatus')
        if(prevStatus == "배달완료"):
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
            message_title = nkey + " 통관상태 업데이트"
            message_body = newStatus
            result = push_service.notify_multiple_devices(registration_ids=registration_id, message_title=message_title, message_body=message_body)
        else:
            continue