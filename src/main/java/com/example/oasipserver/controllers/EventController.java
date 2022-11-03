package com.example.oasipserver.controllers;


import com.example.oasipserver.dtos.EventDTO;
import com.example.oasipserver.dtos.UserDTO;
import com.example.oasipserver.entities.Event;
import com.example.oasipserver.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.validation.Valid;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://intproj21.sit.kmutt.ac.th/")
//@CrossOrigin(origins = "*")
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService service;

    @GetMapping("")
    public List<EventDTO> getAllEvent(){
        String token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
        return service.getAllEvents(token);
    }

    @GetMapping("/{bookingId}")
    public EventDTO getEventById(@PathVariable Integer bookingId) {
        String token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
        return service.getEventDetail(bookingId, token);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("!isAuthenticated() or hasAnyRole('admin', 'student')")
    public Event create(@Valid @RequestBody EventDTO newEvent){
        String email = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("account");
        return service.save(newEvent, email);
    }

    @DeleteMapping("/{bookingId}")
    public void delete(@PathVariable Integer bookingId) {
        String token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
        service.deleteEvent(bookingId, token);
    }

    @PutMapping("/{bookingId}")
    public Event update(@Valid @RequestBody Event updateEvent, @PathVariable Integer bookingId) {
        String token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
        return service.updateEvent(updateEvent , bookingId, token);
    }
    @GetMapping("/upcoming")
    public List<EventDTO> upcomingEvent(){
        return service.getUpcomingEvents();
    }

    @GetMapping("/past")
    public List<EventDTO> pastEvents(){
        return service.getPastEvents();
    }

    @GetMapping("/sort-date/{date}")
    public List<EventDTO> getEventByDate(@PathVariable String date) {
        return service.getEventByDate(date);
    }

}
