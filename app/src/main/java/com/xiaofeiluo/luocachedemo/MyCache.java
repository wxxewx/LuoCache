package com.xiaofeiluo.luocachedemo;

import android.app.Application;

import com.xiaofeiluo.luocache.strategy.IStrategy;

public class MyCache implements IStrategy {
    @Override
    public void initCache(Application context, String cacheName) {

    }

    @Override
    public Object getCache(String key, Class type) {
        return null;
    }

    @Override
    public boolean setCache(String key, Object value) {
        return false;
    }

    @Override
    public boolean removeCache(String key) {
        return false;
    }
}
