package org.zkoss.zul.fusionchart;

import org.zkoss.zul.AbstractChartModel;

public class AbstractFusionchartModel extends AbstractChartModel {
	private static final long serialVersionUID = 20110120122455L;
	
	protected void fireEvent(int type, Comparable series, Object data) {
		super.fireEvent(type, series, data);
	}
}
