package com.angadi.springresourceserver.business.service;

import com.angadi.springresourceserver.business.domain.RoomReservation;
import com.angadi.springresourceserver.data.entities.Reservation;
import com.angadi.springresourceserver.data.entities.Room;
import com.angadi.springresourceserver.data.repository.GuestRepositry;
import com.angadi.springresourceserver.data.repository.ReservationRepository;
import com.angadi.springresourceserver.data.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;

@Service
public class RoomReservationService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepositry;
   // private final GuestRepositry guestRepositry;

    @Autowired
    public RoomReservationService(RoomRepository roomRepository, ReservationRepository reservationRepositry, GuestRepositry guestRepositry){
        this.roomRepository = roomRepository;
        this.reservationRepositry = reservationRepositry;
        //this.guestRepositry = guestRepositry;
    }

    public List<RoomReservation> getRoomReservationService(Date date){
        Iterable<Room> rooms = this.roomRepository.findAll();
        Map<Long, RoomReservation> roomReservationMap = new HashMap<>();
        rooms.forEach(room -> {
            RoomReservation roomReservation = new RoomReservation();
            roomReservation.setRoom_ID(room.getId());
            roomReservation.setRoom_name(room.getName());
            roomReservation.setRoom_number(room.getRoomNumber());
            roomReservationMap.put(room.getId(),roomReservation);
        });

        Iterable<Reservation> reservations = this.reservationRepositry.findReservationByResDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        reservations.forEach(reservation -> {
            RoomReservation roomReservation = roomReservationMap.get(reservation.getId());
            //Optional<Guest> guest = this.guestRepositry.findById(reservation.getId());
            roomReservation.setDate(date);

            roomReservation.setGuest_ID(reservation.getGuest().getId());
            roomReservation.setFirst_name(reservation.getGuest().getFirstName());
            roomReservation.setLast_name(reservation.getGuest().getLastName());

        });

        List<RoomReservation> roomReservationList = new ArrayList<>(roomReservationMap.values());
        roomReservationList.sort((o1, o2) -> {
            if(o1.getRoom_name()==o2.getRoom_name()){
                return o1.getRoom_name().compareTo(o2.getRoom_name());

            }
            return o1.getRoom_name().compareTo(o2.getRoom_name());
        });
        return roomReservationList;

    }
}