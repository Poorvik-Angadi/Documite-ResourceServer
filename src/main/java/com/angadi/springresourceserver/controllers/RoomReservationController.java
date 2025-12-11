package com.angadi.springresourceserver.controllers;

import com.angadi.springresourceserver.business.service.RoomReservationService;
import com.angadi.springresourceserver.business.domain.RoomReservation;
import com.angadi.springresourceserver.requests.RoomReservationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class RoomReservationController {

    public final RoomReservationService roomReservationService;

    @Autowired
    public RoomReservationController(RoomReservationService reservationService){
        this.roomReservationService = reservationService;
    }

    @Operation(summary = "Get Reservations by Date", description = "Returns all available rooms and reservation details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.OK)
    public List<RoomReservation> getReservations(@RequestBody(required = true) RoomReservationRequest request, @Parameter(hidden=true) @AuthenticationPrincipal Jwt jwt){
        Date date;
        if(request.getDate()!=null){
            date = request.getDate();
        }
        else {
            date = new Date();
        }
        return this.roomReservationService.getRoomReservationService(date);



    }
}
