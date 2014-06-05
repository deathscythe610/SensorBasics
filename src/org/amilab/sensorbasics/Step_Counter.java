package org.amilab.sensorbasics;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.content.Context;
import android.widget.Toast;


public class Step_Counter {
	public enum STATES{
		IDLE, MAX_DETECT, MIN_DETECT, AFTER_MAX_DETECT, AFTER_MIN_DETECT, MID_STEP_DELAY
	}
	private STATES state = STATES.IDLE;
	
	private int azHistory_Size = 2;
	private boolean IsCheckMode =false;
	private boolean IsStep = false;
	private float positive_threshold = 0.5f;
	private float negative_threshold = -0.5f;
	private int maxThresHoldPass = 0;
	private int minThresHoldPass = 0;
	private int step_delay = 150;			//Minimum time after second half step is detected to return to IDLE state
	private int step_wait_max = 400;		//Maximum time wait after first half step is detected
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
				if (((triggertime - this.laststeptime)>this.step_delay) && (az>this.positive_threshold)){
					this.state = STATES.MAX_DETECT;
				}
				break;
			case MAX_DETECT:								//If az continue to rise then go to next state else return to IDLE
				if (az>this.positive_threshold){
					this.state = STATES.AFTER_MAX_DETECT;
				}
				else{
					this.state = STATES.IDLE;
				}
				break;
			case AFTER_MAX_DETECT:					//If az decrease below azAvg then mark a valid first half step else wait for peak
				if (az>=this.azAvg()){
					//wait for peak
				}
				else{
					this.maxThresHoldPass++;
					this.laststeptime = triggertime;
					this.state = STATES.MID_STEP_DELAY;
				}
				break;
			case MID_STEP_DELAY:
				if((az<this.negative_threshold){
					this.state = STATES.MIN_DETECT;
				}
				else if ((triggertime-this.laststeptime)>this.step_wait_max){
					this.state = STATES.IDLE;
					
				}
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
	
	public void pushazHistory(float val){
		for (int i=this.azHistory_Size-1; i>0; i--){
			this.azHistory[i] = this.azHistory[i-1];
		}
		this.azHistory[0] = val;
	}
	public float azAvg(){
		float sum = 0;
		for (int i=0; i<this.azHistory_Size; i++){
			sum += this.azHistory[i];
		}
		return sum/this.azHistory_Size;
		}
	}