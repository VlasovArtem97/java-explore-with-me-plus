package ru.practicum.ewm.request.mapper;

import ru.practicum.ewm.request.dto.RequestDTO;
import ru.practicum.ewm.request.model.Request;

public class RequestMapper {

    private RequestMapper() {

    }

    public static RequestDTO toRequestDTO(Request request) {
        return RequestDTO.builder()
                .id(request.getId())
                .created(request.getCreated())
                .eventId(request.getEvent().getId())
                .requesterId(request.getRequester().getId())
                .requestStatus(request.getRequestStatus())
                .build();
    }
}