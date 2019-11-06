package com.example.custom;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

    private ListView listView;
    ArrayList<CustomListItemClass> customList;
    CustomListAdapter adapter;

    TextView detail_name, detail_number, detail_status;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("통관조회");
        setContentView(R.layout.activity_detail);

        detail_name = (TextView) findViewById(R.id.detail_name);
        detail_number = (TextView) findViewById(R.id.detail_number);
        detail_status = (TextView) findViewById(R.id.detail_status);

        intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String cu = currentUser.getUid();
        String item = intent.getStringExtra("toDetail");
        mDatabase.child("users").child(cu).child(item).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String product_name = dataSnapshot.child("productName").getValue(String.class);
                        String trace_number = dataSnapshot.child("traceNumber").getValue(String.class);
                        String now_status = dataSnapshot.child("nowStatus").getValue(String.class);
                        detail_name.setText(product_name);
                        detail_number.setText(trace_number);
                        detail_status.setText(now_status);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

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
                            temp.statusTime = fileSnapshot.child("statusTime").getValue(String.class);
                            customList.add(0, temp);
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

                builder.setTitle("도움말").setMessage("도움말 내용을 여기 넣으시오");

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
                Toast.makeText(this, "수정팝업", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
