package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.util.LocalDateSerializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NewsSummaryDto {
    private Integer id;
    private String title;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    private String brief;
    private String resource;
    private List<String> variants = new ArrayList<>();
    private Boolean isActive;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public List<String> getVariants() {
        return variants;
    }

    public void setVariants(List<String> variants) {
        this.variants = variants;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
