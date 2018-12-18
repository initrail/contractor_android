package com.integrail.networkers.data_representations.account_representations;
public class Account{
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phone;
    protected String zip;
    protected String password;
    protected String passwordCheck;
    public Account(String fname, String lname, String email, String phone, String zip, String password, String passwordCheck){
        firstName = fname;
        lastName = lname;
        this.email = email;
        this.phone = phone;
        this.zip = zip;
        this.password = password;
        this.passwordCheck = passwordCheck;
    }
}