package com.angadi.springresourceserver.data.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "guest")
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "guest_id", nullable = false)
    private Long id;

    @Column(name = "first_name", length = 64)
    private String firstName;

    @Column(name = "last_name", length = 64)
    private String lastName;

    @Column(name = "email_address", length = 64)
    private String emailAddress;

    @Column(name = "address", length = 64)
    private String address;

    @Column(name = "country", length = 32)
    private String country;

    @Column(name = "state", length = 12)
    private String state;

    @Column(name = "phone_number", length = 24)
    private String phoneNumber;



}
