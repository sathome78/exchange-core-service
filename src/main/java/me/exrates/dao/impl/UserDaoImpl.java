package me.exrates.dao.impl;

import me.exrates.dao.UserDao;
import me.exrates.exception.UserNotFoundException;
import me.exrates.model.User;
import me.exrates.model.dto.TemporalToken;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.main.UserFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;

@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LogManager.getLogger(UserDaoImpl.class);

    private final String SELECT_USER =
            "SELECT USER.id, u.email AS parent_email, USER.finpassword, USER.nickname, USER.email, USER.password, USER.regdate, " +
                    "USER.phone, USER.status, USER_ROLE.name AS role_name FROM USER " +
                    "INNER JOIN USER_ROLE ON USER.roleid = USER_ROLE.id LEFT JOIN REFERRAL_USER_GRAPH " +
                    "ON USER.id = REFERRAL_USER_GRAPH.child LEFT JOIN USER AS u ON REFERRAL_USER_GRAPH.parent = u.id ";

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    private RowMapper<User> getUserRowMapper() {
        return (resultSet, i) -> {
            final User user = new User();
            user.setId(resultSet.getInt("id"));
            user.setNickname(resultSet.getString("nickname"));
            user.setEmail(resultSet.getString("email"));
            user.setPassword(resultSet.getString("password"));
            user.setRegdate(resultSet.getDate("regdate"));
            user.setPhone(resultSet.getString("phone"));
            user.setStatus(UserStatus.values()[resultSet.getInt("status") - 1]);
            user.setRole(UserRole.valueOf(resultSet.getString("role_name")));
            user.setFinpassword(resultSet.getString("finpassword"));
            try {
                user.setParentEmail(resultSet.getString("parent_email")); // May not exist for some users
            } catch (final SQLException e) {/*NOP*/}
            return user;
        };
    }

    private RowMapper<User> getUserRowMapperWithoutRoleAndParentEmail() {
        return (resultSet, i) -> {
            final User user = new User();
            user.setId(resultSet.getInt("id"));
            user.setNickname(resultSet.getString("nickname"));
            user.setEmail(resultSet.getString("email"));
            user.setPassword(resultSet.getString("password"));
            user.setRegdate(resultSet.getDate("regdate"));
            user.setPhone(resultSet.getString("phone"));
            user.setStatus(UserStatus.values()[resultSet.getInt("status") - 1]);
            user.setFinpassword(resultSet.getString("finpassword"));
            return user;
        };
    }

    public int getIdByEmail(String email) {
        String sql = "SELECT id FROM USER WHERE email = :email";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    @Override
    public boolean setNickname(String newNickName, String userEmail) {
        String sql = "UPDATE USER SET nickname=:nickname WHERE email = :email";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("nickname", newNickName);
        namedParameters.put("email", userEmail);
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    public boolean create(User user) {
        String sqlUser = "insert into USER(nickname,email,password,phone,status,roleid ) " +
                "values(:nickname,:email,:password,:phone,:status,:roleid)";
        String sqlWallet = "INSERT INTO WALLET (currency_id, user_id) select id, :user_id from CURRENCY;";
        String sqlNotificationOptions = "INSERT INTO NOTIFICATION_OPTIONS(notification_event_id, user_id, send_notification, send_email) " +
                "select id, :user_id, default_send_notification, default_send_email FROM NOTIFICATION_EVENT; ";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("email", user.getEmail());
        namedParameters.put("nickname", user.getNickname());
        if (user.getPassword() != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            namedParameters.put("password", hashedPassword);
        } else {
            namedParameters.put("password", user.getPassword());
        }
        String phone = user.getPhone();
        if (user.getPhone() != null && user.getPhone().equals("")) {
            phone = null;
        }
        namedParameters.put("phone", phone);
        namedParameters.put("status", String.valueOf(user.getStatus().getStatus()));
        namedParameters.put("roleid", String.valueOf(user.getRole().getRole()));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sqlUser, new MapSqlParameterSource(namedParameters), keyHolder);
        int userId = keyHolder.getKey().intValue();
        Map<String, Integer> userIdParamMap = Collections.singletonMap("user_id", userId);

        return namedParameterJdbcTemplate.update(sqlWallet, userIdParamMap) > 0
                && namedParameterJdbcTemplate.update(sqlNotificationOptions, userIdParamMap) > 0;
    }

    @Override
    public void createUserDoc(final int userId, final List<Path> paths) {
        final String sql = "INSERT INTO USER_DOC (user_id, path) VALUES (:userId, :path)";
        List<HashMap<String, Object>> collect = paths.stream()
                .map(path -> new HashMap<String, Object>() {
                    {
                        put("userId", userId);
                        put("path", path.toString());
                    }
                }).collect(Collectors.toList());
        namedParameterJdbcTemplate.batchUpdate(sql, collect.toArray(new HashMap[paths.size()]));
    }

    @Override
    public List<UserFile> findUserDoc(final int userId) {
        final String sql = "SELECT * FROM USER_DOC where user_id = :userId";
        return namedParameterJdbcTemplate.query(sql, singletonMap("userId", userId), (resultSet, i) -> {
            final UserFile userFile = new UserFile();
            userFile.setId(resultSet.getInt("id"));
            userFile.setUserId(resultSet.getInt("user_id"));
            userFile.setPath(Paths.get(resultSet.getString("path")));
            return userFile;
        });
    }

    public List<UserRole> getAllRoles() {
        String sql = "select name from USER_ROLE";
        return namedParameterJdbcTemplate.query(sql, (rs, row) -> {
            UserRole role = UserRole.valueOf(rs.getString("name"));
            return role;
        });
    }


    @Override
    public UserRole getUserRoleById(Integer id) {
        String sql = "select USER_ROLE.name as role_name from USER " +
                "inner join USER_ROLE on USER.roleid = USER_ROLE.id where USER.id = :id ";
        Map<String, Integer> namedParameters = Collections.singletonMap("id", id);
        return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, (rs, row) -> UserRole.valueOf(rs.getString("role_name")));
    }


    @Override
    public boolean createAdminAuthoritiesForUser(Integer userId, UserRole role) {
        String sql = "INSERT INTO USER_ADMIN_AUTHORITY SELECT :user_id, admin_authority_id, enabled " +
                "FROM ADMIN_AUTHORITY_ROLE_DEFAULTS " +
                "WHERE role_id = :role_id";
        Map<String, Integer> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("role_id", role.getRole());
        return namedParameterJdbcTemplate.update(sql, params) > 0;

    }

    @Override
    public boolean hasAdminAuthorities(Integer userId) {
        String sql = "SELECT COUNT(*) FROM USER_ADMIN_AUTHORITY WHERE user_id = :user_id ";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        Integer count = namedParameterJdbcTemplate.queryForObject(sql, params, Integer.class);
        return count > 0;
    }

    @Override
    public boolean removeUserAuthorities(Integer userId) {
        String sql = "DELETE FROM USER_ADMIN_AUTHORITY WHERE user_id = :user_id ";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        return namedParameterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public User findByEmail(String email) {
        String sql = SELECT_USER + "WHERE USER.email = :email";
        final Map<String, String> params = new HashMap<String, String>() {
            {
                put("email", email);
            }
        };
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, getUserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(String.format("email: %s", email));
        }
    }

    public User getUserById(int id) {
        String sql = SELECT_USER + "WHERE USER.id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", String.valueOf(id));
        return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, getUserRowMapper());
    }

    public User getUserByTemporalToken(String token) {
        String sql = "SELECT * FROM USER WHERE USER.id =(SELECT TEMPORAL_TOKEN.user_id FROM TEMPORAL_TOKEN WHERE TEMPORAL_TOKEN.value=:token_value)";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("token_value", token);
        return namedParameterJdbcTemplate.query(sql, namedParameters, getUserRowMapperWithoutRoleAndParentEmail()).get(0);
    }

    @Override
    public User getCommonReferralRoot() {
        final String sql = "SELECT USER.id, nickname, email, password, finpassword, regdate, phone, status, USER_ROLE.name as role_name FROM COMMON_REFERRAL_ROOT INNER JOIN USER ON COMMON_REFERRAL_ROOT.user_id = USER.id INNER JOIN USER_ROLE ON USER.roleid = USER_ROLE.id LIMIT 1";
        final List<User> result = namedParameterJdbcTemplate.query(sql, getUserRowMapper());
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }


    public boolean ifNicknameIsUnique(String nickname) {
        String sql = "SELECT id FROM USER WHERE nickname = :nickname";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("nickname", nickname);
        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            if (rs.next()) {
                return rs.getInt("id");
            } else return 0;
        }).isEmpty();
    }

    public boolean ifEmailIsUnique(String email) {
        String sql = "SELECT id FROM USER WHERE email = :email";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            if (rs.next()) {
                return rs.getInt("id");
            } else return 0;
        }).isEmpty();
    }

    public String getIP(int userId) {
        String sql = "SELECT ipaddress FROM USER WHERE id = :userId";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("userId", String.valueOf(userId));
        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            if (rs.next()) {
                return rs.getString("ipaddress");
            }
            return null;
        }).get(0);
    }

    public boolean setIP(int id, String ip) {
        String sql = "UPDATE USER SET ipaddress = :ipaddress WHERE id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("ipaddress", ip);
        namedParameters.put("id", String.valueOf(id));
        return namedParameterJdbcTemplate.update(sql, namedParameters) > 0;
    }

    public boolean addIPToLog(int userId, String ip) {
        String sql = "insert INTO IP_Log (ip,user_id) values(:ip,:userId)";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("ip", ip);
        namedParameters.put("userId", String.valueOf(userId));
        return namedParameterJdbcTemplate.update(sql, namedParameters) > 0;
    }

    public boolean update(UpdateUserDto user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName(); //get logged in username
        LOGGER.debug("Updating user: " + user.getEmail() + " by " + currentUser +
                ", newRole: " + user.getRole() + ", newStatus: " + user.getStatus());

        String sql = "UPDATE USER SET";
        StringBuilder fieldsStr = new StringBuilder(" ");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        /*email is present in UpdateUserDto but used for hold email to send notification only, not for update email*/
        if (user.getPhone() != null) {
            fieldsStr.append("phone = '" + user.getPhone()).append("',");
        }
        if (user.getStatus() != null) {
            fieldsStr.append("status = " + user.getStatus().getStatus()).append(",");
        }
        if (user.getRole() != null) {
            fieldsStr.append("roleid = " + user.getRole().getRole()).append(",");
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            fieldsStr.append("password = '" + passwordEncoder.encode(user.getPassword())).append("',");
        }
        if (user.getFinpassword() != null && !user.getFinpassword().isEmpty()) {
            fieldsStr.append("finpassword = '" + passwordEncoder.encode(user.getFinpassword())).append("',");
        }
        if (fieldsStr.toString().trim().length() == 0) {
            return true;
        }
        sql = sql + fieldsStr.toString().replaceAll(",$", " ") + "WHERE USER.id = :id";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("id", user.getId());
        return namedParameterJdbcTemplate.update(sql, namedParameters) > 0;
    }

    public boolean createTemporalToken(TemporalToken token) {
        String sql = "insert into TEMPORAL_TOKEN(value,user_id,token_type_id,check_ip) values(:value,:user_id,:token_type_id,:check_ip)";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("value", token.getValue());
        namedParameters.put("user_id", String.valueOf(token.getUserId()));
        namedParameters.put("token_type_id", String.valueOf(token.getTokenType().getTokenType()));
        namedParameters.put("check_ip", token.getCheckIp());
        return namedParameterJdbcTemplate.update(sql, namedParameters) > 0;
    }

    public TemporalToken verifyToken(String token) {
        String sql = "SELECT * FROM TEMPORAL_TOKEN WHERE VALUE= :value";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("value", token);
        ArrayList<TemporalToken> result = (ArrayList<TemporalToken>) namedParameterJdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<TemporalToken>() {
            @Override
            public TemporalToken mapRow(ResultSet rs, int rowNumber) throws SQLException {
                TemporalToken temporalToken = new TemporalToken();
                temporalToken.setId(rs.getInt("id"));
                temporalToken.setUserId(rs.getInt("user_id"));
                temporalToken.setValue(token);
                temporalToken.setAlreadyUsed(rs.getBoolean("already_used"));
                temporalToken.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                temporalToken.setExpired(rs.getBoolean("expired"));
                temporalToken.setTokenType(TokenType.convert(rs.getInt("token_type_id")));
                temporalToken.setCheckIp(rs.getString("check_ip"));
                return temporalToken;
            }
        });
        return result.size() == 1 ? result.get(0) : null;
    }

    public boolean deleteTemporalToken(TemporalToken token) {
        if (token == null) {
            return false;
        }
        String sql = "delete from TEMPORAL_TOKEN where id = :id";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("id", String.valueOf(token.getId()));
        return namedParameterJdbcTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public boolean deleteTemporalToken(String tempToken) {
        String sql = "delete from TEMPORAL_TOKEN where value = :token";
        Map<String, String> namedParameters = new HashMap<>(1);
        namedParameters.put("token", tempToken);
        return namedParameterJdbcTemplate.update(sql, namedParameters) > 0;
    }

    public boolean deleteTemporalTokensOfTokentypeForUser(TemporalToken token) {
        if (token == null) {
            return false;
        }
        String sql = "delete from TEMPORAL_TOKEN where user_id = :user_id and token_type_id=:token_type_id";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("user_id", String.valueOf(token.getUserId()));
        namedParameters.put("token_type_id", String.valueOf(token.getTokenType().getTokenType()));
        return namedParameterJdbcTemplate.update(sql, namedParameters) > 0;
    }


    public List<TemporalToken> getAllTokens() {
        String sql = "SELECT * FROM TEMPORAL_TOKEN";
        ArrayList<TemporalToken> result = (ArrayList<TemporalToken>) namedParameterJdbcTemplate.query(sql, new BeanPropertyRowMapper<TemporalToken>() {
            @Override
            public TemporalToken mapRow(ResultSet rs, int rowNumber) throws SQLException {
                TemporalToken temporalToken = new TemporalToken();
                temporalToken.setId(rs.getInt("id"));
                temporalToken.setUserId(rs.getInt("user_id"));
                temporalToken.setValue(rs.getString("value"));
                temporalToken.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                temporalToken.setExpired(rs.getBoolean("expired"));
                temporalToken.setTokenType(TokenType.convert(rs.getInt("token_type_id")));
                temporalToken.setCheckIp(rs.getString("check_ip"));
                return temporalToken;
            }
        });
        return result;
    }

    @Override
    public String getAvatarPath(Integer userId) {
        String sql = "SELECT avatar_path FROM USER where id = :id";
        Map<String, Integer> params = Collections.singletonMap("id", userId);
        return namedParameterJdbcTemplate.queryForObject(sql, params, (resultSet, row) -> resultSet.getString("avatar_path"));
    }

    @Override
    public List<Integer> findFavouriteCurrencyPairsById(int userId) {
        String sql = "SELECT currency_pair_id FROM USER_FAVORITE_CURRENCY_PAIRS WHERE user_id = :userId";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("userId", userId);
        }};
        return namedParameterJdbcTemplate.queryForList(sql, params, Integer.class);
    }

    public boolean delete(User user) {
        boolean result;
        String sql = "delete from USER where id = :id";
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("id", String.valueOf(user.getId()));
        result = namedParameterJdbcTemplate.update(sql, namedParameters) > 0;
        if (!result) {
            LOGGER.warn("requested user deleting was not fulfilled. userId = " + user.getId());
        }
        return result;
    }

    @Override
    public boolean setPreferredLang(int userId, Locale locale) {
        String sql = "UPDATE USER SET preferred_lang=:preferred_lang WHERE id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", String.valueOf(userId));
        namedParameters.put("preferred_lang", locale.toString());
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    @Override
    public String getPreferredLang(int userId) {
        String sql = "SELECT preferred_lang FROM USER WHERE id = :id";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("id", userId);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public String getPreferredLangByEmail(String email) {
        String sql = "SELECT preferred_lang FROM USER WHERE email = :email";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean insertIp(String email, String ip) {
        String sql = "INSERT INTO USER_IP (user_id, ip)" +
                " SELECT id, '" + ip + "'" +
                " FROM USER " +
                " WHERE USER.email = :email";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        return namedParameterJdbcTemplate.update(sql, namedParameters) > 0;
    }

    public boolean setIpStateConfirmed(int userId, String ip) {
        String sql = "UPDATE USER_IP " +
                " SET confirmed = true, confirm_date = NOW() " +
                " WHERE user_id = :user_id AND ip = :ip";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("user_id", String.valueOf(userId));
        namedParameters.put("ip", ip);
        return namedParameterJdbcTemplate.update(sql, namedParameters) > 0;
    }



    @Override
    public Long saveTemporaryPassword(Integer userId, String password, Integer tokenId) {
        String sql = "INSERT INTO API_TEMP_PASSWORD(user_id, password, date_creation, temporal_token_id) VALUES (:userId, :password, NOW(), :tokenId);";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("userId", userId);
        namedParameters.put("password", encodedPassword);
        namedParameters.put("tokenId", tokenId);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(namedParameters), keyHolder);
        return (Long) keyHolder.getKey();
    }


    public String get2faSecretByEmail(String email) {
        String sql = "SELECT 2fa_secret FROM USER WHERE email = :email";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, String.class);
    }

    public boolean set2faSecretCode(String email) {
        System.out.println("set2faSecretCode");
        String sql = "UPDATE USER SET USER.2fa_secret =:secret " +
                "WHERE USER.email = :email";
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("email", email);
            put("secret", Base32.random());
        }};
        return namedParameterJdbcTemplate.update(sql, namedParameters) > 0;
    }



    @Override
    public String getEmailById(Integer id) {
        String sql = "SELECT email FROM USER WHERE id = :id";
        return namedParameterJdbcTemplate.queryForObject(sql, Collections.singletonMap("id", id), String.class);
    }

    @Override
    public UserRole getUserRoleByEmail(String email) {
        String sql = "select USER_ROLE.name as role_name from USER " +
                "inner join USER_ROLE on USER.roleid = USER_ROLE.id where USER.email = :email ";
        Map<String, String> namedParameters = Collections.singletonMap("email", email);
        return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, (rs, row) ->
                UserRole.valueOf(rs.getString("role_name")));
    }

    @Override
    public void savePollAsDoneByUser(String email) {
        String sql = "UPDATE USER SET USER.tmp_poll_passed = 1 " +
                " WHERE USER.email = :email ";
        Map<String, String> namedParameters = Collections.singletonMap("email", email);
        namedParameterJdbcTemplate.update(sql, namedParameters);
    }

    @Override
    public boolean updateLast2faNotifyDate(String email) {
        String sql = "UPDATE USER SET USER.2fa_last_notify_date =:date " +
                "WHERE USER.email = :email";
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("email", email);
            put("date", LocalDate.now());
        }};
        return namedParameterJdbcTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public LocalDate getLast2faNotifyDate(String email) {
        String sql = "SELECT USER.2fa_last_notify_date FROM USER WHERE email = :email";
        LocalDate date = null;
        try {
            date = namedParameterJdbcTemplate.queryForObject(sql, Collections.singletonMap("email", email), LocalDate.class);
        } catch (Exception e) {
            return null;
        }
        return date;
    }

    @Override
    public String getPinByEmailAndEvent(String email, NotificationMessageEventEnum event) {
        final String sql = String.format("SELECT %s_pin FROM USER " +
                " WHERE email = :email ", event.name().toLowerCase());
        return namedParameterJdbcTemplate.queryForObject(sql, Collections.singletonMap("email", email), String.class);
    }

    @Override
    public void updatePinByUserEmail(String userEmail, String pin, NotificationMessageEventEnum event) {
        String sql = String.format("UPDATE USER SET %s_pin = :pin " +
                "WHERE USER.email = :email", event.name().toLowerCase());
        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("email", userEmail);
            put("pin", pin);
        }};
        namedParameterJdbcTemplate.update(sql, namedParameters);
    }

    @Override
    public boolean manageUserFavouriteCurrencyPair(int userId, int currencyPairId, boolean delete) {
        String sql = "INSERT IGNORE INTO USER_FAVORITE_CURRENCY_PAIRS (user_id, currency_pair_id) " +
                " VALUES(:userId, :currencyPairId)";
        if (delete) {
            sql = "DELETE FROM USER_FAVORITE_CURRENCY_PAIRS WHERE user_id = :userId AND currency_pair_id = :currencyPairId";
        }
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("userId", userId);
            put("currencyPairId", currencyPairId);
        }};
        return namedParameterJdbcTemplate.update(sql, params) >= 0;
    }

}
