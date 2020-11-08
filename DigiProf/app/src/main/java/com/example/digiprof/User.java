package com.example.digiprof;

public class User {
    public String UserID, email, password;

    public User(){}

    public User(String newEmail, String newPassword){
        email = newEmail;
        password = newPassword;
        UserID = "";
    }
}
