package com.simonkim.custom;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    private Intent intent;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private  Button btn_trace_local, btn_trace_finish;


    private ListView listView;
    private ImageView empty_img;
    ArrayList<CustomListItemClass> customList;
    CustomListAdapter adapter;

    String item;

    TextView detail_name, detail_number, detail_status;

    protected String getItem() {
        return item;
    }

    String targetLink;

    public void setTargetLink(String targetLink) {
        this.targetLink = targetLink;
    }

    public String getTargetLink() {
        return targetLink;
    }

    private AdView mAdView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("통관조회");
        setContentView(R.layout.activity_detail);
        btn_trace_local = (Button) findViewById(R.id.trace_local);
        btn_trace_finish = (Button) findViewById(R.id.trace_finish);


        detail_name = (TextView) findViewById(R.id.detail_name);
        detail_number = (TextView) findViewById(R.id.detail_number);
        detail_status = (TextView) findViewById(R.id.detail_status);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        empty_img = (ImageView) findViewById(R.id.empty_img);
        intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        final String cu = currentUser.getUid();
        item = intent.getStringExtra("toDetail");


        mDatabase.child("users").child(cu).child(item).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String product_name = dataSnapshot.child("productName").getValue(String.class);
                        String trace_number = dataSnapshot.child("traceNumber").getValue(String.class);
                        String now_status = dataSnapshot.child("nowStatus").getValue(String.class);
                        String trace_company = dataSnapshot.child("traceCompany").getValue(String.class);
                        detail_name.setText(product_name);
                        detail_number.setText(trace_number);
                        detail_status.setText(now_status);
                        if(trace_company == null) {
                            setTargetLink("");
                        } else if(trace_company.equals("우체국")) {
                            setTargetLink("http://service.epost.go.kr/trace.RetrieveRegiPrclDeliv.postal?sid1=" + trace_number);
                        }
                        else if(trace_company.equals("대한통운")) {
                            setTargetLink("http://nplus.doortodoor.co.kr/web/detail.jsp?slipno=" + trace_number);
                        }
                        else if(trace_company.equals("한진택배")) {
                            setTargetLink("http://www.hanjin.co.kr/Delivery_html/inquiry/result_waybill.jsp?wbl_num=" + trace_number);
                        }
                        else if(trace_company.equals("EMS")) {
                            setTargetLink("http://service.epost.go.kr/trace.RetrieveEmsTrace.postal?ems_gubun=E&POST_CODE=" + trace_number);
                        }
                        else if(trace_company.equals("DHL")) {
                            setTargetLink("http://www.dhl.co.kr/publish/kr/ko/eshipping/track.high.html?pageToInclude=RESULTS&type=fasttrack&AWB=" + trace_number);
                        }
                        else if(trace_company.equals("FedEx")) {
                            setTargetLink("http://www.fedex.com/Tracking?ascend_header=1&clienttype=dotcomreg&cntry_code=kr&language=korean&tracknumbers=" + trace_number);
                        }
                        else if(trace_company.equals("로젠택배")) {
                            setTargetLink("http://www.ilogen.com/iLOGEN.Web.New/TRACE/TraceView.aspx?gubun=slipno&slipno=" + trace_number);
                        }
                        else if(trace_company.equals("천일택배")) {
                            setTargetLink("http://www.chunil.co.kr/HTrace/HTrace.jsp?transNo=" + trace_number);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        btn_trace_local.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.parse((getTargetLink()));
                    intent.setData(uri);
                    startActivity(intent);
            }
        });

        btn_trace_finish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);

                builder.setTitle("배달완료처리").setMessage(item + "을(를) 정말 수령하셨습니까?\n완료처리 이후에는 추적이 제공되지 않습니다.");

                builder.setPositiveButton("닫기", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                    }
                });

                builder.setNegativeButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        mDatabase.child("users").child(cu).child(item).child("nowStatus").setValue("배달완료");
                        Toast.makeText(getApplicationContext(), "처리되었습니다", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        listView = (ListView) findViewById(R.id.customStatusList);
        mDatabase.child("users").child(cu).child(item).child("customStatus").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        customList = new ArrayList<>();
                        for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                            CustomListItemClass temp = new CustomListItemClass();
                            temp.statusLocation = fileSnapshot.child("statusLocation").getValue(String.class);
                            temp.statusMessage = fileSnapshot.child("statusMessage").getValue(String.class);
                            String rawTime = fileSnapshot.child("statusTime").getValue(String.class);
                            temp.statusTime = rawTime.substring(0,4) + "." + rawTime.substring(4,6) + "." + rawTime.substring(6,8) + " " + rawTime.substring(8,10) + ":" + rawTime.substring(10,12) + ":" + rawTime.substring(12,14);
                            customList.add(0, temp);
                        }
                        if(!customList.isEmpty()) {
                            empty_img.setVisibility(View.INVISIBLE);
                        } else {
                            empty_img.setVisibility(View.VISIBLE);
                        }
                        adapter = new CustomListAdapter(customList);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TAG: ", "Failed to read value", databaseError.toException());
                    }
                });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("도움말").setMessage("1. 통관종류\n\n" +
                        "- 목록통관 : 미화 150$(미국발은 200$) 이하의 상품에 대해 별도 절차나 관부가세와 통관수수료없이 진행되는 통관(개인통관고유부호 필요)\n\n" +
                        "- 목록배제통관 : 목록통관에서 특정 물품들에 대해 일반통관으로 정식통관을 진행하는 통관(의약품, 한약재, 야생동물관련제품, 농림축수산물 검역대상물품, 건강기능식품, 저작권위반 의심물품, 식품/과자류, 화장품, 통관정보가 부정확한 상품, 기타 세관 판단물품)\n\n" +
                        "- 일반통관 : 목록통관이 아닌 제품에 대해 정식 절차를 거쳐 관부가세와 통관수수료를 납부해야하는 통관(개인통관고유부호 필요)\n\n\n" +
                        "2. 통관절차(EMS제외)\n\n" +
                        "- 적하목록제출 : 화물이 도착하기 전 세관에서 도착예정정보를 받은 상태입니다.\n\n" +
                        "- 적하목록심사완료 : 세관에서 화물 도착정보를 확인하고 허가한 상태입니다.\n\n" +
                        "- 하선/하기신고 : 화물이 도착하여 물건을 내린 상태입니다.\n\n" +
                        "- 하선/하기장소 반입기간연장 : 통관이 지연될 것으로 예상되어 보관기간을 연장하였습니다.\n\n" +
                        "- 통관목록접수 : 통관절차가 시작되었습니다.\n\n" +
                        "- 반입/반출신고 : 세관에서 물품 심사를 진행했습니다. 또는 통관장이 없는 항구로 입항하여 통관장으로 출발/도착하였습니다.\n\n" +
                        "- 통관보류 : 통관 과정에서 문제가 발생하여 보류된 상태입니다.\n\n" +
                        "- 통관목록심사완료 : 심사가 완료되었습니다. 목록통관의 경우 국내택배사로 인계됩니다.\n\n" +
                        "- 보세운송 : 심사가 완료되어 임시 창고로 운송합니다. 단, 통관장이 없는 항구로 입항하여 통관장까지 이동하는 경우에도 같은 문구가 나오니 참고하시기 바랍니다.\n\n" +
                        "- 수입신고 : 관부가세/수수료를 납부할 준비가 되었습니다. 관세를 납부하시면 됩니다.\n\n" +
                        "- 수입신고수리 : 관세납부가 완료되었습니다.\n\n" +
                        "- 물품반출 : 물건이 반출되어 국내배송사로 인계됩니다.");

                builder.setPositiveButton("닫기", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            case R.id.action_fix:
                intent = new Intent(getApplicationContext(), FixtraceActivity.class);
                intent.putExtra("toFix", getItem());
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
