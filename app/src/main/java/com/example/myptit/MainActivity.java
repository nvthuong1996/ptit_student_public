package com.example.myptit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import org.json.*;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import android.widget.*;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String jsonData = "";

    BarChart chart;
    LineChart lineChart;
    TextView txtCPA;
    TextView txtHoTen;
    TextView txtLop;
    TextView txtNhanXet;
    TextView txtTinChiNo;
    TextView txtTinChiTichLuy;
    JSONObject fulldata;
    private FirebaseAuth mAuth;
    int VERSIONAPP = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.chart = (BarChart) findViewById(R.id.chart);
        this.lineChart = (LineChart) findViewById(R.id.lineChart);
        this.txtNhanXet = (TextView) findViewById(R.id.txtNhanXet_TongQuan);
        this.txtHoTen = (TextView) findViewById(R.id.txtHoTen_TongQuan);
        this.txtLop = (TextView) findViewById(R.id.txtLop_TongQuan);
        this.txtTinChiTichLuy = (TextView) findViewById(R.id.txtTInChiTichLuy_TongQuan);
        this.txtTinChiNo = (TextView) findViewById(R.id.txtTinChiNo_TongQuan);
        this.txtCPA = (TextView) findViewById(R.id.txtCPA_TongQuan);

        this.mAuth = FirebaseAuth.getInstance();

        init();


