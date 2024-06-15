package com.example.socialmediaapp.Mappers;


import com.example.socialmediaapp.Models.PostImage;
import com.example.socialmediaapp.Responses.PostImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostImageMapper {

    @Mapping(source = "post.id",target = "postId")
    PostImageResponse imageToResponse(PostImage postImage);

}
