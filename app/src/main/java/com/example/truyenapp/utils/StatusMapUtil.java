package com.example.truyenapp.utils;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class StatusMapUtil {
    private static  Map<String, String> statusMap = new HashMap<>();

    static {
        statusMap.put(SystemConstant.STATUS_FULL_KEY, SystemConstant.STATUS_FULL_VALUE);
        statusMap.put(SystemConstant.STATUS_UPDATING_KEY, SystemConstant.STATUS_UPDATING_VALUE);
    }

    public static Map<String, String> getStatusMap() {
        return statusMap;
    }

    // get value by key
    public static String getValue(String key) {
        return statusMap.get(key);
    }

    // get key by value
    public static String getKey(String value) {
        for (Map.Entry<String, String> entry : statusMap.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
