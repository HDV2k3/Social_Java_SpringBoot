package com.example.socialmediaapp.Service;


import com.example.socialmediaapp.Mappers.UserImageMapper;
import com.example.socialmediaapp.Models.UserImage;
import com.example.socialmediaapp.Repository.UserImageRepository;
import com.example.socialmediaapp.Responses.UserImageResponse;
import com.example.socialmediaapp.Utils.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Optional;

@Service
public class UserImageService {

    private final UserImageRepository userImageRepository;
    private final UserService userService;
    private final UserImageMapper userImageMapper;
    @Autowired
    public UserImageService(UserImageRepository userImageRepository, UserService userService, UserImageMapper userImageMapper) {
        this.userImageRepository = userImageRepository;
        this.userService = userService;
        this.userImageMapper = userImageMapper;
    }

    public UserImageResponse upload(MultipartFile file, int userId) throws IOException {
        UserImage userImage = new UserImage();
        userImage.setData(ImageUtil.compressImage(file.getBytes()));
        userImage.setName(file.getOriginalFilename());
        userImage.setType(file.getContentType());
        userImage.setUser(userService.getById(userId));
        userImageRepository.save(userImage);
        return userImageMapper.userImageToResponse(userImage);
    }

    public byte[] download(int id){
        Optional<UserImage> userImage = userImageRepository.findByUser_Id(id);
        return ImageUtil.decompressImage(userImage.get().getData());
    }
}
