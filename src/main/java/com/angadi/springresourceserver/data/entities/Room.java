package com.angadi.springresourceserver.data.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "room_id", nullable = false)
    private Long id;

    @Size(max = 16)
    @NotNull
    @Column(name = "name", nullable = false, length = 16)
    private String name;

    @Size(max = 2)
    @NotNull
    @Column(name = "room_number", nullable = false, length = 2)
    private String roomNumber;

    @Size(max = 2)
    @NotNull
    @Column(name = "bed_info", nullable = false, length = 2)
    private String bedInfo;

}