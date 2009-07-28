/* JFreeChartEngine.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Aug 1 10:30:48     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkex.zul.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.chart.encoders.ImageFormat;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.CategoryLabelEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.JFreeChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.TickLabelEntity;
import org.jfree.chart.entity.TitleEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.plot.WaferMapPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.renderer.WaferMapRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.general.WaferMapDataset;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.DefaultWindDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.WindDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.zkoss.lang.Objects;
import org.zkoss.util.TimeZones;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Area;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.ChartModel;
import org.zkoss.zul.DialModel;
import org.zkoss.zul.DialModelRange;
import org.zkoss.zul.DialModelScale;
import org.zkoss.zul.GanttModel;
import org.zkoss.zul.HiLoModel;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.WaferMapModel;
import org.zkoss.zul.XYModel;
import org.zkoss.zul.XYZModel;
import org.zkoss.zul.GanttModel.GanttTask;
import org.zkoss.zul.WaferMapModel.IntPair;
import org.zkoss.zul.impl.ChartEngine;


/**
 * A chart engine implemented with JFreeChart.
 *
 * <p>This is the JFreeChart base chart engine implementation. All chart would 
 * support drilldown by providing Area hot spots. Each Area would callback to 
 * {@link org.zkoss.zul.event.ChartAreaListener} class that application developers
 * can do processing on each area.</p>
 *
 * <p>Note that useful {@link org.zkoss.zul.ChartModel} information is put in 
 * Area's custom attribute Map so you can retrieve them by calling Area's 
 * getAttribute(key) method and use them in drilldown function. Following is 
 * the table of keys for different ChartModel.</p>
 *
 * <table>
 *   <tr><th>model</th><th>key</th></tr>
 *   <tr><td>{@link PieModel}</td><td>entity</td></tr>
 *   <tr><td></td><td>category</td></tr>
 *   <tr><td></td><td>value</td></tr>
 *	
 *   <tr><td>{@link CategoryModel}</td><td>entity</td></tr>
 *   <tr><td></td><td>series</td></tr>
 *   <tr><td></td><td>category</td></tr>
 *   <tr><td></td><td>value</td></tr>
 *
 *   <tr><td>{@link XYModel}</td><td>entity</td></tr>
 *   <tr><td></td><td>series</td></tr>
 *   <tr><td></td><td>x</td></tr>
 *   <tr><td></td><td>y</td></tr>
 *
 *   <tr><td>{@link XYZModel}</td><td>entity</td></tr>
 *   <tr><td>since 3.5.0</td><td>series</td></tr>
 *   <tr><td></td><td>x</td></tr>
 *   <tr><td></td><td>y</td></tr>
 *   <tr><td></td><td>z</td></tr>
 *
 *   <tr><td>{@link HiLoModel}</td><td>entity</td></tr>
 *   <tr><td></td><td>series</td></tr>
 *   <tr><td></td><td>date</td></tr>
 *   <tr><td></td><td>open</td></tr>
 *   <tr><td></td><td>high</td></tr>
 *   <tr><td></td><td>low</td></tr>
 *   <tr><td></td><td>close</td></tr>
 *   <tr><td></td><td>volumn</td></tr>
 * </table>
 * <p>Following is the explanation for each key:</p>
 * <ul>
 * <li>entity: entity is used to distinguish the differnt entities on a
 * chart. It might be TITLE, CATEGORY, DATA, LEGEND. The most important 
 * entity might be DATA entity where the real chart data point is located. 
 * For example, the DATA Area is each slice in a Pie chart.</li>
 * <li>category: category name of the associated data.</li>
 * <li>value: value of the associated data.</li>
 * <li>series: series name of the associated data.</li>
 * <li>x: x value of the XYModel and XYZModel</li>
 * <li>y: y value of the XYModel and XYZModel</li>
 * <li>z: z value of the XYZModel</li>
 * <li>date, open, high, low, close, volumn: data of the HiLoModel</li>
 * </ul>
 *
 * <p>See also <a href="http://www.jfree.org/jfreechart">jFreeChart</a>.
 * @author henrichen
 * @since 3.0.0
 */
public class JFreeChartEngine implements ChartEngine, java.io.Serializable {
	//as long as the series name is not set
	private static final String DEFAULT_HI_LO_SERIES = "High Low Data";
	private static final SimpleDateFormat _dateFormat = new SimpleDateFormat();
	
	//caching chartImpl if type and 3d are the same 
	private transient boolean _threeD; 
	private transient String _type; 
	private transient ChartImpl _chartImpl; //chart implementaion
	
