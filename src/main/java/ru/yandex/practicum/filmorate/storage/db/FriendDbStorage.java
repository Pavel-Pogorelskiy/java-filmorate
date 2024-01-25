package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
@Primary
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(int userId, int friendId) {
        jdbcTemplate.update("Insert into friends values (?,?)", userId, friendId);
    }

    @Override
    public List<User> getFriend(int userId) {
        return jdbcTemplate.query("Select * From users As u " +
                "Where u.user_id In (Select f.friends_id From friends As f Where f.user_id = ?)",
                (rs, rowNum) -> User.builder()
                        .id(rs.getInt("user_id"))
                        .name(rs.getString("name"))
                        .email(rs.getString("email"))
                        .login(rs.getString("login"))
                        .birthday(rs.getDate("birthday").toLocalDate())
                        .build(), userId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        jdbcTemplate.update("Delete From friends Where user_id = ? And friends_id = ? ", userId, friendId);
    }

    @Override
    public List<User> commonFriends(int userId, int friendId) {
        return jdbcTemplate.query("select * from users where users.user_id in (Select fr.friends_id " +
                "From friends as fr Where fr.friends_id in (Select f.friends_id From friends as f " +
                "Where f.user_id = ?) and fr.user_id = ?)",
                (rs, rowNum) -> User.builder()
                        .id(rs.getInt("user_id"))
                        .name(rs.getString("name"))
                        .email(rs.getString("email"))
                        .login(rs.getString("login"))
                        .birthday(rs.getDate("birthday").toLocalDate())
                        .build(), userId, friendId);
    }
}
