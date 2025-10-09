package ru.practicum.ewm.event.service;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.event.status.SortForParamPublicEvent;
import ru.practicum.ewm.event.status.StateEvent;
import ru.practicum.ewm.event.status.StateForUpdateEvent;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.UserService;
import ru.practicum.ewm.util.PageRequestUtil;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserService userService;
    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;
    private final LocationMapper locationMapper;
    private final CategoryService categoryService;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;


    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userService.findUserById(userId);
        Category category = categoryMapper.toCategory(categoryService.getCategory(newEventDto.getCategory()));
        Location location = locationRepository.save(locationMapper.toLocation(newEventDto.getLocation()));
        Event event = eventRepository.save(eventMapper.toEvent(newEventDto, user, category, location));
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> findEventByUserId(Long userId, int from, int size) {
        userService.findUserById(userId);
        Pageable pageable = PageRequestUtil.of(from, size, Sort.by("id").ascending());
        return eventRepository.findEventByUserId(userId, pageable).getContent().stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto findEventByIdAndEventId(Long userId, Long eventId) {
        userService.findUserById(userId);
        Event event = findEventWithOutDto(userId, eventId);
        return eventMapper.toEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        userService.findUserById(userId);
        Event event = findEventWithOutDto(userId, eventId);
        //проверка статуса
        if (event.getState().equals(StateEvent.PUBLISHED)) {
            throw new ConflictException("Данный Event невозможно изменить, поскольку он уже опубликован");
        } else if (updateEventUserRequest.getStateAction() != null &&
                (event.getState().equals(StateEvent.CANCELED) &&
                        updateEventUserRequest.getStateAction().equals(StateForUpdateEvent.SEND_TO_REVIEW))) {
            event.setState(StateEvent.PENDING);
        } else if (updateEventUserRequest.getStateAction() != null &&
                (event.getState().equals(StateEvent.PENDING) &&
                        updateEventUserRequest.getStateAction().equals(StateForUpdateEvent.CANCEL_REVIEW))) {
            event.setState(StateEvent.CANCELED);
        }
        //проверка даты
        if (updateEventUserRequest.getEventDate() != null && updateEventUserRequest.getEventDate()
                .isAfter(LocalDateTime.now().plusHours(2))) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        //проверка категории и локации
        Event updateEventWithCategoryAndLocation = updateCategoryAndLocation(updateEventUserRequest, event);

        Event save = eventRepository.save(
                eventMapper.toUpdateEvent(updateEventUserRequest, updateEventWithCategoryAndLocation));
        return eventMapper.toEventFullDto(save);
    }

    @Override
    public Event findEventById(Long eventId) {
        return eventRepository.findEventById(eventId).orElseThrow(() ->
                new NotFoundException("Event c id - " + eventId + " не найден"));
    }

    @Transactional
    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        Event event = findEventById(eventId);
        if (!event.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Дата начала изменяемого события должна быть " +
                    "не ранее чем за час от текущего времени. Текущая дата события: " + event.getEventDate());
        }
        if (!event.getState().equals(StateEvent.PENDING)) {
            throw new ConflictException("Статус у события, которое планируется опубликовать, " +
                    "должен быть PENDING. Текущий статус: " + event.getState());
        }
        if (updateEventAdminRequestDto.getStateAction().equals(StateForUpdateEvent.PUBLISH_EVENT)) {
            event.setState(StateEvent.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        }
        if (updateEventAdminRequestDto.getStateAction().equals(StateForUpdateEvent.REJECT_EVENT)) {
            event.setState(StateEvent.CANCELED);
        }
        Event updateEventWithCategoryAndLocation = updateCategoryAndLocation(updateEventAdminRequestDto, event);
        Event save = eventRepository.save(
                eventMapper.toUpdateEvent(updateEventAdminRequestDto, updateEventWithCategoryAndLocation));
        return eventMapper.toEventFullDto(save);
    }

    @Override
    public List<EventFullDto> findEventByParamsAdmin(EventAdminParamDto eventParamDto) {
        BooleanBuilder booleanBuilder = EventRepository.PredicatesForParamAdmin.build(eventParamDto);

        Pageable pageable = PageRequestUtil.of(eventParamDto.getFrom(),
                eventParamDto.getSize(), Sort.by("id").ascending());

        return eventRepository.findAll(booleanBuilder, pageable).getContent().stream()
                .map(eventMapper::toEventFullDto).toList();
//        QEvent qEvent = QEvent.event;
//        List<Event> events = queryFactory.selectFrom(qEvent)
//                .leftJoin(qEvent.category).fetchJoin()
//                .leftJoin(qEvent.initiator).fetchJoin()
//                .leftJoin(qEvent.location).fetchJoin()
//                .where(EventRepository.PredicatesForParamAdmin.build(eventParamDto))
//                .orderBy(qEvent.initiator.id.asc())
//                .offset(eventParamDto.getFrom())
//                .limit(eventParamDto.getSize())
//                .fetch();
//        return events.stream()
//                .map(eventMapper::toEventFullDto)
//                .toList();
    }

    @Override
    public List<EventShortDto> findEventByParamsPublic(EventPublicParamsDto eventPublicParamsDto) {
        BooleanBuilder booleanBuilder = EventRepository.PredicatesForParamPublic.build(eventPublicParamsDto);

//        QEvent qEvent = QEvent.event;

//        List<Event> events = queryFactory.selectFrom(qEvent)
//                .leftJoin(qEvent.category).fetchJoin()
//                .leftJoin(qEvent.initiator).fetchJoin()
//                .leftJoin(qEvent.location).fetchJoin()
//                .where(EventRepository.PredicatesForParamPublic.build(eventPublicParamsDto))
//                .orderBy(eventPublicParamsDto.getSort().equals(SortForParamPublicEvent.EVENT_DATE)? qEvent.eventDate.desc(): qEvent.views.desc())
//                .offset(eventPublicParamsDto.getFrom())
//                .limit(eventPublicParamsDto.getSize())
//                .fetch();
//        return events.stream()
//                .map(eventMapper::toEventShortDto)
//                .toList();
        String sort = (eventPublicParamsDto.getSort() != null &&
                eventPublicParamsDto.getSort().equals(SortForParamPublicEvent.EVENT_DATE))
                ? "eventDate"
                : (eventPublicParamsDto.getSort() != null && eventPublicParamsDto.getSort()
                .equals(SortForParamPublicEvent.VIEWS))
                ? "views"
                : "id";

        Pageable pageable = PageRequestUtil.of(eventPublicParamsDto.getFrom(),
                eventPublicParamsDto.getSize(), Sort.by(sort).descending());

        return eventRepository.findAll(booleanBuilder, pageable).getContent().stream()
                .map(eventMapper::toEventShortDto).toList();
    }

    @Transactional
    private Event updateCategoryAndLocation(UpdateEventUserRequest updateEventUserRequest, Event event) {
        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryMapper.toCategory(
                    categoryService.getCategory(updateEventUserRequest.getCategory()));
            event.setCategory(category);
        }
        if (updateEventUserRequest.getLocation() != null) {
            Location location = locationRepository.save(locationMapper.toLocation(updateEventUserRequest.getLocation()));
            event.setLocation(location);
        }
        return event;
    }

    @Override
    public EventFullDto findPublicEventById(Long eventId) {
        Event event = findEventById(eventId);
        if (!event.getState().equals(StateEvent.PUBLISHED)) {
            throw new NotFoundException("Событие не доступно. Статус события: " + event.getState());
        }
        return eventMapper.toEventFullDto(event);
    }

    private Event findEventWithOutDto(Long userId, Long eventId) {
        return eventRepository.findEventByUserIdAndEventId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Event c id - " + eventId + " не найден у пользователя с id - " + userId));
    }


}