	/**
	 * create specific type of chart drawing engine. This implementation use 
	 * JFreeChart engine.
	 */
	private ChartImpl getChartImpl(Chart chart) {
		if (Objects.equals(chart.getType(), _type) && _threeD == chart.isThreeD()) {
			return _chartImpl;
		}

		if (Chart.PIE.equals(chart.getType())) 
			_chartImpl = chart.isThreeD() ? new Pie3d() : new Pie();

		else if (Chart.RING.equals(chart.getType()))
			_chartImpl = new Ring();
					
		else if (Chart.BAR.equals(chart.getType()))
			_chartImpl = chart.isThreeD() ? new Bar3d() : new Bar();
		
		else if (Chart.LINE.equals(chart.getType()))
			_chartImpl = chart.isThreeD() ? new Line3d() : new Line();
			
		else if (Chart.AREA.equals(chart.getType()))
			_chartImpl = new AreaImpl();
					
		else if (Chart.STACKED_BAR.equals(chart.getType()))
			_chartImpl = chart.isThreeD() ? new StackedBar3d() : new StackedBar();

		else if (Chart.STACKED_AREA.equals(chart.getType()))
			_chartImpl = new StackedArea();
					
		else if (Chart.WATERFALL.equals(chart.getType()))
			_chartImpl = new Waterfall();
					
		else if (Chart.POLAR.equals(chart.getType()))
			_chartImpl = new Polar();

		else if (Chart.SCATTER.equals(chart.getType()))
			_chartImpl = new Scatter();

		else if (Chart.TIME_SERIES.equals(chart.getType())) {
			_chartImpl = new TimeSeries();
			final TimeZone tz = chart.getTimeZone();
			if (tz != null) {
				_dateFormat.setTimeZone(tz);
			}
			if (chart.getDateFormat() != null) {
				_dateFormat.applyPattern(chart.getDateFormat());
			}

		} else if (Chart.STEP_AREA.equals(chart.getType()))
			_chartImpl = new StepArea();

		else if (Chart.STEP.equals(chart.getType()))
			_chartImpl = new Step();
			
		else if (Chart.HISTOGRAM.equals(chart.getType()))			
			_chartImpl = new Histogram();

		else if (Chart.CANDLESTICK.equals(chart.getType())) {			
			_chartImpl = new Candlestick();
			final TimeZone tz = chart.getTimeZone();
			if (tz != null) {
				_dateFormat.setTimeZone(tz);
			}
			if (chart.getDateFormat() != null) {
				_dateFormat.applyPattern(chart.getDateFormat());
			}

		} else if (Chart.HIGHLOW.equals(chart.getType())) {			
			_chartImpl = new Highlow();
			final TimeZone tz = chart.getTimeZone();
			if (tz != null) {
				_dateFormat.setTimeZone(tz);
			}
			if (chart.getDateFormat() != null) {
				_dateFormat.applyPattern(chart.getDateFormat());
			}

		} else if (Chart.BUBBLE.equals(chart.getType()))
			_chartImpl = new Bubble();
			
		else if (Chart.WAFERMAP.equals(chart.getType()))
			_chartImpl = new Wafermap();
			
		else if (Chart.GANTT.equals(chart.getType())) {
			_chartImpl = new Gantt();
			final TimeZone tz = chart.getTimeZone();
			if (tz != null) {
				_dateFormat.setTimeZone(tz);
			}
			if (chart.getDateFormat() == null) {
				_dateFormat.applyPattern("MMM d ''yy");
			} else {
				_dateFormat.applyPattern(chart.getDateFormat());
			}

		} else if (Chart.WIND.equals(chart.getType())) {
			_chartImpl = new Wind();
			final TimeZone tz = chart.getTimeZone();
			if (tz != null) {
				_dateFormat.setTimeZone(tz);
			}
			if (chart.getDateFormat() != null) {
				_dateFormat.applyPattern(chart.getDateFormat());
			}
		} else if (Chart.DIAL.equals(chart.getType())) {
			_chartImpl = new Dial();
			
		} else 
			throw new UiException("Unsupported chart type yet: "+chart.getType());

		_threeD = chart.isThreeD();
		_type = chart.getType();
		return _chartImpl;
	}
	
	/**
	* Developers with special needs can override this method to apply own rendering properties on the created JFreeChart. The
	* original implementation of this method simply return false to tell the engine to apply its default rendering properties. Return true
	* tells the engine NOT to apply its default rendering properties.
	*
	* @param jfchart the created JFreeChart
	* @param chart the ZK chart component associated with this ChartEngine.
	* @return true to tell this engine NOT to apply its default rendering properties; false to tell this engine TO apply its default rendering properties.
	* @since 3.0.1
	*/
	protected boolean prepareJFreeChart(JFreeChart jfchart, Chart chart) {
		return false; //request original engine to also do the setting.
	}

	//-- ChartEngine --//
	public byte[] drawChart(Object data) {
		Chart chart = (Chart) data;
		ChartImpl impl = getChartImpl(chart);

		JFreeChart jfchart = impl.createChart(chart);

		//feature#1814621 
		//call prepareJFreeChart so developer can override it and do their own parameter settings
		if (!prepareJFreeChart(jfchart, chart)) {
			Plot plot = (Plot) jfchart.getPlot();
			float alpha = (float)(((float)chart.getFgAlpha()) / 255);
			plot.setForegroundAlpha(alpha);
			
			alpha = (float)(((float)chart.getBgAlpha()) / 255);
			plot.setBackgroundAlpha(alpha);

			int[] bgRGB = chart.getBgRGB();
			if (bgRGB != null) {
				plot.setBackgroundPaint(new Color(bgRGB[0], bgRGB[1], bgRGB[2], chart.getBgAlpha()));
			}

	        int[] paneRGB = chart.getPaneRGB();
	        if (paneRGB != null) {
	            jfchart.setBackgroundPaint(new Color(paneRGB[0], paneRGB[1], paneRGB[2], chart.getPaneAlpha()));
	        }
	        
	        //since 3.6.1, use JFreeChart 1.0.13. Make it appear like previous version(background, grid lines)
	        if (plot instanceof CategoryPlot) {
	        	final CategoryPlot cplot = (CategoryPlot) plot;
	        	cplot.setRangeGridlinePaint(new Color(0xc0, 0xc0, 0xc0));
	        } else if (plot instanceof XYPlot) {
	        	final XYPlot xyplot = (XYPlot) plot;
	        	xyplot.setRangeGridlinePaint(Color.LIGHT_GRAY);
	        	xyplot.setDomainGridlinePaint(Color.LIGHT_GRAY);
	        } else if (plot instanceof PolarPlot) {
	        	final PolarPlot pplot = (PolarPlot) plot;
	        	pplot.setAngleGridlinePaint(Color.LIGHT_GRAY);
	        	pplot.setRadiusGridlinePaint(Color.LIGHT_GRAY);
	        }
		}
		
		//callbacks for each area
		ChartRenderingInfo jfinfo = new ChartRenderingInfo();
		BufferedImage bi = jfchart.createBufferedImage(chart.getIntWidth(), chart.getIntHeight(), Transparency.TRANSLUCENT, jfinfo);
		
		//remove old areas 	
		if (chart.getChildren().size() > 20)
			chart.invalidate(); //improve performance if too many chart
		chart.getChildren().clear();

		if (Events.isListened(chart, Events.ON_CLICK, false) || chart.isShowTooltiptext()) {
			int j = 0;
			String preUrl = null;
			for(Iterator it=jfinfo.getEntityCollection().iterator();it.hasNext();) {
				ChartEntity ce = ( ChartEntity ) it.next();
				final String url = ce.getURLText();
	
				//workaround JFreeChart's bug (skip replicate areas)
				if (url != null) { 
					if (preUrl == null) {
						preUrl = url;
					} else if (url.equals(preUrl)) { //start replicate, skip
						break;
					}
				}
				
				//1. JFreeChartEntity area cover the whole chart, will "mask" other areas
				//2. LegendTitle area cover the whole legend, will "mask" each legend 
				//3. PlotEntity cover the whole chart plotting araa, will "mask" each bar/line/area
				if (!(ce instanceof JFreeChartEntity) 
				&& !(ce instanceof TitleEntity && ((TitleEntity)ce).getTitle() instanceof LegendTitle)
				&& !(ce instanceof PlotEntity)) {
					Area area = new Area();
					area.setParent(chart);
				 	area.setCoords(ce.getShapeCoords());
				 	area.setShape(ce.getShapeType());
				    area.setId("area_"+chart.getId()+'_'+(j++));
				    if (chart.isShowTooltiptext() && ce.getToolTipText() != null) {
				    	area.setTooltiptext(ce.getToolTipText());
				    }
				    area.setAttribute("url", ce.getURLText());
				    impl.render(chart, area, ce);
					if (chart.getAreaListener() != null) {
						try {
							chart.getAreaListener().onRender(area, ce);
						} catch (Exception ex) {
							throw UiException.Aide.wrap(ex);
						}
					}
				}
			}
		}
		//clean up the "LEGEND_SEQ"
		//used for workaround LegendItemEntity.getSeries() always return 0
		//used for workaround TickLabelEntity no information
	    chart.removeAttribute("LEGEND_SEQ"); 
	    chart.removeAttribute("TICK_SEQ"); 

		try {		
			//encode into png image format byte array
			return EncoderUtil.encode(bi, ImageFormat.PNG, true);
		} catch(java.io.IOException ex) {
			throw UiException.Aide.wrap(ex);
		}
	}
	
