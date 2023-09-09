package ru.practicum.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
    //List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique);
    //EndpointHitDto save(EndpointHitDto hitDto);

    void save(EndpointHitDto hitDto);


}
