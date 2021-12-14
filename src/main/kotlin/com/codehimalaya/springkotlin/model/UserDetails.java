package com.codehimalaya.springkotlin.model;

import javax.persistence.*;

@Entity
@Table(name = "user_details")
public class UserDetails {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "phone_num", nullable = false, unique = true)
    private String phoneNum;

    @Column(name = "accNum", nullable = false, unique = true)
    private String accNum;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "user_id", nullable = false)
    private String userId;

    public UserDetails() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getAccNum() {
        return accNum;
    }

    public void setAccNum(String accNum) {
        this.accNum = accNum;
    }
}