	//-- utilities --//
	/**
	 * transfer a PieModel into JFreeChart PieDataset.
	 */
	private PieDataset PieModelToPieDataset(PieModel model) {
		final DefaultPieDataset dataset = new DefaultPieDataset();
		for (final Iterator it = model.getCategories().iterator(); it.hasNext();) {
			Comparable category = (Comparable) it.next();
			Number value = model.getValue(category);
			dataset.setValue(category, value);
		}
		return dataset;
	}

	/**
	 * transfer a CategoryModel into JFreeChart PieDataset.
	 */
	private PieDataset CategoryModelToPieDataset(CategoryModel model) {
		final DefaultPieDataset dataset = new DefaultPieDataset();
		Comparable defaultSeries = null;
		int max = 0;
		for (final Iterator it = model.getKeys().iterator(); it.hasNext();) {
			final List key = (List) it.next();
			Comparable series = (Comparable) key.get(0);
			if (defaultSeries == null) {
				defaultSeries = series;
				max = model.getCategories().size();
			}
			if (!Objects.equals(defaultSeries, series)) {
				continue;
			}
			Comparable category = (Comparable) key.get(1);
			Number value = (Number) model.getValue(series, category);
			dataset.setValue(category, value);

			if (--max == 0) break; //no more in this series
		}
		return dataset;
	}

	/**
	 * transfer a CategoryModel into JFreeChart CategoryDataset.
	 */
	private CategoryDataset CategoryModelToCategoryDataset(CategoryModel model) {
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (final Iterator it = model.getKeys().iterator(); it.hasNext();) {
			final List key = (List) it.next();
			Comparable series = (Comparable) key.get(0);
			Comparable category = (Comparable) key.get(1);
			Number value = (Number) model.getValue(series, category);
			dataset.setValue(value, series, category);
		}
		return dataset;
	}

	/**
	 * transfer a XYModel into JFreeChart XYSeriesCollection.
	 */
	private XYDataset XYModelToXYDataset(XYModel model) {
		final XYSeriesCollection dataset = new XYSeriesCollection();
		for (final Iterator it = model.getSeries().iterator(); it.hasNext();) {
			final Comparable series = (Comparable) it.next();
			XYSeries xyser = new XYSeries(series, model.isAutoSort());
			final int size = model.getDataCount(series);
			for(int j = 0; j < size; ++j) {
				xyser.add(model.getX(series, j), model.getY(series, j), false);
			}
			dataset.addSeries(xyser);
		}
		return dataset;
	}

	/**
	 * transfer a XYModel into JFreeChart DefaultTableXYDataset.
	 */
	private TableXYDataset XYModelToTableXYDataset(XYModel model) {
		final DefaultTableXYDataset dataset = new DefaultTableXYDataset();
		for (final Iterator it = model.getSeries().iterator(); it.hasNext();) {
			final Comparable series = (Comparable) it.next();
			XYSeries xyser = new XYSeries(series, false, false);
			final int size = model.getDataCount(series);
			for(int j = 0; j < size; ++j) {
				xyser.add(model.getX(series, j), model.getY(series, j), false);
			}
			dataset.addSeries(xyser);
		}
		return dataset;
	}

	/**
	 * transfer a XYModel into JFreeChart TimeSeriesCollection.
	 */
	private XYDataset XYModelToTimeDataset(XYModel model, Chart chart) {
		TimeZone tz = chart.getTimeZone();
		if (tz == null) tz = TimeZones.getCurrent();
		String p = chart.getPeriod();
		if (p == null) p = Chart.MILLISECOND;
		Class pclass = (Class) _periodMap.get(p);
		if (pclass == null) {
			throw new UiException("Unsupported period for Time Series chart: "+p);
		}
		final TimeSeriesCollection dataset = new TimeSeriesCollection(tz);
		
		for (final Iterator it = model.getSeries().iterator(); it.hasNext();) {
			final Comparable series = (Comparable) it.next();
			final org.jfree.data.time.TimeSeries tser = 
				new org.jfree.data.time.TimeSeries(series);
				//new org.jfree.data.time.TimeSeries(series, pclass); //deprecated since JFreeChart 10.0.13
			final int size = model.getDataCount(series);
			for(int j = 0; j < size; ++j) {
				final RegularTimePeriod period = RegularTimePeriod.createInstance(
					pclass, new Date(model.getX(series, j).longValue()), tz);
				tser.addOrUpdate(period, model.getY(series, j));
			}
			dataset.addSeries(tser);
		}
		return dataset;
	}

	/**
	 * transfer a XYZModel into JFreeChart XYZDataset.
	 * @since 3.5.0
	 */
	private XYZDataset XYZModelToXYZDataset(XYZModel model) {
		final DefaultXYZDataset dataset = new DefaultXYZDataset();
		for (final Iterator it = model.getSeries().iterator(); it.hasNext();) {
			final Comparable seriesKey = (Comparable) it.next();
			final int size = model.getDataCount(seriesKey);
			final double[][] data = new double[3][size];
			for(int j = 0; j < size; ++j) {
				data[0][j] = model.getX(seriesKey, j).doubleValue();
				data[1][j] = model.getY(seriesKey, j).doubleValue();
				data[2][j] = model.getZ(seriesKey, j).doubleValue();
			}
			dataset.addSeries(seriesKey, data);
		}
		return dataset;
	}

