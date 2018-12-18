package com.integrail.networkers.data_representations.account_representations;

/**
 * Created by Integrail on 11/25/2016.
 */

public class ContractorItemSimple {
    private String name;

    public String getEmail() {
        return email;
    }

    private String email;

    public String getPhone() {
        return phone;
    }

    private String phone;
    public ContractorItemSimple(String name){
        this.name = name;
    }
    public ContractorItemSimple(String name, String email, String phone){
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    public String getName(){
        return name;
    }
}
