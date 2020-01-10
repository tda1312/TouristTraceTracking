package vn.edu.usth.touristtracetracking.ForegroundService;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.usth.touristtracetracking.R;
import vn.edu.usth.touristtracetracking.RetrofitHandler;
import vn.edu.usth.touristtracetracking.History.SendHistoryResponse;
import vn.edu.usth.touristtracetracking.User;
import vn.edu.usth.touristtracetracking.storage.SharePrefManager;

public class MyBackgroundService extends Service {
    private static final String CHANNEL_ID = "my_channel";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = "vn.edu.usth.touristtracetracking" +
            ".started_from_notification";

    public final IBinder mBinder = new LocalBinder();
    private static final long UPDATE_INTERVAL_IN_MIL = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MIL = UPDATE_INTERVAL_IN_MIL / 2;
    private static final int NOTIFICATION_ID = 123456789;
    private boolean mChangingConfiguration = false;
    private NotificationManager mNotificationManager;

    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Handler mServiceHandler;
    private Location mLocation;

    List<LocationData> historyList = new ArrayList<LocationData>();

    public MyBackgroundService() {

    }

    @Override
    public void onCreate() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread("USTH");
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false);
        if(startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    public void removeLocationUpdates() {
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            Common.setRequestStringLocationUpdates(this, false);
            stopSelf();
        }catch (SecurityException e){
            Common.setRequestStringLocationUpdates(this, true);
            Toast.makeText(this, "Lost location permission. Could not remove update. " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void getLastLocation() {
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Toast.makeText(MyBackgroundService.this, "Failed to getLastLocation()", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (SecurityException e) {
            Toast.makeText(this, "Lost location permission! " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MIL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MIL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void onNewLocation(Location lastLocation) {
        mLocation = lastLocation;
        SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String arrival_time = datetime.format(mLocation.getTime());
        LocationData newLocation = new LocationData(Double.toString(mLocation.getLatitude()),
                Double.toString(mLocation.getLongitude()), arrival_time);

        // send to other activity with Event Bus
        EventBus.getDefault().postSticky(newLocation);

        // add new location to list
        historyList.add(newLocation);
        // send the list to server
        if(historyList.size() == 3) {
            sendHistory(historyList);
            historyList.clear();
        }
        // Update notification content if running in foreground service
        if(serviceRunningInForeground(this)) {
            mNotificationManager.notify(NOTIFICATION_ID, getNotification());
        }
    }

    private void sendHistory(List<LocationData> historyList){
        SharePrefManager sharePrefManager = SharePrefManager.getInstance(MyBackgroundService.this);
        User user = sharePrefManager.getUser();

        Call<SendHistoryResponse> call = RetrofitHandler.getInstance()
                .getApi()
                .addHistory(user.getId(),"Bearer " + sharePrefManager.getToken(), historyList);

        call.enqueue(new Callback<SendHistoryResponse>() {
            @Override
            public void onResponse(Call<SendHistoryResponse> call, Response<SendHistoryResponse> response) {
                if(response.body() != null && response.body().isSuccess()) {
                    Log.i("ABCDE", response.code() + "hehe");
                } else {
                    Log.i("ABCDE", response.body().isSuccess() + " yahoooooooo! " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SendHistoryResponse> call, Throwable t) {
                Log.i("ABCDE", t.getMessage());
            }
        });
    }

    private Notification getNotification() {
        Intent intent = new Intent(this, MyBackgroundService.class);
        String text = Common.getLocationText(mLocation);

        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MapsActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_my_location_black_24dp)
                .addAction(R.drawable.ic_launch_black_24dp, "Launch", activityPendingIntent)
                .addAction(R.drawable.ic_cancel_black_24dp, "Remove", servicePendingIntent)
                .setContentText(text)
                .setContentTitle(Common.getLocationTitle(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0L});

        // set the channel id for android O
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }
        return builder.build();
    }

    private boolean serviceRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service:manager.getRunningServices(Integer.MAX_VALUE)) {
            if(getClass().getName().equals(service.service.getClassName()))
                if(service.foreground)
                    return true;
        }
        return false;
    }

    public void requestLocationUpdates() {
        Common.setRequestStringLocationUpdates(this, true);
        startService(new Intent(getApplicationContext(), MyBackgroundService.class));

        try{
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        } catch (SecurityException e){
            Toast.makeText(this, "Lost location permission. Could not request it. " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public class LocalBinder extends Binder {
        MyBackgroundService getService() {
            return MyBackgroundService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {

        if(!mChangingConfiguration && Common.requestStringLocationUpdates(this)) {
            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacks(null);
        super.onDestroy();
    }
}
