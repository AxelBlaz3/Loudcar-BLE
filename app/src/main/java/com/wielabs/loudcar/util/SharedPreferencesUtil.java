package com.wielabs.loudcar.util;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class SharedPreferencesUtil {
    private final String PREF_LOUD_CAR = "com.wielabs.LOUD_CAR";
    private final String KEY_FONT_FAMILY = "com.wielabs.loudcar.FONT_FAMILY";
    private final String KEY_SPEED = "com.wielabs.loudcar.SPEED";
    private final String KEY_REVERSE_TEXT = "com.wielabs.loudcar.REVERSE_TEXT";
    private final String KEY_DIRECTION = "com.wielabs.loudcar.DIRECTION";
    private final String KEY_FLASH = "com.wielabs.loudcar.FLASH";
    private final String KEY_FREQUENCY = "com.wielabs.loudcar.FREQUENCY";
    private final String KEY_PRIORITY = "com.wielabs.loudcar.PRIORITY";
    private final String KEY_IMPLANT = "com.wielabs.loudcar.IMPLANT";
    private final String KEY_EDGING = "com.wielabs.loudcar.EDGING";
    private final String KEY_ANIMATION = "com.wielabs.loudcar.ANIMATION";
    private final String KEY_IS_FIRST_LAUNCH = "come.wielabs.loudcar.KEY_IS_FIRST_LAUNCH";
    private SharedPreferences sharedPreferences;

    @Inject
    SharedPreferencesUtil(@ApplicationContext Context context) {
        if (sharedPreferences == null)
            sharedPreferences = context.getSharedPreferences(PREF_LOUD_CAR, Context.MODE_PRIVATE);
    }

    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean(KEY_IS_FIRST_LAUNCH, true);
    }

    public void saveIsFirstLaunch(boolean isFirstLaunch) {
        sharedPreferences.edit().putBoolean(KEY_IS_FIRST_LAUNCH, isFirstLaunch).apply();
    }

    public int getFontFamilySelection() {
        return sharedPreferences.getInt(KEY_FONT_FAMILY, 1);
    }

    public int getSpeedSelection() {
        return sharedPreferences.getInt(KEY_SPEED, 1);
    }

    public boolean getReverseTextToggle() {
        return sharedPreferences.getBoolean(KEY_REVERSE_TEXT, false);
    }

    public int getDirectionSelection() {
        return sharedPreferences.getInt(KEY_DIRECTION, 1);
    }

    public boolean getFlash() {
        return sharedPreferences.getBoolean(KEY_FLASH, false);
    }

    public int getFrequency() {
        return sharedPreferences.getInt(KEY_FREQUENCY, 1);
    }

    public int getPriority() {
        return sharedPreferences.getInt(KEY_PRIORITY, 1);
    }

    public int getImplant() {
        return sharedPreferences.getInt(KEY_IMPLANT, 1);
    }

    public boolean getEdging() {
        return sharedPreferences.getBoolean(KEY_EDGING, false);
    }

    public int getAnimation() {
        return sharedPreferences.getInt(KEY_ANIMATION, 1);
    }

    public void setFlash(boolean newValue) {
        sharedPreferences.edit().putBoolean(KEY_FLASH, newValue).apply();
    }

    public void setFrequency(int frequency) {
        sharedPreferences.edit().putInt(KEY_FREQUENCY, frequency).apply();
    }

    public void setPriority(int priority) {
        sharedPreferences.edit().putInt(KEY_PRIORITY, priority).apply();
    }

    public void setImplant(int implant) {
        sharedPreferences.edit().putInt(KEY_IMPLANT, implant).apply();
    }

    public void setEdging(boolean newValue) {
        sharedPreferences.edit().putBoolean(KEY_EDGING, newValue).apply();
    }

    public void setAnimation(int selection) {
        sharedPreferences.edit().putInt(KEY_ANIMATION, selection).apply();
    }

    public void setFontFamily(int selection) {
        sharedPreferences.edit().putInt(KEY_FONT_FAMILY, selection).apply();
    }

    public void setSpeed(int selection) {
        sharedPreferences.edit().putInt(KEY_SPEED, selection).apply();
    }

    public void setReverseText(boolean isSet) {
        sharedPreferences.edit().putBoolean(KEY_REVERSE_TEXT, isSet).apply();
    }

    public void setDirection(int selection) {
        sharedPreferences.edit().putInt(KEY_DIRECTION, selection).apply();
    }
}
