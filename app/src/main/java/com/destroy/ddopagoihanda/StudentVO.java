package com.destroy.ddopagoihanda;

public class StudentVO {
    int id;
    String name;
    String email;
    String phone;
    String photo;
    String memo;
    int score;

    public StudentVO(int id, String name, String email, String phone,  String photo, String memo, int score) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.memo = memo;
        this.photo = photo;
        this.score = score;
    }
}

