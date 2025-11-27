package com.example.ivymoda.DAO;

import androidx.room.*;

import com.example.ivymoda.Entity.GioHang;

import java.util.List;

@Dao
public interface GioHangDao {

    @Query("SELECT * FROM GioHang WHERE maTaiKhoan = :maTaiKhoan ORDER BY maGioHang DESC LIMIT 1")
    GioHang getByTaiKhoan(int maTaiKhoan);

    @Query("SELECT maGioHang FROM giohang WHERE maTaiKhoan = :maTaiKhoan LIMIT 1")
    int getMaGioHangByTaiKhoan(int maTaiKhoan);

    @Insert
    void insert(GioHang gh);

    @Update
    void update(GioHang gh);

    @Delete
    void delete(GioHang gh);
}