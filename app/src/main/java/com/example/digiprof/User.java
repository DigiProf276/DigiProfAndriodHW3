// User Header
// Group 13: DigiProf
// Main Coder: Hieu
// Modifiers: Andy
// Modifications:
// - Added Comments and Code Style
// - Code Review and Testing
// - Implemented User


package com.example.digiprof;

/**
 * User Class stores a users information during the UserRegistration process.
 */
public class User {
    public String UserID, email, password;

    public User() {
    }

    public User(String newEmail, String newPassword) {
        email = newEmail;
        password = newPassword;
        UserID = "";
    }
}
