package android.reveil;



import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


public class reveil_controleur extends Activity {
    private static final String TAG = "reveil";
	private TextView mTimeDisplay;

	private Button mPickTime;

	private int mHour;
	private int mMinute;

	static final int TIME_DIALOG_ID = 0;

	public static final String PREFS_NAME = "MyPrefsFile";


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.page1);
        
        // capture our View elements
        mTimeDisplay = (TextView) findViewById(R.id.timer);
        mPickTime = (Button) findViewById(R.id.setTimer);

        // add a click listener to the button
        mPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        mHour=settings.getInt("heure",12);
        mMinute=settings.getInt("minute",34);
        
        

        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.Launch);
        button.setOnClickListener(mStartAlarmListener);
        button = (Button)findViewById(R.id.Stop_Alarm);
        button.setOnClickListener(mStopAlarmListener);
        
        // display the current date
        updateDisplay();
    }
    
    private OnClickListener mStartAlarmListener = new OnClickListener() {
        public void onClick(View v) {

            
            // On click start notification
        	Log.d(TAG,"On click listen");
            Intent i = new Intent(reveil_controleur.this,Reveil_notifieur.class);
            Log.d(TAG,"On click starting");
            startService(i);
            Log.d(TAG,"On click starting");
            
            
            // Tell the user about what we did.
            Toast.makeText(reveil_controleur.this, R.string.repeating_scheduled,
                    Toast.LENGTH_LONG).show();
        }
    };

    
    
    private OnClickListener mStopAlarmListener = new OnClickListener() {
        public void onClick(View v) {
            // And cancel the alarm.
        	//!!!!!!!! Prb de stop
        	Intent intent = new Intent(reveil_controleur.this,Reveil_notifieur.class); 
        	stopService(intent);

            // Tell the user about what we did.
            Toast.makeText(reveil_controleur.this, R.string.repeating_unscheduled,
                    Toast.LENGTH_LONG).show();

        }
    };
    
 // updates the time we display in the TextView
    private void updateDisplay() {
        mTimeDisplay.setText(
            new StringBuilder()
                    .append(pad(mHour)).append(":")
                    .append(pad(mMinute)));
       
       
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    
 // the callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
        new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
                // We need an Editor object to make preference changes.
                // All objects are from android.context.Context
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("heure", mHour);
                editor.putInt("minute", mMinute);

                // Commit the edits!
                editor.commit();

                updateDisplay();
            }
        };
        
        
    
        @Override
        protected Dialog onCreateDialog(int id) {
            switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, true);
            }
            return null;
        }
}