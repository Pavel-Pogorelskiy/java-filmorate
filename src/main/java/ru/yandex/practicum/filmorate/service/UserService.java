package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    InMemoryUserStorage storage;

    public void addFriend(int id, int friendId) {
        storage.validateId(id);
        storage.validateId(friendId);
        User user = storage.get(id);
        User friend = storage.get(friendId);
        Set<Integer> userFriends;
        Set<Integer> friendFriends;
        userFriends = user.getFriends();
        friendFriends = friend.getFriends();
        userFriends.add(friendId);
        friendFriends.add(id);
        user.setFriends(userFriends);
        friend.setFriends(friendFriends);
        storage.uptade(user);
        storage.uptade(friend);
    }
    public List<User> getFriend(int id) {
        storage.validateId(id);
        List<User> friend = storage.getAll().stream()
               .filter(it -> it.getFriends() != null && it.getFriends().contains(id))
               .collect(Collectors.toList());
        return friend;
    }

    public void deleteFriend(int id, int friendId) {
        storage.validateId(id);
        storage.validateId(friendId);
        User user = storage.get(id);
        User friend = storage.get(friendId);
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> friendFriends = friend.getFriends();
        userFriends.remove(friendId);
        friendFriends.remove(id);
        user.setFriends(userFriends);
        friend.setFriends(friendFriends);
        storage.uptade(user);
        storage.uptade(friend);
    }

    public List <User> commonFriends(int id, int otherId) {
        storage.validateId(id);
        storage.validateId(otherId);
        if (storage.get(id).getFriends().size() == 0 && storage.get(otherId).getFriends().size() == 0) {
            return new ArrayList<>();
        }
        Set<Integer> userFriends = new HashSet<>(storage.get(id).getFriends());
        Set<Integer> friendFriends = new HashSet<>(storage.get(otherId).getFriends());
        userFriends.retainAll(friendFriends);
        List <User> commonFriends = storage.getAll().stream()
                .filter(it -> userFriends.contains(it.getId()))
                .collect(Collectors.toList());
        return commonFriends;
    }
}