	/**
	 * transfer a WaferMapModel into JFreeChart WaferMapDataset.
	 * @since 3.5.0
	 */
	private WaferMapDataset WaferMapModelToWaferMapDataset(WaferMapModel model) {
		final WaferMapDataset dataset = new WaferMapDataset(model.getXsize(), model.getYsize(), new Double(model.getSpace()));
		for (final Iterator it = model.getEntrySet().iterator(); it.hasNext();) {
			final Entry me = (Entry) it.next();
			final IntPair ip = (IntPair) me.getKey();
			final Number val = (Number) me.getValue();
			final int x = ip.getX() + 1; //move to WaferMapDataset x is 1-based
			final int y = ip.getY();
			dataset.addValue(val.intValue(), x, y);
		}
		return dataset;
	}
	
	/**
	 * transfer a GanttModel into JFreeChart GanttDataset.
	 * @since 3.5.0
	 */
	private GanttCategoryDataset GanttModelToGanttDataset(GanttModel model) {
		final TaskSeriesCollection dataset = new TaskSeriesCollection();
		final Comparable[] allseries = model.getAllSeries();
		final int sz = allseries.length;
		for (int j = 0; j < sz; ++j) {
			final Comparable series = allseries[j];
			final TaskSeries taskseries = new TaskSeries((String) series);
			final GanttTask[] tasks = model.getTasks(series);
			final int tsz = tasks.length;
			for (int k = 0; k < tsz; ++k) {
				final GanttTask task = tasks[k];
				final Task jtask = newTask(task);
				addSubtask(jtask, task);
				taskseries.add(jtask); 
			}
			dataset.add(taskseries);
		}
		
		return dataset;
	}
	private Task newTask(GanttTask task) {
		final Task jtask = new Task(task.getDescription(), task.getStart(), task.getEnd());
		jtask.setPercentComplete(task.getPercent());
		return jtask;
	}
	
	private void addSubtask(Task jtask, GanttTask task) {
		final GanttTask[] tasks = task.getSubtasks();
		final int sz = tasks.length;
		for (int j = 0; j < sz; ++j) {
			final GanttTask subtask = tasks[j];
			final Task jsubtask = newTask(subtask);
			jtask.addSubtask(jsubtask);
			addSubtask(jsubtask, subtask); //recursive
		}
	}
	
	/**
	 * transfer a XYZModel into JFreeChart WindDataset.
	 * @since 3.5.0
	 */
	private WindDataset XYZModelToWindDataset(XYZModel model) {
		final Collection allseries = model.getSeries(); 
		final int ssize = allseries.size();
		final Object[][][] wobjs = new Object[ssize][][];
		int k = 0;
		for (final Iterator it = allseries.iterator(); it.hasNext();++k) {
			final Comparable seriesKey = (Comparable) it.next();
			final int size = model.getDataCount(seriesKey);
			final Object[][] data = new Object[size][3];
			wobjs[k] = data;
			for(int j = 0; j < size; ++j) {
				data[j][0] = model.getX(seriesKey, j);
				data[j][1] = model.getY(seriesKey, j);
				data[j][2] = model.getZ(seriesKey, j);
			}
		}
		return new DefaultWindDataset((List)allseries, wobjs);
	}
	
	private static Map _periodMap = new HashMap(10);	
	static {
		_periodMap.put(Chart.MILLISECOND, org.jfree.data.time.Millisecond.class);
		_periodMap.put(Chart.SECOND, org.jfree.data.time.Second.class);
		_periodMap.put(Chart.MINUTE, org.jfree.data.time.Minute.class);
		_periodMap.put(Chart.HOUR, org.jfree.data.time.Hour.class);
		_periodMap.put(Chart.DAY, org.jfree.data.time.Day.class);
		_periodMap.put(Chart.WEEK, org.jfree.data.time.Week.class);
		_periodMap.put(Chart.MONTH, org.jfree.data.time.Month.class);
		_periodMap.put(Chart.QUARTER, org.jfree.data.time.Quarter.class);
		_periodMap.put(Chart.YEAR, org.jfree.data.time.Year.class);
	}

	/**
	 * transfer a HiLoModel into JFreeChart DefaultOHLCDataset
	 */
	private OHLCDataset HiLoModelToOHLCDataset(HiLoModel model) {
		final int size = model.getDataCount();
		final OHLCDataItem[] items = new OHLCDataItem[size];
		
		for(int j = 0; j < size; ++j) {
			Date date = model.getDate(j);
			Number open = model.getOpen(j);
			Number high = model.getHigh(j);
			Number low = model.getLow(j);
			Number close = model.getClose(j);
			Number volume = model.getVolume(j);
			
			OHLCDataItem item = new OHLCDataItem(date, 
				doubleValue(open), doubleValue(high), 
				doubleValue(low), doubleValue(close), 
				doubleValue(volume));
			items[j] = item;
		}
		
		Comparable series = model.getSeries();
		if (series == null) {
			series = DEFAULT_HI_LO_SERIES;
		}
		return new DefaultOHLCDataset(series, items);
	}
	
	private double doubleValue(Number n) {
		return n == null ? 0.0 : n.doubleValue();
	}

	/**
	 * decode LegendItemEntity into key-value pair of Area's componentScope.
	 * @param area the Area where the final attribute is set
	 * @param info the LegendItemEntity to be decoded.
	 */
	private void decodeLegendInfo(Area area, LegendItemEntity info, Chart chart) {
		if (info == null) {
			return;
		}
		final ChartModel model = chart.getModel(); 
		final int seq = ((Integer)chart.getAttribute("LEGEND_SEQ")).intValue();
		
		if (model instanceof PieModel) {
			Comparable category = ((PieModel)model).getCategory(seq);
			area.setAttribute("category", category);
			area.setAttribute("value", ((PieModel)model).getValue(category));
		    if (chart.isShowTooltiptext() && info.getToolTipText() == null) {
		    	area.setTooltiptext(category.toString());
		    }
		} else if (model instanceof CategoryModel) {
			Comparable series = ((CategoryModel)model).getSeries(seq);
			area.setAttribute("series", series);
		    if (chart.isShowTooltiptext() && info.getToolTipText() == null) {
		    	area.setTooltiptext(series.toString());
		    }
		} else if (model instanceof XYModel) {
			Comparable series = ((XYModel)model).getSeries(seq);
			area.setAttribute("series", series);
		    if (chart.isShowTooltiptext() && info.getToolTipText() == null) {
		    	area.setTooltiptext(series.toString());
		    }
		} else if (model instanceof HiLoModel) {
			Comparable series = ((HiLoModel)model).getSeries();
			if (series == null) {
				series = DEFAULT_HI_LO_SERIES;
			}
			area.setAttribute("series", series);
		    if (chart.isShowTooltiptext() && info.getToolTipText() == null) {
		    	area.setTooltiptext(series.toString());
		    }
		} else if (model instanceof GanttModel) {
			final Comparable series = info.getSeriesKey();
			area.setAttribute("series", series);
		    if (chart.isShowTooltiptext() && info.getToolTipText() == null) {
		    	area.setTooltiptext(series.toString());
		    }
		}
	}

