package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import me.exrates.util.LocalDateSerializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NewsSummaryDto {
    private Integer id;
    private String title;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    private String brief;
    private String resource;
    private List<String> variants = new ArrayList<>();
    private Boolean isActive;

}
