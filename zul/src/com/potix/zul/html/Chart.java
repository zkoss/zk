/* Chart.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 1 10:30:48     2006, Created by henrichen@potix.com
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
import com.potix.lang.Strings;

import org.jfree.chart.*;
import org.jfree.chart.encoders.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.entity.*;
import org.jfree.data.general.*;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.awt.Paint;
import java.awt.Color;
import java.util.Iterator;


/**
 * A base chart class written with JFreeChart engine.
 *
 * <p>This is the JFreeChart base chart class implementation. All specific chart 
 * is derived from this class. All chart would support drilldown by 
 * providing Area hot spots. Each Area would callback to 
 * {@link com.potix.zul.html.AreaRenderer} class that application developers
 * can do preprocessing on each area.</p>
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public abstract class Chart extends AbstractChart {
	private DatasetChangeListener _changeListener;
	private AreaRenderer _preRenderer;
	private Dataset _dataset;
	
	/**
	 * Set the renderer which is used to pre-rendering the area in chart.
	 * It is genereally used to assoicate dataset information in the
	 * Area's componentScope.
	 */
	protected void setPreRenderer(AreaRenderer renderer) {
		if (_preRenderer != renderer) {
			_preRenderer = renderer;
		}
	}
	
	/**
	 * Get the renderer which is used to pre-rendering the area in chart.
	 * It is genereally used to assoicate dataset information in the
	 * Area's componentScope.
	 */
	protected AreaRenderer getPreRenderer() {
		return _preRenderer;
	}

	/** JFreeChart native interface.
	 */
	protected Dataset getInnerDataset() {
		return _dataset;
	}
	
	/** JFreechart native interface.
	 */
	protected void setInnerDataset(Dataset dataset) {
		if (_dataset != dataset) {
			initChangeListener();
			if (_dataset != null) {
				_dataset.removeChangeListener(_changeListener);
			}
			if (dataset != null) {
				dataset.addChangeListener(_changeListener);
			}
			_dataset = dataset;
		}
		
		//always redraw
		smartDrawChart();
	}
	private void initChangeListener() {
		if (_changeListener == null) {
			_changeListener = new DatasetChangeListener() {
				public void datasetChanged(DatasetChangeEvent event) {
					smartDrawChart();
				}
			};
		}
	}
	
	/**
	 * The methods to be derived by subclass to create specific type of chart.
	 * This implementation use JFreeChart engine.
	 */
	abstract protected JFreeChart createChart();

	protected byte[] drawChart() {
		if (Strings.isBlank(getWidth()))
			throw new UiException("must specify width");
			
		if (Strings.isBlank(getHeight()))
			throw new UiException("must specify height");
			
		JFreeChart chart = createChart();
		
		Plot plot = (Plot) chart.getPlot();
		float alpha = (float)(((float)getFgAlpha()) / 255);
		plot.setForegroundAlpha(alpha);
		
		alpha = (float)(((float)getBgAlpha()) / 255);
		plot.setBackgroundAlpha(alpha);
		
		int[] bgRGB = getBgRGB();
		if (bgRGB != null) {
			plot.setBackgroundPaint(new Color(bgRGB[0], bgRGB[1], bgRGB[2], getBgAlpha()));
		}

		//callbacks for each area
		ChartRenderingInfo info = new ChartRenderingInfo();
		BufferedImage bi = chart.createBufferedImage(getIntWidth(), getIntHeight(), BufferedImage.TRANSLUCENT, info);
		
		//remove old areas 	
		getChildren().clear();
		
		int j = 0;
		for(Iterator it=info.getEntityCollection().getEntities().iterator();it.hasNext();) {
			ChartEntity ce = ( ChartEntity ) it.next();
			Area area = new Area();
			if (getPreRenderer() != null) {
				try {
					getPreRenderer().render(area, ce);
				} catch(Exception ex) {
					throw UiException.Aide.wrap(ex);
				}
			}
			area.setParent(this);
		 	area.setCoords(ce.getShapeCoords());
		 	area.setShape(ce.getShapeType());
		    area.setId(getId()+'_'+(j++));
		    if (isShowTooltiptext() && ce.getToolTipText() != null) {
		    	area.setTooltiptext(ce.getToolTipText());
		    }
		    area.setAttribute("url", ce.getURLText());
			if (getAreaRenderer() != null) {
				try {
					getAreaRenderer().render(area, ce);
				} catch (Exception ex) {
					throw UiException.Aide.wrap(ex);
				}
			}
		}

		try {		
			//encode into png image format byte array
			return EncoderUtil.encode(bi, ImageFormat.PNG, true);
		} catch(java.io.IOException ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	
	//-- utilities --//
	/**
	 * decode url requests into key-value pair of Area's componentScope.
	 * @param area the Area where the final attribute is set
	 * @param url the url to be decoded.
	 */
	protected void decodeURL(Area area, String url) {
		if (url == null) {
			return;
		}
		int j = url.indexOf("?");
		if (j < 0) {
			return;
		}
		url = url.substring(j+1);
		
		do {
			j = url.indexOf("&amp;");
			if (j < 0) {
				storePair(area, url);
				break;
			} else {
				String pair = url.substring(0, j);
				storePair(area, pair);
				if ((j+5) >= url.length()) {
					break; //no more
				}
				url = url.substring(j+5);
			}
		} while(true);
	}
	
	private void storePair(Area area, String url) {
		int j = url.indexOf("=");
		if (j > 0) {
			area.setAttribute(url.substring(0, j), url.substring(j+1));
		}
	}
}
