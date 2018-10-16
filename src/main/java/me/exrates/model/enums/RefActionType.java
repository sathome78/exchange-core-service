package me.exrates.model.enums;

import java.util.Arrays;

/**
 * Created by maks on 14.04.2017.
 */
public enum RefActionType {
    init, search, toggle;

    public static RefActionType convert(String name) {
        return Arrays.stream(RefActionType.values()).filter(ot -> ot.name().equals(name)).findAny()
                .orElseThrow(RuntimeException::new);
    }
}
