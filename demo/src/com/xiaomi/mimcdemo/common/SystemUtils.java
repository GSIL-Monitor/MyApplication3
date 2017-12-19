package com.xiaomi.mimcdemo.common;

import android.content.Context;

public class SystemUtils {
    private static Context sContext;

    public static void initialize(Context ctx) {
        sContext = ctx.getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
