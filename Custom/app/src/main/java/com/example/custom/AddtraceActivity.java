package com.example.custom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddtraceActivity extends AppCompatActivity {
    private EditText product_name_input;
    private EditText trace_number_input;
    private Button save_btn;

    private Spinner trace_year;
    ArrayList<Integer> arrayList;
    ArrayAdapter<Integer> arrayAdapter;

    private Spinner trace_company;
    ArrayList<String> companyList;
    ArrayAdapter<String> companyAdapter;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("송장추가");
        setContentView(R.layout.activity_addtrace);

        product_name_input = (EditText) findViewById(R.id.product_name_input);
        trace_number_input = (EditText) findViewById(R.id.trace_number_input);
        save_btn = (Button) findViewById(R.id.save);

        arrayList = new ArrayList<>();
        arrayList.add(2019);
        arrayList.add(2018);
        arrayList.add(2017);
        arrayList.add(2016);

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                arrayList);

        trace_year = (Spinner)findViewById(R.id.trace_year_spinner);
        trace_year.setAdapter(arrayAdapter);
        trace_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        companyList = new ArrayList<>();
        companyList.add("EMS");
        companyList.add("대한통운");
        companyList.add("한진택배");
        companyList.add("DHL");

        companyAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                companyList);

        trace_company = (Spinner)findViewById(R.id.trace_company_spinner);
        trace_company.setAdapter(companyAdapter);
        trace_company.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String p_name = product_name_input.getText().toString();
                String t_number = trace_number_input.getText().toString();
                String t_year = trace_year.getSelectedItem().toString();
                String t_company = trace_company.getSelectedItem().toString();
                AddTraceClass forSave = new AddTraceClass(p_name, t_number, t_year, t_company);

                mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                String cu = currentUser.getUid();
                mDatabase.child("users").child(cu).child(p_name).setValue(forSave);

                Toast.makeText(getApplicationContext(), "저장되었습니다", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),
                        TraceActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
