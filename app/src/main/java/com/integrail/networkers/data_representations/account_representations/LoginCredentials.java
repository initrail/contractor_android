package com.integrail.networkers.data_representations.account_representations;

import org.json.JSONObject;

public class LoginCredentials {
    private String email;
    private String password;
    private String uniqueId;
    public LoginCredentials(String email, String password){
        this.email = email;
        this.password = password;
    }
    public String getPassword(){
        return password;
    }
    public String getEmail(){
        return email;
    }
    public String getUniqueId(){
        return uniqueId;
    }
    public String toJSON(){
        JSONObject signIn = new JSONObject();
        try{
            signIn.put("email", email);
            signIn.put("password", password);
        } catch (Exception e){
            e.printStackTrace();
        }
        return String.valueOf(signIn);
    }
}

