/*
 * Copyright 2019 SMD Technologies, s.r.o. All rights reserved.
 */

package services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class broadcastMetabolizationServiceRestarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(broadcastMetabolizationServiceRestarter.class.getSimpleName(), "Metabolization service stopped! Restarting..");
        context.startService(new Intent(context, caffeineMetabolizationService.class));
    }
}
