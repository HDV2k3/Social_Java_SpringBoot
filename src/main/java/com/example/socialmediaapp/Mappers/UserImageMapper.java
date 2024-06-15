package com.example.socialmediaapp.Mappers;


import com.example.socialmediaapp.Models.UserImage;
import com.example.socialmediaapp.Responses.UserImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserImageMapper {

    @Mapping(source = "user.id",target = "userId")
    UserImageResponse userImageToResponse(UserImage userImage);

}
