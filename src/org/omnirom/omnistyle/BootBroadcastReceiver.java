/*
 *  Copyright (C) 2019 The Project Pearl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.omnirom.omnistyle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.app.AlarmManager;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.content.ComponentName;

import java.util.Calendar;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String PREFS_NAME = "SharedPrefs";
    private List<String> mAppOverlays;
    private List<String> mOverlayCompose = new ArrayList<>();
    private OverlayUtils mOverlayUtils;

    @Override
    public void onReceive(Context context, Intent intent) {
        final SharedPreferences PrefsReader = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int clockDayHour = PrefsReader.getInt("clockDayHour", 6);
        int clockNightHour = PrefsReader.getInt("clockNightHour", 19);
        int clockDayMin = PrefsReader.getInt("clockDayMin", 0);
        int clockNightMin = PrefsReader.getInt("clockNightMin", 0);
        if (PrefsReader.getBoolean("switch", false)) {
            mOverlayUtils = new OverlayUtils(context);
            mAppOverlays = new ArrayList();
            mAppOverlays.addAll(Arrays.asList(mOverlayUtils.getAvailableThemes(OverlayUtils.OMNI_APP_THEME_PREFIX)));


            long time = System.currentTimeMillis();
            long timeexpnight = getTime(clockNightHour, clockNightMin);
            long timeexpday = getTime(clockDayHour, clockDayMin);
            mOverlayCompose.add(OverlayUtils.KEY_THEMES_DISABLED);
            mOverlayCompose.add(OverlayUtils.KEY_THEMES_DISABLED);
            mOverlayCompose.add(OverlayUtils.KEY_THEMES_DISABLED);
            if(time >= timeexpday && time < timeexpnight) {
                mOverlayCompose.set(0, PrefsReader.getString("AccentDay", ""));
                mOverlayCompose.set(1, PrefsReader.getString("PrimaryDay", ""));
                mOverlayCompose.set(2, PrefsReader.getString("NotificationDay", ""));
            } else {
                mOverlayCompose.set(0, PrefsReader.getString("AccentNight", ""));
                mOverlayCompose.set(1, PrefsReader.getString("PrimaryNight", ""));
                mOverlayCompose.set(2, PrefsReader.getString("NotificationNight", ""));
            }
            List<String> allOverlays = new ArrayList<String>();
            allOverlays.addAll(mOverlayCompose);

            if (!mOverlayCompose.get(0).equals(OverlayUtils.KEY_THEMES_DISABLED)
                    || !mOverlayCompose.get(1).equals(OverlayUtils.KEY_THEMES_DISABLED)) {
                allOverlays.addAll(mAppOverlays);
            }
            mOverlayUtils.enableThemeList(allOverlays);
            setSunriseThemeAlarm(context, clockDayHour, clockDayMin);
            setSunsetThemeAlarm(context, clockNightHour, clockNightMin);
            Log.d("ThemeAlarmConst", ""+clockDayHour+" "+clockDayMin+" "+clockNightHour+" "+clockNightMin);
        }
    }

    public long getTime(int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        return calendar.getTimeInMillis();
    }

    public void setSunriseThemeAlarm(Context mContext, int hour, int min) {

        long time = getTime(hour,min);
        //Avoid setting alarm in past
        if (time < System.currentTimeMillis()) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            time = calendar.getTimeInMillis();
        }

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        ComponentName receiver = new ComponentName(mContext, SunriseThemeAlarm.class);
        PackageManager pm = mContext.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent i = new Intent(mContext, SunriseThemeAlarm.class);

        PendingIntent pi = PendingIntent.getBroadcast(mContext, 1, i, PendingIntent.FLAG_CANCEL_CURRENT);

        am.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pi);
    }

    public void setSunsetThemeAlarm(Context mContext, int hour, int min) {

        long time = getTime(hour,min);

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        ComponentName receiver = new ComponentName(mContext, SunsetThemeAlarm.class);
        PackageManager pm = mContext.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent j = new Intent(mContext, SunsetThemeAlarm.class);

        PendingIntent pi = PendingIntent.getBroadcast(mContext, 2, j, PendingIntent.FLAG_CANCEL_CURRENT);

        am.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pi);
    }
}
