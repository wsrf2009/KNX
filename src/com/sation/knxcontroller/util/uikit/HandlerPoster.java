package com.sation.knxcontroller.util.uikit;

import java.util.LinkedList;
import java.util.Queue;

import com.sation.knxcontroller.util.Log;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

public class HandlerPoster extends Handler {
	private final String TAG = "HandlerPoster";
	private final int ASYNC = 0x1;  
    private final int SYNC = 0x2;  
    private final Queue<Runnable> asyncPool;  
    private final Queue<UIKitSyncPost> syncPool;  
    private final int maxMillisInsideHandleMessage;  
    private boolean asyncActive;  
    private boolean syncActive;  
  
    HandlerPoster(Looper looper, int maxMillisInsideHandleMessage) {  
        super(looper);  
        this.maxMillisInsideHandleMessage = maxMillisInsideHandleMessage;  
        asyncPool = new LinkedList<Runnable>();  
        syncPool = new LinkedList<UIKitSyncPost>();  
    }  
  
    void dispose() {  
        this.removeCallbacksAndMessages(null);  
        this.asyncPool.clear();  
        this.syncPool.clear();  
    }  
  
    void async(Runnable runnable) {  
        synchronized (asyncPool) {  
            asyncPool.offer(runnable);  
            if (!asyncActive) {  
                asyncActive = true;  
                if (!sendMessage(obtainMessage(ASYNC))) {  
                    try {
						throw new Exception("Could not send handler message");
					} catch (Exception e) {
						e.printStackTrace();
					}  
                }  
            }  
        }  
    }  
  
    void sync(UIKitSyncPost post) {  
        synchronized (syncPool) {  
            syncPool.offer(post);  
            if (!syncActive) {  
                syncActive = true;  
                if (!sendMessage(obtainMessage(SYNC))) {  
                    try {
						throw new Exception("Could not send handler message");
					} catch (Exception e) {
						e.printStackTrace();
					}  
                }  
            }  
        }  
    }  
  
    @Override  
    public void handleMessage(Message msg) {  
        if (msg.what == ASYNC) {  
            boolean rescheduled = false;  
            try {  
                long started = SystemClock.uptimeMillis();  
                while (null != asyncPool.peek()) {  
                    Runnable runnable = asyncPool.poll();  
                    if (runnable == null) {  
                        synchronized (asyncPool) {  
                            // Check again, this time in synchronized  
                            runnable = asyncPool.poll();  
                            if (runnable == null) {  
                                asyncActive = false;  
                                return;  
                            }  
                        }  
                    }  
                    runnable.run();  
                    long timeInMethod = SystemClock.uptimeMillis() - started;  
                    if (timeInMethod >= maxMillisInsideHandleMessage) {  
                        if (!sendMessage(obtainMessage(ASYNC))) {  
                            try {
								throw new Exception("Could not send handler message");
							} catch (Exception e) {
								e.printStackTrace();
							}  
                        }  
                        rescheduled = true;  
                        return;  
                    }  
                }  
            } catch (Exception ex) {
            	Log.e(TAG, ex.getMessage());
            } finally {  
                asyncActive = rescheduled;  
            }  
        } else if (msg.what == SYNC) {  
            boolean rescheduled = false;  
            try {  
                long started = SystemClock.uptimeMillis();  
                while (null != syncPool.peek()) {  
                    UIKitSyncPost post = syncPool.poll();
                    if (post == null) {  
                        synchronized (syncPool) {  
                            // Check again, this time in synchronized  
                            post = syncPool.poll();  
                            if (post == null) {  
                                syncActive = false;  
                                return;  
                            }  
                        }  
                    }  
                    post.run();  
                    long timeInMethod = SystemClock.uptimeMillis() - started;  
                    if (timeInMethod >= maxMillisInsideHandleMessage) {  
                        if (!sendMessage(obtainMessage(SYNC))) {  
                            try {
								throw new Exception("Could not send handler message");
							} catch (Exception e) {
								e.printStackTrace();
							}  
                        }  
                        rescheduled = true;  
                        return;  
                    }  
                }  
            } catch (Exception ex) {
            	Log.e(TAG, ex.getLocalizedMessage());
            } finally {  
                syncActive = rescheduled;  
            }  
        } else super.handleMessage(msg);  
    } 
}
