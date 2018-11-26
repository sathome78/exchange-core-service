package me.exrates.dao.impl;

import me.exrates.dao.G2faDao;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class G2faDaoImpl implements G2faDao {

    @Autowired
    @Qualifier("masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public String getGoogleAuthSecretCodeByUser(Integer userId) {
        String sql = "SELECT secret_code FROM 2FA_GOOGLE_AUTHENTICATOR WHERE user_id = :user_id";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("user_id", userId);
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, String.class);
        }catch (EmptyResultDataAccessException e){
            return "";
        }
    }

    @Override
    public void setGoogleAuthSecretCode(Integer userId) {
        String sql = "UPDATE 2FA_GOOGLE_AUTHENTICATOR SET secret_code = :secret WHERE user_id = :user_id" ;
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("user_id", userId);
            put("secret", Base32.random());
        }};
        jdbcTemplate.update(sql, namedParameters);
    }

    @Override
    public void setGoogleAuthSecretCode(Integer userId, String secretCode) {
        String sql = "INSERT INTO 2FA_GOOGLE_AUTHENTICATOR (user_id, enable, secret_code) "
                + " VALUES (:user_id, true, :secret)";
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("user_id", userId);
            put("secret", secretCode);
        }};
        jdbcTemplate.update(sql, namedParameters);
    }

    @Override
    public void set2faGoogleAuthenticator(Integer userId) {
        String sql = "INSERT INTO 2FA_GOOGLE_AUTHENTICATOR (user_id, enable, secret_code) VALUES (:user_id, false, :secret) ";
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("user_id", userId);
            put("secret", Base32.random());
        }};
        jdbcTemplate.update(sql, namedParameters);
    }

    @Override
    public void setEnable2faGoogleAuth(Integer userId, Boolean connection) {
        String sql = "UPDATE 2FA_GOOGLE_AUTHENTICATOR SET enable =:connection WHERE user_id = :user_id";
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("user_id", userId);
            put("connection", connection);
        }};
        jdbcTemplate.update(sql, namedParameters);
    }

    @Override
    public boolean isGoogleAuthenticatorEnable(Integer userId) {
        String sql = "SELECT enable FROM 2FA_GOOGLE_AUTHENTICATOR WHERE user_id = :user_id";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("user_id", userId);
        try {
            return jdbcTemplate.queryForObject(sql, namedParameters, Boolean.class);
        }catch (EmptyResultDataAccessException e){
            return false;
        }
    }

}
