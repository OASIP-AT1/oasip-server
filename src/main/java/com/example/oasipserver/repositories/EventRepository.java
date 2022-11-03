package com.example.oasipserver.repositories;

import com.example.oasipserver.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event , Integer> {
    List<Event> findAllByEventStartTimeGreaterThanEqual(ZonedDateTime now);
    List<Event> findAllByEventStartTimeIsBefore(ZonedDateTime now);

    @Query(value = "select * from event e where e.eventStartTime = ?1" ,nativeQuery = true)
    List<Event> findEventByDate(String date);
    @Query(value = "select * from event e where e.bookingEmail = ?1 order by e.eventStartTime desc" ,nativeQuery = true)
    List<Event> findAllByEmail(String email);
    @Query(value = "select * from event e join eventcategory ec on e.eventCategory = ec.eventCategoryId " +
            "join categoryowner c on ec.eventCategoryId = c.eventCategoryId " +
            "join user u on u.user_id = c.user_user_id where c.user_user_id = ?1 " +
            "order by e.eventStartTime desc" ,nativeQuery = true)
    List<Event> findAllByEventCategory(String Id);
}
