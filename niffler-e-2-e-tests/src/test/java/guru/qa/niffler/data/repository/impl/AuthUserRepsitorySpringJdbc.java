package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepsitorySpringJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserEntity create(AuthUserEntity userAuth) {
        try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO public.user( username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)" +
                        " VALUES (?, ?, ?, ?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS);
             PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO public.authority(user_id, authority) " +
                             "VALUES (?, ?);")
        ) {
            userPs.setString(1, userAuth.getUsername());
            userPs.setString(2, userAuth.getPassword());
            userPs.setBoolean(3, userAuth.getEnabled());
            userPs.setBoolean(4, userAuth.getAccountNonExpired());
            userPs.setBoolean(5, userAuth.getAccountNonLocked());
            userPs.setBoolean(6, userAuth.getCredentialsNonExpired());

            userPs.executeUpdate();
            final UUID generationKey;

            try (ResultSet rs = userPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generationKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            userAuth.setId(generationKey);

            for (AuthAuthorityEntity a : userAuth.getAuthorities()) {
                authorityPs.setObject(1, generationKey);
                authorityPs.setString(2, a.getAuthority().toString());
                authorityPs.addBatch();
                authorityPs.clearParameters();
            }
            authorityPs.executeBatch();
            return userAuth;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthUserEntity update(AuthUserEntity authUser) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "UPDATE public.user SET password=? "
                        + "WHERE id=?"
        )) {
            ps.setString(1, authUser.getPassword());
            ps.setObject(2, authUser.getId());
            ps.executeUpdate();
            return authUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM public.user u INNER JOIN public.authority a ON u.id = a.user_id WHERE id=?",
                        AuthUserEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM public.user u INNER JOIN public.authority a ON u.id=a.user_id WHERE username=?"
        )) {
            ps.setObject(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                AuthUserEntity user = null;
                List<AuthAuthorityEntity> list = new ArrayList<>();
                while (rs.next()) {
                    if (user == null) {
                        user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                    }
                    AuthUserEntity aue = new AuthUserEntity();
                    aue.setId(rs.getObject("id", UUID.class));
                    aue.setUsername(rs.getString("username"));
                    aue.setPassword(rs.getString("password"));
                    aue.setEnabled(rs.getBoolean("enabled"));
                    aue.setAccountNonLocked(rs.getBoolean("account_non_expired"));
                    aue.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    aue.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    AuthAuthorityEntity authAuthority = new AuthAuthorityEntity();
                    authAuthority.setId(rs.getObject("id", UUID.class));
                    authAuthority.setUser(user);
                    authAuthority.setAuthority(Authority.valueOf(rs.getString("authority")));
                    list.add(authAuthority);
                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    user.setAuthorities(list);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM public.user u INNER JOIN public.authority a ON u.id=a.user_id"
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                List<AuthUserEntity> listUser = new ArrayList<>();
                List<AuthAuthorityEntity> listAuthority = new ArrayList<>();
                AuthUserEntity user = null;
                int rowNum = 1;
                while (rs.next()) {
                    if (user == null || user.getId() != rs.getObject("id", UUID.class)) {
                        if (rowNum > 1) {
                            user.setAuthorities(listAuthority);
                            listUser.add(user);
                            listAuthority.clear();
                        }
                        user = AuthUserEntityRowMapper.instance.mapRow(rs, rowNum);
                    }
                    AuthUserEntity aue = new AuthUserEntity();
                    aue.setId(rs.getObject("id", UUID.class));
                    aue.setUsername(rs.getString("username"));
                    aue.setPassword(rs.getString("password"));
                    aue.setEnabled(rs.getBoolean("enabled"));
                    aue.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    aue.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    aue.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    AuthAuthorityEntity authAuthority = new AuthAuthorityEntity();
                    authAuthority.setId(rs.getObject("id", UUID.class));
                    authAuthority.setUser(user);
                    authAuthority.setAuthority(Authority.valueOf(rs.getString("authority")));
                    listAuthority.add(authAuthority);
                    rowNum++;
                }
                return listUser;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(AuthUserEntity authUser) {
        try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE public.user WHERE id=? ");
             PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "DELETE FROM public.authority WHERE  user_id=?")
        ) {
            userPs.setObject(1, authUser.getId());
            int resExecuteUpdate = userPs.executeUpdate();
            if (resExecuteUpdate == 0) {
                throw new SQLException("Can't find deleted user");
            }
            for (AuthAuthorityEntity ae : authUser.getAuthorities()) {
                authorityPs.setObject(1, ae.getUser());
                int resAuthorityExecuteUpdate = authorityPs.executeUpdate();
                if (resAuthorityExecuteUpdate == 0) {
                    throw new SQLException("Can't find deleted authority");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
