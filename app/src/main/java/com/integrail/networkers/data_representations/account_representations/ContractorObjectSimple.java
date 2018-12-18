package com.integrail.networkers.data_representations.account_representations;

public class ContractorObjectSimple {
    private String name;
    private String firstName;
    private String lastName;
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    private String email;
    public ContractorObjectSimple(String n, String e){
        setName(n);
        setEmail(e);
    }
    public String getName() {
        return firstName+" "+lastName;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
