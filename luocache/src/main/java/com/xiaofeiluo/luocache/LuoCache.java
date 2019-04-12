package com.xiaofeiluo.luocache;

import android.app.Application;

import com.xiaofeiluo.luocache.strategy.IStrategy;
import com.xiaofeiluo.luocache.strategy.SpStrategy;


public class LuoCache {


    private static Application context;

    private LuoCache() {

    }

    private LuoCache(Application application) {

    }

    public static void init(Application application) {
        context = application;
    }

    public static IStrategy getStrategy(String cacheName) {
        if (context == null) {
            throw new NullPointerException("must init the LuoCache");
        }
        SpStrategy spStrategy = new SpStrategy(context);
        spStrategy.initCache(cacheName);
        return spStrategy;
    }


}
