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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myptit.adapter.LichHocAdapter;
import com.example.myptit.model.LichHocModel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TKBActivity extends AppCompatActivity {
    String data;
    LichHocAdapter lichHocAdapter;
    ArrayList<LichHocModel> lichHocModelArrayList;
    ListView lvLichHoc;
    TextView txtDay;
    ImageView tangtuan;
    ImageView giamtuan;
    int maxtuan = 0;
    int currenttuan = 0;

    public void tangTuan() {

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_tkb);
        this.txtDay = (TextView) findViewById(R.id.txtDay_LichHoc);
        this.txtDay.setText(new SimpleDateFormat("EEE, dd - MM - yyyy").format(new Date()));
        this.lvLichHoc = (ListView) findViewById(R.id.lvLichHoc_LichHoc);
        this.tangtuan = (ImageView) findViewById(R.id.imgAdd_tangtuan);
        this.giamtuan = (ImageView) findViewById(R.id.imgAdd_gtuan);


        this.txtDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currenttuan = lichHocModelArrayList.get(0).getCurrentTuan();

                try {
                    Collections.sort(lichHocModelArrayList, new Comparator<LichHocModel>() {
                        @Override
                        public int compare(LichHocModel lhs, LichHocModel rhs) {
                            return -lhs.getSortValue(currenttuan) + rhs.getSortValue(currenttuan);
                        }
                    });

                    lichHocAdapter = new LichHocAdapter(TKBActivity.this, R.layout.item_lichhoc, lichHocModelArrayList,currenttuan);
                    lvLichHoc.setAdapter(lichHocAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.tangtuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currenttuan < maxtuan){
                    currenttuan ++;
                }else{
                    return;
                }

                try {
                    if(lichHocModelArrayList.size() == 0){
                        return;
                    }

                    Collections.sort(lichHocModelArrayList, new Comparator<LichHocModel>() {
                        @Override
                        public int compare(LichHocModel lhs, LichHocModel rhs) {
                            return -lhs.getSortValue(currenttuan) + rhs.getSortValue(currenttuan);
                        }
                    });

                    lichHocAdapter = new LichHocAdapter(TKBActivity.this, R.layout.item_lichhoc, lichHocModelArrayList,currenttuan);
                    lvLichHoc.setAdapter(lichHocAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.giamtuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currenttuan > 1){
                    currenttuan --;
                }else{
                    return;
                }

                try {
                    if(lichHocModelArrayList.size() == 0){
                        return;
                    }

                    Collections.sort(lichHocModelArrayList, new Comparator<LichHocModel>() {
                        @Override
                        public int compare(LichHocModel lhs, LichHocModel rhs) {
                            return -lhs.getSortValue(currenttuan) + rhs.getSortValue(currenttuan);
                        }
                    });

                    lichHocAdapter = new LichHocAdapter(TKBActivity.this, R.layout.item_lichhoc, lichHocModelArrayList,currenttuan);
                    lvLichHoc.setAdapter(lichHocAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        readData();
        this.lichHocModelArrayList = new ArrayList();
        try {
            JSONArray lichHocJsonA = new JSONObject(this.data).getJSONArray("thoikhoabieu");
            for (int i = 0; i < lichHocJsonA.length(); i++) {
                JSONObject lichHocJsonO = lichHocJsonA.getJSONObject(i);
                LichHocModel lichHocModel = new LichHocModel();
                lichHocModel.setCBGV(lichHocJsonO.getString("CBGV"));
                lichHocModel.setDSSV(lichHocJsonO.getString("DSSV"));
                lichHocModel.setKDK(lichHocJsonO.getString("KDK"));
                lichHocModel.setMaLop(lichHocJsonO.getString("maLop"));
                lichHocModel.setMaMH(lichHocJsonO.getString("maMH"));
                lichHocModel.setNMH(lichHocJsonO.getString("NMH"));
                lichHocModel.setPHONG(lichHocJsonO.getString("PHONG"));
                lichHocModel.setST(lichHocJsonO.getString("ST"));
                lichHocModel.setSTC(lichHocJsonO.getString("STC"));
                lichHocModel.setSTCHP(lichHocJsonO.getString("STCHP"));
                lichHocModel.setTenMh(lichHocJsonO.getString("tenMh"));
                lichHocModel.setTH(lichHocJsonO.getString("TH"));
                lichHocModel.setTietBD(lichHocJsonO.getString("tietBD"));
                lichHocModel.setTUAN(lichHocJsonO.getString("TUAN"));
                lichHocModel.sethBatDau(lichHocJsonO.getString("hBatDau"));
                lichHocModel.setNgaybatdau(lichHocJsonO.getString("ngaybatdau"));
                lichHocModel.setTHU(lichHocJsonO.getString("THU"));
                lichHocModel.setGV(lichHocJsonO.getString("GV"));

                String tuan = lichHocJsonO.getString("TUAN");
                maxtuan = maxtuan > tuan.length() ? maxtuan : tuan.length();
                this.lichHocModelArrayList.add(lichHocModel);
            }

            if(this.lichHocModelArrayList.size() == 0){
                return;
            }

            this.currenttuan = this.lichHocModelArrayList.get(0).getCurrentTuan();

            Collections.sort(this.lichHocModelArrayList, new Comparator<LichHocModel>() {
                @Override
                public int compare(LichHocModel lhs, LichHocModel rhs) {
                    return -lhs.getSortValue(currenttuan) + rhs.getSortValue(currenttuan);
                }
            });

            this.lichHocAdapter = new LichHocAdapter(this, R.layout.item_lichhoc, this.lichHocModelArrayList,currenttuan);
            this.lvLichHoc.setAdapter(this.lichHocAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void readData() {
        try {
            String fileName = "data.json";
            FileInputStream in = this.openFileInput(fileName);

            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            StringBuffer buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            this.data = buffer.toString();

        } catch (Exception e) {

        }
    }
}
