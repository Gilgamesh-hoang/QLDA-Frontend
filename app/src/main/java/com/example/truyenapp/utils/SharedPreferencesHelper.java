package com.example.truyenapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Helper class for managing SharedPreferences.
 */
public class SharedPreferencesHelper {

    /**
     * Saves an object to SharedPreferences.
     *
     * @param context The context used to access SharedPreferences.
     * @param object  The object to be saved.
     * @param prefKey The key to be associated with the object.
     * @param <T>     The type of the object to be saved.
     */
    public static <T> void savePreference(Context context, T object, String prefKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SystemConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object); // Convert object to JSON string
        editor.putString(prefKey, json); // Use prefKey to store the object with a custom key
        editor.apply();
    }


    /**
     * Deletes a preference with a specified key from SharedPreferences.
     *
     * @param context The context used to access SharedPreferences.
     * @param prefKey The key of the preference to be deleted.
     */
    public static void deletePreference(Context context, String prefKey ) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SystemConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(prefKey);
        editor.apply();
    }


    /**
     * Retrieves an object from SharedPreferences.
     *
     * @param context The context used to access SharedPreferences.
     * @param prefKey The key associated with the object.
     * @param type    The class type of the object to be retrieved.
     * @param <T>     The type of the object to be retrieved.
     * @return The retrieved object.
     */
    public static <T> T getObject(Context context, String prefKey, Class<T> type) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SystemConstant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(prefKey, null);
        return gson.fromJson(json, type);
    }
}
