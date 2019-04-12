package com.xiaofeiluo.luocache;

import android.app.Application;

import com.xiaofeiluo.luocache.strategy.IStrategy;
import com.xiaofeiluo.luocache.strategy.SpStrategy;


public class LuoCache {


    private static Application context;

    private static Class<? extends IStrategy> strategy;

    private LuoCache() {

    }

    public static void init(Application application) {
        context = application;
    }


    public static void init(Application application, Class<? extends IStrategy> customStrategy) {
        context = application;
        strategy = customStrategy;
    }

    public static IStrategy getStrategy(String cacheName) {
        if (context == null) {
            throw new NullPointerException("must init the LuoCache");
        }

        if (strategy == null) {
            SpStrategy spStrategy = new SpStrategy();
            spStrategy.initCache(context,cacheName);
            return spStrategy;
        }
        //用户自定义了缓存策略
        else {
            try {
                IStrategy iStrategy = strategy.newInstance();
                iStrategy.initCache(context,cacheName);
                return iStrategy;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


}
