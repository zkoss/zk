package org.zkoss.zul.fusionchart;

import org.zkoss.zul.GanttModel;

public class AbstractFusionchartGanttModel extends GanttModel {
	private static final long serialVersionUID = 20110120122322L;

	protected void fireEvent(int type, Comparable series, Object data) {
		super.fireEvent(type, series, data);
	}
}
