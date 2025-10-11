package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.RequestDTO;

import java.util.List;

public interface RequestService {

    RequestDTO addRequestCurrentUser(Long userId, Long eventId);

    List<RequestDTO> getRequestsCurrentUser(Long userId);

    RequestDTO cancelRequestCurrentUser(Long userId, Long requestId);
}