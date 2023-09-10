package ru.practicum.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    List<ViewStatsDto> getStatsWithUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<ViewStatsDto> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    void save(EndpointHitDto hitDto);
}
