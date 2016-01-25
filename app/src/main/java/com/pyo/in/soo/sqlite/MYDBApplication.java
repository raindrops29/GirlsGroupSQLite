package com.pyo.in.soo.sqlite;

import android.app.Application;
import android.content.Context;

/**
 * Created by samsung on 2016-01-24.
 */
public class MYDBApplication extends Application {
  private static Context  girlsContext;

    @Override
    public void onCreate() {
        super.onCreate();
        girlsContext = this;
    }
    public static Context getGirlsContext(){
        return girlsContext;
    }
}
