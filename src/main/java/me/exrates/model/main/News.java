package me.exrates.model.main;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import me.exrates.util.LocalDateSerializer;

import java.time.LocalDate;

@Data
public class News implements Cloneable {
    private Integer id;
    private String title;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    private String brief;
    private String resource;
    private String content;
    private String newsVariant;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isActive;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
