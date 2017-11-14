package com.sation.knxcontroller.util.uikit;

import android.os.Looper;

public class UIKit {
	private static HandlerPoster mainPoster = null;  
	  
    private static HandlerPoster getMainPoster() {  
        if (mainPoster == null) {  
            synchronized (UIKit.class) {  
                if (mainPoster == null) {  
                    mainPoster = new HandlerPoster(Looper.getMainLooper(), 20);  
                }  
            }
        }
        return mainPoster;  
    }  
  
    /** 
     * Asynchronously 
     * The child thread asynchronous run relative to the main thread, 
     * not blocking the child thread 
     * 
     * @param runnable Runnable Interface 
     */  
    public static void runOnMainThreadAsync(Runnable runnable) {  
        if (Looper.myLooper() == Looper.getMainLooper()) {  
            runnable.run();  
            return;  
        }  
        getMainPoster().async(runnable);  
    }  
  
    /** 
     * Synchronously 
     * The child thread relative thread synchronization operation, 
     * blocking the child thread, 
     * thread for the main thread to complete 
     * 
     * @param runnable Runnable Interface 
     */  
    public static void runOnMainThreadSync(Runnable runnable) {  
        if (Looper.myLooper() == Looper.getMainLooper()) {  
            runnable.run();  
            return;  
        }  
        UIKitSyncPost poster = new UIKitSyncPost(runnable);  
        getMainPoster().sync(poster);  
        poster.waitRun();  
    }  
  
    public static void dispose() {  
        if (mainPoster != null) {  
            mainPoster.dispose();  
            mainPoster = null;  
        }  
    }  
}
