package com.example.socialmediaapp.Service;


import com.example.socialmediaapp.Mappers.CommentMapper;
import com.example.socialmediaapp.Models.Comment;
import com.example.socialmediaapp.Repository.CommentRepository;
import com.example.socialmediaapp.Request.CommentAddRequest;
import com.example.socialmediaapp.Request.CommentUpdateRequest;
import com.example.socialmediaapp.Responses.CommentGetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Autowired
    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public void add(CommentAddRequest commentAddRequest){
        Comment comment = commentMapper.addRequestToComment(commentAddRequest);
        commentRepository.save(comment);
    }

    public List<CommentGetResponse> getAll(){
        List<Comment> comments = commentRepository.findAll();
        return commentMapper.commentsToResponses(comments);
    }

    public CommentGetResponse getById(int id){
        Comment comment = commentRepository.findById(id).orElse(null);
        return  commentMapper.commentToResponse(comment);
    }

    public List<CommentGetResponse> getAllByPost(int postId){
        List<Comment> comments = commentRepository.findAllByPost_Id(postId);
        return commentMapper.commentsToResponses(comments);
    }

    public List<CommentGetResponse> getAllByUser(int userId){
        List<Comment> comments = commentRepository.findAllByUser_Id(userId);
        return commentMapper.commentsToResponses(comments);
    }
    public void update(int id, CommentUpdateRequest commentUpdateRequest){
        Comment commentToUpdate = commentRepository.findById(id).orElse(null);
        if (commentToUpdate!=null){
            commentToUpdate.setDescription(commentUpdateRequest.getDescription());
        }
    }

    public void delete(int id){
        commentRepository.deleteById(id);
    }
}
