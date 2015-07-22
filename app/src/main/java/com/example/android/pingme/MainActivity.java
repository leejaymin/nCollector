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

package com.example.android.pingme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends Activity {

    private Intent mServiceIntent;
    private Spinner spinner;
    private int notificationPriority;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.priority_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();
                if (parent.getItemAtPosition(position) == "MAX")
                    notificationPriority = NotificationCompat.PRIORITY_MIN;
                else if (parent.getItemAtPosition(position) == "HIGH")
                    notificationPriority = NotificationCompat.PRIORITY_HIGH;
                else if (parent.getItemAtPosition(position) == "DEFAULT")
                    notificationPriority = NotificationCompat.PRIORITY_DEFAULT;
                else if (parent.getItemAtPosition(position) == "LOW")
                    notificationPriority = NotificationCompat.PRIORITY_LOW;
                else if (parent.getItemAtPosition(position) == "MIN")
                    notificationPriority = NotificationCompat.PRIORITY_MIN;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Creates an explicit Intent to start the service that constructs and
        // issues the notification.
        mServiceIntent = new Intent(getApplicationContext(), PingService.class);
    }

    /*
     * Gets the values the user entered and adds them to the intent that will be
     * used to launch the IntentService that runs the timer and issues the
     * notification.
     */
    public void onPingClick(View v) {
        int seconds;

        // Gets the reminder text the user entered.
        EditText msgText = (EditText) findViewById(R.id.edit_reminder);
        String message = msgText.getText().toString();
        //String priority = spinner.
        mServiceIntent.putExtra(CommonConstants.NOTIFICATION_PRIORITY, notificationPriority);
        mServiceIntent.putExtra(CommonConstants.EXTRA_MESSAGE, message);
        mServiceIntent.setAction(CommonConstants.ACTION_PING);
        Toast.makeText(this, R.string.timer_start, Toast.LENGTH_SHORT).show();

        // The number of seconds the timer should run.
        EditText editText = (EditText)findViewById(R.id.edit_seconds);
        String input = editText.getText().toString();

        if(input == null || input.trim().equals("")){
            // If user didn't enter a value, sets to default.
            seconds = R.string.seconds_default;
        } else {
            seconds = Integer.parseInt(input);
        }
        int milliseconds = (seconds * 1000);
        mServiceIntent.putExtra(CommonConstants.EXTRA_TIMER, milliseconds);
        // Launches IntentService "PingService" to set timer.
        startService(mServiceIntent);
    }
}
