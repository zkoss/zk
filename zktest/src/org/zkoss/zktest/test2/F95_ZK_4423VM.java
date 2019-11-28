/* F95_ZK_4423VM.java

		Purpose:

		Description:

		History:
				Tue Nov 03 12:34:49 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.ui.event.PortalDropEvent;
import org.zkoss.zul.ListModelList;

public class F95_ZK_4423VM {
	
	private ListModelList<F95_ZK_4423KanbanTask> todoTasks;
	private ListModelList<F95_ZK_4423KanbanTask> activeTasks;
	private ListModelList<F95_ZK_4423KanbanTask> completeTasks;

	private Map<String,ListModelList<F95_ZK_4423KanbanTask>> taskLists;
	
	@Init
	public void init() {
		todoTasks = new ListModelList<F95_ZK_4423KanbanTask>();
		todoTasks.add(new F95_ZK_4423KanbanTask("todo task 1", "todo task 1"));
		todoTasks.add(new F95_ZK_4423KanbanTask("todo task 2", "todo task 2"));
		activeTasks = new ListModelList<F95_ZK_4423KanbanTask>();
		activeTasks.add(new F95_ZK_4423KanbanTask("active task 1", "active task 1"));
		activeTasks.add(new F95_ZK_4423KanbanTask("active task 2", "active task 2"));
		completeTasks = new ListModelList<F95_ZK_4423KanbanTask>();
		completeTasks.add(new F95_ZK_4423KanbanTask("complete task 1", "complete task 1"));
		completeTasks.add(new F95_ZK_4423KanbanTask("complete task 2", "complete task 2"));
		taskLists = new HashMap<String, ListModelList<F95_ZK_4423KanbanTask>>();
		taskLists.put("todo", todoTasks);
		taskLists.put("active", activeTasks);
		taskLists.put("complete", completeTasks);

	}


	public ListModelList<F95_ZK_4423KanbanTask> getTodoTasks() {
		return todoTasks;
	}


	public ListModelList<F95_ZK_4423KanbanTask> getActiveTasks() {
		return activeTasks;
	}


	public ListModelList<F95_ZK_4423KanbanTask> getCompleteTasks() {
		return completeTasks;
	}

	@Command
	public void portalMove(@BindingParam("from") String from, @BindingParam("to") String to, @BindingParam("evt") PortalDropEvent evt) {
		if (Math.abs(evt.getDroppedColumnIndex() - evt.getDraggedColumnIndex()) <= 1) {
			F95_ZK_4423KanbanTask task = taskLists.get(from).get(evt.getDraggedIndex());
			taskLists.get(from).remove(evt.getDraggedIndex());
			taskLists.get(to).add(evt.getDroppedIndex(), task);
			BindUtils.postNotifyChange(this, "todoTasks", "activeTasks", "completeTasks");
			Clients.log(taskLists);
		} else {
			Clients.log("cross 2 column!");
		}
		evt.preventDefault(); // to stop the default portal move behavior
	}
}
