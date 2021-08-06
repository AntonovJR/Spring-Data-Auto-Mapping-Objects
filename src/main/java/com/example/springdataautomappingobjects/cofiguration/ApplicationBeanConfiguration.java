package com.example.springdataautomappingobjects.cofiguration;

import com.example.springdataautomappingobjects.model.dto.GameAddDto;
import com.example.springdataautomappingobjects.model.entity.Game;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class ApplicationBeanConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(GameAddDto.class, Game.class)
                .addMappings(mapper ->
                        mapper.map(GameAddDto::getThumbnailUrl, Game::setImageThumbnail));
        Converter<String, LocalDate> localDateConverter = mappingContext -> mappingContext.getSource() == null
                ? LocalDate.now()
                :LocalDate.parse(mappingContext.getSource(),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        modelMapper.addConverter(localDateConverter);
        return modelMapper;
    }
}
