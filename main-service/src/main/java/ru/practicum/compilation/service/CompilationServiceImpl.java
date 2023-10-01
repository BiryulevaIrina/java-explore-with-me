package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getAllCompilations(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return compilationRepository.findAll(pageable).stream()
                .map(CompilationMapper::mapToCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompilationDto> getCompilationsByPinned(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return compilationRepository.findByPinned(pageable).stream()
                .map(CompilationMapper::mapToCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Event> allEvents = new ArrayList<>();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            allEvents = eventRepository.findAllById(newCompilationDto.getEvents());
        }
        Set<Event> events = new HashSet<>(allEvents);
        Compilation compilation = CompilationMapper.mapToCompilation(newCompilationDto);
        compilation.setEvents(events);
        if (newCompilationDto.getPinned() == null) {
            compilation.setPinned(false);
        }
        return CompilationMapper.mapToCompilationDto(compilationRepository.save(compilation));
    }


    @Override
    public CompilationDto getCompilationById(Long compId) {
        return compilationRepository.findById(compId)
                .map(CompilationMapper::mapToCompilationDto)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + compId
                        + " не найдена или недоступна."));
    }

    @Override
    public CompilationDto update(Long compId, NewCompilationDto compilationDto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборки с идентификатором " + compId
                        + " нет в базе."));
        if (compilationDto.getEvents() != null) {
            Set<Event> events = getEvents(compilationDto.getEvents());
            compilation.setEvents(events);
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        if (compilation.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        compilationRepository.save(compilation);
        return CompilationMapper.mapToCompilationDto(compilation);
    }

    @Override
    public void delete(Long compId) {
        try {
            compilationRepository.deleteById(compId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Подборки с id = " + compId
                    + " не найдена или недоступна.");
        }
    }

    private Set<Event> getEvents(Set<Long> events) {
        if (events == null || events.isEmpty()) {
            return Set.of();
        } else {
            return eventRepository.findAllByIdIn(events);
        }
    }
}
