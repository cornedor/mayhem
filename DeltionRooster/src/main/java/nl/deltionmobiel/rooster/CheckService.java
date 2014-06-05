package nl.deltionmobiel.rooster;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class CheckService extends Service {
    public CheckService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("DR_onCreate");

        Toast.makeText(getApplicationContext(), "Hello Service", Toast.LENGTH_LONG);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(;;) {
                    try {
                        Thread.sleep(1000 * 60 , 0);
                        new Data(getApplicationContext()).getTimes();
                        System.out.println("Tick");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
