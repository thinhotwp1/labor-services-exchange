package com.example.test;

import java.util.SortedMap;

public class Main1 {
    public static void main(String[] args) {

        // Tạo entity con người, các thộc tính: (name, age, gender, address, number)
        // gán du lieu va in ra cac thuoc tinh cua con nguoi do
//        Person thinh = new Person();
//        thinh.setName("Thinh");
//        thinh.setAge(23);
//        thinh.setGender("Nam");
//        thinh.setAddress("123 ABC, TP. Ha Noi");
//        thinh.setPhoneNumber("0923001270");
//        thinh.setMoneyAmount(200.5);
//
//        // sout + tab
//        System.out.println(thinh.getName());
//        System.out.println(thinh.getAge());

        // Tao entity man hinh: inch, hangSanXuat, ngaySanXuat, giaTien, canNang
        // In ra man hinh cac gia tri
        ManHinh manHinhLG = new ManHinh();
        manHinhLG.setInch(24);
        manHinhLG.setCanNang(3);
        manHinhLG.setGiaTien(1700);
        manHinhLG.setHangSanXuat("LG");
        manHinhLG.setNgaySanXuat("20.1.2024");
        System.out.println(manHinhLG.getCanNang());
        System.out.println(manHinhLG.getGiaTien());
        System.out.println(manHinhLG.getHangSanXuat());
        System.out.println(manHinhLG.getNgaySanXuat());
        System.out.println(manHinhLG.getInch());
    }

}
