package com.example.oasipserver.entities;

import com.example.oasipserver.entities.Eventcategory;
import com.example.oasipserver.entities.User;

import javax.persistence.*;

@Entity
@Table(name = "categoryowner")
public class Categoryowner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventcategoryownerId", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eventCategoryId", nullable = false)
    private Eventcategory eventCategoryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_user_id", nullable = false)
    private User userId;

    public User getUserUser() {
        return userId;
    }

    public void setUserUser(User userId) {
        this.userId = userId;
    }

    public Eventcategory getEventCategory() {
        return eventCategoryId;
    }

    public void setEventCategory(Eventcategory eventCategoryId) {
        this.eventCategoryId = eventCategoryId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}