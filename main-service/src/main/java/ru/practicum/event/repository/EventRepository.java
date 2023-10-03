package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    Set<Event> findAllByIdIn(Set<Long> ids);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Event findByInitiatorIdAndId(Long userId, Long eventId);

    @Query("SELECT e FROM Event AS e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (:text IS NULL OR UPPER(e.annotation) LIKE UPPER(concat('%', :text, '%')) OR UPPER(e.description) " +
            "LIKE UPPER(CONCAT('%', :text, '%')))" +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND e.eventDate > :rangeStart " +
            "AND e.eventDate < :rangeEnd " +
            "AND (:onlyAvailable IS NULL OR e.confirmedRequests < e.participantLimit OR e.participantLimit = 0)")
    List<Event> findEventsWithFilter(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable pageable);

    @Query("SELECT e FROM Event AS e " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND e.eventDate > :rangeStart " +
            "AND e.eventDate < :rangeEnd")
    List<Event> findEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    Boolean existsByCategoryId(Long catId);

}
