package com.xiaofeiluo.luocache.strategy;

public interface IStrategy {

    public void initCache(String cacheName);

    public Object getCache(String key, Class type);

    public  boolean setCache(String key, Object value);

    public boolean removeCache(String key);
}
