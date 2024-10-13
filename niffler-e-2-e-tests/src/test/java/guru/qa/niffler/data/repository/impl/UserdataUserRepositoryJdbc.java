package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserEntityRowMapper;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserdataUserRepositoryJdbc implements UserdataUserRepository {
    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO public.user(username, currency, firstname, surname, photo, photo_small, full_name)" +
                        "VALUES (?, ?, ? ,?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            ps.executeUpdate();
            final UUID generationKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generationKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            user.setId(generationKey);
            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity update(UserEntity authUser) {
        return null;
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        try (
                PreparedStatement frienshipPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                        "INSERT INTO public.friendship(requester_id, addressee_id, status, created_date) " +
                                "VALUES (?, ?, ?, ?);"
                )
        ) {
            frienshipPs.setObject(1, requester.getId());
            frienshipPs.setObject(2, addressee.getId());
            frienshipPs.setString(3, FriendshipStatus.ACCEPTED.toString());
            frienshipPs.setDate(4, new Date(new java.util.Date().getTime()));
            frienshipPs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addInvitation(UserEntity requester, UserEntity addressee) {
        try (
                PreparedStatement frienshipPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                        "INSERT INTO public.friendship(requester_id, addressee_id, status, created_date) " +
                                "VALUES (?, ?, ?, ?);"
                )
        ) {

            frienshipPs.setObject(1, requester.getId());
            frienshipPs.setObject(2, addressee.getId());
            frienshipPs.setString(3, FriendshipStatus.PENDING.toString());
            frienshipPs.setDate(4, new Date(new java.util.Date().getTime()));
            frienshipPs.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {

        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM public.user AS u, friendship AS f " +
                        "WHERE u.id = ? AND (u.id=f.requester_id OR (u.id=f.addressee_id AND f.status='PENDING'))"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                UserEntity user = null;
                List<FriendshipEntity> listRequests = new ArrayList<>();
                List<FriendshipEntity> listAddressees = new ArrayList<>();
                while (rs.next()) {
                    if(user == null) {
                        user = UserEntityRowMapper.instance.mapRow(rs, 1);
                    }
                    FriendshipEntity fu = new FriendshipEntity();
                    fu.setRequester(rs.getObject("requester_id", UserEntity.class));
                    fu.setAddressee(rs.getObject("addressee_id", UserEntity.class));
                    fu.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                    fu.setCreatedDate(rs.getDate("created_date"));
                    if (user.getId() == fu.getRequester().getId()) {
                        listRequests.add(fu);
                    } else {
                        listAddressees.add(fu);
                    }
                }
              if (user == null) {
                  return Optional.empty();
              }  else {
                  user.setFriendshipAddressees(listAddressees);
                  user.setFriendshipRequests(listRequests);
                  return Optional.of(user);
              }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM public.user WHERE username = ?"
        )) {
            ps.setObject(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserEntity ue = new UserEntity();
                    ue.setId(rs.getObject("id", UUID.class));
                    ue.setUsername(rs.getString("username"));
                    ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    ue.setFirstname(rs.getString("firstname"));
                    ue.setSurname(rs.getString("surname"));
                    ue.setPhoto(rs.getBytes("photo"));
                    ue.setPhotoSmall(rs.getBytes("photo_small"));
                    ue.setFullname(rs.getString("full_name"));
                    return Optional.of(ue);
                } else {
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UserEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM public.user"
        )) {
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                List<UserEntity> list = new ArrayList<>();
                while (rs.next()) {
                    UserEntity ue = new UserEntity();
                    ue.setId(rs.getObject("id", UUID.class));
                    ue.setUsername(rs.getString("username"));
                    ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    ue.setFirstname(rs.getString("firstname"));
                    ue.setSurname(rs.getString("surname"));
                    ue.setPhoto(rs.getBytes("photo"));
                    ue.setPhotoSmall(rs.getBytes("photo_small"));
                    ue.setFullname(rs.getString("full_name"));
                    list.add(ue);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM public.user WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                } else {
                    throw new SQLException("Can't find deleted user");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}