package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventsStorage eventsStorage;

    private final UserStorage userStorage;

    public Event addMarkEvent(int userId, int filmId, Event.EventOperation operation) {

        return eventsStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(Event.EventType.MARK)
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
        return eventsStorage.getEventsUser(userId);
    }
}
