package ru.practicum.ewm.compilations.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.compilations.dto.CompilationDTO;
import ru.practicum.ewm.compilations.dto.RequestToCreateNewCompilationDTO;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.event.mapper.EventMapper;

import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public static Compilation toCompilation(RequestToCreateNewCompilationDTO newCompilationDTO) {
        return Compilation.builder()
                .pinned(newCompilationDTO.getPinned())
                .title(newCompilationDTO.getTitle())
                .build();
    }

    public static CompilationDTO toCompilationDTO(Compilation compilation, EventMapper eventMapper) {
        return CompilationDTO.builder()
                .id(compilation.getId())
                .events(compilation.getEvents().stream()
                        .map(eventMapper::toEventShortDto)
                        .collect(Collectors.toList()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}