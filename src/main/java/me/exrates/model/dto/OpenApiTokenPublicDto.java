package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.util.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class OpenApiTokenPublicDto {
    private Long id;
    private String alias;
    private Integer userId;
    private String publicKey;
    private Boolean allowTrade;
    private Boolean allowWithdraw;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime generationDate;
}
