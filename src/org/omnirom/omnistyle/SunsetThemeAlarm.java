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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SunsetThemeAlarm extends BroadcastReceiver {
    private static final String PREFS_NAME = "SharedPrefs";
    private List<String> mAppOverlays;
    private List<String> mOverlayCompose = new ArrayList<>();
    private OverlayUtils mOverlayUtils;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        mOverlayUtils = new OverlayUtils(context);

        mAppOverlays = new ArrayList();
        mAppOverlays.addAll(Arrays.asList(mOverlayUtils.getAvailableThemes(OverlayUtils.OMNI_APP_THEME_PREFIX)));

        mOverlayCompose.add(OverlayUtils.KEY_THEMES_DISABLED);
        mOverlayCompose.add(OverlayUtils.KEY_THEMES_DISABLED);
        mOverlayCompose.add(OverlayUtils.KEY_THEMES_DISABLED);
        mOverlayCompose.set(0, prefs.getString("AccentNight", ""));
        mOverlayCompose.set(1, prefs.getString("PrimaryNight", ""));
        mOverlayCompose.set(2, prefs.getString("NotificationNight", ""));

        List<String> allOverlays = new ArrayList<String>();
        allOverlays.addAll(mOverlayCompose);

        if (!mOverlayCompose.get(0).equals(OverlayUtils.KEY_THEMES_DISABLED)
                || !mOverlayCompose.get(1).equals(OverlayUtils.KEY_THEMES_DISABLED)) {
            allOverlays.addAll(mAppOverlays);
        }
        mOverlayUtils.enableThemeList(allOverlays);

        Log.d("ThemeAlarm", "Alarm fired");
    }
}
