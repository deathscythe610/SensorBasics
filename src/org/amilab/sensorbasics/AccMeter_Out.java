package org.amilab.sensorbasics;

/**
 * THIS ACTIVITY PROVIDE THE FINAL RESULT OF THE LINEAR ACCELERATION AFTER SUBTRACTION OF GRAVITY INFLUENCE 
 * AND FILTERD USING DIFFERENT TYPE OF FILTER
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ListActivity;
import android.app.Fragment;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import org.amilab.sensorbasics.Step_Counter;


public class AccMeter_Out extends ListActivity implements SensorEventListener, Runnable {
	
	
	/**
	 * DEFINIRION PART
	 */
	//define sensor type
	private SensorManager mAccManager;
	private Sensor mAcc;
	private Sensor mMag;
	private Sensor mRot;
	
	//define array to hold gravity, linear_acc and raw acc
	private static float[] linear_acceleration = new float[]{0,0,0};
	private static float[] raw_acceleration= new float[]{0,0,0};
	private static float[] normalize_acceleration= new float[]{0,0,0};
	private static float[] global_acceleration = new float[]{0,0,0};
	
	//define array to hold Magnetic, Inclination, Rotation, Orientation matrix
	private static float[] mGeoMagnetic = new float[3];
	private static float[] mInclination = new float[16];
	private static float[] mRotation = new float[16];
	private static float[] mOrientation = new float[16];
	private static float[] Inverse_Rotation = new float[16];
	private static float[] RotationVector = new float[6];
	
	//define inclination of device in rad
	private static float Inclination_rad = 0f;
	
	//define Name for Log file columns 
	
	private String plotAccelXAxisTitle = "X_RAW";
	private String plotAccelYAxisTitle = "Y_RAW";
	private String plotAccelZAxisTitle = "Z_RAW";
	private String plotNormalizeXAxisTitle = "X_NORMALIZE";
	private String plotNormalizeYAxisTitle = "Y_NORMALIZE";
	private String plotNormalizeZAxisTitle = "Z_NORMALIZE";
	private String plotLPFAndDevXAxisTitle = "X_LINEAR";
	private String plotLPFAndDevYAxisTitle = "Y_LINEAR";
	private String plotLPFAndDevZAxisTitle = "Z_LINEAR";
	private String plotGloBalXAxisTtile = "X_GLOBAL";
	private String plotGloBalYAxisTtile = "Y_GLOBAL";
	private String plotGloBalZAxisTtile = "Z_GLOBAL";
	private String plotMagneticXAxis = "X_MAGNETIC";
	private String plotMagneticYAxis = "Y_MAGNETIC";
	private String plotMagneticZAxis = "Z_MAGNETIC";
	
	//define float variable
	public static final float EARTH_GRAVITY = 9.8f;
	
	//define LPFType 
	private AndDev_Acc_Filter AndDevFilter;
	
	private List<String> Acc_Reading = new ArrayList<String>();
	
	//define array adapter type
	ArrayAdapter<String> Acc_Adapter;
	
	//define boolean variable 
	private boolean logData = false;
	private boolean ready = false;
	private boolean Usemagnetic = true;
	
	// Output log
	private String log;
	
	
	// The generation of the log output
	private int generation = 0;
	
	// Log output time stamp
	private long logTime = 0;
	
	//Handler for runnale 
	private Handler handler;
	
	//Step Counter
	private Step_Counter counter = new Step_Counter(this);
	
	//Dstance Traveled
	private double StrideLength = 0;
	private double DistanceTraveled = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		setContentView(R.layout.fragment_acc_meter__out);
		
		AndDevFilter = new AndDev_Acc_Filter();
		
		mAccManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAcc = mAccManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMag = mAccManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mRot = mAccManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		
		mAccManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
		mAccManager.registerListener(this, mMag, SensorManager.SENSOR_DELAY_NORMAL);
		mAccManager.registerListener(this, mRot, SensorManager.SENSOR_DELAY_NORMAL);
		
		Acc_Reading.add("Global Acceleration X axis: " + global_acceleration[0]);
		Acc_Reading.add("Global Acceleration Y axis: " + global_acceleration[1]);
		Acc_Reading.add("Global Acceleration Z axis: " + global_acceleration[2]);
		Acc_Reading.add("Step walked: " + counter.stepcount);
		Acc_Reading.add("Last Stride Lenght: " + StrideLength);
		Acc_Reading.add("Total Distance Traveled: " + DistanceTraveled);
		
		Acc_Adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Acc_Reading);
		setListAdapter(Acc_Adapter);
		handler = new Handler();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		handler.post(this);
		mAccManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
		mAccManager.registerListener(this, mMag, SensorManager.SENSOR_DELAY_NORMAL);
		mAccManager.registerListener(this, mRot, SensorManager.SENSOR_DELAY_NORMAL);
	}
		
	
	@Override
	protected void onPause(){
		super.onPause();
		//counter.ResetStep();;
		mAccManager.unregisterListener(this);
		if (logData)
		{
			writeLogToFile();
		}
		handler.removeCallbacks(this);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy){
		//To do code here 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.acc_meter__out, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{

		// Log the data
		case R.id.action_log:
			startDataLog();
			return true;
		//Reset step count
		case R.id.action_resetStep:
			counter.ResetStep();
			DistanceTraveled = 0;
			StrideLength = 0;
			Acc_Reading.set(4, "Last Stride Length: " + StrideLength);
			Acc_Adapter.notifyDataSetChanged();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
			View rootView = inflater.inflate(R.layout.fragment_acc_meter__out,
					container, false);
			return rootView;
		}
	}
		
	public void onSensorChanged(SensorEvent event){
	     
		 switch(event.sensor.getType()){
		 	case Sensor.TYPE_MAGNETIC_FIELD:
				System.arraycopy(event.values, 0, AccMeter_Out.mGeoMagnetic, 0, event.values.length);	
				break;	
		 	case Sensor.TYPE_ACCELEROMETER:
				System.arraycopy(event.values, 0, AccMeter_Out.raw_acceleration, 0, event.values.length);
				if (raw_acceleration[0] != 0) 
					ready = true;
				break;
		 	case Sensor.TYPE_ROTATION_VECTOR:
		 		System.arraycopy(event.values, 0, AccMeter_Out.RotationVector, 0, event.values.length);
			default:
				return;
	     }
		 
		 if (!ready)
			 return;
		 //Raw Acceleration calculation
		 normalize_acceleration[0] = raw_acceleration[0]/EARTH_GRAVITY;
		 normalize_acceleration[1] = raw_acceleration[1]/EARTH_GRAVITY;
		 normalize_acceleration[2] = raw_acceleration[2]/EARTH_GRAVITY;
		 //Linear Acceleration calculation
		 linear_acceleration = AndDevFilter.AddSample(normalize_acceleration);
		 
		 if (Usemagnetic){
		 /**
		  * Get Rotation vector using accelerometer reading and magnetometer reading
		  * Can get error from magnetic disturbance from electronic equipment indoor
		  */
			 boolean bSuccess = SensorManager.getRotationMatrix(mRotation, mInclination, raw_acceleration, mGeoMagnetic);
			 if (bSuccess){
				 SensorManager.getOrientation(mRotation, mOrientation);
				 SensorManager.getInclination(mInclination);
			 }
		 }
		 else{
		 /**
		  * Get Rotation vector from Rotation matric, which is acquire from the TYPE_ROTATION_VECTOR
		  * Good at static, longer time means higher drift error 
		  */
			 SensorManager.getRotationMatrixFromVector(mRotation, RotationVector);
		 }
		 android.opengl.Matrix.invertM(Inverse_Rotation, 0, mRotation, 0);
		 float[] Temp = {linear_acceleration[0], linear_acceleration[1], linear_acceleration[2], 0};
		 float[] TempResult = new float[4];
		 android.opengl.Matrix.multiplyMV(TempResult, 0, Inverse_Rotation, 0, Temp, 0);
		 for (int i=0;i<global_acceleration.length;i++){
			 global_acceleration[i] = TempResult[i];
		 }
		 Acc_Reading.set(0, "Global Acceleration X axis: " + String.valueOf(global_acceleration[0]));
		 Acc_Reading.set(1, "Global Acceleration Y axis: " + String.valueOf(global_acceleration[1]));
		 Acc_Reading.set(2, "Global Acceleration Z axis: " + String.valueOf(global_acceleration[2]));
		 Acc_Adapter.notifyDataSetChanged();
	}
	
	
	
	/**
	 * The log runs on its own thread in order to keep the UI from Hanging and the output smooth
	 * Step Count also run in 100 ms frequency in order to track great change in acceleration 
	 */

	public void run()
	{
		handler.postDelayed(this, 100);
		StrideLength = counter.Stepcount(global_acceleration[2]);
		DistanceTraveled = Step_Counter.round(DistanceTraveled + StrideLength, 2); 
		Acc_Reading.set(3,  "Step counted: " + counter.stepcount);
		//Keep the result of last stride length on screen 
		if (StrideLength!=0) Acc_Reading.set(4, "Last Stride Length: " + StrideLength);
		Acc_Reading.set(5, "Total Distance Traveled: " + DistanceTraveled);
		Acc_Adapter.notifyDataSetChanged();
		logData();
	}
	
	
	/**
	 * THIS FUNCTION INITIALIZE THE WRITE LOG PROCESS AND ADD HEADERS TO THE LOG FILES
	 */
	
	private void startDataLog()
	{
		if (logData == false)
		{
			CharSequence text = "Logging Data";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(this, text, duration);
			toast.show();

			String headers = "Generation" + ",";

			headers += "Timestamp" + ",";

			headers += this.plotAccelXAxisTitle + ",";

			headers += this.plotAccelYAxisTitle + ",";

			headers += this.plotAccelZAxisTitle + ",";
			
			headers += this.plotNormalizeXAxisTitle + ",";
			
			headers += this.plotNormalizeYAxisTitle + ",";
			
			headers += this.plotNormalizeZAxisTitle + ",";

			headers += this.plotLPFAndDevXAxisTitle + ",";

			headers += this.plotLPFAndDevYAxisTitle + ",";

			headers += this.plotLPFAndDevZAxisTitle + ",";
			
			headers += this.plotGloBalXAxisTtile + ",";
			
			headers += this.plotGloBalYAxisTtile + ",";
			
			headers += this.plotGloBalZAxisTtile + ",";
			
			headers += this.plotMagneticXAxis + ",";
			
			headers += this.plotMagneticYAxis + ",";
			
			headers += this.plotMagneticZAxis + ",";
			
			headers += "Step Count" + ",";
			
			headers += "Acc Dif" + ",";
			
			log = headers + "\n";

			Acc_Reading.add("Logging in Process, Click button again to stop");
			Acc_Adapter.notifyDataSetChanged();
			logData = true;
		}
		else
		{
			if (Acc_Reading.size()==7) Acc_Reading.remove(6);
			Acc_Adapter.notifyDataSetChanged();
			logData = false;
			writeLogToFile();
		}
	}

	/**
	 * THIS FUNCTION IS CALLED EVERY 100MS TO LOG DOWN ALL INFORMATION IF LOG OPTION IS ON
	 */
	private void logData()
	{
		if (logData)
		{
			if (generation == 0)
			{
				logTime = System.currentTimeMillis();
			}

			log += System.getProperty("line.separator");
			log += generation++ + ",";
			log += System.currentTimeMillis() - logTime + ",";

			log += raw_acceleration[0] + ",";
			log += raw_acceleration[1] + ",";
			log += raw_acceleration[2] + ",";
			
			log += normalize_acceleration[0] + ",";
			log += normalize_acceleration[1] + ",";
			log += normalize_acceleration[2] + ",";

			log += linear_acceleration[0] + ",";
			log += linear_acceleration[1] + ",";
			log += linear_acceleration[2] + ",";
			
			log += global_acceleration[0] + ",";
			log += global_acceleration[1] + ",";
			log += global_acceleration[2] + ",";
			
			log+= mGeoMagnetic[0] + ",";
			log+= mGeoMagnetic[1] + ",";
			log+= mGeoMagnetic[2] + ",";
			
			log+= counter.stepcount + ",";
			log+= counter.difference + ",";
		}
	}
	
	/**
	 * WRITE LOG FILE TO /DEFAULT_MEMORY_LOCATION/Log 
	 */
	private void writeLogToFile()
	{
		Calendar c = Calendar.getInstance();
		String filename = "AccelerationFilter-" + c.get(Calendar.YEAR) + "-"
				+ c.get(Calendar.DAY_OF_WEEK_IN_MONTH) + "-"
				+ c.get(Calendar.HOUR) + "-" + c.get(Calendar.HOUR) + "-"
				+ c.get(Calendar.MINUTE) + "-" + c.get(Calendar.SECOND)
				+ ".csv";

		File dir = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "Log");
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		System.out.println(dir);
		File file = new File(dir, filename);

		FileOutputStream fos;
		byte[] data = log.getBytes();
		try
		{
			fos = new FileOutputStream(file);
			fos.write(data);
			fos.flush();
			fos.close();

			CharSequence text = "Log Saved";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(this, text, duration);
			toast.show();
		}
		catch (FileNotFoundException e)
		{
			CharSequence text = e.toString();
			System.out.println(text);
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(this, text, duration);
			toast.show();
		}
		catch (IOException e)
		{
			// handle exception
		}
		finally
		{
			// Update the MediaStore so we can view the file without rebooting.
			// Note that it appears that the ACTION_MEDIA_MOUNTED approach is
			// now blocked for non-system apps on Android 4.4.
			MediaScannerConnection.scanFile(this, new String[]
			{ "file://" + Environment.getExternalStorageDirectory() }, null,
					new MediaScannerConnection.OnScanCompletedListener()
					{
						@Override
						public void onScanCompleted(final String path,
								final Uri uri)
						{
				
						}
					});
		}
	}

}
