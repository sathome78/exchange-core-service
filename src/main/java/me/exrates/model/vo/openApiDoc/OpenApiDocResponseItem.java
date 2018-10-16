package me.exrates.model.vo.openApiDoc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class OpenApiDocResponseItem {
    private String name;
    private String descriptionCode;
    private List<Object> descriptionCodeArgs;

    public OpenApiDocResponseItem(String name, String descriptionCode) {
        this.name = name;
        this.descriptionCode = descriptionCode;
        this.descriptionCodeArgs = Collections.emptyList();
    }
}
