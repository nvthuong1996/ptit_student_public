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

public class BangDiemActivity extends AppCompatActivity {
    String data;
    BangDiemAdapter diemHocPhanAdapter;
    ArrayList<HocPhanModel> diemHocPhanModelArrayList;
    ListView lvDiemHocPhan;
    Spinner spnPhanLoai;
    TextView txtCPA;
    TextView txtSoTinChiTichLuy;


    /* renamed from: hust.nhatlx.mysishust.BangDiem_Activity$2 */
    class C03402 implements OnItemSelectedListener {
        C03402() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            switch (position) {
                case 0:
                    BangDiemActivity.this.hienThiTatCa();
                    return;
                case 1:
                    BangDiemActivity.this.hienThiMonDiemAAplus();
                    return;
                case 2:
                    BangDiemActivity.this.hienThiMonDiemBplus();
                    return;
                case 3:
                    BangDiemActivity.this.hienThiMonDiemB();
                    return;
                case 4:
                    BangDiemActivity.this.hienThiMonDiemCplus();
                    return;
                case 5:
                    BangDiemActivity.this.hienThiMonDiemC();
                    return;
                case 6:
                    BangDiemActivity.this.hienThiMonDiemDplus();
                    return;
                case 7:
                    BangDiemActivity.this.hienThiMonDiemD();
                    return;
                case 8:
                    BangDiemActivity.this.hienThiMonDiemF();
                    return;
                default:
                    return;
            }
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_bang_diem);
        this.lvDiemHocPhan = (ListView) findViewById(R.id.lvBangDiem_BangDiem);
        this.txtCPA = (TextView) findViewById(R.id.txtCPA_BangDiem);
        this.txtSoTinChiTichLuy = (TextView) findViewById(R.id.txtSoTinChi_BangDiem);
        this.spnPhanLoai = (Spinner) findViewById(R.id.spnPhanLoai_BangDiem);

