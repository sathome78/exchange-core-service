package me.exrates.model.onlineTableDto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import me.exrates.util.LocalDateSerializer;

import java.time.LocalDate;

@Setter
@Getter
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

}
