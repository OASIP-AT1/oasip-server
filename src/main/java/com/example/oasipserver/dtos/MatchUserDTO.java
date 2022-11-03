package com.example.oasipserver.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchUserDTO {
    private Integer id;
    @Email
    @Size(max = 50)
    private String email;
    @NotNull
    @Size(min = 8,max = 14)
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public String getPassword(){return password;}
    public void setPassword(String password){ this.password = password.trim(); }
}
