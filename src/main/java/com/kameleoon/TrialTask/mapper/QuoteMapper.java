package com.kameleoon.TrialTask.mapper;

import com.kameleoon.TrialTask.dto.QuoteContentDto;
import com.kameleoon.TrialTask.model.Quote;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface QuoteMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "text", source = "text")
    void updateQuoteFromDto(QuoteContentDto dto, @MappingTarget Quote entity);
}
