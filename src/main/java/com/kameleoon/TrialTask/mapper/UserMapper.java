package com.kameleoon.TrialTask.mapper;

import com.kameleoon.TrialTask.dto.UserAuthDto;
import com.kameleoon.TrialTask.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "email", source = "email")
    void updateUserFromDto(UserAuthDto dto, @MappingTarget User entity);
}
