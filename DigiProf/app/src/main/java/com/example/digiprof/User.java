package com.example.digiprof;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String UserID, email, password;

    public User(){}

    public User(String newEmail, String newPassword){
        email = newEmail;
        password = newPassword;
        UserID = "";
    }
}
