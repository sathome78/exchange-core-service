package me.exrates.controller.ngcontroller.dao.impl;

import me.exrates.controller.ngcontroller.dao.UserDocVerificationDao;
import me.exrates.controller.ngcontroller.model.UserDocVerificationDto;
import me.exrates.controller.ngcontroller.model.enums.VerificationDocumentType;
import me.exrates.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDocVerificationDaoImpl implements UserDocVerificationDao {

    private static final Logger logger = LogManager.getLogger(UserDocVerificationDaoImpl.class);

    private static final String TABLE_NAME = "USER_VERIFICATION_DOCS";
    private static final String USER_ID_COL = "user_id";
    private static final String DOC_TYPE_COL = "document_type";
    private static final String IMAGE_ENCODED = "image_encoded";

    private static final String USER_ID_KEY = "userId";

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;

    @Override
    public UserDocVerificationDto save(UserDocVerificationDto verificationDto) {
        String sql = "INSERT INTO " + TABLE_NAME
                + " (" + String.join(", ", USER_ID_COL, DOC_TYPE_COL, IMAGE_ENCODED) + ")"
                + " VALUES (:userId, :docType, :encoded)"
                + " ON DUPLICATE KEY UPDATE " + IMAGE_ENCODED + " = :encoded";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(USER_ID_KEY, verificationDto.getUserId())
                .addValue("docType", verificationDto.getDocumentType().toString())
                .addValue("encoded", verificationDto.getEncoded());
        boolean result = namedParameterJdbcTemplate.update(sql, params) > 0;
        return result ? verificationDto : null;
    }

    @Override
    public boolean delete(UserDocVerificationDto verificationDto) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE  " + USER_ID_COL + " =:userId";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(USER_ID_KEY, verificationDto.getUserId());
        return namedParameterJdbcTemplate.update(sql, parameters) > 0;
    }

    @Override
    public UserDocVerificationDto findByUserIdAndDocumentType(Integer userId, VerificationDocumentType documentType) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " +  USER_ID_COL + "=:userId AND " + DOC_TYPE_COL + "=:docType;";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(USER_ID_KEY, userId)
                .addValue("docType", documentType.toString());
        try {
            return slaveJdbcTemplate.queryForObject(sql, params, getRowMapper());
        } catch (EmptyResultDataAccessException exc) {
            logger.debug("No doc found for userId: {} and type: {}", userId, documentType);
            return null;
        }
    }

    @Override
    public List<UserDocVerificationDto> findAllByUser(User user) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " +  USER_ID_COL + "=:userId;";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(USER_ID_KEY, user.getId());
        try {
            return slaveJdbcTemplate.query(sql, params, getRowMapper());
        } catch (Exception exc) {
            logger.debug("No doc found for user: {}", user.getEmail());
            return null;
        }
    }

    private RowMapper<UserDocVerificationDto> getRowMapper() {
        return (rs, rowNum) -> UserDocVerificationDto
                .builder()
                .userId(rs.getInt(USER_ID_COL))
                .documentType(VerificationDocumentType.of(rs.getString(DOC_TYPE_COL)))
                .encoded(blobConverter(rs.getBlob(IMAGE_ENCODED)))
                .build();
    }

    private static String blobConverter(java.sql.Blob blob) {
        try {
            return new String(blob.getBytes(1L, (int) blob.length()));
        } catch (SQLException e) {
            return "";
        }
    }
}
