package me.exrates.dao.userOperation;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
public class UserOperationDaoImpl implements UserOperationDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public boolean getStatusAuthorityForUserByOperation(int userId, UserOperationAuthority userOperationAuthority) {
        final String sql = "SELECT user_operation_id FROM USER_OPERATION_AUTHORITY " +
                "WHERE user_id=:userId AND user_operation_id=:userOperationId AND enabled=1";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("userOperationId", userOperationAuthority.getOperationId());

        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class) > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

}

