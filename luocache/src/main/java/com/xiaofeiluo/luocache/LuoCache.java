package com.xiaofeiluo.luocache;

import android.app.Application;
import android.content.Context;

import com.xiaofeiluo.luocache.strategy.IStrategy;
import com.xiaofeiluo.luocache.strategy.SpStrategy;

public class LuoCache {

    public static IStrategy strategy;

    public static void init(Application context) {
        strategy= new SpStrategy(context);
    }


}
