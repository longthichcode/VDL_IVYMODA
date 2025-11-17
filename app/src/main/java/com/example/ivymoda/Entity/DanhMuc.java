package com.example.ivymoda.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "DanhMuc")
public class DanhMuc {
    @PrimaryKey(autoGenerate = true)
    public int maDanhMuc;
    public String tenDanhMuc;
}