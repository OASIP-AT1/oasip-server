package com.example.oasipserver.repositories;

import com.example.oasipserver.entities.Eventcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventCategoryRepository extends JpaRepository<Eventcategory, Integer>{

    @Query(value = "select * from eventcategory e " +
            "where e.eventCategoryName like %?1%",nativeQuery = true)
    List<Eventcategory> uniqueCategoryName(String name);
    @Query(value = "select * from eventcategory e " +
            "join categoryowner c on e.eventCategoryId = c.eventCategoryId " +
            "where c.user_user_id = ?1",nativeQuery = true)
    List<Eventcategory> findEventByLecturer(String Id);
}
