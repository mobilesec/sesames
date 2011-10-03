/***************************************************************************** 	
 *  Project: Sesame-S Client
 *  Description: mobile client for interaction with the sesame-s system
 *  Author: Peter Riedl
 *  Copyright: Peter Riedl, 06/2011
 *
 ******************************************************************************/
package at.sesame.fhooe.lib.vpn;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * this thread updates the remaining time displayed
 * in the VPN connection progress dialog
 * @author admin
 *
 */
public class ProgressThread 
extends Thread 
{
	/**
	 * the handler for information exchange
	 */
	private Handler mHandler;
	
	/**
	 * boolean flag indicating whether the thread is running
	 */
    private boolean mRunning = false;
    
    /**
     * the current runtime in ms
     */
    private int mRuntime = 0;
    
    /**
     * sleeping time of the ProgressThread in ms
     */
    private int mProgressStep = 100;
   
    /**
     * creates a new ProgressThread
     * @param h the handler to transmit the progress information
     */
    public ProgressThread(Handler h) {
        mHandler = h;
    }
   
    @Override
    public void run() {
        mRunning = true;  
 
        while (mRunning) {
            try {
                Thread.sleep(mProgressStep);
            } catch (InterruptedException e) {
                Log.e("ERROR", "Thread Interrupted");
            }
            mRuntime+=mProgressStep;
            Message msg = mHandler.obtainMessage();
            msg.arg1 = mRuntime;
            mHandler.sendMessage(msg);
        }
    }
   
    /**
     * stops the ProgressThread and resets the runtime to 0
     */
    public void stopProgressThread() {
    	
        mRunning = false;
        mRuntime = 0;
    }
}

