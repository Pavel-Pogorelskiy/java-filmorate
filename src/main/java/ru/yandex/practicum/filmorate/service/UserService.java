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
    private static final String REMOVE_FRIEND = "remove";
    private static final String ADD_FRIEND = "add";
    @Autowired
    private InMemoryUserStorage storage;

    public void addFriend(int id, int friendId) {
        storage.validateId(id);
        storage.validateId(friendId);
        storage.uptade(uptadeFriend(storage.get(id), friendId, ADD_FRIEND));
        storage.uptade(uptadeFriend(storage.get(friendId), id, ADD_FRIEND));
    }

    public User uptadeFriend(User user, int idFriend, String operation) {
        Set<Integer> userFriends = user.getFriends();
        if (operation.equals(ADD_FRIEND)) {
            userFriends.add(idFriend);
            user.setFriends(userFriends);
        }
        if (operation.equals(REMOVE_FRIEND)) {
            userFriends.remove(user.getId());
            user.setFriends(userFriends);
        }
        return user;
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
        storage.uptade(uptadeFriend(storage.get(id), friendId, REMOVE_FRIEND));
        storage.uptade(uptadeFriend(storage.get(friendId), id, REMOVE_FRIEND));
    }

    public List<User> commonFriends(int id, int otherId) {
        storage.validateId(id);
        storage.validateId(otherId);
        if (storage.get(id).getFriends().size() == 0 && storage.get(otherId).getFriends().size() == 0) {
            return new ArrayList<>();
        }
        Set<Integer> userFriends = new HashSet<>(storage.get(id).getFriends());
        Set<Integer> friendFriends = new HashSet<>(storage.get(otherId).getFriends());
        userFriends.retainAll(friendFriends);
        List<User> commonFriends = storage.getAll().stream()
                .filter(it -> userFriends.contains(it.getId()))
                .collect(Collectors.toList());
        return commonFriends;
    }
}
