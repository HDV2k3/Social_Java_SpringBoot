package com.example.socialmediaapp.Service;

import com.example.socialmediaapp.Mappers.PostImageMapper;
import com.example.socialmediaapp.Models.PostImage;
import com.example.socialmediaapp.Repository.PostImageRepository;
import com.example.socialmediaapp.Responses.PostImageResponse;
import com.example.socialmediaapp.Utils.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Optional;

@Service
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final PostService postService;
    private final PostImageMapper postImageMapper;
    @Autowired
    public PostImageService(PostImageRepository postImageRepository, PostService postService, PostImageMapper postImageMapper) {
        this.postImageRepository = postImageRepository;
        this.postService = postService;
        this.postImageMapper = postImageMapper;
    }

//    public PostImageResponse upload(MultipartFile file, int postId) throws IOException {
//        PostImage postImage = new PostImage();
//        postImage.setName(file.getOriginalFilename());
//        postImage.setType(file.getContentType());
//        postImage.setData(ImageUtil.compressImage(file.getBytes()));
//        postImage.setPost(postService.getById(postId));
//        postImageRepository.save(postImage);
//        return postImageMapper.imageToResponse(postImage);
//    }

    public byte[] download(int id){
        Optional<PostImage> postImage = postImageRepository.findPostImageByPost_Id(id);
        if (postImage.isPresent()){
            return ImageUtil.decompressImage(postImage.get().getData());
        }
        return null;
    }
}
