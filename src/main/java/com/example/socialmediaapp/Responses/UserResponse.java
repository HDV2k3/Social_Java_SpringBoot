package com.example.socialmediaapp.Responses;
import com.example.socialmediaapp.Models.User;
import lombok.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Data
@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
    private int id;
    private String name;
    private String lastName;
    private String email;
    private List<UserFollowerResponse> followers;
    private List<UserFollowingResponse> following;
    Set<RoleResponse> roles;

    public UserResponse(int id, String name, String lastName, String email, List<UserFollowerResponse> followers, List<UserFollowingResponse> following, Set<RoleResponse> roles) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.followers = followers;
        this.following = following;
        this.roles = roles;
    }

    public UserResponse(User user) {

    }
}
