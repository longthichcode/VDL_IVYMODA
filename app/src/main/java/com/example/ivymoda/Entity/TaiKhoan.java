package com.example.ivymoda.Entity;

import androidx.room.*;
import java.util.Date;

@Entity(tableName = "TaiKhoan",
        foreignKeys = @ForeignKey(entity = VaiTro.class,
                parentColumns = "maVaiTro",
                childColumns = "maVaiTro",
                onDelete = ForeignKey.CASCADE))
public class TaiKhoan {
    @PrimaryKey(autoGenerate = true)
    public int maTaiKhoan;

    public String matKhau;
    public String tenDangNhap;
    public String soDienThoai;
    public String email;
    public String hoTen;
    public Date ngaySinh;
    public String diaChi;
    public String gioiTinh; // "Nam", "Nữ"
    @ColumnInfo(index = true)
    public int maVaiTro;
    public Date ngayTao;

    @Override
    public String toString() {
        return "TaiKhoan{" +
                "maTaiKhoan=" + maTaiKhoan +
                ", matKhau='" + matKhau + '\'' +
                ", tenDangNhap='" + tenDangNhap + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", email='" + email + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", diaChi='" + diaChi + '\'' +
                ", gioiTinh='" + gioiTinh + '\'' +
                ", maVaiTro=" + maVaiTro +
                ", ngayTao=" + ngayTao +
                '}';
    }
}