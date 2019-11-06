package com.example.custom;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class TraceActivity extends AppCompatActivity {

    private ListView listView;
    ArrayList<TraceListItemClass> traceList;
    ListAdapter adapter;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("배송목록");
        setContentView(R.layout.activity_trace);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        final String cu = currentUser.getUid();

        listView = (ListView) findViewById(R.id.tracelist);

        mDatabase.child("users").child(cu).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                traceList = new ArrayList<>();
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    TraceListItemClass temp = new TraceListItemClass();
                    temp.product_name = fileSnapshot.child("productName").getValue(String.class);
                    temp.trace_company = fileSnapshot.child("traceCompany").getValue(String.class);
                    temp.trace_number = fileSnapshot.child("traceNumber").getValue(String.class);
                    temp.now_status = fileSnapshot.child("nowStatus").getValue(String.class);
                    traceList.add(temp);
                }
                adapter = new ListAdapter(traceList);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG: ", "Failed to read value", databaseError.toException());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String toDetail = traceList.get(position).product_name;
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("toDetail", toDetail);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String toDelete = traceList.get(position).product_name;
                mDatabase.child("users").child(cu).child(toDelete).removeValue().addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("설정").setMessage("공사중...");

                builder.setPositiveButton("닫기", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                    }
                });

                builder.setNegativeButton("로그아웃", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getApplicationContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            case R.id.action_add:
                Intent intent = new Intent(getApplicationContext(),
                        AddtraceActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
