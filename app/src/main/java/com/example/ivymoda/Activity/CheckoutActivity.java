package com.example.ivymoda.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.ivymoda.AppDatabase;
import com.example.ivymoda.DAO.*;
import com.example.ivymoda.Entity.*;
import com.example.ivymoda.PayOSApi;
import com.example.ivymoda.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckoutActivity extends AppCompatActivity {

    private GioHangDao gioHangDao;
    private GioHang_SanPhamDao gioHangSPDao;
    private SanPhamDao sanPhamDao;

    private int maTaiKhoan = 1;
    private int maGioHang = -1;
    String s ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, "ivymoda_db")
                .allowMainThreadQueries() // tạm thời cho test
                .build();

        gioHangDao = db.gioHangDao();
        gioHangSPDao = db.gioHangSPDao();
        sanPhamDao = db.sanPhamDao();

        // BƯỚC 1: ĐẢM BẢO CÓ VAI TRÒ "Khách hàng" (maVaiTro = 1)
        if (db.vaiTroDao().getAll().isEmpty()) {
            VaiTro khachHang = new VaiTro();
            khachHang.tenVaiTro = "Khách hàng";
            db.vaiTroDao().insert(khachHang);
        }

        // BƯỚC 2: TẠO USER TEST (an toàn, không lỗi foreign key)
        TaiKhoan testUser = db.taiKhoanDao().getByTenDangNhap("testuser");
        if (testUser == null) {
            testUser = new TaiKhoan();
            testUser.tenDangNhap = "testuser";
            testUser.matKhau = "123456";
            testUser.hoTen = "Test User";
            testUser.email = "test@ivymoda.com";
            testUser.soDienThoai = "0909999999";
            testUser.maVaiTro = 1; // chắc chắn có VaiTro này rồi
            testUser.ngayTao = new Date();
            db.taiKhoanDao().insert(testUser);
        }

        maTaiKhoan = testUser.maTaiKhoan;

        // BƯỚC 3: TẠO GIỎ HÀNG + 2 SẢN PHẨM NẾU CHƯA CÓ
        GioHang gh = db.gioHangDao().getByTaiKhoan(maTaiKhoan);
        s = gh.toString() ;
        if (gh == null) {
            gh = new GioHang();
            gh.maTaiKhoan = maTaiKhoan;
            gh.ngayTao = new Date();
            db.gioHangDao().insert(gh);

            // Lấy lại để có maGioHang
            gh = db.gioHangDao().getByTaiKhoan(maTaiKhoan);
        }

        if (gh != null && db.gioHangSPDao().getByGioHang(gh.maGioHang).isEmpty()) {
            List<SanPham> spList = db.sanPhamDao().getAll();
            if (!spList.isEmpty()) {
                GioHang_SanPham item1 = new GioHang_SanPham();
                item1.maGioHang = gh.maGioHang;
                item1.maSanPham = spList.get(0).maSanPham;
                item1.soLuong = 1;
                db.gioHangSPDao().insert(item1);

                if (spList.size() > 1) {
                    GioHang_SanPham item2 = new GioHang_SanPham();
                    item2.maGioHang = gh.maGioHang;
                    item2.maSanPham = spList.get(1).maSanPham;
                    item2.soLuong = 1;
                    db.gioHangSPDao().insert(item2);
                }
            }
        }

        // BƯỚC CUỐI: HIỂN THỊ GIỎ HÀNG
        loadGioHangAndShow();
    }

    private void taoGioHangTestNeuCan(AppDatabase db) {
        GioHang gh = db.gioHangDao().getByTaiKhoan(maTaiKhoan);
        if (gh != null) return; // đã có thì thôi

        // TẠO GIỎ HÀNG MỚI
        gh = new GioHang();
        gh.maTaiKhoan = maTaiKhoan;
        gh.ngayTao = new Date();
        db.gioHangDao().insert(gh);

        // LẤY LẠI ĐỂ CÓ maGioHang
        gh = db.gioHangDao().getByTaiKhoan(maTaiKhoan);
        if (gh == null) return;

        List<SanPham> spList = db.sanPhamDao().getAll();
        if (spList.isEmpty()) return;

        // THÊM 2 SẢN PHẨM VÀO GIỎ
        GioHang_SanPham item1 = new GioHang_SanPham();
        item1.maGioHang = gh.maGioHang;
        item1.maSanPham = spList.get(0).maSanPham;
        item1.soLuong = 1;
        db.gioHangSPDao().insert(item1);

        if (spList.size() > 1) {
            GioHang_SanPham item2 = new GioHang_SanPham();
            item2.maGioHang = gh.maGioHang;
            item2.maSanPham = spList.get(1).maSanPham;
            item2.soLuong = 1;
            db.gioHangSPDao().insert(item2);
        }
    }

    private void loadGioHangAndShow() {
        GioHang gioHang = gioHangDao.getByTaiKhoan(maTaiKhoan);
        if (gioHang == null || gioHang.maGioHang <= 0) {
            Toast.makeText(this, "Không tìm thấy giỏ hàng!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        maGioHang = gioHang.maGioHang;
        List<GioHang_SanPham> listSP = gioHangSPDao.getByGioHang(maGioHang);
        if (listSP.isEmpty()) {
            Toast.makeText(this,s , Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Tính tổng + hiển thị (giữ nguyên code cũ của bạn)
        double tongTien = 0;
        StringBuilder chiTiet = new StringBuilder("Đơn hàng gồm:\n");
        for (GioHang_SanPham item : listSP) {
            SanPham sp = sanPhamDao.getById(item.maSanPham);
            if (sp != null) {
                double thanhTien = sp.giaBan * item.soLuong;
                tongTien += thanhTien;
                chiTiet.append("• ").append(sp.tenSanPham)
                        .append(" x").append(item.soLuong)
                        .append(" = ").append(formatPrice(thanhTien)).append("\n");
            }
        }

        TextView tvChiTiet = findViewById(R.id.tvChiTietDonHang);
        TextView tvTongTien = findViewById(R.id.tvTongTien);
        Button btnThanhToan = findViewById(R.id.btnThanhToanPayOS);

        if (tvChiTiet != null) tvChiTiet.setText(chiTiet.toString());
        if (tvTongTien != null) tvTongTien.setText(formatPrice(tongTien));

        if (btnThanhToan != null) {
            double finalTongTien = tongTien;
            btnThanhToan.setOnClickListener(v -> createPayOSPayment(finalTongTien, listSP));
        }
    }

    private void createPayOSPayment(double tongTien, List<GioHang_SanPham> listSP) {
        int orderCode = 100000 + new Random().nextInt(900000);

        // === BODY GỬI LÊN PAYOS ===
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("orderCode", orderCode);
        body.put("amount", (int) tongTien);
        body.put("description", "DH" + orderCode);
        body.put("returnUrl", "ivymoda://payment?status=success&orderCode=" + orderCode);
        body.put("cancelUrl",  "ivymoda://payment?status=cancel&orderCode=" + orderCode);

        // items: CÓ trong body -> PayOS hiện sản phẩm
        List<Map<String, Object>> items = new ArrayList<>();
        for (GioHang_SanPham item : listSP) {
            SanPham sp = sanPhamDao.getById(item.maSanPham);
            if (sp != null) {
                Map<String, Object> i = new LinkedHashMap<>();
                i.put("name", sp.tenSanPham);
                i.put("quantity", item.soLuong);
                i.put("price", (int) sp.giaBan);
                items.add(i);
            }
        }
        body.put("items", items);

        // === KEY ===
        String clientId = "d2205659-ce5c-4775-9c3b-2b45306ce0a6";
        String apiKey   = "3cc76ca0-480c-454d-8ef6-830c081f9c5d";
        String checksumKey = "9e7bbebffd69c7178826e10c3420dbd0b863a42bdaf825065b3f281d9a25b0cb";

        // === TẠO SIGNATURE ====
        @SuppressLint("DefaultLocale") String dataToSign = String.format("amount=%d&cancelUrl=%s&description=%s&orderCode=%d&returnUrl=%s",
                (int) tongTien,
                "ivymoda://payment?status=cancel&orderCode=" + orderCode,
                "DH" + orderCode,
                orderCode,
                "ivymoda://payment?status=success&orderCode=" + orderCode
        );

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(
                    "9e7bbebffd69c7178826e10c3420dbd0b863a42bdaf825065b3f281d9a25b0cb".getBytes("UTF-8"),
                    "HmacSHA256"
            );
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(dataToSign.getBytes("UTF-8"));

            StringBuilder signature = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) signature.append('0');
                signature.append(hex);
            }

            body.put("signature", signature.toString());

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tạo chữ ký", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(checksumKey.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(dataToSign.getBytes("UTF-8"));

            StringBuilder signature = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) signature.append('0');
                signature.append(hex);
            }

            body.put("signature", signature.toString());

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api-merchant.payos.vn/v2/payment-requests/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PayOSApi api = retrofit.create(PayOSApi.class);

            Call<Map<String, Object>> call = api.createPaymentRaw(clientId, apiKey, body);

            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> res = response.body();
                        String code = (String) res.get("code");
                        if ("00".equals(code)) {
                            Map<String, Object> data = (Map<String, Object>) res.get("data");
                            String url = (String) data.get("checkoutUrl");

                            luuDonHangVaoDatabase(orderCode, tongTien, listSP);
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            Toast.makeText(CheckoutActivity.this, "Đang chuyển sang PayOS...", Toast.LENGTH_LONG).show();
                        } else {
                            String desc = (String) res.get("desc");
                            Toast.makeText(CheckoutActivity.this, "PayOS lỗi: " + desc, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(CheckoutActivity.this, "Lỗi server PayOS", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(CheckoutActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tạo chữ ký: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void luuDonHangVaoDatabase(int orderCode, double tongTien, List<GioHang_SanPham> listSP) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this); // DÙNG getInstance() – ĐÚNG CHUẨN!!!

            try {
                db.beginTransaction(); // BẮT ĐẦU GIAO DỊCH – ĐẢM BẢO HOÁ ĐƠN + CHI TIẾT ĐỀU THÀNH CÔNG HOẶC ĐỀU FAIL

                // 1. TẠO HOÁ ĐƠN + GÁN ORDERCODE TỪ PAYOS (QUAN TRỌNG NHẤT!!!)
                HoaDon hoaDon = new HoaDon();
                hoaDon.orderCode = orderCode;                    // GÁN ORDERCODE ĐỂ TÌM LẠI SAU
                hoaDon.maTaiKhoan = maTaiKhoan;
                hoaDon.tongTien = tongTien;
                hoaDon.trangThai = "Chờ thanh toán";
                hoaDon.ngayTao = new Date();
                hoaDon.diaChiNhanHang = "Hà Nội";
                hoaDon.thoiGianNhanHang = new Date(System.currentTimeMillis() + 3L * 24 * 60 * 60 * 1000);

                // Insert hoá đơn
                db.hoaDonDao().insert(hoaDon);

                // LẤY maHoaDon VỪA TẠO (Room tự sinh)
                HoaDon hoaDonVuaTao = db.hoaDonDao().getHoaDonByOrderCode(orderCode);
                if (hoaDonVuaTao == null || hoaDonVuaTao.maHoaDon == 0) {
                    throw new Exception("Không lấy được maHoaDon vừa tạo!");
                }

                // 2. THÊM CHI TIẾT HOÁ ĐƠN
                for (GioHang_SanPham ghsp : listSP) {
                    SanPham sp = sanPhamDao.getById(ghsp.maSanPham);
                    if (sp != null) {
                        SanPham_HoaDon sphd = new SanPham_HoaDon();
                        sphd.maHoaDon = hoaDonVuaTao.maHoaDon;
                        sphd.maSanPham = ghsp.maSanPham;
                        sphd.soLuong = ghsp.soLuong;
                        sphd.giaBan = sp.giaBan;

                        db.sanPhamHoaDonDao().insert(sphd);
                    }
                }

                // 3. XÓA GIỎ HÀNG SAU KHI ĐẶT HÀNG THÀNH CÔNG
                if (maGioHang != -1) {
                    db.gioHangSPDao().clearGioHang(maGioHang);
                }

                // ĐÁNH DẤU GIAO DỊCH THÀNH CÔNG
                db.setTransactionSuccessful();

                // THÔNG BÁO THÀNH CÔNG TRÊN UI
                runOnUiThread(() -> {
                    Toast.makeText(this, "Tạo đơn hàng #" + orderCode + " thành công!\nĐang chuyển sang PayOS...", Toast.LENGTH_LONG).show();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi tạo đơn hàng: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } finally {
                db.endTransaction(); // KẾT THÚC GIAO DỊCH
            }
        }).start();
    }

    private String formatPrice(double price) {
        NumberFormat f = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return f.format(price) + "đ";
    }
}