package com.xiaofeiluo.luocache.strategy;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedHashMap;

import in.co.ophio.secure.core.KeyStoreKeyGenerator;
import in.co.ophio.secure.core.ObscuredPreferencesBuilder;


public class SpStrategy implements IStrategy {

    private Application context;
    private SharedPreferences sharedPreferences;
    private final Gson gson;


    private LinkedHashMap<String, Object> cacheMap;

    public SpStrategy() {
        cacheMap = new LinkedHashMap<>();
        gson = new Gson();
    }

    @Override
    public void initCache(Application context, String cacheName) {
        this.context = context;
        String key = getKey(context, cacheName);
        sharedPreferences = new ObscuredPreferencesBuilder().setApplication(context).obfuscateValue(true)
                .obfuscateKey(true).setSharePrefFileName(cacheName).setSecret(key)
                .createSharedPrefs();
    }

    @Override
    public Object getCache(String key, Class type) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        Object value = cacheMap.get(key);
        if (value != null) {
            return value;
        }
        if (type == String.class) {
            value = sharedPreferences.getString(key, null);
        } else if (type == long.class) {
            value = sharedPreferences.getLong(key, 0);
        } else if (type == int.class) {
            value = sharedPreferences.getInt(key, 0);
        } else if (type == float.class) {
            value = sharedPreferences.getFloat(key, 0);
        } else if (type == boolean.class) {
            value = sharedPreferences.getBoolean(key, false);
        } else {
            String cacheJson = sharedPreferences.getString(key, null);
            if (!TextUtils.isEmpty(cacheJson)) {
                value = gson.fromJson(cacheJson, type);
            }
        }
        if (value != null) {
            cacheMap.put(key, value);
        }
        return value;
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
        //持久化成功之后再更新到内存中
        if (commit) {
            cacheMap.put(key, value);
        }

        return commit;
    }

    @Override
    public boolean removeCache(String key) {
        boolean commit = sharedPreferences.edit().remove(key).commit();
        if (commit) {
            cacheMap.remove(key);
        }
        return commit;
    }


    private String getKey(Application application, String cacheName) {
        try {
            return KeyStoreKeyGenerator
                    .get(application, cacheName)
                    .loadOrGenerateKeys();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
