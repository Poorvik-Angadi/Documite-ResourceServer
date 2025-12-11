package com.angadi.springresourceserver.data.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Array;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "gdrive_documents", schema ="dmite")
@Entity
public class GDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "doc_id", nullable = false)
    private Long doc_id;

    @NotNull
    @Column(name ="name", nullable = false)
    private String name;

    @NotNull
    @Column(name ="location", nullable = false)
    private String location;

    @NotNull
    @Column(name ="user_id", nullable = false)
    private int userid;

    @NotNull
    @Column(name ="type", nullable = false)
    private String type;

    @Column(name ="year")
    private String year;


    @Column(name ="validity", columnDefinition = "DATE[]")
    private LocalDate[] validity;

}
