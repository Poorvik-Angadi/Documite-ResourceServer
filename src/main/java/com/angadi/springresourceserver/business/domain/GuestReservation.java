package com.angadi.springresourceserver.business.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class GuestReservation {
    private String first_name;
    private String last_name;
    private String guest_email;
    private String guest_phone_number;

}
