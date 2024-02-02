package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Event extends BaseUnit {

    @NotNull
    private Long timestamp;

    @Min(1)
    private int userId;

    private EventType eventType;

    private EventOperation operation;

    @Min(1)
    private int entityId;

    @JsonProperty("eventId")
    public int getId() {
        return super.getId();
    }

    public enum EventType {
        LIKE,
        REVIEW,
        FRIEND
    }

    public enum EventOperation {
        REMOVE,
        ADD,
        UPDATE
    }
}