package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by Maks on 13.12.2017.
 */
@Data
@Builder(toBuilder = true)
public class AlertDto {

    private String text;
    @NotNull
    private String alertType;
    @NotNull
    private boolean enabled;
    private LocalDateTime eventStart;
    private Integer lenghtOfWorks;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer minutes;
    @JsonIgnore
    private LocalDateTime launchDateTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long timeRemainSeconds;

    @Tolerate
    public AlertDto() {
    }
}
