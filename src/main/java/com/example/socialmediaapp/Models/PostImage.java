package com.example.socialmediaapp.Models;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "post_image")
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;
    @Column(name = "url_image_post",columnDefinition = "VARBINARY",length = 1000)
    private String urlImagePost;
    @Lob
    @Column(name = "data",columnDefinition = "VARBINARY",length = 1000)
    private byte[] data;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "post_id")
    Post post;

}
