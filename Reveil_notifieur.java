
package android.reveil;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;



public class Reveil_notifieur extends Service {
	private static final String TAG = "reveil";
	NotificationManager mNM;
	private int mHour;
	private int mMinute;
	public static final String PREFS_NAME = "MyPrefsFile";
	private PendingIntent mAlarmSender;
	AlarmManager am;
	
	@Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);


        showNotification();
        Log.d("DeviceListActivity", "start On Create");
        

        //!!!!!!!!!!!!!!!!!!!!!!!!!
        // Rajouter le lanceur d'alarme(mHour,mMinute)
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mHour=settings.getInt("heure",12);
        mMinute=settings.getInt("minute",34);
        
        long firstTime=next_reveil(mHour,mMinute);
        mAlarmSender = PendingIntent.getActivity(this,
                0, new Intent(this, Reveil_alarm.class), 0);
        
        am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,
                        firstTime, 5*60*1000, mAlarmSender);
	}
	
	long next_reveil(int h, int m){
        final Calendar c = Calendar.getInstance();
        int now_min = c.get(Calendar.MINUTE );
        int now_H = c.get(Calendar.HOUR_OF_DAY);
        int now_M = c.get(Calendar.MONTH);
        int now_D = c.get(Calendar.DATE);
        int now_Y = c.get(Calendar.YEAR);
        c.set(now_Y,now_M,now_D,mHour,mMinute,00) ;
        if (now_H>h || (now_H==h && now_min>m )) c.add(Calendar.DATE, 1);
        // 24 h = prb pour le passage heure d'été heure d'hiver
        //Calendar gere  le passage des mois et années.
		Log.w(TAG,"alarme prevue : " + c.toString());
        return c.getTimeInMillis();
	}
	
    @Override
    public void onDestroy() {
    	am.cancel(mAlarmSender);
    	

        mNM.cancel(R.string.alarm_service_started);


        Toast.makeText(this, R.string.alarm_service_finished, Toast.LENGTH_SHORT).show();
    }    
    
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {

        CharSequence text = getText(R.string.alarm_service_started);

        Notification notification = new Notification(R.drawable.test, text,
                System.currentTimeMillis());


        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, reveil_controleur.class), 0);


        notification.setLatestEventInfo(this, getText(R.string.titre),
                       text, contentIntent);

 
        mNM.notify(R.string.alarm_service_started, notification);
    }
    
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
