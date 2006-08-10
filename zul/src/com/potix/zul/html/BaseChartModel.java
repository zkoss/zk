/* BaseChartModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 03 12:51:14     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.lang.Objects;
import com.potix.zul.html.event.ChartDataEvent;
import com.potix.zul.html.event.ChartDataListener;

import org.jfree.data.general.*;
import org.jfree.data.category.*;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * A Base chart  data model implementation of {@link ChartModel}. All data model
 * used by the {@link SimpleChartEngine} is derived from this class.
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 * @see ChartModel
 */
abstract public class BaseChartModel extends AbstractChartModel
implements java.io.Serializable {
	private static final long serialVersionUID = 20060809L;
	private DatasetChangeListener _changeListener;
	protected Dataset _dataset;

	private void initChangeListener() {
		if (_changeListener == null) {
			_changeListener = new DatasetChangeListener() {
				public void datasetChanged(DatasetChangeEvent event) {
					fireEvent(ChartDataEvent.CHANGED, null, null);
				}
			};
		}
	}

	public Object getNativeModel() {
		return _dataset;
	}
	
	/** Set native data model.
	 */
	public void setNativeModel(Object dataset) {
		if (_dataset != dataset) {
			initChangeListener();
			if (_dataset != null) {
				_dataset.removeChangeListener(_changeListener);
			}
			if (dataset != null) {
				((Dataset)dataset).addChangeListener(_changeListener);
			}
			_dataset = (Dataset)dataset;
		}
		
		//always redraw
		fireEvent(ChartDataEvent.CHANGED, null, null);
	}

	//-- Object --//
	public int hashCode() {
		return _dataset.hashCode();
	}
	
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (other == null || !(other instanceof BaseChartModel)) {
			return false;
		}
		final BaseChartModel otherm = (BaseChartModel) other;
		return _dataset.equals(otherm._dataset);
	}
}