	/**
	 * decode TickLabelEntity into key-value pair of Area's componentScope.
	 * @param area the Area where the final attribute is set
	 * @param info the TickLabelEntity to be decoded.
	 */
	private void decodeTickLabelInfo(Area area, TickLabelEntity info, Chart chart) {
		if (info == null) {
			return;
		}
		final ChartModel model = chart.getModel(); 
		final int seq = ((Integer)chart.getAttribute("TICK_SEQ")).intValue();
		
		if (model instanceof CategoryModel) {
			Comparable category = ((CategoryModel)model).getCategory(seq);
			area.setAttribute("category", category);
		    if (chart.isShowTooltiptext() && info.getToolTipText() == null) {
		    	area.setTooltiptext(category.toString());
		    }
		}
	}
	
	private void decodeCategoryLabelInfo(Area area, CategoryLabelEntity info, Chart chart) {
		final Comparable category = info.getKey(); 
		area.setAttribute("category", category);
	    if (chart.isShowTooltiptext() && info.getToolTipText() == null) {
	    	area.setTooltiptext(category.toString());
	    }
	}

	/**
	 * decode PieSectionEntity into key-value pair of Area's componentScope.
	 * @param area the Area where the final attribute is set
	 * @param info the PieSectionEntity to be decoded.
	 */
	private void decodePieInfo(Area area, PieSectionEntity info) {
		if (info == null) {
			return;
		}
		
		PieDataset dataset = info.getDataset();
		Comparable category = info.getSectionKey();
		area.setAttribute("category", category);
		area.setAttribute("value", dataset.getValue(category));
	}
	
	/**
	 * decode CategoryItemEntity into key-value pair of Area's componentScope.
	 * @param area the Area where the final attribute is set
	 * @param info the CategoryItemEntity to be decoded.
	 */
	private void decodeCategoryInfo(Area area, CategoryItemEntity info) {
		if (info == null) {
			return;
		}
		
		CategoryDataset dataset = info.getDataset();
		Comparable category = info.getColumnKey();
		Comparable series = info.getRowKey();
		
		area.setAttribute("series", series);
		area.setAttribute("category", category);
		
		if (dataset instanceof GanttCategoryDataset) {
			final GanttCategoryDataset gd = (GanttCategoryDataset) dataset;
			area.setAttribute("start", gd.getStartValue(series, category));
			area.setAttribute("end", gd.getEndValue(series, category));
			area.setAttribute("percent", gd.getPercentComplete(series, category));
		} else {
			area.setAttribute("value", dataset.getValue(series, category));
		}
	}

	/**
	 * decode XYItemEntity into key-value pair of Area's componentScope.
	 * @param area the Area where the final attribute is set
	 * @param info the XYItemEntity to be decoded.
	 * @param chart TODO
	 */
	private void decodeXYInfo(Area area, XYItemEntity info, Chart chart) {
		if (info == null) {
			return;
		}
		TimeZone tz = chart.getTimeZone();
		if (tz == null) tz = TimeZones.getCurrent();

		XYDataset dataset = info.getDataset();
		int si = info.getSeriesIndex();
		int ii = info.getItem();

		area.setAttribute("series", dataset.getSeriesKey(si));
		
		if (dataset instanceof OHLCDataset) {
			OHLCDataset ds = (OHLCDataset) dataset;
			area.setAttribute("date", new Date(ds.getX(si, ii).longValue()));
			area.setAttribute("open", ds.getOpen(si, ii));
			area.setAttribute("high", ds.getHigh(si, ii));
			area.setAttribute("low", ds.getLow(si, ii));
			area.setAttribute("close", ds.getClose(si, ii));
			area.setAttribute("volume", ds.getVolume(si, ii));
		} else if (dataset instanceof XYZDataset) {
			XYZDataset ds = (XYZDataset) dataset;
			area.setAttribute("x", ds.getX(si, ii));
			area.setAttribute("y", ds.getY(si, ii));
			area.setAttribute("z", ds.getZ(si, ii));
		} else {
			area.setAttribute("x", dataset.getX(si, ii));
			area.setAttribute("y", dataset.getY(si, ii));
		}
		
	}
	
	//-- Chart specific implementation --//
	/** base chart */
	abstract private class ChartImpl {
		abstract void render(Chart chart, Area area, ChartEntity info);
		abstract JFreeChart createChart(Chart chart);
	}
	
