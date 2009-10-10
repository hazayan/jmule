/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule team ( jmule@jmule.org / http://jmule.org )
 *
 *  Any parts of this program derived from other projects, or contributed
 *  by third-party developers are copyrighted by their respective authors.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package org.jmule.core.utils.timer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created on Aug 28, 2009
 * @author binary256
 * @version $Revision: 1.3 $
 * Last changed by $Author: binary255 $ on $Date: 2009/10/10 07:47:53 $
 */
public class JMTimer {
	private Queue<TaskExecutor> taskList = new ConcurrentLinkedQueue<TaskExecutor>();
	
	public JMTimer() {
	}
	
	public void stop() {
		for(TaskExecutor task : taskList)
			task.stopTask();
		taskList.clear();
	}
	
	public void addTask(JMTimerTask task, long waitTime, boolean repeat) {
		TaskExecutor executor = new TaskExecutor(waitTime, task, repeat);
		taskList.add(executor);
		executor.start();	
	}
	
	public void addTask(JMTimerTask task, long waitTime) {
		addTask(task, waitTime ,false);
	}
	
	private void removeTask(TaskExecutor executor) {
		taskList.remove(executor);
	}
	
	public void removeTask(JMTimerTask removeTask) {
		for(TaskExecutor task_executor : taskList) 
			if (task_executor.getTask().equals(removeTask)) {
				task_executor.stopTask();
				taskList.remove(task_executor);
				return ;
			}
	}
	
	public void cancelAllTasks() {
		for(TaskExecutor task_executor : taskList) {
			task_executor.stopTask();
		}
		taskList.clear();
	}
	
	
	private class TaskExecutor extends Thread {
		private boolean stop = false;
		private long waitTime;
		private boolean repeatTask = false;
		
		private JMTimerTask task;
		
		public TaskExecutor(long waitTime, JMTimerTask task, boolean repeatTask) {
			super("Task executor " +task);
			this.waitTime = waitTime;
			this.task = task;
			this.repeatTask = repeatTask;
		}
		
		public void run() {
			do {
				if (task.mustStopTask())
					break;
				try {
					this.join(waitTime);
				} catch (InterruptedException e) {
					if (stop) {
						break;
					}
				}

				if (task.mustStopTask())
					break;

				try {
					task.run();
				} catch (Throwable t) {
					t.printStackTrace();
				}

			} while (repeatTask);
			removeTask(this);
		}

		public JMTimerTask getTask() {
			return task;
		}
		
		public void stopTask() {
			stop = true;
			this.interrupt();
		}
	}

}
