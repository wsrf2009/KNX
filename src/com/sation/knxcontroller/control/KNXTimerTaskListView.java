/**
 * 
 */
package com.sation.knxcontroller.control;

import java.io.Serializable;

/**
 * @author wangchunfeng
 *
 */
public class KNXTimerTaskListView extends KNXControlBase {
	private static final long serialVersionUID = 1L;

	private KNXTimer SelectedTimer;
	public void setSelectedTimer (KNXTimer timer) {
		this.SelectedTimer = timer;
	}
	public KNXTimer getSelectedTimer() {
		return this.SelectedTimer;
	}
	
	public class KNXTimer implements Serializable
    {
		private static final long serialVersionUID = 1L;
		
        public KNXTimer(String name, int id)
        {
            this.Name = name;
            this.Id = id;
        }
        
        private String Name;
        public void setName(String name){
        	this.Name = name;
        }
        public String getName() {
        	return this.Name;
        }
        
        private int Id;
        public void setId(int id) {
        	this.Id = id;
        }
        public int getId() {
        	return this.Id;
        }
    }
}
