package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundDataException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User data) {
        SimpleJdbcInsert simpleJdbcInsert =
                new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        Map<String, Object> params = Map.of(
                "email", data.getEmail(),
                "name", data.getName(),
                "login", data.getLogin(),
                "birthday", data.getBirthday());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        data.setId(id.intValue());
        return data;
    }

    @Override
    public User uptade(User data) {
        validateId(data.getId());
        jdbcTemplate.update(
                "Update users Set name = ?, login = ?, " +
                        "email = ?, birthday = ? Where user_id = ?",
                data.getName(), data.getLogin(), data.getEmail(),
                data.getBirthday(), data.getId());
        return data;
    }

    @Override
    public User get(int id) {
        validateId(id);
        List<User> users = jdbcTemplate.query(
                "Select * From users Where user_id = ?",
                UserDbStorage::createUser, id);
        if (users.size() != 1) {
            throw new NotFoundDataException("Пользователь больше или меньше одного");
        }
        return users.get(0);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(
                "Select * From users",UserDbStorage::createUser);
    }

    @Override
    public void delete(int id) {
        jdbcTemplate.update(
                "Delete From users Where user_id = ?", id);
    }

    static User createUser(ResultSet rs, int RowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    public void validateId(int id) {
        List<User> users = jdbcTemplate.query(
                "Select * From users Where user_id = ?",
                UserDbStorage::createUser, id);
        if (users.size() == 0) {
            throw new NotFoundDataException("Пользователя с id = " + id + " не существует");
        }
    }
}