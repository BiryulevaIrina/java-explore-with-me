package ru.practicum.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    List<ViewStatsDto> getAllWithUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<ViewStatsDto> getAll(LocalDateTime start, LocalDateTime end, List<String> uris);

    void save(EndpointHitDto hitDto);
}
