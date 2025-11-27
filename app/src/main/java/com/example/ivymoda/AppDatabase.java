package com.example.ivymoda;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.ivymoda.Convert.DateConverter;
import com.example.ivymoda.DAO.*;
import com.example.ivymoda.Entity.*;

@Database(entities = {
        SanPham.class,
        DanhMuc.class,
        TaiKhoan.class,
        VaiTro.class,
        GioHang.class,
        GioHang_SanPham.class,
        HoaDon.class,
        SanPham_HoaDon.class
}, version = 6, exportSchema = false)

@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    // TẤT CẢ DAO – ĐÃ BỔ SUNG VaiTroDao
    public abstract SanPhamDao sanPhamDao();
    public abstract DanhMucDao danhMucDao();
    public abstract TaiKhoanDao taiKhoanDao();
    public abstract VaiTroDao vaiTroDao();           // ĐÃ THÊM DÒNG NÀY
    public abstract GioHangDao gioHangDao();
    public abstract GioHang_SanPhamDao gioHangSPDao();
    public abstract HoaDonDao hoaDonDao();
    public abstract SanPham_HoaDonDao sanPhamHoaDonDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "ivymoda_db"
                            )
                            .fallbackToDestructiveMigration() // xóa DB cũ nếu cấu trúc thay đổi
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "ivymoda_db"
                            )
                            .allowMainThreadQueries()           // tạm thời để test nhanh như thần
                            .fallbackToDestructiveMigration()   // không bao giờ lỗi schema nữa
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}