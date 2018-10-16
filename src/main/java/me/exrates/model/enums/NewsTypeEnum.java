package me.exrates.model.enums;


import lombok.extern.log4j.Log4j2;
import me.exrates.exception.UnsupportedNewsTypeIdException;
import me.exrates.exception.UnsupportedNewsTypeNameException;
import me.exrates.model.newsEntity.NewsType;

import java.util.Arrays;

@Log4j2
public enum NewsTypeEnum {
    NEWS(1),
    MATERIALS(2),
    WEBINAR(3),
    VIDEO(4),
    EVENT(5),
    FEASTDAY(6),
    PAGE(7);

    private Integer code;

    NewsTypeEnum(Integer code) {
        this.code = code;
    }

    public static NewsTypeEnum convert(NewsType newsType) {
        return convert(newsType.getId());
    }

    public static NewsTypeEnum convert(int id) {
        return Arrays.stream(NewsTypeEnum.class.getEnumConstants())
                .filter(e -> e.code == id)
                .findAny()
                .orElseThrow(() -> new UnsupportedNewsTypeIdException(String.valueOf(id)));
    }

    public static NewsTypeEnum convert(String name) {
        return Arrays.stream(NewsTypeEnum.class.getEnumConstants())
                .filter(e -> e.name().equals(name))
                .findAny()
                .orElseThrow(() -> new UnsupportedNewsTypeNameException(name));
    }

    public Integer getCode() {
        return code;
    }
}
