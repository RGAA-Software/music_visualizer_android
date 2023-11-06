package com.shark.dynamics.basic.tips;

import android.content.Context;
import android.widget.Toast;

public class Tips {

    public static void tips(Context context, String msg) {
        if (context == null) {
            return;
        }
        try {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
