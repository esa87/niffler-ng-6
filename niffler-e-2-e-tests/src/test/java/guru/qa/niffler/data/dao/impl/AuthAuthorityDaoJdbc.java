package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.Authority;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public void create(AuthAuthorityEntity... authAuthority) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO public.authority(user_id, authority) " +
                        "VALUES (?, ?);",
                Statement.RETURN_GENERATED_KEYS
        )) {
            for (int i = 0; i < authAuthority.length; i++) {
                ps.setObject(1, authAuthority[i].getUserId());
                ps.setString(2, authAuthority[i].getAuthority().toString());
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthAuthorityEntity> findByUserId(UUID userId) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM public.authority WHERE userId="
        )) {
            ps.setObject(1, userId);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                List<AuthAuthorityEntity> list = new ArrayList<>();
                if (rs.next()) {
                    AuthAuthorityEntity authority = new AuthAuthorityEntity();
                    authority.setId(rs.getObject("id", UUID.class));
                    authority.setUserId(rs.getObject("user_id", UUID.class));
                    authority.setAuthority(rs.getObject("authority", Authority.class));
                    list.add(authority);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthAuthorityEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM public.authority"
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                List<AuthAuthorityEntity> list = new ArrayList<>();
                if (rs.next()) {
                    AuthAuthorityEntity authority = new AuthAuthorityEntity();
                    authority.setId(rs.getObject("id", UUID.class));
                    authority.setUserId(rs.getObject("user_id", UUID.class));
                    authority.setAuthority(rs.getObject("authority", Authority.class));
                    list.add(authority);
                }
                return list;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(AuthAuthorityEntity authAuthority) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE FROM public.authority WHERE  user_id=?"
        )) {
            ps.setObject(1, authAuthority.getUserId());
            int resExecuteUpdate = ps.executeUpdate();
            if (resExecuteUpdate == 0) {
                throw new SQLException("Can't find deleted AuthAuthority");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
