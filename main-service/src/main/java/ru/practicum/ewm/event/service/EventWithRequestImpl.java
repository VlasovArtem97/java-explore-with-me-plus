package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.status.StatusRequest;
import ru.practicum.ewm.request.dto.RequestDTO;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.service.RequestService;
import ru.practicum.ewm.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class EventWithRequestImpl implements EventWithRequest {

    private final EventService eventService;
    private final UserService userService;
    private final RequestService requestService;

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestUser(Long userId, Long eventId,
                                                            EventRequestStatusUpdateRequest request) {
        userService.findUserById(userId);
        Event event = eventService.findEventWithOutDto(userId, eventId);
        List<Request> requestList = requestService.findRequestsByIds(request.getRequestIds());
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        for (Request req : requestList) {
            if (req.getRequestStatus().equals(RequestStatus.CANCELED)) {
                throw new ConflictException("Заявку c Id: " + req.getId() +
                        " можно одобрить, если у нее статус: " + RequestStatus.PENDING);
            }
        }

        if (request.getStatus().equals(StatusRequest.REJECTED)) {
            for (Request req : requestList) {
                req.setRequestStatus(RequestStatus.CANCELED);
                rejectedRequests.add(req);
            }
        }

        if (Objects.equals(event.getConfirmedRequests(), event.getParticipantLimit())) {
            throw new ConflictException("У события достигнут лимит запросов на участие.");
        } else if (!event.getRequestModeration() || event.getParticipantLimit().equals(0L)) {
            for (Request req : requestList) {
                req.setRequestStatus(RequestStatus.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                confirmedRequests.add(req);
            }
        } else {
            for (Request req : requestList) {
                if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
                    req.setRequestStatus(RequestStatus.CANCELED);
                    rejectedRequests.add(req);
                } else {
                    req.setRequestStatus(RequestStatus.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    rejectedRequests.add(req);
                }
            }
        }

        List<Request> allRequest = new ArrayList<>();
        allRequest.addAll(confirmedRequests);
        allRequest.addAll(rejectedRequests);
        requestService.saveRequestList(allRequest);

        eventService.saveEventWithRequest(event);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests.stream().map(RequestMapper::toRequestDTO).toList())
                .rejectedRequests(rejectedRequests.stream().map(RequestMapper::toRequestDTO).toList())
                .build();
    }

    @Override
    public List<RequestDTO> getEventRequest(Long userId, Long eventId) {
        userService.findUserById(userId);
        eventService.findEventWithOutDto(userId, eventId);
        return requestService.getRequestByEventId(eventId);
    }
}
