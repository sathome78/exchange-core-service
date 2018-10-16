package me.exrates.model.main;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class OpenApiToken implements RowMapper<OpenApiToken> {

    private Long id;
    private Integer userId;
    private String userEmail;
    private String alias;
    private String publicKey;
    private String privateKey;
    private Boolean allowTrade = true;
    private Boolean allowWithdraw = false;
    private LocalDateTime generationDate;

    public OpenApiToken mapRow(ResultSet rs, int i) throws SQLException {
        OpenApiToken token = new OpenApiToken();
        token.setId(rs.getLong("token_id"));
        token.setUserId(rs.getInt("user_id"));
        token.setUserEmail(rs.getString("email"));
        token.setAlias(rs.getString("alias"));
        token.setPublicKey(rs.getString("public_key"));
        token.setPrivateKey(rs.getString("private_key"));
        token.setAllowTrade(rs.getBoolean("allow_trade"));
        token.setAllowWithdraw(rs.getBoolean("allow_withdraw"));
        token.setGenerationDate(rs.getTimestamp("date_generation").toLocalDateTime());
        return token;
    }
}
