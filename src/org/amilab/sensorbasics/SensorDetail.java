package org.amilab.sensorbasics;

/**
 * THIS ACTIVITY LIST OUT THE DETAIL OF THE CHOSEN DEVICE, WHICH INCLUDE:
 * NAME, MAX RANGE, MIN DELAY, VENDOR, RESOLUTION, TYPE 
 */
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.os.Build;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class SensorDetail extends ListActivity {
	private SensorManager mSensorManager;
	private int sensornumber;
	private int sensorType;
	private String sensorTypeName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_sensor_detail);
		//Get sensor name from the SensorActivity
		Intent intent = getIntent();
		String sensorname = intent.getStringExtra(SensorActivity.EXTRA_MESSAGE);
		//Toast.makeText(this, sensorname, Toast.LENGTH_LONG).show();
		
		//Compare sensor name with list of sensor
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> listSensor = mSensorManager.getSensorList(Sensor.TYPE_ALL);
		for(int i=0;i<listSensor.size();i++){
			if ((listSensor.get(i).getName().equals(sensorname))){
				sensornumber = i;
			}
		}
		//Toast.makeText(this, String.valueOf(sensornumber), Toast.LENGTH_LONG).show();
		sensorType = listSensor.get(sensornumber).getType();
		sensorTypeName = sensorGetType(sensorType);
		//Add Detail to a list string
		List<String> listsensorDetail = new ArrayList<String>();
		listsensorDetail.add("Sensor name: " + sensorname);
		listsensorDetail.add("Sensor vendor: " + listSensor.get(sensornumber).getVendor());
		listsensorDetail.add("Sensor Maximum Range: " + String.valueOf(listSensor.get(sensornumber).getMaximumRange()));
		listsensorDetail.add("Sensor Min Delay: " + String.valueOf(listSensor.get(sensornumber).getMinDelay()));
		listsensorDetail.add("Sensor resolution: " + String.valueOf(listSensor.get(sensornumber).getResolution()));
		listsensorDetail.add("Sensor power: " + String.valueOf(listSensor.get(sensornumber).getPower()));
		listsensorDetail.add("Sensor Type: " + sensorTypeName);	
		
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listsensorDetail));
	}

	public String sensorGetType(int sensorNo){
		String sensorName;
		switch(sensorNo){
		case 1:
			sensorName = "TYPE_ACCELEROMETER";
			break;
		case 13:
			sensorName = "TYPE_AMBIENT_TEMPERATURE";
			break;
		case 15:
			sensorName = "TYPE_GAME_ROTATION_VECTOR";
			break;
		case 20:
			sensorName = "TYPE_GEOMAGNETIC_ROTATION_VECTOR";
			break;
		case 9:
			sensorName = "TYPE_GRAVITY";
			break;
		case 4:
			sensorName= "TYPE_GYROSCOPE";
			break;
		case 16:
			sensorName = "TYPE_GYROSCOPE_UNCALIBRATED";
			break;
		case 5:
			sensorName = "TYPE_LIGHT";
			break;
		case 10:
			sensorName = "TYPE_LINEAR_ACCELERATION";
			break;
		case 2:
			sensorName = "TYPE_MAGNETIC_FIELD";
			break;
		case 14:
			sensorName = "TYPE_MAGNETIC_FIELD_UNCALIBRATED";
			break;
		case 11:
			sensorName = "TYPE_ROTATION_VECTOR";
			break;
		case 17:
			sensorName = "TYPE_SIGNIFICANT_MOTION";
			break;
		case 3:
			sensorName = "TYPE_ORIENTATION";
			break;
		case 6	:
			sensorName = "TYPE_PRESSURE";
			break;
		case 8:
			sensorName = "TYPE_PROXIMITY";
			break;
		case 12:
			sensorName = "TYPE_RELATIVE_HUMIDITY";
			break;
		default: 
			sensorName = "TYPE_UNKNOWN";	
		}
	return sensorName;	
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_sensor_detail,
					container, false);
			return rootView;
		}
	}

}
