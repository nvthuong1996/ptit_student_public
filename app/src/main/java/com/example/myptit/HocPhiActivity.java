package com.example.myptit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class HocPhiActivity extends AppCompatActivity {
    TextView txtView1;
    TextView txtView2;
    TextView txtView3;
    TextView txtView4;
    TextView txtView5;
    TextView txtView6;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoc_phi);
        TextView txtView1 = (TextView) findViewById(R.id.textView);
        TextView txtView2 = (TextView) findViewById(R.id.textView2);
        TextView txtView3 = (TextView) findViewById(R.id.textView14);
        TextView txtView4 = (TextView) findViewById(R.id.textView16);
        TextView txtView5 = (TextView) findViewById(R.id.textView17);
        TextView txtView6 = (TextView) findViewById(R.id.textView19);

        readData();

        try {
            JSONObject hocphi = new JSONObject(this.data).getJSONObject("hocphi");
            txtView1.setText(hocphi.getString("tongsotinchi"));
            txtView2.setText(hocphi.getString("tongsotinchihp"));
            txtView3.setText(hocphi.getString("tongso1"));
            txtView4.setText(hocphi.getString("tongso2"));
            txtView5.setText(hocphi.getString("sotiendadong"));
            txtView6.setText(hocphi.getString("sotienconno")+ " VNĐ");

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void readData() {
        try {
            String fileName = "data.json";
            FileInputStream in = this.openFileInput(fileName);

            BufferedReader br= new BufferedReader(new InputStreamReader(in));

            StringBuffer buffer = new StringBuffer();
            String line = null;
            while((line= br.readLine())!= null)  {
                buffer.append(line).append("\n");
            }
            this.data = buffer.toString();

        } catch (Exception e) {

        }
    }
}
