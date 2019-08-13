package services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import broadcasters.SensorRestarterBroadcastReceiver;
import notifications.*;

public class caffeineMetabolizationService extends Service {

    // Variables
    public static final String SAVE = "Caffeinator%Service%Save%File";
    public static final String SEND_TO_ACTIVITY = "Caffeinator%Share%File";

    long oldTime;
    long newTime;
    long differenceTime;

    float caffeineToCalculate;
    float caffeineIntakeMetabolized;
    float caffeineBloodValue;
    float caffeineAddValue;
    float caffeineToAbsorb;

    float caffeineAbsorptionCoefficient;
    float caffeineHalfLifeMetabolizationCoefficient;
    float caffeineZeroMetabolizationCoefficient;

    int notificationDelay;
    int counter;
    int randomNotification;
    int halflifeDuration = 21600;
    int caffeineToZeroDuration = 64800;
    int caffeineAbsorptionDuration = 2700;

    private boolean tooMuchCaffeineBool = false;
    private boolean appearedBefore1;
    private boolean appearedBefore2;
    private boolean appearedBefore3;
    private boolean appearedBefore4;
    private boolean appearedBefore5;

    Thread computeDifference;

    Handler updateHandler = new Handler();
    Handler calculationHandler = new Handler();

    Context ctx = this;

