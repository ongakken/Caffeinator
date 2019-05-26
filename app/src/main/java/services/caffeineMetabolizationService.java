package services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import broadcasters.SensorRestarterBroadcastReceiver;

import static android.support.constraint.Constraints.TAG;

public class caffeineMetabolizationService extends Service {

    // Variables
    int count = 0;
    long oldTime;
    long newTime;
    long differenceTime;
    public int counter;
    private Timer metabolizationTimer = new Timer();
    Context ctx = this;
    public static final String SAVE = "Caffeinator%Service%Save%File";

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

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //Check for time differences
                if(differenceTime >= 1) {
                    for (; differenceTime >= 1; differenceTime--) {
                        //Do the same work as below, or in other words do the missing work
                        Log.i("in timer", "in timer ++++  " + (counter += 1));
                    }
                }
                //Do some work
                Log.i("in timer", "in timer ++++  "+ (counter += 1) +" Difference: " + differenceTime);
                oldTime = System.currentTimeMillis();
                saveData();
                Log.i("in timer", "DATA SAVED!  ");
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void loadData() {
        SharedPreferences prefsDataLoad = getSharedPreferences(SAVE, MODE_PRIVATE);
        counter = prefsDataLoad.getInt("counter", 0);
        oldTime = prefsDataLoad.getLong("oldTime", newTime);
    }

    public void saveData() {
        SharedPreferences prefsDataSave = getSharedPreferences(SAVE, MODE_PRIVATE);
        SharedPreferences.Editor dataSave = prefsDataSave.edit();
        dataSave.putInt("counter", counter);
        dataSave.putLong("oldTime", oldTime);
        dataSave.apply();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
