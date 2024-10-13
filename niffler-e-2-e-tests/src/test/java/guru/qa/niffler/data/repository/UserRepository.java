package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    UserEntity createUser(UserEntity user);

    void addFriendship(UserEntity requester, UserEntity addressee);

    void addInvitation(UserEntity requester, UserEntity addressee);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> findAll();

    void delete(UserEntity user);
}