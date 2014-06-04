package org.amilab.sensorbasics;

/**
 * THIS ACTIVITY IMPLEMENT THE FILTER TAKEN FROM ANDROID DEVELOPER WEBSITE TO FILTER ACCELEROMETER INPUT
 * INPUT: RAW ACCELEROMETER READING 
 * OUTOUT: FILETRED LINEAR ACCELEROMETER 
 */
import android.util.Log;

public class AndDev_Acc_Filter implements Acc_LPF_Interface {
	
	private boolean alphaStatic = false;

	// Constants for the low-pass filters
	private float timeConstant = 0.18f;
	private float alpha = 0.8f;
	private float dt = 0;

	// Timestamps for the low-pass filters
	private float timestamp = System.nanoTime();
	private float timestampOld = System.nanoTime();

	private int count = 0;

	// Gravity and linear accelerations components for the
	// Wikipedia low-pass filter
	private float[] gravity = new float[]{ 0, 0, 0 };
		
	private float[] linearAcceleration = new float[]{ 0, 0, 0 };

	// Raw accelerometer data
	private float[] input_acceleration = new float[]{ 0, 0, 0 };
	
	
	public float[] AddSample(float[] acceleration) {
		// Copy the input acceleration into local array 
		 System.arraycopy(acceleration, 0, this.input_acceleration, 0, acceleration.length);
		
		 if (!alphaStatic)
			{
				timestamp = System.nanoTime();

				// Find the sample period (between updates).
				// Convert from nanoseconds to seconds
				dt = 1 / (count / ((timestamp - timestampOld) / 1000000000.0f));

				alpha = timeConstant / (timeConstant + dt);
				
			}
		 count++;
		 if (count>5){
			gravity[0] = alpha * gravity[0] + (1 - alpha) * input_acceleration[0];
			gravity[1] = alpha * gravity[1] + (1 - alpha) * input_acceleration[1];
			gravity[2] = alpha * gravity[2] + (1 - alpha) * input_acceleration[2];
			
			linearAcceleration[0] = input_acceleration[0] - gravity[0];
			linearAcceleration[1] = input_acceleration[1] - gravity[1];
			linearAcceleration[2] = input_acceleration[2] - gravity[2];
		 }

		return linearAcceleration;
	}
	
	/**
	 * Indicate whether alpha is Static or not 
	 */
	public void IsStatic(boolean alphastatic){
		this.alphaStatic=alphastatic;
	}
	
	/**
	 * Indicate the value of alpha
	 */
	public void Setslpha(float alphavalue){
		this.alpha=alphavalue;
	}



}