//        hienThiBarChart();
//        hienThiLineChart();
    }

    public void init() {
        String json = readData();
        if (json.equals("")) {
            // open ativity login
            Intent messageIntent = new Intent(this, LoginActivity.class);
            messageIntent.putExtra("requestCode", 1);
            startActivityForResult(messageIntent, 1);
            return;
        }
        this.jsonData = json;
        try {
            this.fulldata = new JSONObject(json);
            handleLogin();
            int versionApp = this.fulldata.getInt("appversion");
            if (VERSIONAPP < versionApp) {
                Toast.makeText(getApplicationContext(), "Bạn cần UPDATE ứng dụng bằng CH PLAY để tương thích với dữ liệu mới", Toast.LENGTH_LONG).show();
            } else if (VERSIONAPP > versionApp) {
                Toast.makeText(getApplicationContext(), "Bạn cần UPDATE dữ liệu để tương thích với phiên bản mới", Toast.LENGTH_LONG).show();
                updateData();
                return;
            }

            JSONObject tongket = fulldata.getJSONObject("tongket");

            double tbtichluyhe4 = tongket.getDouble("tbtichluyhe4");
            int tichluy = tongket.getInt("tichluy");
            int tinchino = tongket.getInt("tinchino");


            this.txtTinChiTichLuy.setText("" + tichluy);
            this.txtCPA.setText("" + tbtichluyhe4);
            this.txtTinChiNo.setText("" + tinchino);


            if ((tbtichluyhe4) < 2.5d) {
                this.txtNhanXet.setText("Hazz, cố gắng lên bạn, CPA thấp quá :'(");
                //this.txtNhanXet.setTextColor(SupportMenu.CATEGORY_MASK);
            } else if ((tbtichluyhe4) >= 3.2d) {
                this.txtNhanXet.setText("Good, cố gắng duy trì nha bạn :D");
                this.txtNhanXet.setTextColor(-16711936);
            } else {
                this.txtNhanXet.setText("OK, tiếp tục cố gắng nha bạn :)");
            }

            JSONObject basicInfo = fulldata.getJSONObject("baseinfo");
            String tensv = basicInfo.getString("ten_sv");
            String tenLop = basicInfo.getString("lop");
            txtHoTen.setText(tensv);
            txtLop.setText(tenLop);

        } catch (Exception ex) {

        }

        hienThiBarChart();
        hienThiLineChart();


    }

    private void handleLogin() {
        System.out.println("test");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            try {
                mAuth.signInWithCustomToken(this.fulldata.getString("token"))
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Authentication success.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            System.out.println("oke");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            init();
        }
    }

    public void updateData() {
        Intent messageIntent = new Intent(this, LoginActivity.class);
        messageIntent.putExtra("requestCode", 2);
        try {
            JSONObject info = fulldata.getJSONObject("baseinfo");
            String mssv = info.getString("mssv");
            messageIntent.putExtra("mssv", mssv);
        } catch (Exception ex) {
            messageIntent.putExtra("mssv", "");
        }

        startActivityForResult(messageIntent, 1);
    }

    public void saveDataDefault(String content) {
        String fileName = "data.json";
        FileOutputStream outputStream = null;
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readData() {
        try {
            String fileName = "data.json";
            FileInputStream in = this.openFileInput(fileName);

            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            StringBuffer buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            return buffer.toString();

        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            updateData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_HocPhi) {
            Intent messageIntent = new Intent(this, HocPhiActivity.class);
            startActivity(messageIntent);
        } else if (id == R.id.nav_MonNo) {
            Intent messageIntent = new Intent(this, MonNoActivity.class);
            startActivity(messageIntent);

        } else if (id == R.id.nav_LichHoc) {
            Intent messageIntent = new Intent(this, TKBActivity.class);
            startActivity(messageIntent);
        } else if (id == R.id.nav_LichThi) {
            Intent messageIntent = new Intent(this, LichThiActivity.class);
            startActivity(messageIntent);
        } else if (id == R.id.nav_BangDiem) {
            Intent messageIntent = new Intent(this, BangDiemActivity.class);
            startActivity(messageIntent);
        } else if (id == R.id.nav_DuKienHoc) {
            Intent messageIntent = new Intent(this, DuKienActivity.class);
            startActivity(messageIntent);
        } else if (id == R.id.nav_DangXuat) {
            saveDataDefault("");
            Intent messageIntent = new Intent(this, LoginActivity.class);
            messageIntent.putExtra("requestCode", 1);
            startActivityForResult(messageIntent, 1);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void hienThiBarChart() {
        try {
            JSONArray barchardata = new JSONObject(jsonData).getJSONArray("barchar");
            ArrayList<BarEntry> entries = new ArrayList();
            List<String> labels = new ArrayList<String>();
            if (barchardata.length() != 0) {
                for (int i = 0; i < barchardata.length(); i++) {
                    JSONObject diemhocphan = barchardata.getJSONObject(i);
                    String he4 = diemhocphan.getString("he4");
                    int stc = diemhocphan.getInt("stc");
                    entries.add(new BarEntry(i, (float) stc));
                    labels.add(he4);
                }
            }
            BarDataSet dataSet = new BarDataSet(entries, "Số tín chỉ");
            dataSet.setValueTextSize(12.0f);

            BarData barData = new BarData(dataSet);

            this.chart.setData(barData);

            this.chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

            this.chart.getXAxis().setPosition(XAxisPosition.BOTTOM);
            dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            this.chart.getXAxis().setTextSize(12.0f);
            this.chart.animateY(1000);
            this.chart.getDescription().setEnabled(false);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

    private void hienThiLineChart() {
        try {
            JSONArray barchardata = new JSONObject(jsonData).getJSONArray("lineChart");

            ArrayList<Entry> entries = new ArrayList();
            List<String> labels = new ArrayList<String>();
            if (barchardata.length() != 0) {
                for (int i = 0; i < barchardata.length(); i++) {
                    JSONObject diemhocphan = barchardata.getJSONObject(i);
                    double tbhkhe4 = diemhocphan.getDouble("tbtichluyhe4");
                    String hk = diemhocphan.getString("hk");
                    entries.add(new BarEntry(i, (float) tbhkhe4));
                    if (i % 2 == 0) {
                        labels.add(hk);
                    } else {
                        labels.add(hk.substring(2));
                    }
                }
            }
            LineDataSet dataSet = new LineDataSet(entries, "CPA");
            dataSet.setValueTextSize(12.0f);

            LineData dataLine = new LineData(dataSet);

            this.lineChart.setData(dataLine);

            this.lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));

            this.lineChart.getXAxis().setPosition(XAxisPosition.BOTTOM);
            dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            this.lineChart.getXAxis().setTextSize(12.0f);
            this.lineChart.animateY(1000);
            this.lineChart.getDescription().setEnabled(false);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

}
