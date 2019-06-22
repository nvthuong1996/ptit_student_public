package com.example.myptit;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myptit.model.HocPhanModel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DuKienActivity extends AppCompatActivity {
    double cpaMucTieu;
    String data;
    EditText edtSoTinChiCanHoc;
    ImageView imgAdd;
    ImageView imgNhanXetMucTieu;
    ListView lvCaiThien;
    RadioGroup radGMucTieu;
    TextView txtCPA;
    TextView txtNhanXetMucTieu;
    TextView txtSoTinChi;
    EditText txtDuKienToic;
    Button tinhdoitoic;
    int tongsotinchitichluy;
    double cpahientai;
    Button tinhcpatuychinh;
    EditText cpadukienhoc;
    TextView txtNhanXetMucTieuTongtin;
    TextView tinchiDukien;
    Button resetCPA;


    class C03513 implements OnCheckedChangeListener {
        C03513() {
        }

        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            RadioButton viewBtn = (RadioButton) findViewById(checkedId);
            if (viewBtn == null || !viewBtn.isChecked()) {
                return;
            }
            switch (checkedId) {
                case R.id.radXuatSac:
                    DuKienActivity.this.cpaMucTieu = 3.6d;
                    DuKienActivity.this.tinhToanMucTieu();
                    return;
                case R.id.radGioi:
                    DuKienActivity.this.cpaMucTieu = 3.2d;
                    DuKienActivity.this.tinhToanMucTieu();
                    return;
                case R.id.radKha:
                    DuKienActivity.this.cpaMucTieu = 2.5d;
                    DuKienActivity.this.tinhToanMucTieu();
                    return;
                case R.id.radTrungBinh:
                    DuKienActivity.this.cpaMucTieu = 2.0d;
                    DuKienActivity.this.tinhToanMucTieu();
                    return;
                case R.id.radYeuTruong:
                    DuKienActivity.this.txtNhanXetMucTieu.setText("Cứ chơi thôi :v");
                    DuKienActivity.this.imgNhanXetMucTieu.setBackgroundResource(R.drawable.quaydithim);
                    return;
                default:
                    return;
            }
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_du_kien);
        this.lvCaiThien = (ListView) findViewById(R.id.lvDuKien_DuKien);
        this.txtCPA = (TextView) findViewById(R.id.txtCPA_DuKien);
        this.txtSoTinChi = (TextView) findViewById(R.id.txtSoTinCHi_DuKien);
        this.imgAdd = (ImageView) findViewById(R.id.imgAdd_DuKienActivity);
        this.edtSoTinChiCanHoc = (EditText) findViewById(R.id.edtSoTinChiCanHoc);
        this.radGMucTieu = (RadioGroup) findViewById(R.id.radG_MucTieu);
        this.txtNhanXetMucTieu = (TextView) findViewById(R.id.txtNhanXetMucTieu);
        this.imgNhanXetMucTieu = (ImageView) findViewById(R.id.imgNhanXetMucTieu);
        this.txtDuKienToic = (EditText) findViewById(R.id.dukientoeic);
        this.tinhdoitoic = (Button) findViewById(R.id.button_tinhdoitoeic);
        this.tinhcpatuychinh = (Button) findViewById(R.id.cpatuychinh);
        this.cpadukienhoc = (EditText) findViewById(R.id.editdukiencpa);
        this.txtNhanXetMucTieuTongtin = (TextView) findViewById(R.id.textView21);
        this.resetCPA = (Button) findViewById(R.id.resetcpa_btn);


        /*this.imgAdd.setOnClickListener(new C03502());
        readData();
        hienThiListView();
        hienThiCPAvaSoTinChiCaiThien();
        addEvents();*/
        readData();
        initData();

        this.tinhdoitoic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tinhDoiToeic();
            }
        });
        this.tinhcpatuychinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radGMucTieu.clearCheck();
                if (cpadukienhoc.getText().toString().equals("")) {
                    return;
                }
                cpaMucTieu = Double.parseDouble(cpadukienhoc.getText().toString());
                tinhToanMucTieu();
            }
        });
        this.resetCPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgNhanXetMucTieu.setBackgroundResource(R.drawable.muctieucuathimlagi);
                radGMucTieu.clearCheck();
                txtNhanXetMucTieu.setText("Mục tiêu của bạn là gì ?");
                initData();
            }
        });


    }

    private void initData(){
        try {
            JSONObject dataJsonO = new JSONObject(this.data);
            JSONObject tongket = dataJsonO.getJSONObject("tongket");
            String tbtichluyhe4 = tongket.getString("tbtichluyhe4");
            int tinchitichluy = tongket.getInt("tichluy");
            int tinchihknay = dataJsonO.getInt("sotinchihknay");
            if (tinchihknay == 0) {
                this.edtSoTinChiCanHoc.setText("150");
            } else {
                this.txtNhanXetMucTieuTongtin.setText("Tổng số tín (" + (tinchitichluy + tinchihknay) + " là tính đến hết học kì này" + "): ");
                this.edtSoTinChiCanHoc.setText("" + (tinchitichluy + tinchihknay));
            }

            this.txtCPA.setText(tbtichluyhe4 + "/" + tinchitichluy);
        } catch (Exception e) {

        }
        radGMucTieu_isChecked();
        this.tongsotinchitichluy = 0;
    }


    private void tinhDoiToeic() {
        try {
            int toic = Integer.parseInt(this.txtDuKienToic.getText().toString());
            ArrayList<HocPhanModel> diemHocPhanModelArrayList = new ArrayList();
            int sotinchitichluytienganh = 0;
            JSONObject objectdata = new JSONObject(this.data);
            JSONArray bangDiemHocPhan = objectdata.getJSONArray("listDiemThi");
            double tongTichLuyTiengAnh = 0;
            int tongtinchitienganh = 0;
            for (int i = bangDiemHocPhan.length() - 1; i >= 0; i--) {
                JSONObject diemHocPhan = bangDiemHocPhan.getJSONObject(i);
                if (!diemHocPhan.getString("he4").equals("F")) {
                    String mssmh = diemHocPhan.getString("msmh");
                    if (mssmh.equals("TAB1") || mssmh.equals("TAB2") || mssmh.equals("BAS1141") || mssmh.equals("BAS1142") || mssmh.equals("BAS1143") || mssmh.equals("BAS1144")) {
                        int stc = Integer.parseInt(diemHocPhan.getString("stc"));
                        tongtinchitienganh += stc;
                        String diemhe4 = diemHocPhan.getString("he4");
                        if (diemhe4.equals("A") || diemhe4.equals("A+")) {
                            tongTichLuyTiengAnh += stc * 4.0;
                        } else if (diemhe4.equals("B+")) {
                            tongTichLuyTiengAnh += stc * 3.5;
                        } else if (diemhe4.equals("B")) {
                            tongTichLuyTiengAnh += stc * 3;
                        } else if (diemhe4.equals("C+")) {
                            tongTichLuyTiengAnh += stc * 2.5;
                        } else if (diemhe4.equals("C")) {
                            tongTichLuyTiengAnh += stc * 2;
                        } else if (diemhe4.equals("D+")) {
                            tongTichLuyTiengAnh += stc * 1.5;
                        } else if (diemhe4.equals("D")) {
                            tongTichLuyTiengAnh += stc * 1;
                        }
                    }
                }
            }

            JSONObject tongket = objectdata.getJSONObject("tongket");
            int tinchitichluy = tongket.getInt("tichluy");
            double cpabandau = Double.parseDouble(tongket.getString("tbtichluyhe4"));

            int tinchihknay = objectdata.getInt("sotinchihknay");
            this.edtSoTinChiCanHoc.setText("" + (tinchitichluy + tinchihknay - tongtinchitienganh + 14));
            this.txtNhanXetMucTieuTongtin.setText("Tổng số tín (" + (tinchitichluy + tinchihknay - tongtinchitienganh + 14) + " là tính đến hết học kì này" + "): ");


            double cpatrutienganh = cpabandau * tinchitichluy - tongTichLuyTiengAnh;

            double trungbinh = 0;
            this.tongsotinchitichluy = tinchitichluy - tongtinchitienganh + 14;
            if (toic >= 450 && toic < 490) {
                trungbinh = (cpatrutienganh + 7 * 3.5 + 7 * 3.0) / (this.tongsotinchitichluy);
            } else if (toic >= 490) {
                trungbinh = (cpatrutienganh + 7 * 4.0 + 7 * 4.0) / (this.tongsotinchitichluy);
            } else {
                Toast.makeText(getApplicationContext(), "Từ 450 Toeic trở nên mới đổi được điểm", Toast.LENGTH_SHORT).show();
            }
            double sotang = trungbinh - cpabandau;
            this.cpahientai = trungbinh;
            Toast.makeText(getApplicationContext(), "CPA/tín của bạn tăng: " + String.format("%.2f", sotang) + "/" + (14 - tongtinchitienganh), Toast.LENGTH_SHORT).show();
            this.txtCPA.setText(String.format("%.2f", trungbinh) + "/" + this.tongsotinchitichluy);


            this.imgNhanXetMucTieu.setBackgroundResource(R.drawable.muctieucuathimlagi);
            this.txtNhanXetMucTieu.setText("Mục tiêu của bạn là gì ?");
            this.radGMucTieu.clearCheck();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "Số toeic không hợp lệ", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void radGMucTieu_isChecked() {
        this.radGMucTieu.setOnCheckedChangeListener(new C03513());
    }

    private double converHe4toNumber(String he4) {
        switch (he4) {
            case "A":
                return 4.0;
            case "A+":
                return 4.0;
            case "B+":
                return 3.5;
            case "B":
                return 3.0;
            case "C+":
                return 2.5;
            case "C":
                return 2.0;
            case "D+":
                return 1.5;
            case "D":
                return 1.0;
        };
        return 0.0;
    }

    private void tinhToanMucTieu() {
        try {
            JSONObject dataJsonO = new JSONObject(this.data);
            JSONObject tongket = dataJsonO.getJSONObject("tongket");
            int tinchitichluy;
            double cpabandau;
            if (this.tongsotinchitichluy == 0) {
                tinchitichluy = tongket.getInt("tichluy");
                String tbtichluyhe4 = tongket.getString("tbtichluyhe4");
                cpabandau = Double.parseDouble(tbtichluyhe4);
            } else {
                tinchitichluy = this.tongsotinchitichluy;
                cpabandau = this.cpahientai;
            }


            String edtSoTinCHiCanHoc1 = this.edtSoTinChiCanHoc.getText().toString();
            int tongsotin = Integer.parseInt(edtSoTinCHiCanHoc1);
            if (tongsotin <= tinchitichluy) {
                Toast.makeText(getApplicationContext(), "Tổng số tín không được nhỏ hơn hoặc bằng số tin chỉ tích lũy", Toast.LENGTH_SHORT).show();
            } else {
                double muctieu = this.cpaMucTieu;
                if (muctieu < 1.0d) {
                    Toast.makeText(getApplicationContext(), "Thấp quá không thèm tính", Toast.LENGTH_SHORT).show();
                    return;
                } else if (muctieu < 2.0d) {
                    Toast.makeText(getApplicationContext(), "Bạn cần ít nhất 2.0 để ra trường", Toast.LENGTH_SHORT).show();
                } else if (muctieu > 4.0d) {
                    Toast.makeText(getApplicationContext(), "CPA nên nhỏ hơn hoặc bằng 4.0", Toast.LENGTH_SHORT).show();
                    return;
                }

                // loai bo mon da cai thien
                JSONArray dangcaithien = dataJsonO.getJSONArray("dangcaithien");
                double tongtichluycaithien = 0.0;
                int tongtinchidangcaithien = 0;
                for (int i = dangcaithien.length() - 1; i >= 0; i--) {
                    JSONObject item = dangcaithien.getJSONObject(i);
                    double he4 = converHe4toNumber(item.getString("he4"));
                    if(he4 > 0){
                        tongtinchidangcaithien += item.getInt("stc");
                    }
                    tongtichluycaithien += item.getInt("stc") * he4;
                }

                cpabandau = (cpabandau*tinchitichluy - tongtichluycaithien)/(tinchitichluy-tongtinchidangcaithien);
                tinchitichluy = tinchitichluy - tongtinchidangcaithien;

                double tbmuctieu = (muctieu * tongsotin - cpabandau * tinchitichluy) / (tongsotin - tinchitichluy);

                if (tbmuctieu < 1) {
                    txtNhanXetMucTieu.setText("Cứ qua môn là được");
                    Toast.makeText(getApplicationContext(), "Cứ qua môn là được", Toast.LENGTH_SHORT).show();
                    this.imgNhanXetMucTieu.setBackgroundResource(R.drawable.quaydithim);
                } else if (tbmuctieu > 4) {
                    txtNhanXetMucTieu.setText("Mục tiêu không thể đạt được");
                    Toast.makeText(getApplicationContext(), "Mục tiêu không thể đạt được", Toast.LENGTH_SHORT).show();
                    this.imgNhanXetMucTieu.setBackgroundResource(R.drawable.chungtakhongthuocvenhau2);
                } else {
                    DuKienActivity.this.imgNhanXetMucTieu.setBackgroundResource(R.drawable.addnew);
                    Toast.makeText(getApplicationContext(), "Trung bình các môn còn lại: " + String.format("%.2f", tbmuctieu), Toast.LENGTH_SHORT).show();
                    txtNhanXetMucTieu.setText("Trung bình các môn còn lại: " + String.format("%.2f", tbmuctieu));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(), "Số tín chỉ không hợp lệ", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
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
            e.printStackTrace();
        }
    }
}