        readData();
        hienThiCPA();
        hienThiSpinner();
    }

    private void hienThiSpinner() {
        List<String> phanLoaiList = new ArrayList();
        phanLoaiList.add("All");
        phanLoaiList.add("A/A+");
        phanLoaiList.add("B+");
        phanLoaiList.add("B");
        phanLoaiList.add("C+");
        phanLoaiList.add("C");
        phanLoaiList.add("D+");
        phanLoaiList.add("D");
        phanLoaiList.add("F");
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, phanLoaiList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spnPhanLoai.setAdapter(adapter);
        this.spnPhanLoai.setOnItemSelectedListener(new C03402());
    }

    private void hienThiCPA() {
        try {
            JSONObject tongket = new JSONObject(this.data).getJSONObject("tongket");
            double tbtichluyhe4 = tongket.getDouble("tbtichluyhe4");
            int tichluy = tongket.getInt("tichluy");
            int tinchino = tongket.getInt("tinchino");
            this.txtCPA.setText(""+tbtichluyhe4);


        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            this.txtSoTinChiTichLuy.setText(String.valueOf(tongSoTinChi) + " tín");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.diemHocPhanAdapter = new BangDiemAdapter(this, R.layout.content_bang_diem, this.diemHocPhanModelArrayList);
        this.lvDiemHocPhan.setAdapter(this.diemHocPhanAdapter);
    }

    private void hienThiMonDiemD() {
        this.diemHocPhanModelArrayList = new ArrayList();
        try {
            JSONArray bangDiemHocPhan = new JSONObject(this.data).getJSONArray("listDiemThi");
            Integer tongSoTinChi = Integer.valueOf(0);
            for (int i = bangDiemHocPhan.length() - 1; i >= 0; i--) {
                JSONObject diemHocPhan = bangDiemHocPhan.getJSONObject(i);
                if (diemHocPhan.getString("he4").equals("D")) {
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
            this.txtSoTinChiTichLuy.setText(String.valueOf(tongSoTinChi) + " tín");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.diemHocPhanAdapter = new BangDiemAdapter(this, R.layout.content_bang_diem, this.diemHocPhanModelArrayList);
        this.lvDiemHocPhan.setAdapter(this.diemHocPhanAdapter);
    }

    private void hienThiMonDiemDplus() {
        this.diemHocPhanModelArrayList = new ArrayList();
        try {
            JSONArray bangDiemHocPhan = new JSONObject(this.data).getJSONArray("listDiemThi");
            Integer tongSoTinChi = Integer.valueOf(0);
            for (int i = bangDiemHocPhan.length() - 1; i >= 0; i--) {
                JSONObject diemHocPhan = bangDiemHocPhan.getJSONObject(i);
                if (diemHocPhan.getString("he4").equals("D+")) {
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
            this.txtSoTinChiTichLuy.setText(String.valueOf(tongSoTinChi) + " tín");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.diemHocPhanAdapter = new BangDiemAdapter(this, R.layout.content_bang_diem, this.diemHocPhanModelArrayList);
        this.lvDiemHocPhan.setAdapter(this.diemHocPhanAdapter);
    }

    private void hienThiMonDiemC() {
        this.diemHocPhanModelArrayList = new ArrayList();
        try {
            JSONArray bangDiemHocPhan = new JSONObject(this.data).getJSONArray("listDiemThi");
            Integer tongSoTinChi = Integer.valueOf(0);
            for (int i = bangDiemHocPhan.length() - 1; i >= 0; i--) {
                JSONObject diemHocPhan = bangDiemHocPhan.getJSONObject(i);
                if (diemHocPhan.getString("he4").equals("C")) {
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
            this.txtSoTinChiTichLuy.setText(String.valueOf(tongSoTinChi) + " tín");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.diemHocPhanAdapter = new BangDiemAdapter(this, R.layout.content_bang_diem, this.diemHocPhanModelArrayList);
        this.lvDiemHocPhan.setAdapter(this.diemHocPhanAdapter);
    }

    private void hienThiMonDiemCplus() {
        this.diemHocPhanModelArrayList = new ArrayList();
        try {
            JSONArray bangDiemHocPhan = new JSONObject(this.data).getJSONArray("listDiemThi");
            Integer tongSoTinChi = Integer.valueOf(0);
            for (int i = bangDiemHocPhan.length() - 1; i >= 0; i--) {
                JSONObject diemHocPhan = bangDiemHocPhan.getJSONObject(i);
                if (diemHocPhan.getString("he4").equals("C+")) {
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
            this.txtSoTinChiTichLuy.setText(String.valueOf(tongSoTinChi) + " tín");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.diemHocPhanAdapter = new BangDiemAdapter(this, R.layout.content_bang_diem, this.diemHocPhanModelArrayList);
        this.lvDiemHocPhan.setAdapter(this.diemHocPhanAdapter);
    }

    private void hienThiMonDiemB() {
        this.diemHocPhanModelArrayList = new ArrayList();
        try {
            JSONArray bangDiemHocPhan = new JSONObject(this.data).getJSONArray("listDiemThi");
            Integer tongSoTinChi = Integer.valueOf(0);
            for (int i = bangDiemHocPhan.length() - 1; i >= 0; i--) {
                JSONObject diemHocPhan = bangDiemHocPhan.getJSONObject(i);
                if (diemHocPhan.getString("he4").equals("B")) {
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
            this.txtSoTinChiTichLuy.setText(String.valueOf(tongSoTinChi) + " tín");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.diemHocPhanAdapter = new BangDiemAdapter(this, R.layout.content_bang_diem, this.diemHocPhanModelArrayList);
        this.lvDiemHocPhan.setAdapter(this.diemHocPhanAdapter);
    }

    private void hienThiMonDiemBplus() {
        this.diemHocPhanModelArrayList = new ArrayList();
        try {
            JSONArray bangDiemHocPhan = new JSONObject(this.data).getJSONArray("listDiemThi");
            Integer tongSoTinChi = Integer.valueOf(0);
            for (int i = bangDiemHocPhan.length() - 1; i >= 0; i--) {
                JSONObject diemHocPhan = bangDiemHocPhan.getJSONObject(i);
                if (diemHocPhan.getString("he4").equals("B+")) {
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
            this.txtSoTinChiTichLuy.setText(String.valueOf(tongSoTinChi) + " tín");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.diemHocPhanAdapter = new BangDiemAdapter(this, R.layout.content_bang_diem, this.diemHocPhanModelArrayList);
        this.lvDiemHocPhan.setAdapter(this.diemHocPhanAdapter);
    }

    private void hienThiMonDiemAAplus() {
        this.diemHocPhanModelArrayList = new ArrayList();
        try {
            JSONArray bangDiemHocPhan = new JSONObject(this.data).getJSONArray("listDiemThi");
            Integer tongSoTinChi = Integer.valueOf(0);
            for (int i = bangDiemHocPhan.length() - 1; i >= 0; i--) {
                JSONObject diemHocPhan = bangDiemHocPhan.getJSONObject(i);
                if (diemHocPhan.getString("he4").equals("A") || diemHocPhan.getString("he4").equals("A+")) {
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
            this.txtSoTinChiTichLuy.setText(String.valueOf(tongSoTinChi) + " tín");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.diemHocPhanAdapter = new BangDiemAdapter(this, R.layout.content_bang_diem, this.diemHocPhanModelArrayList);
        this.lvDiemHocPhan.setAdapter(this.diemHocPhanAdapter);
    }

    private void hienThiTatCa() {
        this.diemHocPhanModelArrayList = new ArrayList();
        try {
            JSONArray bangDiemHocPhan = new JSONObject(this.data).getJSONArray("listDiemThi");
            Integer tongSoTinChi = Integer.valueOf(0);
            for (int i = bangDiemHocPhan.length() - 1; i >= 0; i--) {
                JSONObject diemHocPhan = bangDiemHocPhan.getJSONObject(i);
                HocPhanModel diemHocPhanModel = new HocPhanModel();
                diemHocPhanModel.setMaHocPhan(diemHocPhan.getString("msmh"));
                diemHocPhanModel.setTenHocPhan(diemHocPhan.getString("tenmh"));
                diemHocPhanModel.setSoTinChi(diemHocPhan.getString("stc"));
                diemHocPhanModel.setDiemChu(diemHocPhan.getString("he4"));
                diemHocPhanModel.setDiemSo(Double.valueOf(diemHocPhan.getDouble("he10")));
                diemHocPhanModel.setDiemQuaTrinh(""+diemHocPhan.getDouble("tc"));
                diemHocPhanModel.setDiemCuoiKi(""+diemHocPhan.getDouble("thi"));
                this.diemHocPhanModelArrayList.add(diemHocPhanModel);
                tongSoTinChi = Integer.valueOf(tongSoTinChi.intValue() + diemHocPhan.getInt("stc"));
            }
            this.txtSoTinChiTichLuy.setText(String.valueOf(tongSoTinChi) + " tín");
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
