package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;
import me.exrates.model.ColorScheme;

@Data
@Builder
public class PageLayoutSettingsDto {

    private int userId;
    private ColorScheme scheme;
    private boolean isLowColorEnabled;

}
