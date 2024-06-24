package com.example.socialmediaapp.Mappers;


import com.example.socialmediaapp.DTO.ChatDTO;
import com.example.socialmediaapp.Models.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "receiver.id", target = "receiverId")
    ChatDTO chatToDTO(Chat chat);

    @Mapping(source = "senderId", target = "sender.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    Chat dtoToChat(ChatDTO chatDTO);
}