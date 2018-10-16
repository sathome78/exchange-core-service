package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SurveyDto {
    private Integer id;
    private String description;
    private String token;
    private String json;
    private List<SurveyItem> items = new ArrayList<>();

    @Getter
    @Setter
    @AllArgsConstructor
    public static class SurveyItem {
        private String name;
        private String title;

    }
}
