package com.example.socialmediaapp.Mappers;





import com.example.socialmediaapp.Models.Comment;
import com.example.socialmediaapp.Request.CommentAddRequest;
import com.example.socialmediaapp.Responses.CommentGetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "user.id",target = "userId")
    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "user.name",target = "userName")
    @Mapping(source = "user.lastName",target = "userLastName")
    CommentGetResponse commentToResponse(Comment comment);
    List<CommentGetResponse> commentsToResponses(List<Comment> comments);
    @Mapping(source = "userId",target = "user.id")
    @Mapping(source = "postId",target = "post.id")
    Comment addRequestToComment(CommentAddRequest commentAddRequest);
}
