package com.example.ivymoda.DAO;
import androidx.room.*;

import com.example.ivymoda.Entity.SanPham;

import java.util.List;

@Dao
public interface SanPhamDao {
    @Query("SELECT * FROM SanPham ORDER BY ngayTao DESC")
    List<SanPham> getAll();

    @Query("SELECT * FROM SanPham WHERE maDanhMuc = :maDanhMuc")
    List<SanPham> getByDanhMuc(int maDanhMuc);

    @Query("SELECT * FROM SanPham WHERE tenSanPham LIKE '%' || :keyword || '%'")
    List<SanPham> search(String keyword);

    @Insert
    void insert(SanPham sp);

    @Update
    void update(SanPham sp);

    @Delete
    void delete(SanPham sp);
}