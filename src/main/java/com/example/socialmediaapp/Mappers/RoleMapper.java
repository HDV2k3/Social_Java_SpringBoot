package com.example.socialmediaapp.Mappers;


import com.example.socialmediaapp.Models.Role;
import com.example.socialmediaapp.Request.RoleRequest;
import com.example.socialmediaapp.Responses.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
