package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
@Slf4j
@Validated
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @PositiveOrZero
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @Positive
                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос на получение списка подборок при pinned = {}, from = {}, size = {}",
                pinned, from, size);
        if (pinned == null) {
            pinned = false;
        }
        return pinned
                ? compilationService.getCompilationsByPinned(from, size)
                : compilationService.getAllCompilations(from, size);
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@Min(1) @PathVariable Long compId) {
        log.info("Получен GET-запрос на получение подборки с ID={}", compId);
        return compilationService.getCompilationById(compId);
    }
}
