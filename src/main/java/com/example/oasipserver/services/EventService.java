package com.example.oasipserver.services;


import com.example.oasipserver.dtos.EventDTO;
import com.example.oasipserver.entities.Event;
import com.example.oasipserver.jwt.JwtTokenUtil;
import com.example.oasipserver.repositories.CategoryOwnerRepository;
import com.example.oasipserver.repositories.EventRepository;
import com.example.oasipserver.repositories.UserRepository;
import com.example.oasipserver.utils.ListMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;
    private final ModelMapper modelMapper;
    private final ListMapper listMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    public List<EventDTO> getAllEvents (String token){
        token = token.replace("Bearer " , "");
        String emailCheck = jwtTokenUtil.getUsernameFromToken(token);
        String role = userRepository.findRole(emailCheck);
        if(role.equals("student")) {
            List<Event> eventList = repository.findAllByEmail(emailCheck);
            return listMapper.mapList(eventList, EventDTO.class, modelMapper);
        }else if(role.equals("lecturer")){
            String usrId = userRepository.findId(emailCheck);
            List<Event> eventList = repository.findAllByEventCategory(usrId);
            return listMapper.mapList(eventList, EventDTO.class, modelMapper);
        }
        List<Event> eventList = repository.findAll(Sort.by("eventStartTime").descending());
        return listMapper.mapList(eventList, EventDTO.class, modelMapper);
    }

    public EventDTO getEventDetail(Integer bookingId, String token){
        token = token.replace("Bearer " , "");
        String emailCheck = jwtTokenUtil.getUsernameFromToken(token);
        Event event = repository.findById(bookingId).orElseThrow(()->new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Event id "+ bookingId + " does not exist!!"));
        if(!emailCheck.trim().equals(event.getBookingEmail().trim())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You dont have permission");
        }
        return modelMapper.map(event , EventDTO.class);
    }

    public Event save(EventDTO newEvent, String email){
//        token =  token.replace("Bearer ", "");
//        String emailCheck = jwtTokenUtil.getUsernameFromToken(token);
//        if(!emailCheck.trim().equals(newEvent.getBookingEmail().trim())){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your Email is Invalid");
//        }
//        String role = userRepository.findRole(emailCheck);
//        if(role == "lecturer"){
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You dont have permission");
//        }
        if(!email.equals("guest")){
        if(!email.trim().equals(newEvent.getBookingEmail().trim())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your Email is Invalid");
        }
        String role = userRepository.findRole(email);
        if(role.equals("lecturer")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You dont have permission");
        }
        }
        List<Event> e1 = repository.findAll();
        Event e2 = modelMapper.map(newEvent, Event.class);
        ZonedDateTime oldTimeStart;
        ZonedDateTime oldTimeEnd;
        ZonedDateTime newTimeStart = newEvent.getEventStartTime();
        ZonedDateTime newTimeEnd = newEvent.getEventStartTime().plusMinutes(newEvent.getEventDuration());
        for(int i = 0; i < e1.size() ; i++) {
            if(e1.get(i).getEventCategory().getId() == e2.getEventCategory().getId()) {
                oldTimeStart = e1.get(i).getEventStartTime();
                oldTimeEnd = e1.get(i).getEventStartTime().plusMinutes(e1.get(i).getEventDuration());
                if (overlapTime(oldTimeStart, oldTimeEnd, newTimeStart, newTimeEnd)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This start time is overlap other event!!!");
                }
            }
        }

        return repository.saveAndFlush(e2);
    }

    public boolean overlapTime(ZonedDateTime date_Start1 ,ZonedDateTime date_End1,ZonedDateTime date_Start2, ZonedDateTime date_End2) {
        return date_Start1.isBefore(date_End2) && date_Start2.isBefore(date_End1);
    }

    public Event updateEvent(Event updateEvent, Integer bookingId, String token) {
        Event event = repository.findById(bookingId).map(existEvent -> mapEvent(existEvent, updateEvent)).orElseGet(()->
        {
            updateEvent.setId(bookingId);
            return updateEvent;
        });
        token = token.replace("Bearer " , "");
        String emailCheck = jwtTokenUtil.getUsernameFromToken(token);
        if(!emailCheck.trim().equals(event.getBookingEmail().trim())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You dont have permission");
        }
        String role = userRepository.findRole(emailCheck);
        if(role.equals("lecturer")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You dont have permission");
        }
        List<Event> e1 = repository.findAll();
        ZonedDateTime oldTimeStart;
        ZonedDateTime oldTimeEnd;
        ZonedDateTime newTimeStart = event.getEventStartTime();
        ZonedDateTime newTimeEnd = event.getEventStartTime().plusMinutes(event.getEventDuration());
        for(int i = 0; i < e1.size() ; i++) {
            if(e1.get(i).getEventCategory().getId() == event.getEventCategory().getId() && e1.get(i).getId() != event.getId()) {
                oldTimeStart = e1.get(i).getEventStartTime();
                oldTimeEnd = e1.get(i).getEventStartTime().plusMinutes(e1.get(i).getEventDuration());
                if (overlapTime(oldTimeStart, oldTimeEnd, newTimeStart, newTimeEnd)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,  "This start time is overlap other event!!!");
                }
            }
        }

        return repository.saveAndFlush(event);
    }

    private Event mapEvent(Event existEvent, Event updateEvent) {
        if(updateEvent.getEventStartTime() != null) {
            existEvent.setEventStartTime(updateEvent.getEventStartTime());
        }
        if(updateEvent.getEventNotes() != null) {
            existEvent.setEventNotes(updateEvent.getEventNotes());
        }else existEvent.setEventNotes(null);
        return existEvent;
    }

    public void deleteEvent(Integer bookingId,String token){
        Event event = repository.findById(bookingId).orElseThrow(() -> new ResponseStatusException
                (HttpStatus.NOT_FOUND , "This id " + bookingId+ " does not exist!!"));
        token = token.replace("Bearer " , "");
        String emailCheck = jwtTokenUtil.getUsernameFromToken(token);
        if(!emailCheck.trim().equals(event.getBookingEmail().trim())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You dont have permission");
        }
        String role = userRepository.findRole(emailCheck);
        if(role.equals("lecturer")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You dont have permission");
        }
        repository.deleteById(bookingId);
    }

    public List<EventDTO> getUpcomingEvents (){
        ZonedDateTime now = ZonedDateTime.now();
        List<Event> eventList = repository.findAllByEventStartTimeGreaterThanEqual(now);
        eventList.sort((o1,o2) -> o1.getEventStartTime().compareTo(o2.getEventStartTime()));
        return listMapper.mapList(eventList, EventDTO.class, modelMapper);
    }

    public List<EventDTO> getPastEvents(){
        ZonedDateTime now = ZonedDateTime.now();
        List<Event> eventList = repository.findAllByEventStartTimeIsBefore(now);
        eventList.sort((o1,o2) -> o2.getEventStartTime().compareTo(o1.getEventStartTime()));
        return  listMapper.mapList(eventList , EventDTO.class , modelMapper);
    }
    public List<EventDTO> getEventByDate(String date){
     List<Event> events = repository.findEventByDate(date);
     events.sort(((o1, o2) -> o1.getEventStartTime().compareTo(o2.getEventStartTime())));
     return listMapper.mapList(events , EventDTO.class , modelMapper);
    }
}
