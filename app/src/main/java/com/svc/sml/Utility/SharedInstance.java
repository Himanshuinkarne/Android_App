package com.svc.sml.Utility;

import android.app.Activity;

/**
 * Created by himanshu on 12/22/15.
 */
public class SharedInstance  extends Activity{
    private static SharedInstance ourInstance = new SharedInstance();

    public static SharedInstance getInstance() {
        return ourInstance;
    }
}
