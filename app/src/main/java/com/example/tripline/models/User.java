package com.example.tripline.models;

// a User represents a user of the app who can log in and add/view trips
public class User {

    private String firstName;
    private String lastName;
    private String emailAddress;
    private String password;

    public User(String fName, String lName, String emailAddress, String password) {
        this.firstName = fName;
        this.lastName = lName;
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
