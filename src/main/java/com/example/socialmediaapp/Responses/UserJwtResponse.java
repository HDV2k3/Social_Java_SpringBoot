package com.example.socialmediaapp.Responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserJwtResponse {
    private int id;
    private String lastName;
    private  String name;
    private String email;
    private String roles;
    String token;
    boolean authenticated;
}
