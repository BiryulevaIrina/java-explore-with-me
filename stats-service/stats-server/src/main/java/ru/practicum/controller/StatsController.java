package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam
                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        log.debug("Получен GET-запрос на получение статистики за период с {} до {}", start, end);
        if (end.isBefore(start)) throw new BadRequestException("Ошибка при введении начала и окончания периода " +
                "получения статистических данных.");
        if (unique) {
            return statsService.getStatsWithUniqueIp(start, end, uris);
        } else {
            return statsService.getAllStats(start, end, uris);
        }
    }

    @PostMapping("/hit")
    public ResponseEntity<String> createEndpointHit(@RequestBody EndpointHitDto hitDto) {
        statsService.save(hitDto);
        log.debug("Получен POST-запрос на сохранение информации, что был запрос к эндпоинту: {}", hitDto.getUri());
        return new ResponseEntity<>("Сохранена информация", HttpStatus.CREATED);
    }
}
