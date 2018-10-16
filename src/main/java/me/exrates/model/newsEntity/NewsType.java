package me.exrates.model.newsEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.enums.NewsTypeEnum;


@Getter
@Setter
@NoArgsConstructor
public class NewsType {
    private Integer id;

    private String name;

    public NewsType(NewsTypeEnum newsTypeEnum) {
        this.id = newsTypeEnum.getCode();
        this.name = newsTypeEnum.name();
    }

}
