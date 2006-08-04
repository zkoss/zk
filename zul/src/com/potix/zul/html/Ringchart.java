/* Ringchart.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 1 15:38:08     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.UiException;
import com.potix.image.AImage;

import org.jfree.chart.*;
import org.jfree.chart.encoders.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.entity.*;
import org.jfree.data.general.*;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.awt.Paint;


/**
 * A ring chart class written with JFreeChart engine.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Ringchart extends Piechart {
	protected JFreeChart createChart() {
		return ChartFactory.createRingChart(getTitle(), getDataset(), isShowLegend(), isShowTooltiptext(), true);
	}
}
