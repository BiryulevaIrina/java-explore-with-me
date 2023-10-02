package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
@Slf4j
@Validated
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createNewCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Получен POST-запрос на добавление админом новой подборки");
        return compilationService.create(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@Min(1) @PathVariable Long compId) {
        log.info("Получен DELETE-запрос на удаление подборки с ID={}", compId);
        compilationService.delete(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@Min(1) @PathVariable Long compId,
                                            @Valid @RequestBody(required = false) UpdateCompilationDto compilationDto) {
        log.info("Получен PATCH-запрос на изменение подборки c id={}", compId);
        return compilationService.update(compId, compilationDto);
    }


}
