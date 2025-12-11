package com.angadi.springresourceserver.data.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users", schema = "dmite")
public class Users {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
    @Id
    private Long userId;

    @Column(name ="user_name", nullable = false)
    private String userName;

}

