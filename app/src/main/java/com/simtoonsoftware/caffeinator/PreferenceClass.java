package com.simtoonsoftware.caffeinator;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceClass {
    public static final String PREFERENCE_NAME = "PREFERENCE%DATA";
    private final SharedPreferences sharedpreferences;

    public PreferenceClass(Context context) {
        sharedpreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public int getInt() {
        int count = sharedpreferences.getInt("count", 0);
        return count;
    }

    public void setInt(int count) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("count", count);
        editor.commit();
    }

    public void clearInt() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("count");
        editor.commit();
    }

    public float getFloat() {
        float Float = sharedpreferences.getFloat("float", 0);
        return Float;
    }

    public void setFloat(float Float) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putFloat("float", Float);
        editor.commit();
    }

    public void clearFloat() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("float");
        editor.commit();
    }

    public String getString() {
        String data = sharedpreferences.getString("data", "Empty");
        return data;
    }

    public void setString(int count) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("data", "data");
        editor.commit();
    }

    public void clearString() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("data");
        editor.commit();
    }

}
