package me.exrates.model.onlineTableDto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.util.LocalDateSerializer;

import java.time.LocalDate;

public class NewsDto extends OnlineTableDto {
    private Integer id;
    private String title;
    private String brief;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    private String resource;
    private String variant;
    private String ref;

    public NewsDto() {
        this.needRefresh = true;
    }

    public NewsDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /*getters setters*/

    public boolean isNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

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

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }
}
