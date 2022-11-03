package com.example.oasipserver.repositories;

import com.example.oasipserver.entities.Categoryowner;
import com.example.oasipserver.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryOwnerRepository extends JpaRepository<Categoryowner, Integer> {
}
