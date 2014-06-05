package org.amilab.sensorbasics;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.content.Context;
import android.widget.Toast;


public class Step_Counter {
	public enum STATES{
		IDLE, MAX_DETECT, MIN_DETECT, AFTER_MAX_DETECT, AFTER_MIN_DETECT, MIN_STEP_DELAY
	}
	private STATES state = STATES.IDLE;
	
	private int azHistory_Size = 2;
	private boolean IsCheckMode =false;
	private boolean IsStep = false;
	private float positive_uthreshold = 0.8f;
	private float negative_uthreshold = -0.1f;
	private float positive_lthreshold = 0.1f;
	private float negative_lthreshold  = -0.8f;
	private int step_delay = 150;			//Minimum time after second half step is detected to return to IDLE state
	private int step_wait_max = 400;		//Maximum time wait after first half step is detected
	private int step_wait_min = 100;		//Minimum time wait after first half step is detected 
	private float lastMaximum = 0;
	private float lastMinimum = 0;
	private int laststeptime = 0;
	
	private float azHistory[] = new float[azHistory_Size];
	private Float DataGap = Float.valueOf(0f);
	private Context mContext;
	private double StrideLenght ;
	
	public int stepcount = 0;
	public float difference = 0;
	
	
	
	public Step_Counter(Context context){
		mContext = context; 
	}
	/**
	 * This function check if the user step or not
	 * A step is detected if there is a positive change of Z global acceleration between lower and upper threshold
	 * Followed by a negative change in Z global acceleration between lower and upper threshold
	 * If any of those change happen separately, the system will see as noise and do not that instance as a step  
	 * @param measured_data
	 * @return IsStep
	 */
	private boolean CheckStep(float az, int triggertime){
		if (az>lastMaximum)
			lastMaximum =az;
		if (az<lastMinimum)
			lastMinimum = az;
		switch(this.state){
			case IDLE:
				this.lastMaximum = 0;
				this.lastMinimum = 0;
				if ((triggertime - this.laststeptime)>step
		}
		return IsStep;
	}
	//This function invoke CheckStep to see if there is a step made by user, if yes add the step number by 1 
	public double Stepcount(float measured_data){
		if (CheckStep(measured_data)){
			GetStrideLenght();
			stepcount++;
			return StrideLenght;
		}
	return 0;
	}
	
	//This reset the step count to 0 
	public void ResetStep(){
		stepcount = 0;
		IsStep = false;
		IsCheckMode = false;
	}
	public void GetStrideLenght(){
		DataGap = Math.abs(difference);
		StrideLenght = round(Math.sqrt(Math.sqrt((DataGap).doubleValue())), 2);  
	}
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
}