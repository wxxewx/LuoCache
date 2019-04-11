package com.xiaofeiluo.luocache.strategy;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

public class SpStrategy implements IStrategy {

    private Application context;
    private static SharedPreferences sharedPreferences;
    private final Gson gson;

    public SpStrategy(Application context) {
        this.context = context;
        gson = new Gson();
    }

    @Override
    public void initCache(String cacheName) {
        sharedPreferences = context.getSharedPreferences(cacheName, Context.MODE_PRIVATE);
    }

    @Override
    public Object getCache(String key, Class type) {
        if (type == String.class) {
            return sharedPreferences.getString(key, null);
        } else if (type == long.class) {
            return sharedPreferences.getLong(key, 0);
        } else if (type == int.class) {
            return sharedPreferences.getInt(key, 0);
        } else if (type == float.class) {
            return sharedPreferences.getFloat(key, 0);
        } else if (type == boolean.class) {
            return sharedPreferences.getBoolean(key, false);
        } else {
            String cache = sharedPreferences.getString(key, null);
            if (!TextUtils.isEmpty(cache)) {
                return gson.fromJson(cache, type);
            }
        }
        return null;
    }


    @Override
    public boolean setCache(String key, Object value) {
        if (value == null) {
            return false;
        }
        boolean commit;
        if (value instanceof String) {
            commit = sharedPreferences.edit().putString(key, (String) value).commit();
        } else if (value instanceof Integer) {
            commit = sharedPreferences.edit().putInt(key, (Integer) value).commit();
        } else if (value instanceof Float) {
            commit = sharedPreferences.edit().putFloat(key, (Float) value).commit();
        } else if (value instanceof Boolean) {
            commit = sharedPreferences.edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Long) {
            commit = sharedPreferences.edit().putLong(key, (Long) value).commit();
        } else {
            String json = gson.toJson(value);
            commit = sharedPreferences.edit().putString(key, json).commit();
        }

        return commit;
    }

    @Override
    public boolean removeCache(String key) {
        return sharedPreferences.edit().remove(key).commit();
    }
}
