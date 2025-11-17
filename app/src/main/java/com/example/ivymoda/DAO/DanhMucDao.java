package com.example.ivymoda.DAO;
import androidx.room.*;

import com.example.ivymoda.Entity.DanhMuc;

import java.util.List;

@Dao
public interface DanhMucDao {
    @Query("SELECT * FROM DanhMuc") List<DanhMuc> getAll();
    @Insert void insert(DanhMuc dm);
    @Update void update(DanhMuc dm);
    @Delete void delete(DanhMuc dm);
}