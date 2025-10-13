package ru.practicum.ewm.compilations.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilations.dto.CompilationDTO;
import ru.practicum.ewm.compilations.dto.RequestToCreateNewCompilationDTO;
import ru.practicum.ewm.compilations.dto.UpdateCompilationDTO;
import ru.practicum.ewm.compilations.mapper.CompilationMapper;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.compilations.repository.CompilationRepository;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, EventRepository eventRepository, EventMapper eventMapper) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public CompilationDTO addCompilation(RequestToCreateNewCompilationDTO newCompilationDTO) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDTO);

        List<Long> eventsId = newCompilationDTO.getEventsId();
        if (eventsId != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(eventsId));
        }

        Compilation savedCompilation = compilationRepository.save(compilation);

        return CompilationMapper.toCompilationDTO(savedCompilation, eventMapper);
    }

    @Override
    public CompilationDTO updateCompilation(Long compId, UpdateCompilationDTO updateCompilationDTO) {
        Compilation foundCompilation = findCompilationById(compId);

        List<Long> eventsId = updateCompilationDTO.getEventsId();
        Boolean pinned = updateCompilationDTO.getPinned();
        String title = updateCompilationDTO.getTitle();

        if (eventsId != null) {
            List<Event> events = updateCompilationDTO.getEventsId().stream().map(id -> Event.builder()
                            .id(id)
                            .build())
                    .collect(Collectors.toList());

            foundCompilation.setEvents(events);
        }

        foundCompilation.setPinned(pinned);
        foundCompilation.setTitle(title);

        Compilation savedCompilation = compilationRepository.save(foundCompilation);

        return CompilationMapper.toCompilationDTO(savedCompilation, eventMapper);
    }

    private Compilation findCompilationById(long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Подборка с указанным id - %d не найдена.", compId)));
    }

    @Override
    public List<CompilationDTO> getCompilationsList(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        if (pinned != null) {
            List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable);

            return toCompilationListDTO(compilations);
        } else {
            List<Compilation> compilations = compilationRepository.findAll(pageable).getContent();

            return toCompilationListDTO(compilations);
        }
    }

    private List<CompilationDTO> toCompilationListDTO(List<Compilation> compilations) {
        return compilations.stream()
                .map(compilation -> CompilationMapper.toCompilationDTO(compilation, eventMapper))
                .toList();
    }

    @Override
    public CompilationDTO getCompilationById(Long compId) {
        return CompilationMapper.toCompilationDTO(findCompilationById(compId), eventMapper);
    }

    @Override
    public void removeCompilation(Long compId) {
        findCompilationById(compId);
        compilationRepository.deleteById(compId);
    }
}