package com.example.myptit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myptit.adapter.BangDiemAdapter;
import com.example.myptit.model.HocPhanModel;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MonNoActivity extends AppCompatActivity {
    String data;
    BangDiemAdapter diemHocPhanAdapter;
    ArrayList<HocPhanModel> diemHocPhanModelArrayList;
    ListView lvDiemHocPhan;
    TextView txtTinChiNo;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_mon_no);
        this.lvDiemHocPhan = (ListView) findViewById(R.id.lvMonNo_MonNo);
        this.txtTinChiNo = (TextView) findViewById(R.id.txtTinChiNo_MonNoActivity);

        readData();
        hienThiMonDiemF();
    }

    private void hienThiMonDiemF() {
        this.diemHocPhanModelArrayList = new ArrayList();
        try {
            JSONArray bangDiemHocPhan = new JSONObject(this.data).getJSONArray("listDiemThi");
            Integer tongSoTinChi = Integer.valueOf(0);
            for (int i = bangDiemHocPhan.length() - 1; i >= 0; i--) {
                JSONObject diemHocPhan = bangDiemHocPhan.getJSONObject(i);
                if (diemHocPhan.getString("he4").equals("F")) {
                    HocPhanModel diemHocPhanModel = new HocPhanModel();
                    diemHocPhanModel.setMaHocPhan(diemHocPhan.getString("msmh"));
                    diemHocPhanModel.setTenHocPhan(diemHocPhan.getString("tenmh"));
                    diemHocPhanModel.setSoTinChi(diemHocPhan.getString("stc"));
                    diemHocPhanModel.setDiemChu(diemHocPhan.getString("he4"));
                    diemHocPhanModel.setDiemSo(Double.valueOf(diemHocPhan.getDouble("he10")));
                    diemHocPhanModel.setDiemQuaTrinh(""+diemHocPhan.getDouble("tc"));
                    diemHocPhanModel.setDiemCuoiKi(""+diemHocPhan.getDouble("thi"));
                    this.diemHocPhanModelArrayList.add(diemHocPhanModel);
                    tongSoTinChi = Integer.valueOf(tongSoTinChi.intValue() + diemHocPhan.getInt("stc"));                }
            }
            this.txtTinChiNo.setText(String.valueOf(tongSoTinChi) + " tín");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.diemHocPhanAdapter = new BangDiemAdapter(this, R.layout.content_bang_diem, this.diemHocPhanModelArrayList);
        this.lvDiemHocPhan.setAdapter(this.diemHocPhanAdapter);
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
