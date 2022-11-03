package com.example.oasipserver.entities;

import lombok.Setter;
import lombok.Getter;
import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Table(name = "event", indexes = {
        @Index(name = "fk_event_eventCategory_idx", columnList = "eventCategory")
})
@Getter
@Setter
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookingId", nullable = false)
    private Integer id;

    @Column(name = "bookingName", nullable = false, length = 100)
    private String bookingName;

    @Column(name = "bookingEmail", nullable = false, length = 50)
    private String bookingEmail;

    @ManyToOne(optional = false)
    @JoinColumn(name = "eventCategory", nullable = false)
    private Eventcategory eventCategory;

    @Column(name = "eventStartTime", nullable = false)
    @Future(message = "The date and time should be in the future for the appointment.")
    private ZonedDateTime eventStartTime;

    @Column(name = "eventDuration", nullable = false)
    private Integer eventDuration;

    @Column(name = "eventNotes", length = 500)
    @Size(min=0,max=500)
    private String eventNotes;

}