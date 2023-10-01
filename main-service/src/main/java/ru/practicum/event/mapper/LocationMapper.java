package ru.practicum.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.model.Location;

@Component
public class LocationMapper {
    public static Location toLocation(LocationDto locationDto) {
        Location location = new Location();
        location.setLat(locationDto.getLat());
        location.setLon(locationDto.getLon());
        return location;
    }

    public static LocationDto toLocationDto(Location location) {
        LocationDto locationDto = new LocationDto();
        locationDto.setLon(location.getLon());
        locationDto.setLat(location.getLat());
        return locationDto;
    }
}
