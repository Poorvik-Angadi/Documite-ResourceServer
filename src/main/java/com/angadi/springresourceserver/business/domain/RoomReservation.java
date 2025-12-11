package com.angadi.springresourceserver.business.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RoomReservation {

    private long room_ID;
    private long guest_ID;
    private String room_name;
    private String room_number;
    private String first_name;
    private String last_name;
    private Date date;
}