	/** pie chart */
	private class Pie extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof PieSectionEntity) {
				area.setAttribute("entity", "DATA");
				decodePieInfo(area, (PieSectionEntity)info);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		
		protected PieDataset getDataset(ChartModel model) {
			if (model instanceof CategoryModel) {
				return CategoryModelToPieDataset((CategoryModel)model);
			} else if (model instanceof PieModel) {
				return PieModelToPieDataset((PieModel) model);
			} else {
				throw new UiException("model must be a org.zkoss.zul.PieModel or a org.zkoss.zul.CategoryModel");
			}
		}			
		
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			return ChartFactory.createPieChart(
				chart.getTitle(), 
				getDataset(model), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}
	
	/** pie3d chart */
	private class Pie3d extends Pie {
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			return ChartFactory.createPieChart3D(
				chart.getTitle(), 
				getDataset(model), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}		
		
	/** ring chart */
	private class Ring extends Pie {
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			return ChartFactory.createRingChart(
				chart.getTitle(), 
				getDataset(model), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}		

	/** bar chart */
	private class Bar extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity) info, chart);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer)chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("TICK_SEQ", seq);
				decodeTickLabelInfo(area, (TickLabelEntity) info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (model instanceof CategoryModel) {
				return ChartFactory.createBarChart(
					chart.getTitle(),
					chart.getXAxis(),
					chart.getYAxis(),
					CategoryModelToCategoryDataset((CategoryModel)model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else if (model instanceof XYModel) {
				return ChartFactory.createXYBarChart(
					chart.getTitle(),
					chart.getXAxis(),
					false,
					chart.getYAxis(),
					(IntervalXYDataset) XYModelToXYDataset((XYModel)model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else {
				throw new UiException("model must be a org.zkoss.zul.CategoryModel or a org.zkoss.zul.XYModel");
			}
		}
	}

	/** bar3d chart */
	private class Bar3d extends Bar {
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a org.zkoss.zul.CategoryModel");
			}
			return ChartFactory.createBarChart3D(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				CategoryModelToCategoryDataset((CategoryModel)model),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}
	
	/** area chart */
	private class AreaImpl extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity) info, chart);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer)chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("TICK_SEQ", seq);
				decodeTickLabelInfo(area, (TickLabelEntity) info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (model instanceof CategoryModel) {
				return ChartFactory.createAreaChart(
					chart.getTitle(),
					chart.getXAxis(),
					chart.getYAxis(),
					CategoryModelToCategoryDataset((CategoryModel)model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else if (model instanceof XYModel) {
				return ChartFactory.createXYAreaChart(
					chart.getTitle(),
					chart.getXAxis(),
					chart.getYAxis(),
					XYModelToXYDataset((XYModel)model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else {
				throw new UiException("model must be a org.zkoss.zul.CategoryModel or a org.zkoss.zul.XYModel");
			}
				
		}
	}

	/** line chart */
	private class Line extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity) info, chart);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer)chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("TICK_SEQ", seq);
				decodeTickLabelInfo(area, (TickLabelEntity) info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (model instanceof CategoryModel) {
				return ChartFactory.createLineChart(
					chart.getTitle(),
					chart.getXAxis(),
					chart.getYAxis(),
					CategoryModelToCategoryDataset((CategoryModel) model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else if (model instanceof XYModel) {
				return ChartFactory.createXYLineChart(
					chart.getTitle(),
					chart.getXAxis(),
					chart.getYAxis(),
					XYModelToXYDataset((XYModel) model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else {
				throw new UiException("model must be a org.zkoss.zul.CategoryModel or a org.zkoss.zul.XYModel");
			}
		}
	}

	/** line3d chart */
	private class Line3d extends Line {
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a org.zkoss.zul.CategoryModel");
			}
			return ChartFactory.createLineChart3D(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				CategoryModelToCategoryDataset((CategoryModel) model),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** stackedbar chart */
	private class StackedBar extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer)chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("TICK_SEQ", seq);
				decodeTickLabelInfo(area, (TickLabelEntity) info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a org.zkoss.zul.CategoryModel");
			}
			return ChartFactory.createStackedBarChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				CategoryModelToCategoryDataset((CategoryModel)model),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** stackedbar3d chart */
	private class StackedBar3d extends StackedBar {
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a org.zkoss.zul.CategoryModel");
			}
			return ChartFactory.createStackedBarChart3D(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				CategoryModelToCategoryDataset((CategoryModel)model),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** stackedarea chart */
	//note: cutting area coordinate is not correct.
	private class StackedArea extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity) info, chart);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer)chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("TICK_SEQ", seq);
				decodeTickLabelInfo(area, (TickLabelEntity) info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}

		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (model instanceof CategoryModel) {
				return ChartFactory.createStackedAreaChart(
					chart.getTitle(),
					chart.getXAxis(),
					chart.getYAxis(),
					CategoryModelToCategoryDataset((CategoryModel)model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else if (model instanceof XYModel) {
				return ChartFactory.createStackedXYAreaChart(
					chart.getTitle(),
					chart.getXAxis(),
					chart.getYAxis(),
					XYModelToTableXYDataset((XYModel) model),
					getOrientation(chart.getOrient()), 
					chart.isShowLegend(), 
					chart.isShowTooltiptext(), true);
			} else {
				throw new UiException("model must be a org.zkoss.zul.CategoryModel or a org.zkoss.zul.XYModel");
			}
		}
	}

	/** waterfall chart */
	//note: cuttin area corrdinate is not correct.
	private class Waterfall extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeCategoryInfo(area, (CategoryItemEntity)info);
			} else if (info instanceof TickLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				Integer seq = (Integer)chart.getAttribute("TICK_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("TICK_SEQ", seq);
				decodeTickLabelInfo(area, (TickLabelEntity) info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof CategoryModel)) {
				throw new UiException("model must be a org.zkoss.zul.CategoryModel");
			}
			return ChartFactory.createWaterfallChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				CategoryModelToCategoryDataset((CategoryModel)model),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** polar chart */
	private class Polar extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYModel");
			}
			return ChartFactory.createPolarChart(
				chart.getTitle(),
				XYModelToXYDataset((XYModel)model),
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** scatter chart */
	private class Scatter extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity)info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYModel");
			}
			return ChartFactory.createScatterPlot(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				XYModelToXYDataset((XYModel)model),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** timeseries chart */
	private class TimeSeries extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity) info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYModel");
			}
			final JFreeChart jchart = ChartFactory.createTimeSeriesChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				XYModelToTimeDataset((XYModel)model, chart),
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
			setupDateAxis(jchart, chart);
			return jchart;
		}
	}
	
	private void setupDateAxis(JFreeChart jchart, Chart chart) {
		final Plot plot = jchart.getPlot();
		final DateAxis axisX = (DateAxis) ((XYPlot)plot).getDomainAxis();
		final TimeZone zone = chart.getTimeZone();
		if (zone != null) {
			axisX.setTimeZone(zone);
		}
		if (chart.getDateFormat() != null) {
			axisX.setDateFormatOverride(_dateFormat);
		}
	}

	/** steparea chart */
	private class StepArea extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity)info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYModel");
			}
			return ChartFactory.createXYStepAreaChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				XYModelToXYDataset((XYModel)model),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** step chart */
	private class Step extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity)info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYModel");
			}
			return ChartFactory.createXYStepChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				XYModelToXYDataset((XYModel)model),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}
	
	/** histogram */
	private class Histogram extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity)info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof XYModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYModel");
			}
			return ChartFactory.createHistogram(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				(IntervalXYDataset)XYModelToXYDataset((XYModel)model),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** candlestick */
	private class Candlestick extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity)info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof HiLoModel)) {
				throw new UiException("model must be a org.zkoss.zul.HiLoModel");
			}
			final JFreeChart jchart = ChartFactory.createCandlestickChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				HiLoModelToOHLCDataset((HiLoModel)model),
				chart.isShowLegend());
			setupDateAxis(jchart, chart);
			return jchart;
		}
	}

	/** highlow */
	private class Highlow extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity)info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof HiLoModel)) {
				throw new UiException("model must be a org.zkoss.zul.HiLoModel");
			}
			final JFreeChart jchart = ChartFactory.createHighLowChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				HiLoModelToOHLCDataset((HiLoModel)model),
				chart.isShowLegend());
			setupDateAxis(jchart, chart);
			return jchart;
		}
	}

	/** bubble 
	 * @since 3.5.0
	 */
	private class Bubble extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity)info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof XYZModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYZModel");
			}
			return ChartFactory.createBubbleChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				XYZModelToXYZDataset((XYZModel)model),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
	}

	/** wafermap 
	 * @since 3.5.0
	 */
	private class Wafermap extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
