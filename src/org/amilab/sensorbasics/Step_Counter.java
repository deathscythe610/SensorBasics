package org.amilab.sensorbasics;

import android.content.Context;
import android.widget.Toast;


public class Step_Counter {
	private boolean IsCheckMode =false;
	private boolean IsStep = false;
	private float positive_uthreshold = 0.8f;
	private float negative_uthreshold = -0.1f;
	private float positive_lthreshold = 0.1f;
	private float negative_lthreshold  = -0.8f;
	private float prev_data = 0f;
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
	private boolean CheckStep(float measured_data){
		//Ignore the acceleration data of current check if previous check mark a step
		if (IsStep){
			IsStep = false;
		}
		else {
		difference = measured_data-prev_data;
		if(prev_data!=0){
			//If System has not detect a up rise, check if there is an up rise in data
			if (!IsCheckMode){
				if ((difference>positive_lthreshold) && (difference<positive_uthreshold)){
					IsCheckMode = true;
				}	
			}
			//If the System is already in CheckMode, check if next change is a down turn or not
			else{
				if ((difference>negative_lthreshold) && (difference<negative_uthreshold)){
					IsStep=true;
				}
				//Toast.makeText(mContext, String.valueOf(difference), Toast.LENGTH_SHORT).show();
				//Get out of CheckMode to wait for another uprise
				IsCheckMode = false;
			}
		}
		}
		prev_data = measured_data;
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
		StrideLenght = Math.sqrt(Math.sqrt((DataGap).doubleValue()));  
	}
	
}