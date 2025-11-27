package com.example.ivymoda.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.ivymoda.AppDatabase;
import com.example.ivymoda.Entity.TaiKhoan;
import com.example.ivymoda.MainActivity;
import com.example.ivymoda.R;

public class PaymentResultActivity extends AppCompatActivity {

    private AppDatabase db;
    private int maTaiKhoan = 1; // nếu bố có login thì lấy từ SharedPreferences hoặc Intent
    private int maGioHang = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy database một lần duy nhất
        db = Room.databaseBuilder(this, AppDatabase.class, "ivymoda_db")
                .allowMainThreadQueries() // tạm thời để test, sau này chuyển sang ViewModel + Coroutine
                .build();

        Uri data = getIntent().getData();
        String status = null;
        int orderCode = -1;

        if (data != null) {
            status = data.getQueryParameter("status");
            String codeStr = data.getQueryParameter("orderCode");
            if (codeStr != null) {
                try {
                    orderCode = Integer.parseInt(codeStr);
                } catch (Exception ignored) {}
            }
        }

        // Lấy maTaiKhoan từ tài khoản đang đăng nhập (nếu có)
        TaiKhoan taiKhoan = db.taiKhoanDao().getByTenDangNhap("testuser");
        if (taiKhoan != null) maTaiKhoan = taiKhoan.maTaiKhoan;

        if ("success".equals(status)) {
            setContentView(R.layout.payment_success);
            capNhatDonHangThanhCong(orderCode);
            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();

            findViewById(R.id.btnBackToHome).setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            });

        } else {
            // cancel hoặc lỗi
            setContentView(R.layout.payment_failed);
            capNhatHuyDonHang(orderCode);
            Toast.makeText(this, "Bạn đã hủy thanh toán!", Toast.LENGTH_LONG).show();

            findViewById(R.id.btnTryAgain).setOnClickListener(v -> {
                finish(); // quay lại Checkout để thử lại
            });

            findViewById(R.id.btnBackToCart).setOnClickListener(v -> {
                startActivity(new Intent(this, CheckoutActivity.class));
                finish();
            });
        }
    }

    private void capNhatDonHangThanhCong(int orderCode) {
        if (orderCode != -1) {
            // Cập nhật trạng thái hoá đơn theo orderCode thật
            db.hoaDonDao().updateTrangThaiByOrderCode(orderCode, "Đã thanh toán");
        } else {
            // Nếu không có orderCode (trường hợp cũ), dùng ID cố định
            db.hoaDonDao().updateTrangThai(1, "Đã thanh toán");
        }

        // Xóa giỏ hàng
        maGioHang = db.gioHangDao().getMaGioHangByTaiKhoan(maTaiKhoan);
        if (maGioHang != -1) {
            db.gioHangSPDao().clearGioHang(maGioHang);
        }
    }

    private void capNhatHuyDonHang(int orderCode) {
        if (orderCode != -1) {
            db.hoaDonDao().updateTrangThaiByOrderCode(orderCode, "Huỷ thanh toán");
        } else {
            db.hoaDonDao().updateTrangThai(1, "Huỷ thanh toán");
        }

        // Không xóa giỏ hàng khi hủy → người dùng vẫn giữ được sản phẩm
        // Nếu bố muốn xóa thì bỏ comment dòng dưới
        // db.gioHangSPDao().clearGioHang(maGioHang);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}