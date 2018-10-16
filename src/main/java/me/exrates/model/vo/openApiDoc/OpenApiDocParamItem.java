package me.exrates.model.vo.openApiDoc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class OpenApiDocParamItem {
    private String name;
    private String descriptionCode;
    private List<Object> descriptionCodeArgs;
    private boolean isOptional;

    public OpenApiDocParamItem(String name, String descriptionCode, boolean isOptional) {
        this.name = name;
        this.descriptionCode = descriptionCode;
        this.descriptionCodeArgs = Collections.emptyList();
        this.isOptional = isOptional;
    }
}
