/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.eslab.passme;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * PingService creates a notification that includes 2 buttons: one to snooze the
 * notification, and one to dismiss it.
 */
public class PingService extends IntentService {

    private NotificationManager mNotificationManager;
    private String mMessage;
    private int mMillis;
    private int mPrority;
    NotificationCompat.Builder builder;
    PowerManager powerManager;
    WakeLock wakeLock;

    public PingService() {

        // The super call is required. The background thread that IntentService
        // starts is labeled with the string argument you pass.
        super("com.example.android.pingme");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        // The reminder message the user set.
        mMessage = intent.getStringExtra(CommonConstants.EXTRA_MESSAGE);
        // The timer duration the user set. The default is 10 seconds.
        mMillis = intent.getIntExtra(CommonConstants.EXTRA_TIMER,
                CommonConstants.DEFAULT_TIMER_DURATION);
        // The priority of notification the user set.
        mPrority = intent.getIntExtra(CommonConstants.NOTIFICATION_PRIORITY,
                NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager nm = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // The number of notifications is extracted from user set.
        int mNumberOfNotifications = intent.getIntExtra(CommonConstants.NUMBER_OF_NOTIFICATIONS,1);
        Log.d(CommonConstants.DEBUG_TAG,""+mNumberOfNotifications);


        String action = intent.getAction();
        // This section handles the 3 possible actions:
        // ping, snooze, and dismiss.
        if(action.equals(CommonConstants.ACTION_PING)) {
            issueNotification(intent, mMessage, mNumberOfNotifications);
        } else if (action.equals(CommonConstants.ACTION_SNOOZE)) {
            nm.cancel(CommonConstants.NOTIFICATION_ID);
            Log.d(CommonConstants.DEBUG_TAG, getString(R.string.snoozing));
            // Sets a snooze-specific "done snoozing" message.
            issueNotification(intent, getString(R.string.done_snoozing), mNumberOfNotifications);

        } else if (action.equals(CommonConstants.ACTION_DISMISS)) {
            nm.cancel(CommonConstants.NOTIFICATION_ID);
        }

    }

    private void issueNotification(Intent intent, String msg, int offsetNotificationID) {
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        // Sets up the Snooze and Dismiss action buttons that will appear in the
        // expanded view of the notification.
        Intent dismissIntent = new Intent(this, PingService.class);
        dismissIntent.setAction(CommonConstants.ACTION_DISMISS);
        PendingIntent piDismiss = PendingIntent.getService(this, 0, dismissIntent, 0);

        Intent snoozeIntent = new Intent(this, PingService.class);
        snoozeIntent.setAction(CommonConstants.ACTION_SNOOZE);
        PendingIntent piSnooze = PendingIntent.getService(this, 0, snoozeIntent, 0);

        Intent connectedIntent = new Intent(this, PingService.class);
        connectedIntent.setAction(CommonConstants.ACTION_CONNECTED);
        PendingIntent piConnected = PendingIntent.getService(this, 0, connectedIntent, 0);

        // Constructs the Builder object.
        builder =
                new NotificationCompat.Builder(this)
                .setPriority(mPrority)
                .setSmallIcon(R.drawable.ic_stat_notification)
                .setContentTitle(getString(R.string.notification))
                .setContentText(getString(R.string.ping))
                .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
                /*
                 * Sets the big view "big text" style and supplies the
                 * text (the user's reminder message) that will be displayed
                 * in the detail area of the expanded notification.
                 * These calls are ignored by the support library for
                 * pre-4.1 devices.
                 */
                .setStyle(new NotificationCompat.BigTextStyle()
                     .bigText(msg))
                .addAction (R.drawable.ic_stat_dismiss,
                        getString(R.string.dismiss), piDismiss)
                .addAction (R.drawable.ic_stat_snooze,
                        getString(R.string.snooze), piSnooze)
                .addAction (R.drawable.ic_stat_notification,
                        "ConnectedService", piConnected);

        /*
         * Clicking the notification itself displays ResultActivity, which provides
         * UI for snoozing or dismissing the notification.
         * This is available through either the normal view or big view.
         */
         Intent resultIntent = new Intent(this, ResultActivity.class);
         resultIntent.putExtra(CommonConstants.EXTRA_MESSAGE, msg);
         resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

         // Because clicking the notification opens a new ("special") activity, there's
         // no need to create an artificial back stack.
         PendingIntent resultPendingIntent =
                 PendingIntent.getActivity(
                 this,
                 0,
                 resultIntent,
                 PendingIntent.FLAG_UPDATE_CURRENT
         );

         builder.setContentIntent(resultPendingIntent);
         startTimer(mMillis,offsetNotificationID);
    }

    private void issueNotification(NotificationCompat.Builder builder,int offsetNotificationID) {
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // Including the notification ID allows you to update the notification later on.
        mNotificationManager.notify(CommonConstants.NOTIFICATION_ID+offsetNotificationID, builder.build());
    }

 // Starts the timer according to the number of seconds the user specified.
    private void startTimer(int millis, int offsetNotificationID) {
        Log.d(CommonConstants.DEBUG_TAG, getString(R.string.timer_start));
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WatingForNotiGeneration");
        wakeLock.acquire();
        Log.d(CommonConstants.DEBUG_TAG, getString(R.string.wake_acquire));
        try {
            Thread.sleep(millis);

        } catch (InterruptedException e) {
            Log.d(CommonConstants.DEBUG_TAG, getString(R.string.sleep_error));
        }
        Log.d(CommonConstants.DEBUG_TAG, getString(R.string.timer_finished));
        wakeLock.release();
        Log.d(CommonConstants.DEBUG_TAG, getString(R.string.wake_release));
        for (int i = 1; i <= offsetNotificationID; i++) {
            issueNotification(builder, offsetNotificationID+i);
        }
    }
}