//		System.out.println("info:"+info);
/*			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof XYItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeXYInfo(area, (XYItemEntity)info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
*/		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof WaferMapModel)) {
				throw new UiException("model must be a org.zkoss.zul.WaferMapModel");
			}
			final JFreeChart jfchart = ChartFactory.createWaferMapChart(
				chart.getTitle(),
				WaferMapModelToWaferMapDataset((WaferMapModel)model),
				getOrientation(chart.getOrient()), 
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
				
			final WaferMapRenderer renderer = new MyWaferMapRenderer(); //workaround the paintList not ready issue
			((WaferMapPlot)jfchart.getPlot()).setRenderer(renderer);
			return jfchart;
		}

	}
	//20080211, Henri Chen: To workaround null Legend fillPaint issue, we override the WaferMapRenderer
	//to call lookupSeriesPaint instead of getSeriesPaint() directly.
	private static class MyWaferMapRenderer extends WaferMapRenderer {
		private boolean _inrender = false;
		public Paint getSeriesPaint(int series) {
			if (_inrender) {
				return null; //avoid endless looping
			}
			
			final Paint paint = super.getSeriesPaint(series);
			if (paint != null) {
				return paint;
			}
			
			final boolean pre = _inrender;
			try {
				_inrender = true;
				return lookupSeriesPaint(series); //will recursivly callback to getSeriesPaint() method
			} finally {
				_inrender = pre;
			}
		}
	}

	/** gantt 
	 * @since 3.5.0
	 */
	private class Gantt extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else if (info instanceof CategoryLabelEntity) {
				area.setAttribute("entity", "CATEGORY");
				decodeCategoryLabelInfo(area, (CategoryLabelEntity) info, chart);
			} else if (info instanceof CategoryItemEntity) {
				area.setAttribute("entity", "DATA");
				decodeCategoryInfo(area, (CategoryItemEntity) info);
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(ganttTooltip(chart, area));
				}
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof GanttModel)) {
				throw new UiException("model must be a org.zkoss.zul.GanttModel");
			}
			return ChartFactory.createGanttChart(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				GanttModelToGanttDataset((GanttModel)model),
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
		}
		private String ganttTooltip(Chart chart, Area area) {
			final long start = ((Number)area.getAttribute("start")).longValue();
			final Date startDate = new Date(start);  
			final long end = ((Number)area.getAttribute("end")).longValue();
			final Date endDate = new Date(end);
			final Number percent = ((Number)area.getAttribute("percent"));
			return getGanttTaskTooltip(startDate, endDate, percent);
		}
	}
	
	/**
	 * Returns data tooltiptext of the GanttTask.
	 * @param start the starting date
	 * @param end the ending date
	 * @param percent the complete percentage
	 * @return data tooltiptext of the GanttTask.
	 */
	protected String getGanttTaskTooltip(Date start, Date end, Number percent) {
		return new MessageFormat("{2,number,0.0%}, {0} ~ {1}")
			.format(new Object[] {_dateFormat.format(start), _dateFormat.format(end), percent});
	}

	/** wind 
	 * @since 3.5.0
	 */
	private class Wind extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
