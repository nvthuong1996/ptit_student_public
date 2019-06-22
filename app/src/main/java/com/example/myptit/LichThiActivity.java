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
import com.example.myptit.adapter.LichThiAdapter;
import com.example.myptit.model.LichThiModel;
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

public class LichThiActivity extends AppCompatActivity {
    String data;
    LichThiAdapter lichThiPhanAdapter;
    ArrayList<LichThiModel> lichThiModelArrayList;
    ListView lvDiemHocPhan;
    TextView txtTinChiNo;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_lich_thi);

        this.lvDiemHocPhan = (ListView) findViewById(R.id.lvMonNo_MonNo_);


        readData();
        hienthilichthi();
    }

    private void hienthilichthi() {
        this.lichThiModelArrayList = new ArrayList();
        try {
            JSONArray bangDiemHocPhan = new JSONObject(this.data).getJSONArray("lichthi");
            for(int j = 0; j < bangDiemHocPhan.length(); j++){
                JSONArray itemlichthi = bangDiemHocPhan.getJSONArray(j);
                LichThiModel obj = new LichThiModel();
                for (int i = 1; i < itemlichthi.length(); i++) {
                    String item = itemlichthi.getString(i);
                    if(i == 1){
                        obj.setMamonhoc(item);
                    }else if( i == 2){
                        obj.setTenmonhoc(item);
                    }else if( i == 3){
                        obj.setGhepthi(item);
                    }else if( i == 4){
                        obj.setTothi(item);
                    }else if( i == 5){
                        obj.setSoluong(item);
                    }else if( i == 6){
                        obj.setNgaythi(item);
                    }else if( i == 7){
                        obj.setGiobatdau(item);
                    }else if( i == 8){
                        obj.setSophut(item);
                    }else if( i == 9){
                        obj.setPhong(item);
                    }else if( i == 10){
                        obj.setGhichu(item);
                    }
                }
                this.lichThiModelArrayList.add(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(this.lichThiModelArrayList.size() == 0){
            return;
        }
        this.lichThiPhanAdapter = new LichThiAdapter(this, R.layout.item_lichthi, this.lichThiModelArrayList);
        this.lvDiemHocPhan.setAdapter(this.lichThiPhanAdapter);
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
