package com.example.oasipserver.entities;

import lombok.Builder;
import lombok.Setter;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Table(name = "user", indexes = {
        @Index(name = "email_UNIQUE", columnList = "email", unique = true),
        @Index(name = "name_UNIQUE", columnList = "name", unique = true)
})
@Setter
@Getter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    @Size(max = 100)
    @NotEmpty
    private String name;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    @Email
    @Size(max = 50)
    @NotEmpty
    private String email;

    @Lob
    @Column(name = "role", nullable = false)
    private String role;
    @CreationTimestamp
    @Column(name = "createdOn", nullable = false , insertable = false)
    private Timestamp createdOn;
    @UpdateTimestamp
    @Column(name = "updatedOn", nullable = false , insertable = false )
    private Timestamp updatedOn;

    @Column(name = "password", nullable = false, length = 90)
    private String password;

    public String getRole() {
        return role;
    }
}