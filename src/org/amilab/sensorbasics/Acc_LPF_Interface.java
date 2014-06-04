package org.amilab.sensorbasics;

public interface Acc_LPF_Interface {
	/**
	 * Add Sample 
	 * Input: Acceleration data 
	 * Output: filtered data
	 */
	public float[] AddSample(float[] acceleration);
	
	/**
	 * Indicate whether alpha is static or not
	 */
	public void IsStatic(boolean alphastatic);
	
	/**
	 * Set a value for alpha 
	 */
	
	public void Setslpha(float alphavalue);
}
