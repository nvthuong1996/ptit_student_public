package com.example.myptit.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LichHocModel {
    private String CBGV;
    private String DSSV;
    private String KDK;
    private String maLop;
    private String maMH;
    private String NMH;
    private String PHONG;
    private String ST;
    private String STC;
    private String STCHP;
    private String tenMh;
    private String TH;
    private String THU;
    private String tietBD;
    private String TUAN;
    private String hBatDau;
    private String ngaybatdau;
    private String GV;


    public String getGV() {
        return GV;
    }

    public void setGV(String GV) {
        this.GV = GV;
    }

    public String getNgaybatdau() {
        return ngaybatdau;
    }

    public void setNgaybatdau(String ngaybatdau) {
        this.ngaybatdau = ngaybatdau;
    }

    public String gethBatDau() {
        return hBatDau;
    }

    public void sethBatDau(String hBatDau) {
        this.hBatDau = hBatDau;
    }

    public String getCBGV() {
        return CBGV;
    }

    public void setCBGV(String CBGV) {
        this.CBGV = CBGV;
    }

    public String getDSSV() {
        return DSSV;
    }

    public void setDSSV(String DSSV) {
        this.DSSV = DSSV;
    }

    public String getKDK() {
        return KDK;
    }

    public void setKDK(String KDK) {
        this.KDK = KDK;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getMaMH() {
        return maMH;
    }

    public void setMaMH(String maMH) {
        this.maMH = maMH;
    }

    public String getNMH() {
        return NMH;
    }

    public void setNMH(String NMH) {
        this.NMH = NMH;
    }

    public String getPHONG() {
        return PHONG;
    }

    public void setPHONG(String PHONG) {
        this.PHONG = PHONG;
    }

    public String getST() {
        return ST;
    }

    public void setST(String ST) {
        this.ST = ST;
    }

    public String getSTC() {
        return STC;
    }

    public void setSTC(String STC) {
        this.STC = STC;
    }

    public String getSTCHP() {
        return STCHP;
    }

    public void setSTCHP(String STCHP) {
        this.STCHP = STCHP;
    }

    public String getTenMh() {
        return tenMh;
    }

    public void setTenMh(String tenMh) {
        this.tenMh = tenMh;
    }

    public String getTH() {
        return TH;
    }

    public void setTH(String TH) {
        this.TH = TH;
    }

    public String getTHU() {
        return THU;
    }

    public void setTHU(String THU) {
        this.THU = THU;
    }

    public String getTietBD() {
        return tietBD;
    }

    public String getNhomToThucHanh() {
        if(this.TH.equals("")){
            return this.NMH;
        }else{
            return this.NMH+" thực hành: "+this.TH;
        }
    }

    public void setTietBD(String tietBD) {
        this.tietBD = tietBD;
    }

    public String getTUAN() {
        return TUAN;
    }

    public void setTUAN(String TUAN) {
        this.TUAN = TUAN;
    }

    public int getCurrentTuan(){
        int tuan = 100;
        try{
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date date       = format.parse (this.getNgaybatdau());
            date.setHours(0);
            date.setMinutes(0);

            Date now = new Date();
            long subSecond = now.getTime() - date.getTime();
            if(subSecond > 0){
                float subDate =(float) subSecond / (24 * 60 * 60 * 1000);
                float subTuan =subDate / 7;
                tuan = (int)subTuan;
                if(tuan < subTuan){
                    tuan += 1;
                }
                if(subTuan > tuan){
                    tuan++;
                }
            }else{
                return 0;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return tuan;
    }

    private int converWeekToNumber(String e) {
        String now = new SimpleDateFormat("EEE").format(new Date());

        if (e.equals("Hai") || e.equals("Mon")) {
            if(now.equals("Mon")) return 1;
            return 2;
        };
        if (e.equals("Ba") || e.equals("Tue")){
            if(now.equals("Tue")) return 1;
            return 3;
        };
        if (e.equals("Tư") || e .equals("Wed")) {
            if(now.equals("Wed")) return 1;
            return 4;
        };
        if (e.equals("Năm") || e.equals( "Thu")) {
            if(now.equals("Thu")) return 1;
            return 5;
        };
        if (e.equals("Sáu") || e.equals("Fri")) {
            if(now.equals("Fri")) return 1;
            return 6;
        };
        if (e.equals("Bảy") || e.equals("Sat")) {
            if(now.equals("Sat")) return 1;
            return 7;
        };
        if (e.equals("CN") || e.equals("Sun")) {
            if(now.equals("Sun")) return 1;
            return 8;
        };
        return 0;
    }

    public int getSortValue(int tuanthu){
        String tuan = this.TUAN;

        int day = converWeekToNumber(this.getTHU());
        int tietBD = Integer.parseInt(this.getTietBD());


        try{
            char chart = tuan.charAt(tuanthu-1);
            if(chart != '-'){
                int sort =  ( 10 - day) * 100 + ( 12 - tietBD);
                return sort;
            }else{
                return 0;
            }
        }catch (Exception ex){
            return 0;
        }
    }
}
