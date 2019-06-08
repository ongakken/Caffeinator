package services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import broadcasters.SensorRestarterBroadcastReceiver;

public class caffeineMetabolizationService extends Service {

    // Variables
    public static final String SAVE = "Caffeinator%Service%Save%File";
    public static final String SEND_TO_ACTIVITY = "Caffeinator%Save%File";

    long oldTime;
    long newTime;
    long differenceTime;

    float caffeineIntakeValue;
    float caffeineAddValue;

    Thread updateThread;
    Thread computeDifference;

    Handler updateHandler = new Handler();

    public int counter;

    private Handler computeHandler = new Handler();

    private Timer metabolizationTimer = new Timer();
    Context ctx = this;

    public caffeineMetabolizationService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public caffeineMetabolizationService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // Saving/Loading
        newTime = System.currentTimeMillis();
        loadData();
        differenceTime = (newTime - oldTime) / 1000;
        Log.i("DEBUG", "time difference: " + differenceTime + " oldTime: " + oldTime + " newTime " + newTime);
        startTimer();
        updateHandler.post(executeUpdater);
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        stoptimertask();
        saveData();
        Log.i("EXIT", "ondestroy!");
    }

    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //Check for time differences
                if(differenceTime >= 1) {
                    computeDifference = new Thread(new Runnable() {
                        public void run() {
                            for (; differenceTime >= 1; differenceTime--) {
                                //Do the same work as below, or in other words do the missing work
                                computeMetabolization();
                                Log.i("Watchdog: ", "Can't keep up! " + "Behind: " + (differenceTime));
                            }
                        }
                    });computeDifference.start();
                }
                //Do some work
                computeMetabolization();
                Log.i("in timer", "in timer ++++  "+ (counter += 1) +" Difference: " + differenceTime);
                oldTime = System.currentTimeMillis();
                saveData();
                Log.i("in timer", "DATA SAVED!  ");
                Log.i("Service", " Caffeine Count: " + caffeineIntakeValue);
            }
        };
    }


    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private Runnable executeUpdater = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            receiveData();
            sendData();
            // Repeat this every 250ms
            updateHandler.postDelayed(executeUpdater, 250);
        }
    };

    private void computeMetabolization() {
        if(caffeineIntakeValue > 0) {
            caffeineIntakeValue -= 0.1;
            caffeineIntakeValue = Math.round(caffeineIntakeValue * 100.0f) / 100.0f;
            sendData();
            Log.i("COMPUTE ", "CAFFEINE METABOLIZED | " + "METABOLIZED VALUE: " + (caffeineIntakeValue - caffeineIntakeValue + 0.1) + " CAFFEINE IN SYSTEM: | " + caffeineIntakeValue);
        }
    }

    private void loadData() {
        SharedPreferences prefsDataLoad = getSharedPreferences(SAVE, MODE_PRIVATE);
        caffeineIntakeValue = prefsDataLoad.getFloat("caffeineIntakeValue", caffeineIntakeValue);
        counter = prefsDataLoad.getInt("counter", 0);
        oldTime = prefsDataLoad.getLong("oldTime", newTime);
    }

    private void saveData() {
        SharedPreferences prefsDataSave = getSharedPreferences(SAVE, MODE_PRIVATE);
        SharedPreferences.Editor dataSave = prefsDataSave.edit();
        dataSave.putFloat("caffeineIntakeValue", caffeineIntakeValue);
        dataSave.putInt("counter", counter);
        dataSave.putLong("oldTime", oldTime);
        dataSave.apply();
    }

    private void sendData() {
        SharedPreferences prefsDataSend = getSharedPreferences(SEND_TO_ACTIVITY, MODE_PRIVATE);
        SharedPreferences.Editor dataSend = prefsDataSend.edit();

        dataSend.putFloat("caffeineMetabolizedValue", caffeineIntakeValue);
        dataSend.apply();
    }

    private void receiveData() {
        SharedPreferences prefsDataUpdate = getSharedPreferences(SEND_TO_ACTIVITY, MODE_PRIVATE);
        caffeineAddValue = prefsDataUpdate.getFloat("caffeineAddValue", caffeineAddValue);
        SharedPreferences.Editor dataDelete = prefsDataUpdate.edit();
        if (caffeineAddValue > 0) {
            caffeineIntakeValue += caffeineAddValue;
            caffeineAddValue = 0;
            dataDelete.remove("caffeineAddValue");
            dataDelete.apply();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
