package com.example.socialmediaapp.Models;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "posts")
public class Post {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @NotNull
    @Column(name = "description")
    private String description;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    @Column(name = "titlePost")
    private String titlePost;
    @Column(name = "urlImagePost",columnDefinition = "VARBINARY",length = 1000)
    @ElementCollection
    private List<String> urlImagePost;
    @Column(name = "create_at")
    private LocalDateTime create_at;
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
    Set<Like> likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostImage> postImages = new HashSet<>();


    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
    @JsonManagedReference
    Set<Comment> comments;
}