//			System.out.println("wind info:"+info);
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
		}
		public JFreeChart createChart(Chart chart) {
			ChartModel model = (ChartModel) chart.getModel();
			if (!(model instanceof XYZModel)) {
				throw new UiException("model must be a org.zkoss.zul.XYZModel");
			}
			final JFreeChart jchart = ChartFactory.createWindPlot(
				chart.getTitle(),
				chart.getXAxis(),
				chart.getYAxis(),
				XYZModelToWindDataset((XYZModel)model),
				chart.isShowLegend(), 
				chart.isShowTooltiptext(), true);
			setupDateAxis(jchart, chart);
			return jchart;
		}
	}

	/** dial 
	 * @since 3.6.3
	 */
	private class Dial extends ChartImpl {
		public void render(Chart chart, Area area, ChartEntity info) {
/*			System.out.println("dial info:"+info);
			if (info instanceof LegendItemEntity) {
				area.setAttribute("entity", "LEGEND");
				Integer seq = (Integer)chart.getAttribute("LEGEND_SEQ");
				seq = seq == null ? new Integer(0) : new Integer(seq.intValue()+1);
				chart.setAttribute("LEGEND_SEQ", seq);
				decodeLegendInfo(area, (LegendItemEntity)info, chart);
			} else {
				area.setAttribute("entity", "TITLE");
				if (chart.isShowTooltiptext()) {
					area.setTooltiptext(chart.getTitle());
				}
			}
*/		}
		
		public JFreeChart createChart(Chart chart) {
			final ChartModel model0 = (ChartModel) chart.getModel();
			if (!(model0 instanceof DialModel)) {
				throw new UiException("model must be a org.zkoss.zul.DialModel");
			}
			final DialPlot plot = new DialPlot();
			plot.setView(0.0, 0.0, 1.0, 1.0);
			final DialModel model = (DialModel) model0;
			
			//prepare the DialFrame
			//TODO ArcDialFrame?
			StandardDialFrame dialFrame = new StandardDialFrame();
			final int[] bgRGB = model.getFrameBgRGB();
			if (bgRGB != null) {
				dialFrame.setBackgroundPaint(new Color(bgRGB[0], bgRGB[1], bgRGB[2], model.getFrameBgAlpha()));
			}
			final int[] fgRGB = model.getFrameBgRGB();
			if (fgRGB != null) {
				dialFrame.setForegroundPaint(new Color(fgRGB[0], fgRGB[1], fgRGB[2]));
			}
			//new ArcDialFrame(-120, -300)
			//dialFrame.setInnerRadius(0.2);
			//dialFrame.setOuterRadius(0.95);
			plot.setDialFrame(dialFrame);
			
			//prepare DialBackground
			final int[] bgRGB2 = model.getFrameBgRGB2();
			if (bgRGB2 != null) {
				final GradientPaint gp = new GradientPaint(
					new Point(), new Color(bgRGB[0], bgRGB[1], bgRGB[2]), 
					new Point(), new Color(bgRGB2[0], bgRGB2[1], bgRGB2[2]));
				final DialBackground db = new DialBackground(gp);
				final String direction = model.getGradientDirection();
				GradientPaintTransformType type = GradientPaintTransformType.VERTICAL;
				if ("vertical".equalsIgnoreCase(direction)) {
					type = GradientPaintTransformType.VERTICAL;
				} else if ("horizontal".equalsIgnoreCase(direction)) {
					type = GradientPaintTransformType.HORIZONTAL;
				} else if ("center_vertical".equalsIgnoreCase(direction)) {
					type = GradientPaintTransformType.CENTER_VERTICAL;
				} else if ("center_horizontal".equalsIgnoreCase(direction)) {
					type = GradientPaintTransformType.CENTER_HORIZONTAL;
				} else {
					type = GradientPaintTransformType.VERTICAL;
				}
				db.setGradientPaintTransformer(new StandardGradientPaintTransformer(type));
				plot.setBackground(db);
			}
			
			//prepare DialCap
			final DialCap cap = new DialCap();
			final double capRadius = model.getCapRadius();
			cap.setRadius(capRadius);
			plot.setCap(cap);
			
			//handle the dial data scale.
			for (int j=0, len=model.size(); j < len; ++j) {
				final DialModelScale scale = model.getScale(j);
				
				//prepare the dataset
				final DefaultValueDataset dataset0 = new DefaultValueDataset(scale.getValue());
				plot.setDataset(0, dataset0);
	
				//prepare the text annotation
				final double textRadius = scale.getTextRadius();
				final String text = scale.getText();
				if (text != null && text.length() > 0 && textRadius > 0) {
					DialTextAnnotation annotation0 = new DialTextAnnotation(text);
					Font font = scale.getTextFont();
					if (font == null) {
						font = new Font("Dialog", Font.BOLD, 14);
					}
					annotation0.setFont(font);
					annotation0.setRadius(textRadius);
					plot.addLayer(annotation0);
				}
				
				//prepare the value indicator
				final double valueRadius = scale.getValueRadius();
				if (valueRadius > 0) {
					DialValueIndicator dvi = new DialValueIndicator(j);
					Font font = scale.getValueFont();
					if (font == null) {
						font = new Font("Dialog", Font.PLAIN, 10);
					}
					dvi.setFont(font);
					dvi.setOutlinePaint(Color.darkGray);
					dvi.setRadius(valueRadius);
					dvi.setAngle(scale.getValueAngle());
					plot.addLayer(dvi);
				}
				
				//prepare the scale
				double low = scale.getScaleLowerBound();
				double up = scale.getScaleUpperBound();
				double sa = scale.getScaleStartAngle();
				double ext = scale.getScaleExtent();
				double ti = scale.getMajorTickInterval();
				int tc = scale.getMinorTickCount();
				
				StandardDialScale dscale = new StandardDialScale(low, up, sa, ext, ti, (int) tc);
				
				//handle the tick
				dscale.setTickRadius(scale.getTickRadius());
				dscale.setTickLabelOffset(scale.getTickLabelOffset());
				Font font = scale.getTickFont();
				if (font == null) {
					font = new Font("Dialog", Font.PLAIN, 14);
				}
				dscale.setTickLabelFont(font);
				int[] tickRGB = scale.getTickRGB();
				if (tickRGB != null) {
					Paint tickColor = new Color(tickRGB[0], tickRGB[1], tickRGB[2]);
					dscale.setMajorTickPaint(tickColor);
					dscale.setMinorTickPaint(tickColor);
				}
				plot.addScale(j, dscale);
				plot.mapDatasetToScale(j, j);
	
				//prepare color ranges
				for(int k=0, klen=scale.rangeSize(); k < klen; ++k) {
					final DialModelRange rng = scale.getRange(k);
					int[] rngRGB = rng.getRangeRGB();
					if (rngRGB == null) {
						rngRGB = new int[] {0x00, 0x00, 0xFF};
					}
					final StandardDialRange range = new StandardDialRange(rng.getLowerBound(), rng.getUpperBound(), new Color(rngRGB[0], rngRGB[1], rngRGB[2]));
		            range.setInnerRadius(rng.getInnerRadius());
		            range.setOuterRadius(rng.getOuterRadius());
		            plot.addLayer(range);
				}
				
	            //prepare the needle (pin or pointer), radius/fillPaint/outlinePaint/
				final String type = scale.getNeedleType();
				final double needleRadius = scale.getNeedleRadius();
				int[] needleRGB = scale.getNeedleRGB();
				if (needleRGB == null) {
					needleRGB = model.getFrameFgRGB();
				}
				DialPointer needle = new DialPointer.Pointer(j);
				needle.setRadius(needleRadius);
				if ("pin".equalsIgnoreCase(type)) {
					needle = new DialPointer.Pin(j);
					((DialPointer.Pin)needle).setPaint(new Color(needleRGB[0], needleRGB[1], needleRGB[2]));
				} else {
					((DialPointer.Pointer)needle).setFillPaint(new Color(needleRGB[0], needleRGB[1], needleRGB[2]));
				}
				
				plot.addLayer(needle);
			}
			//prepare JFreeChart
			JFreeChart jchart = new JFreeChart(plot);
			
			return jchart;
		}
	}
	
	private PlotOrientation getOrientation(String orient) {
		return "horizontal".equals(orient) ?
			PlotOrientation.HORIZONTAL : PlotOrientation.VERTICAL;
	}
}
