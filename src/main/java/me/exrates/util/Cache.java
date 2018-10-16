package me.exrates.util;

import me.exrates.model.main.CacheData;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Valk on 20.06.2016.
 */
public class Cache {
    public static boolean checkCache(CacheData cacheData, Object result) {
        HttpServletRequest request = cacheData.getRequest();
        String cacheKey = cacheData.getCacheKey();
        Boolean forceUpdate = cacheData.getForceUpdate();
        Integer resultHash = result.hashCode();
        Map<String, Integer> cacheHashMap = (Map) request.getSession().getAttribute("cacheHashMap");
        if (cacheHashMap == null) {
            cacheHashMap = new HashMap<>();
            cacheHashMap.put(cacheKey, 0);
            request.getSession().setAttribute("cacheHashMap", cacheHashMap);
        }
        Integer currentHash = cacheHashMap.get(cacheKey);
        try {
            if (!forceUpdate && resultHash.equals(currentHash)) {
                return true;
            } else {
                cacheHashMap.put(cacheKey, resultHash);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
