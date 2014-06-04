package org.amilab.sensorbasics;
/**
 * THIS LIST OUT THE AVAILABLE SENSORS IN THE DEVICE THAT RUN THIS ACTIVITY 
 * EACH OF THE DEVICE CAN BE CLICKED ON TO ACESS THE DETAILS 
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
//import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
//import android.os.Build;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class SensorActivity extends ListActivity {
	
	public static final String EXTRA_MESSAGE = "org.amilab.sensorbasics";
	private EditText mUserText;
	private SensorManager mSensorManager;
	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_sensor);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> listSensor = mSensorManager.getSensorList(Sensor.TYPE_ALL);
		
		List<String> listSensorType = new ArrayList<String>();
		for(int i=0;i<listSensor.size();i++){
			listSensorType.add(listSensor.get(i).getName());
		}
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listSensorType));
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_sensor,
					container, false);
			return rootView;
		}
	}

	
    @Override
    @SuppressWarnings("unchecked")
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Intent intent = new Intent(this, SensorDetail.class);
    	intent.putExtra(EXTRA_MESSAGE, l.getItemAtPosition(position).toString());
    	startActivity(intent);
    }

}
