//package com.example.socialmediaapp.Models;
//
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotNull;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.Set;
//
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Entity
//@Table(name = "users")
//public class User {
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Id
//    @Column(name = "id")
//    private int id;
//
//    @NotNull
//    @Column(name = "name")
//    private String name;
//
//    @NotNull
//    @Column(name = "email")
//    @Email
//    private String email;
//
//    @NotNull
//    @Column(name = "last_name")
//    private String lastName;
//
//    @NotNull
//    @Column(name = "password")
//    private String password;
//
//    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
//    Set<Follow> following;
//    @OneToMany(mappedBy = "following",cascade = CascadeType.ALL)
//    Set<Follow> followers;
//    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
//    Set<Post> posts;
//    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
//    Set<Like> likes;
//    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
//    Set<UserImage> images;
//    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
//    Set<Comment>comments;
//    @OneToMany(mappedBy = "sender",cascade = CascadeType.ALL)
//    Set<Chat> chats;
//    @OneToMany(mappedBy = "group",cascade = CascadeType.ALL)
//    Set<ChatGroup> chatGroups;
//
//    @OneToMany(mappedBy = "sender",cascade = CascadeType.ALL)
//    Set<ChatGroupMember> chatGroupMembers;
//    @OneToMany(mappedBy = "sender",cascade = CascadeType.ALL)
//    Set<ChatMessage> chatMessages;
//    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
//     Set<ChatGroupMessage> chatGroupMessages;
//}
package com.example.socialmediaapp.Models;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @NotNull
    @Column(name = "name")
    private String name;
    @NotNull
    @Column(name = "email")
    @Email
    private String email;
    @NotNull
    @Column(name = "last_name")
    private String lastName;
    @NotNull
    @Column(name = "password")
    private String password;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Follow> following;

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private Set<Follow> followers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Like> likes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserImage> images;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @OneToMany(fetch = FetchType.EAGER)
    Set<Role> roles;

}
