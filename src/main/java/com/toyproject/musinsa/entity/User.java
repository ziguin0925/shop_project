package com.toyproject.musinsa.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


//member로 하려 했지만 CustomMemberDetailService에서 parameter를 username 으로 받음.

@Entity
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //IDENTITY를 해야 안겹침.
    @Column(name="user_id")
    private long id;

    private String username;

    private String name;

    private String password;

    private String email;

    private String sex;

    private String phoneNumber;

    private String role;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date create;
}
