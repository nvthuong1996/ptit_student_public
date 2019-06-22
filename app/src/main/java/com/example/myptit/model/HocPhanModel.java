package com.example.myptit.model;

public class HocPhanModel {
    private String diemChu;
    private String diemCuoiKi;
    private String diemQuaTrinh;
    private Double diemSo;
    private String maHocPhan;
    private String soTinChi;
    private String tenHocPhan;

    public String getDiemQuaTrinh() {
        return this.diemQuaTrinh;
    }

    public void setDiemQuaTrinh(String diemQuaTrinh) {
        this.diemQuaTrinh = diemQuaTrinh;
    }

    public String getDiemCuoiKi() {
        return this.diemCuoiKi;
    }

    public void setDiemCuoiKi(String diemCuoiKi) {
        this.diemCuoiKi = diemCuoiKi;
    }

    public String getMaHocPhan() {
        return this.maHocPhan;
    }

    public void setMaHocPhan(String maHocPhan) {
        this.maHocPhan = maHocPhan;
    }

    public String getTenHocPhan() {
        return this.tenHocPhan;
    }

    public void setTenHocPhan(String tenHocPhan) {
        this.tenHocPhan = tenHocPhan;
    }

    public String getSoTinChi() {
        return this.soTinChi;
    }

    public void setSoTinChi(String soTinChi) {
        this.soTinChi = soTinChi;
    }

    public String getDiemChu() {
        return this.diemChu;
    }

    public void setDiemChu(String diemChu) {
        this.diemChu = diemChu;
    }

    public Double getDiemSo() {
        return this.diemSo;
    }

    public void setDiemSo(Double diemSo) {
        this.diemSo = diemSo;
    }

    public String toString() {
        return this.maHocPhan + this.tenHocPhan + this.soTinChi + this.diemChu + this.diemSo + this.diemQuaTrinh + this.diemCuoiKi;
    }
}
