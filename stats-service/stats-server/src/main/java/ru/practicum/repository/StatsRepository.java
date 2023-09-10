package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHit AS e " +
            "WHERE e.timestamp BETWEEN ?1 AND ?2 " +
            "AND (e.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<ViewStatsDto> findStatsWithUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHit AS e " +
            "WHERE e.timestamp BETWEEN ?1 AND ?2 " +
            "AND (e.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<ViewStatsDto> findAllStats(LocalDateTime start, LocalDateTime end, List<String> uris);


}
