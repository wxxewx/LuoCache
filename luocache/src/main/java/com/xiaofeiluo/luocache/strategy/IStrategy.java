package com.xiaofeiluo.luocache.strategy;

import android.app.Application;

public interface IStrategy {

    public void initCache(Application context, String cacheName);

    public Object getCache(String key, Class type);

    public  boolean setCache(String key, Object value);

    public boolean removeCache(String key);
}
