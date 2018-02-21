package com.rockon999.android.leanbacklauncher.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by rockon999 on 2/18/18.
 */

public class StorageUtils {
    public static boolean getBoolean(Context c, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        return prefs.getBoolean(key, true);
    }

    public static void saveBoolean(Context c, String key, boolean b) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, b);
        editor.apply();
    }

    public static List<String> getStringList(Context c, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        String s = prefs.getString(key, "[]");
        try {
            JSONArray array = new JSONArray(s);

            List<String> list = new ArrayList<>();

            for (int x = 0, length = array.length(); x < length; x++) {
                list.add(array.getString(x));
            }

            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveStringList(Context c, String key, List<String> list) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray array = new JSONArray();
        for (String s : list) {
            array.put(s);
        }
        editor.putString(key, array.toString());
        editor.apply();
    }
}
