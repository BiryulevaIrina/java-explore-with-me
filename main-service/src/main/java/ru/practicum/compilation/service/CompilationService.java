package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAllCompilations(int from, int size);

    List<CompilationDto> getCompilationsByPinned(int from, int size);

    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto getCompilationById(Long id);

    CompilationDto update(Long id, NewCompilationDto compilationDto);

    void delete(Long id);


}