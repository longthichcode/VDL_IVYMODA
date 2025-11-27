package com.example.ivymoda.DAO;
import androidx.room.*;

import com.example.ivymoda.Entity.SanPham;

import java.util.List;

@Dao
public interface SanPhamDao {

    @Query("SELECT * FROM SanPham")
    List<SanPham> getAll();

    @Query("SELECT * FROM SanPham WHERE maSanPham = :maSanPham LIMIT 1")
    SanPham getById(int maSanPham);   // ĐÃ THÊM @Query → KHÔNG CÒN LỖI!

    @Query("SELECT * FROM SanPham WHERE maDanhMuc = :maDanhMuc")
    List<SanPham> getByDanhMuc(int maDanhMuc);

    @Insert
    void insert(SanPham sp);

    @Insert
    void insertAll(List<SanPham> list);

    @Update
    void update(SanPham sp);

    @Delete
    void delete(SanPham sp);
}