    public caffeineMetabolizationService(Context applicationContext) {
        super();
        Log.i("Service", "here I am!");
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
        calculationHandler.post(executeCalculationHandler);
        updateHandler.post(executeUpdater);
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent(ctx, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        saveData();
        Log.i("EXIT", "onDestroy!");
    }

    private Runnable executeCalculationHandler = new Runnable() {
        @Override
        public void run() {
            //Check for time differences and for correction overshoot
            if (differenceTime >= 1) {
                computeDifference = new Thread(new Runnable() {
                    public void run() {
                        for (; differenceTime >= 1; differenceTime--) {
                            //Do the same work as below, or in other words do the missing work
                            computeMetabolization();
                            Log.i("Watchdog: ", "Can't keep up! " + "Behind: " + (differenceTime));
                        }
                    }
                });
                computeDifference.start();
            }
            //Do some work
            checkForNewIntake();
            computeMetabolization();
            Log.i("DEBUG", "in timer ++++  " + (counter += 1) + " Difference: " + differenceTime);
            oldTime = System.currentTimeMillis();
            saveData();
            notificationDelay -= 1;
            Log.i("Watchdog", "DATA SAVED!  ");
            Log.i("Service", " Caffeine Count: " + caffeineBloodValue);

            // Repeat this every x ms
            calculationHandler.postDelayed(executeCalculationHandler, 1000);
        }
    };

    private void computeMetabolization() {
        if(caffeineToAbsorb > 0) {
            Log.i("DEBUG", "CAFFEINE TO ABSORB RECEIVED: " + caffeineToAbsorb);
            float caffeineBloodValueBefore = caffeineBloodValue;
            caffeineBloodValue += caffeineAbsorptionCoefficient;
            float caffeineBloodValueAbsorbed = (caffeineBloodValue - caffeineBloodValueBefore);
            Log.i("COMPUTE ", "CAFFEINE ABSORBED | " + "ABSORBED VALUE: " + (caffeineBloodValueAbsorbed) + " CAFFEINE IN BLOOD: | " + caffeineBloodValue + " | CAFFEINE IN BODY: " + caffeineToAbsorb);
            //caffeineToAbsorb = Math.round(caffeineToAbsorb * 100.0f) / 100.0f;
            caffeineToAbsorb -= caffeineBloodValueAbsorbed;
            sendData();
            Log.i("DEBUG", "ABSORBED CAFFEINE SENT: " + caffeineToAbsorb);
        }
        if(caffeineBloodValue > 0) {
            float caffeineIntakeBefore = caffeineBloodValue;
            caffeineBloodValue -= caffeineHalfLifeMetabolizationCoefficient;
            //caffeineBloodValue = Math.round(caffeineBloodValue * 100.0f) / 100.0f;
            caffeineIntakeMetabolized -= (caffeineBloodValue - caffeineIntakeBefore);
            sendData();
            Log.i("COMPUTE ", "CAFFEINE METABOLIZED | " + "METABOLIZED VALUE: " + (caffeineBloodValue - caffeineIntakeBefore) + " CAFFEINE IN SYSTEM: | " + caffeineBloodValue);
        }
        /* ***WIP***
        if(caffeineIntakeMetabolized > 0) {
            float caffeineIntakeMetabolizedBefore = caffeineIntakeMetabolized;
            caffeineIntakeMetabolized -= caffeineZeroMetabolizationCoefficient;
            caffeineIntakeMetabolized = Math.round(caffeineIntakeMetabolized * 100.0f) / 100.0f;
            caffeineBloodValue -= (caffeineIntakeMetabolized - caffeineIntakeMetabolizedBefore);
            sendData();
            Log.i("COMPUTE ", "CAFFEINE ZERO METABOLIZED | " + "METABOLIZED ZERO VALUE: " + (caffeineIntakeMetabolized - caffeineIntakeMetabolizedBefore) + " CAFFEINE IN SYSTEM: | " + caffeineBloodValue);
        }  ***WIP***
        */
    }

    private float calculateCaffeineAbsorptionCoefficient() {
        float caffeineToBeAbsorbed = caffeineToCalculate; //Sounds stupid but it won't work otherwise
        float caffeineAbsorptionPerSecond = caffeineToBeAbsorbed / caffeineAbsorptionDuration;
        Log.i("COMPUTE", "CAFFEINE ABSORPTION COEFFICIENT FOUND: " + caffeineAbsorptionPerSecond);
        return caffeineAbsorptionPerSecond;
    }

    private float calculateCaffeineHalfLifeMetabolizationCoefficient() {
        float caffeineIntakeHalved = caffeineToCalculate / 2;
        float caffeinePerSecond = caffeineIntakeHalved / halflifeDuration;
        Log.i("COMPUTE", "CAFFEINE COEFFICIENT FOUND: " + caffeinePerSecond);
        return caffeinePerSecond;
    }
/*   ***WIP***
    private float calculateCaffeineZeroMetabolizationCoefficient() {
        float caffeineIntakeToZero = caffeineIntakeMetabolized / caffeineToZeroDuration;
        Log.i("COMPUTE", "CAFFEINE ZERO COEFFICIENT FOUND: " + caffeineIntakeToZero);
        return caffeineIntakeToZero;
    } ***WIP***
*/
    private void checkForNewIntake() {
        if (caffeineToCalculate > 0) {
            caffeineAbsorptionCoefficient = calculateCaffeineAbsorptionCoefficient();
            caffeineHalfLifeMetabolizationCoefficient = calculateCaffeineHalfLifeMetabolizationCoefficient();
            //caffeineZeroMetabolizationCoefficient = calculateCaffeineZeroMetabolizationCoefficient(); ***WIP***
            caffeineToCalculate = 0;
        }
    }

    private void loadData() {
        SharedPreferences prefsDataLoad = getSharedPreferences(SAVE, MODE_PRIVATE);
        caffeineBloodValue = prefsDataLoad.getFloat("caffeineBloodValue", caffeineBloodValue);
        caffeineToAbsorb = prefsDataLoad.getFloat("caffeineToAbsorb", caffeineToAbsorb);
        counter = prefsDataLoad.getInt("counter", 0);
        oldTime = prefsDataLoad.getLong("oldTime", newTime);
        notificationDelay = prefsDataLoad.getInt("notificationDelay", notificationDelay);
        tooMuchCaffeineBool = prefsDataLoad.getBoolean("tooMuchCaffeineBool", tooMuchCaffeineBool);
    }

    private void saveData() {
        SharedPreferences prefsDataSave = getSharedPreferences(SAVE, MODE_PRIVATE);
        SharedPreferences.Editor dataSave = prefsDataSave.edit();
        dataSave.putFloat("caffeineBloodValue", caffeineBloodValue);
        dataSave.putFloat("caffeineToAbsorb", caffeineToAbsorb);
        dataSave.putInt("counter", counter);
        dataSave.putInt("notificationDelay", notificationDelay);
        dataSave.putLong("oldTime", oldTime);
        dataSave.putBoolean("tooMuchCaffeineBool", tooMuchCaffeineBool);
        dataSave.apply();
    }

    private void sendData() {
        SharedPreferences prefsDataSend = getSharedPreferences(SEND_TO_ACTIVITY, MODE_PRIVATE);
        SharedPreferences.Editor dataSend = prefsDataSend.edit();

        dataSend.putFloat("caffeineBloodValue", caffeineBloodValue);
        dataSend.putFloat("caffeineIntakeValue", caffeineToAbsorb);
        dataSend.apply();
    }

    private void receiveData() {
        SharedPreferences prefsDataUpdate = getSharedPreferences(SEND_TO_ACTIVITY, MODE_PRIVATE);
        caffeineAddValue = prefsDataUpdate.getFloat("caffeineAddValue", caffeineAddValue);
        SharedPreferences.Editor dataDelete = prefsDataUpdate.edit();
        if (caffeineAddValue > 0) {
            caffeineToAbsorb += caffeineAddValue;
            caffeineToCalculate += caffeineAddValue;
            caffeineAddValue = 0;
            dataDelete.remove("caffeineAddValue");
            dataDelete.apply();
        }
    }

    private void checkNotify() {
        // Random notification generator:
        randomNotification = (int) (Math.random()*4);
        // This displays the tooMuchCaffeine notification.
        if (caffeineBloodValue >= 400 && tooMuchCaffeineBool == false) {
            tooMuchCaffeine.notify(ctx, String.valueOf(caffeineBloodValue));
            tooMuchCaffeineBool = true;
        } else if(caffeineBloodValue <= 399.9 && tooMuchCaffeineBool == true) {
            tooMuchCaffeineBool = false;
        }
        //This displays the healthAdvice notifications.
        if (notificationDelay == 0 && randomNotification == 0 && !appearedBefore1) {
            healthAdvice1.notify(ctx);
            appearedBefore1 = true;
            notificationDelay += 21600;
        } else if (notificationDelay == 0 && randomNotification == 1 && !appearedBefore2) {
            healthAdvice2.notify(ctx);
            appearedBefore2 = true;
            notificationDelay += 21600;
        } else if (notificationDelay == 0 && randomNotification == 2 && !appearedBefore3) {
            healthAdvice3.notify(ctx);
            appearedBefore3 = true;
            notificationDelay += 21600;
        } else if (notificationDelay == 0 && randomNotification == 3 && !appearedBefore4) {
            healthAdvice4.notify(ctx);
            appearedBefore4 = true;
            notificationDelay += 21600;
        } else if (notificationDelay == 0 && randomNotification == 4 && !appearedBefore5) {
            healthAdvice5.notify(ctx);
            appearedBefore5 = true;
            notificationDelay += 21600;
        }
        // Check if all the notifications appeared previously and if true, reset them.
        if (appearedBefore1 == true && appearedBefore2 == true && appearedBefore3 == true && appearedBefore4 == true && appearedBefore5 == true) {
            appearedBefore1 = false;
            appearedBefore2 = false;
            appearedBefore3 = false;
            appearedBefore4 = false;
            appearedBefore5 = false;
        }
    }

    private Runnable executeUpdater = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            receiveData();
            checkNotify();
            sendData();
            // Repeat this every 250ms
            updateHandler.postDelayed(executeUpdater, 250);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
