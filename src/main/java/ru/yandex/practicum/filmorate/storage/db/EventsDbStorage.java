package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventsStorage;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Primary
public class EventsDbStorage implements EventsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Event addEvent(Event event) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = Map.of(
                "time_stamp", event.getTimestamp(),
                "user_id", event.getUserId(),
                "event_type", event.getEventType().name(),
                "operation", event.getOperation().name(),
                "entity_id", event.getEntityId());

        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        event.setId(id.intValue());

        return event;
    }

    @Override
    public List<Event> getEventsUser(int userId) {

        return jdbcTemplate.query(
                "SELECT * FROM events " +
                    "WHERE user_id = ?;",
                EventsDbStorage::getEvent, userId);
    }

    static Event getEvent(ResultSet rs, int RowNum) throws SQLException {

        return Event.builder()
                .id(rs.getInt("id"))
                .timestamp(rs.getLong("time_stamp"))
                .userId(rs.getInt("user_id"))
                .eventType(Event.EventType.valueOf(rs.getString("event_type")))
                .operation(Event.EventOperation.valueOf(rs.getString("operation")))
                .entityId(rs.getInt("entity_id"))
                .build();
    }
}
