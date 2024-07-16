package com.example.socialmediaapp.Mappers;


import com.example.socialmediaapp.Models.Permission;
import com.example.socialmediaapp.Request.PermissionRequest;
import com.example.socialmediaapp.Responses.PermissionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
