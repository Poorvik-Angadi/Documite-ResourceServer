package com.angadi.springresourceserver.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RoomReservationRequest {
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Future(message = "The date must be in the Future.")
    @Size(min = 10, max = 10, message = "Date not valid")
    @Schema(name ="Date", example ="2020-01-01",nullable = false )
    private Date date;
}
