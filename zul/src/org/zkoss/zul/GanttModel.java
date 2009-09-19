/* GanttModel.java

	Purpose:
		
	Description:
		
	History:
		Apr 30, 2008 2:01:47 PM, Created by henrichen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.event.ChartDataEvent;

/**
 * A data model for Gantt chart.
 *
 * @author henrichen
 * @see GanttTask
 * @see Chart
 * @since 3.5.0
 */
public class GanttModel extends AbstractChartModel {
	private Map _taskMap = new LinkedHashMap(13); //(series, task list)
	
	public void addValue(Comparable series, GanttTask task) {
		List tasks = (List) _taskMap.get(series);
		if (tasks == null) {
			tasks = new ArrayList(13);
			_taskMap.put(series, tasks);
		}
		if (task.getSeries() != null) {
			throw new UiException("A GanttTask in a series cannot be added again: "+ task.getSeries()+":"+task.getDescription());
		}
		task.setSeries(series);
		task.setOwner(this);
		tasks.add(task);
		fireEvent(ChartDataEvent.ADDED, series, task);
	}
	
	public void removeValue(Comparable series, GanttTask task) {
		final List tasks = (List) _taskMap.get(series);
		if (tasks == null) {
			return;
		}
		tasks.remove(task);
		task.setSeries(null);
		task.setOwner(null);
		fireEvent(ChartDataEvent.REMOVED, series, task);
	}

	/** Return all series of this GanttModel.
	 * @return all series of this GanttModel.
	 */
	public Comparable[] getAllSeries() {
		final Collection allseries = _taskMap.keySet();
		return (Comparable[]) allseries.toArray(new Comparable[allseries.size()]);
	}
	
	public GanttTask[] getTasks(Comparable series) {
		final List tasks = (List) _taskMap.get(series);
		return tasks == null ? new GanttTask[0] : (GanttTask[]) tasks.toArray(new GanttTask[tasks.size()]);
	}

	/**
	 * A Task in an operation series; a helper class used in {@link GanttModel}.
	 * @author henrichen
	 * @since 3.5.0
	 * @see GanttModel
	 */
	public static class GanttTask {
		private Comparable _series;
		private Date _start;
		private Date _end;
		private String _description;
		private double _percent;
		private Collection _subtasks;
		private GanttModel _owner;
		
		public GanttTask(String description, Date start, Date end, double percent) {
			_description = description;
			_start = start;
			_end = end;
			_percent = percent;
			_subtasks = new LinkedList();
		}

		public Date getStart() {
			return _start;
		}

		public void setStart(Date start) {
			if (!Objects.equals(start, _start)) {
				this._start = start;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		public Date getEnd() {
			return _end;
		}

		public void setEnd(Date end) {
			if (!Objects.equals(end, _end)) {
				this._end = end;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		public String getDescription() {
			return _description;
		}

		public void setDescription(String description) {
			if (!Objects.equals(description, _description)) {
				this._description = description;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		public double getPercent() {
			return _percent;
		}

		public void setPercent(double percent) {
			if (percent != _percent) {
				this._percent = percent;
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}

		public void addSubtask(GanttTask task) {
			if (task.getSeries() != null) {
				throw new UiException("A GanttTask in a series cannot be added again: "+ task.getSeries()+":"+task.getDescription());
			}
			task.setSeries(_series);
			task.setOwner(_owner);
			_subtasks.add(task);
			if (_owner != null)
				_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
		}
		
		public void removeSubtask(GanttTask task) {
			if (_subtasks.remove(task)) {
				task.setSeries(null);
				task.setOwner(null);
				if (_owner != null)
					_owner.fireEvent(ChartDataEvent.CHANGED, _series, this);
			}
		}
		
		public GanttTask[] getSubtasks() {
			return (GanttTask[]) _subtasks.toArray(new GanttTask[_subtasks.size()]);
		}
		
		private Comparable getSeries() {
			return _series;
		}
		
		private void setSeries(Comparable series) {
			_series = series;
		}
		
		private void setOwner(GanttModel owner) {
			_owner = owner;
		}
	}
}
