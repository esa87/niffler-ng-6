package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserdataUserRepository {
    UserEntity create(UserEntity authUser);

    UserEntity update(UserEntity authUser);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> findAll();

    void addIncomeInvitation(UserEntity requester, UserEntity addressee);

    void addOutcomeInvitation(UserEntity requester, UserEntity addressee);

    void addFriendship(UserEntity requester, UserEntity addressee);

    void delete(UserEntity authUser);
}