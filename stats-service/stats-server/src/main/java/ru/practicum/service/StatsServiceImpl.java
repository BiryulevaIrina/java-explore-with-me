package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            return statsRepository.findStatsWithUniqueIp(start, end, uris);
        } else {
            return statsRepository.findAllStats(start, end, uris);
        }
    }

    @Override
    public void save(EndpointHitDto hitDto) {
        statsRepository.save(EndpointHitMapper.toEndpointHit(hitDto));
    }
}
