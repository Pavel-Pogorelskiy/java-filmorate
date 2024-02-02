package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.db.*;

import java.time.*;
import java.util.List;

@Service
public class EventService {

    @Autowired
    EventsDbStorage eventsStorage;

    @Autowired
    UserDbStorage userStorage;

    public Event addLikeEvent(int userId, int filmId, Event.EventOperation operation) {

        return eventsStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(Event.EventType.LIKE)
                .operation(operation)
                .entityId(filmId)
                .build());

    }

    public Event addReviewEvent(int userId, int reviewId, Event.EventOperation operation) {

        return eventsStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(Event.EventType.REVIEW)
                .operation(operation)
                .entityId(reviewId)
                .build());
    }

    public Event addFriendEvent(int userId, int friendId, Event.EventOperation operation) {

        return eventsStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(Event.EventType.FRIEND)
                .operation(operation)
                .entityId(friendId)
                .build());
    }

    public List<Event> getEventsUserFriends(int userId) {

        userStorage.validateId(userId);
        return eventsStorage.getEventsUserFriends(userId);
    }
}
