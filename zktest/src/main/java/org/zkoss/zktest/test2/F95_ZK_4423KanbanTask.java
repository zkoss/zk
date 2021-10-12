/* F95_ZK_4423KanbanTask.java

		Purpose:

		Description:

		History:
				Tue Nov 03 12:34:49 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

public class F95_ZK_4423KanbanTask {

	private String taskName;
	private String taskDescription;
	
	public String getTaskName() {
		return taskName;
	}
	
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public String getTaskDescription() {
		return taskDescription;
	}
	
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}
	
	public F95_ZK_4423KanbanTask(String taskName, String taskDescription) {
		super();
		this.taskName = taskName;
		this.taskDescription = taskDescription;
	}
	
	@Override
	public String toString() {
		return taskName;
	}
	
}
