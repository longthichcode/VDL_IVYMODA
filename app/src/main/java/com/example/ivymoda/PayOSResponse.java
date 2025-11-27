package com.example.ivymoda;

// PayOSResponse.java
public class PayOSResponse {
    public int code;
    public String desc;
    public boolean success;
    public Data data;

    public static class Data {
        public String checkoutUrl;
        public String qrCode;
    }
}