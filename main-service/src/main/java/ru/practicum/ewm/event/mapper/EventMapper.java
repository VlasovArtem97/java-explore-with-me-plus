package ru.practicum.ewm.event.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.status.StateEvent;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @BeanMapping(qualifiedByName = "event")
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "user", target = "initiator")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "location", target = "location")
    Event toEvent(NewEventDto newEventDto, User user, Category category, Location location);


    @Named("event")
    @AfterMapping
    default void setDefaultCreatedOn(@MappingTarget Event.EventBuilder event) {
        event.createdOn(LocalDateTime.now());
        event.state(StateEvent.PENDING);
        event.confirmedRequests(0L);
        event.views(0L);
    }

    //    @Mapping(source = "category", target = "category")
//    @Mapping(source = "initiator", target = "initiator")
//    @Mapping(source = "location", target = "location")
    EventFullDto toEventFullDto(Event event);

    //    @Mapping(source = "category", target = "category")
//    @Mapping(source = "initiator", target = "initiator")
    EventShortDto toEventShortDto(Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, qualifiedByName = "updateEvent")
    @Mapping(target = "category", ignore = true)
    void toUpdateEvent(UpdateEventUserRequest updateEventUserRequest, @MappingTarget Event event);
//    {
//        return Event.builder()
//                .id(event.getId())
//                .annotation(updateEventUserRequest.getAnnotation() != null ? updateEventUserRequest.getAnnotation() :
//                        event.getAnnotation())
//                .category(event.getCategory())
//                .description(updateEventUserRequest.getDescription() != null ? updateEventUserRequest.getDescription() :
//                        event.getDescription())
//                .eventDate(event.getEventDate())
//                .paid((updateEventUserRequest.getPaid() != null ? updateEventUserRequest.getPaid() :
//                        event.getPaid()))
//                .participantLimit((updateEventUserRequest.getParticipantLimit() != null ?
//                        updateEventUserRequest.getParticipantLimit() : event.getParticipantLimit()))
//                .title(updateEventUserRequest.getTitle() != null ? updateEventUserRequest.getTitle() : event.getTitle())
//                .state(event.getState())
//                .createdOn(event.getCreatedOn())
//                .requestModeration(updateEventUserRequest.getRequestModeration() != null ?
//                        updateEventUserRequest.getRequestModeration() : event.getRequestModeration())
//                .initiator(event.getInitiator())
//                .confirmedRequests(event.getConfirmedRequests())
//                .publishedOn(event.getPublishedOn() != null ? event.getPublishedOn() : null)
//                .title(updateEventUserRequest.getTitle() != null ? updateEventUserRequest.getTitle() :
//                        event.getTitle())
//                .location(event.getLocation())
//                .build();
//    }
}
