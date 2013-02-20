package android.reveil;

import java.io.FileDescriptor;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Reveil_alarm extends Activity{
    MediaPlayer mp;
    AudioManager am;
    Button b1,b2;
    private static final String TAG = "reveil_alarm";
	
    


	//Le gestionnaire des capteurs
	private SensorManager sensorManager;
	//Notre capteur 
	private Sensor sensor;

	private float mSensorX;
	private float mSensorY;
	private float mSensorZ;
	private float mPasseBasZ=0.0f;
	private static float mSensorB=0.0f;
	private static float g_SEUIL=9.81f*9.81f*2.0f*2.0f;
	// 0 = initial, -1  = bas, 1 = haut
	private static int UpDown=0;
	private static final float UD_SEUIL= 4.0f;
	private static final float ALPHA=0.90f;
	
	
	
	
	private final SensorEventListener sensorListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			//if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)  return;

			// Lire  l'accelero
			mSensorX = event.values[0];
			mSensorY = event.values[1];
			mSensorZ = event.values[2];
			float tmp = mSensorX*mSensorX+mSensorY*mSensorY+mSensorZ*mSensorZ;
			if (mSensorB<tmp){
				mSensorB=tmp;
				if (mSensorB>g_SEUIL) onShake(mSensorB);			
			}
			
			//test  Flip
			// + un passe bas
			mPasseBasZ=ALPHA*mPasseBasZ+(1-ALPHA)*mSensorZ;
			if(UpDown==0){
				if(mPasseBasZ>0.0f) UpDown=1;
				else UpDown=-1;
			}
			if(UpDown<1 && mPasseBasZ>UD_SEUIL){
				UpDown=1;
				onFlip();
			}
			else{
				if(UpDown>-1 && mPasseBasZ<-UD_SEUIL){
					UpDown=-1;
					onFlip();	
				}
			}					

	
		}	
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

	};
	
    public void onShake(float force) {
        Toast.makeText(this, "Phone shaked : " + force, 1000).show();
        //tmp=0.0f;
    }
    
    public void onFlip() {
        Toast.makeText(this, "Phone fliped :  " , 1000).show();
    	mp.stop();

    	finish();
        
    }
	
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "debout !!!!!!!!!!",
                Toast.LENGTH_LONG).show();
        setContentView(R.layout.main);
	
        
        b1= (Button)findViewById(R.id.snooze);
        b1.setOnClickListener(mSnooze);
        b2 = (Button)findViewById(R.id.stop2);
        b2.setOnClickListener(mStop);
        
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor =  sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        
        mp = new MediaPlayer();
        playSong();
        
        		
    }

	@Override
    protected void onResume(){
    	super.onResume();
    	//Lier les évènements de l'accelerometre au listener
    	sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_UI);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		//Retirer le lien entre le listener et les évènements de la boussole numérique
		sensorManager.unregisterListener(sensorListener);
		if(mp.isPlaying()) {
			mp.stop();
		}		

	}   
    
    public void playSong() {
    	if(mp == null) mp = new MediaPlayer();
    	try {
    		if(mp.isPlaying()) {
    				mp.stop();
    		}
    		FileDescriptor fd =  getResources().openRawResourceFd(R.raw.blue).getFileDescriptor();
    		
    		
    		
    		mp.reset();
    		mp.setDataSource(fd);
    		mp.setDataSource("http://broadcast.infomaniak.ch/rsr-couleur3-high.mp3");
    		
    		mp.setAudioStreamType(AudioManager.STREAM_ALARM );
    		mp.prepare();
    		mp.start();
    		Log.d(TAG,"is snooze cliked ??");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

	private OnClickListener mSnooze = new OnClickListener() {
        public void onClick(View v) {
        	Log.d(TAG,"is snooze cliked ??");
        	mp.stop();
        	finish();
        }
    };
    
    private OnClickListener mStop = new OnClickListener() {
        public void onClick(View v) {
        	Log.d(TAG,"is stop cliked ??");
        	mp.stop();
        	Intent intent = new Intent(Reveil_alarm.this,Reveil_notifieur.class); 
        	stopService(intent);

            
            Toast.makeText(Reveil_alarm.this, R.string.repeating_unscheduled,
                    Toast.LENGTH_LONG).show();
            finish();
        }
    };
}
