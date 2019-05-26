package services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class caffeineMatabolizationService extends Service {
    public caffeineMatabolizationService() {

    }
    private Timer metabolizationTimer = new Timer();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Caffeine Matabolization Service: I'm alive!");
        metabolizeCaffeine();
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // WE DO NOT WANT TO USE THIS YET
        return null; // TO BE GARBAGE COLLECTED
    }

    private void metabolizeCaffeine() {
        metabolizationTimer.schedule(new TimerTask() {
            public void run() {
                System.out.println("Metabolization Timer: I'm alive!");

            }
        }, 1000, 1000);
    }
}
