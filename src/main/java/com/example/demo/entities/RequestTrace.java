package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "request_trace")
@Getter @Setter
public class RequestTrace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventId;
    private String url;
    private int httpStatus;

    @Column(length = 2000)
    private String responseBody;

}
