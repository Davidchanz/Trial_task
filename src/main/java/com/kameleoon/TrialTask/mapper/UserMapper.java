package com.kameleoon.TrialTask.mapper;

import com.kameleoon.TrialTask.dto.UserAuthDto;
import com.kameleoon.TrialTask.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserAuthDto dto, @MappingTarget User entity);
}
