package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.EventsStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Primary
public class EventsDbStorage implements EventsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Event addEvent(Event event) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("events")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = Map.of(
                "timestamp", event.getTimestamp(),
                "userId", event.getUserId(),
                "eventType", event.getEventType().name(),
                "operation", event.getOperation().name(),
                "entityId", event.getEntityId());

        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        event.setId(id.intValue());

        return event;
    }

    @Override
    public List<Event> getEventsUserFriends(int userId) {

        return jdbcTemplate.query(
                "SELECT * FROM events " +
                    "WHERE user_id IN " +
                        "(SELECT user_id FROM friends " +
                        "WHERE friends_id = ?) " +
                    "ORDER BY time_stamp DESC;",
                EventsDbStorage::getEvent, userId);
    }

    static Event getEvent(ResultSet rs, int RowNum) throws SQLException {

        return Event.builder()
                .id(rs.getInt("id"))
                .timestamp(LocalDateTime.parse(rs.getTimestamp("time_stamp").toString()))
                .eventType(Event.EventType.valueOf(rs.getString("event_type")))
                .operation(Event.EventOperation.valueOf(rs.getString("operation")))
                .entityId(rs.getInt("entityId"))
                .build();
    }
}
