package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAll(int from, int size);

    List<CompilationDto> getAllByPinned(int from, int size);

    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto getById(Long id);

    CompilationDto update(Long compId, UpdateCompilationDto compilationDto);

    void delete(Long id);


}
