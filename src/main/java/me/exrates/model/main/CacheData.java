package me.exrates.model.main;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.servlet.http.HttpServletRequest;

@Getter
@ToString
public class CacheData {
    private HttpServletRequest request;
    private String cacheKey;
    private Boolean forceUpdate;

    public CacheData(HttpServletRequest request, String cacheKey, Boolean forceUpdate) {
        this.request = request;
        this.cacheKey = cacheKey;
        this.forceUpdate = forceUpdate;
    }
}
