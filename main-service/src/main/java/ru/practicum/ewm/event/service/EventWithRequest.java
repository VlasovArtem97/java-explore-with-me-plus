package ru.practicum.ewm.event.service;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.RequestDTO;

import java.util.List;

public interface EventWithRequest {

    EventRequestStatusUpdateResult updateRequestUser(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    List<RequestDTO> getEventRequest(Long userId, Long eventId);
}
