package com.heapoverflow.cli.utils;

import java.util.HashMap;
import java.util.Map;

public class SafeMap extends HashMap<String, Object> {
    public SafeMap(Map<String, Object> map) {
        super(map);
    }

    public Object getValue(String key) {
        if (!this.containsKey(key)) {
            throw new IllegalArgumentException("Key '" + key + "' not found in response");
        }
        return this.get(key);
    }

    public String getString(String key) {
        return (String) getValue(key);
    }

    public Integer getInt(String key) {
        return (Integer) getValue(key);
    }

    public Double getDouble(String key) {
        return (Double) getValue(key);
    }

    public Boolean getBoolean(String key) {
        return (Boolean) getValue(key);
    }
}
