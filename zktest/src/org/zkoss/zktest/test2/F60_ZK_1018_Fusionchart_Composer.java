package org.zkoss.zktest.test2;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkmax.zul.Fusionchart;
import org.zkoss.zkmax.zul.fusionchart.GanttTableRenderer;
import org.zkoss.zkmax.zul.fusionchart.config.CategoriesPropertiesMap;
import org.zkoss.zkmax.zul.fusionchart.config.CategoryChartConfig;
import org.zkoss.zkmax.zul.fusionchart.config.ChartConfig;
import org.zkoss.zkmax.zul.fusionchart.config.GanttChartCategoriesPropertiesList.GanttChartCategoriesProperties;
import org.zkoss.zkmax.zul.fusionchart.config.GanttChartConfig;
import org.zkoss.zkmax.zul.fusionchart.config.GanttChartSeriesPropertiesMap;
import org.zkoss.zkmax.zul.fusionchart.config.GanttTableConfig;
import org.zkoss.zkmax.zul.fusionchart.config.GanttTableConfig.GanttRowPropertiesList;
import org.zkoss.zkmax.zul.fusionchart.config.GanttTableConfig.GanttTableColumnPropertiesMap;
import org.zkoss.zkmax.zul.fusionchart.config.PieChartConfig;
import org.zkoss.zkmax.zul.fusionchart.config.SeriesPropertiesMap;
import org.zkoss.zkmax.zul.fusionchart.config.XYChartConfig;
import org.zkoss.zul.Area;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.ChartModel;
import org.zkoss.zul.GanttModel;
import org.zkoss.zul.GanttModel.GanttTask;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.SimplePieModel;
import org.zkoss.zul.SimpleXYModel;
import org.zkoss.zul.XYModel;

public class F60_ZK_1018_Fusionchart_Composer  extends GenericForwardComposer {
	private static final Map DEFAULT_MODEL = new HashMap();
	private static final Map DEFAULT_CONF = new HashMap();
	private static final ChartModel CATE_MODEL = createCategoryModel();
	private static final ChartModel XY_MODEL = createXYModel();
	private static final CategoryChartConfig CATE_CONF = createCategoryChartConfig();
	private static final XYChartConfig XY_CONF = createXYChartConfig();
	
	static {
		DEFAULT_MODEL.put(Chart.PIE, createPieModel());
		DEFAULT_MODEL.put(Chart.BAR, CATE_MODEL);
		DEFAULT_MODEL.put(Chart.STACKED_BAR, CATE_MODEL);
		DEFAULT_MODEL.put(Chart.LINE, XY_MODEL);
		DEFAULT_MODEL.put(Chart.AREA, XY_MODEL);
		DEFAULT_MODEL.put(Chart.STACKED_AREA, XY_MODEL);
		DEFAULT_MODEL.put(Chart.GANTT, createGanttModel());
		DEFAULT_MODEL.put(Chart.COMBINATION, CATE_MODEL);
		
		DEFAULT_CONF.put(Chart.PIE, createPieChartConfig());
		DEFAULT_CONF.put(Chart.BAR, CATE_CONF);
		DEFAULT_CONF.put(Chart.STACKED_BAR, CATE_CONF);
		DEFAULT_CONF.put(Chart.LINE, XY_CONF);
		DEFAULT_CONF.put(Chart.AREA, XY_CONF);
		DEFAULT_CONF.put(Chart.STACKED_AREA, XY_CONF);
		DEFAULT_CONF.put(Chart.GANTT, createGanttChartConfig());
		DEFAULT_CONF.put(Chart.COMBINATION, createCombiCategoryChartConfig());
	}
	
	private Listbox typeList;
	private Chart chart;
	private Fusionchart fchart;
	
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		if (!session.hasAttribute("attr")) {
			session.setAttribute("attr", new AttrController());
			
		}
		comp.setAttribute("attr", session.getAttribute("attr"));
	}

	private static PieModel createPieModel() {
		PieModel piemodel = new SimplePieModel();
		piemodel.setValue("C/C++", new Double(12.5));
		piemodel.setValue("Java", new Double(50.2));
		piemodel.setValue("VB", new Double(20.5));
		piemodel.setValue("PHP", new Double(15.5));
		return piemodel;
	}
	
	private static CategoryModel createCategoryModel() {
		 CategoryModel catmodel = new SimpleCategoryModel();
	        catmodel.setValue("2001", "Q1", new Integer(20));
	        catmodel.setValue("2001", "Q2", new Integer(35));
	        catmodel.setValue("2001", "Q3", new Integer(40));
	        catmodel.setValue("2001", "Q4", new Integer(55));
	        catmodel.setValue("2002", "Q1", new Integer(40));
	        catmodel.setValue("2002", "Q2", new Integer(60));
	        catmodel.setValue("2002", "Q3", new Integer(70));
	        catmodel.setValue("2002", "Q4", new Integer(90));
		return catmodel;
	}

	private static XYModel createXYModel() {
		XYModel xymodel = new SimpleXYModel();
		xymodel.addValue("2001", new Integer(10), new Integer(1459));
		xymodel.addValue("2001", new Integer(20), new Integer(1423));
		xymodel.addValue("2001", new Integer(30), new Integer(1382));
		xymodel.addValue("2001", new Integer(40), new Integer(1356));
		xymodel.addValue("2001", new Integer(50), new Integer(1310));
		xymodel.addValue("2001", new Integer(60), new Integer(1282));
		xymodel.addValue("2001", new Integer(70), new Integer(1247));
		xymodel.addValue("2001", new Integer(80), new Integer(1182));
		
		xymodel.addValue("2002", new Integer(10), new Integer(1188));
		xymodel.addValue("2002", new Integer(20), new Integer(1189));
		xymodel.addValue("2002", new Integer(30), new Integer(1177));
		xymodel.addValue("2002", new Integer(40), new Integer(1175));
		xymodel.addValue("2002", new Integer(50), new Integer(1210));
		xymodel.addValue("2002", new Integer(60), new Integer(1280));
		xymodel.addValue("2002", new Integer(70), new Integer(1390));
		xymodel.addValue("2002", new Integer(80), new Integer(1524));
		return xymodel;
	}

	private static GanttModel createGanttModel() {
		GanttModel ganttmodel = new GanttModel();

		ganttmodel.addValue("Scheduled", new GanttTask("Write Proposal", 
				date(2008, 4, 1), date(2008, 4, 5), 0.0));
		ganttmodel.addValue("Scheduled", new GanttTask("Requirements Analysis",
				date(2008, 4, 10), date(2008, 5, 5), 0.0));
		ganttmodel.addValue("Scheduled", new GanttTask("Design Phase", 
				date(2008, 5, 6), date(2008, 5, 30), 0.0));
		ganttmodel.addValue("Scheduled", new GanttTask("Alpha Implementation",
				date(2008, 6, 3), date(2008, 7, 31), 0.0));

		ganttmodel.addValue("Actual", new GanttTask("Write Proposal", 
				date(2008, 4, 1), date(2008, 4, 3), 0.0));
		ganttmodel.addValue("Actual", new GanttTask("Requirements Analysis", 
						date(2008, 4, 10), date(2008, 5, 15), 0.0));
		ganttmodel.addValue("Actual", new GanttTask("Design Phase", 
				date(2008, 5, 15), date(2008, 6, 17), 0.0));
		ganttmodel.addValue("Actual", new GanttTask("Alpha Implementation", 
				date(2008, 7, 1), date(2008, 9, 12), 0.0));
		
		return ganttmodel;
	}

	private static Date date(int year, int month, int day) {
		final java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.set(year, month - 1, day);
		return calendar.getTime();
	}

	
	
	private static PieChartConfig createPieChartConfig() {
		PieChartConfig config = new PieChartConfig();
		config.setPieFillAlpha(95);
		config.setAnimation(false);
		config.getChartProperties().addProperty("pieBorderColor", "FFFFFF");
		
		CategoriesPropertiesMap cConfig = config.getCategoryPropertiesMap();
		cConfig.createCategoryProperties("C/C++").addProperty("color", "AFD8F8");
		cConfig.createCategoryProperties("Java")
		.addProperty("color", "8BBA00").addProperty("isSliced", "1");
		cConfig.createCategoryProperties(2).addProperty("color", "F6BD0F");
		cConfig.createCategoryProperties(3).addProperty("color", "A66EDD");
		
		return config;
	}
	
	private static CategoryChartConfig createCategoryChartConfig() {
		CategoryChartConfig config = new CategoryChartConfig();
		config.setAnimation(false);
		config.getChartProperties().addProperty("rotateNames", "1");
		
		SeriesPropertiesMap sConfig = config.getSeriesPropertiesMap();
		
		sConfig.createSeriesProperties(0)
			.addProperty("color", "AFD8F8").addProperty("parentYAxis", "P");
		sConfig.createSeriesProperties(1)
			.addProperty("color", "FF8000").addProperty("parentYAxis", "S")
			.addProperty("anchorBorderColor", "FF8000")
			.addProperty("lineThickness", "4");
		
		return config;
	}
	
	private static XYChartConfig createXYChartConfig() {
		XYChartConfig config = new XYChartConfig();
		SeriesPropertiesMap sConfig = config.getSeriesPropertiesMap();
		
		sConfig.createSeriesProperties(0)
			.addProperty("color", "0099FF")
			.addProperty("alpha", "100")
			.addProperty("anchorAlpha", "0")
			.addProperty("lineThickness", "2");
		
		sConfig.createSeriesProperties(1)
			.addProperty("color", "FF8000")
			.addProperty("alpha", "80")
			.addProperty("showAnchors", "0");
		
		return config;
	}
	
	private static CategoryChartConfig createCombiCategoryChartConfig() {
		CategoryChartConfig config = new CategoryChartConfig();
		config.setAnimation(false);
		config.setCanvasBgColor("F6DFD9");
		config.getChartProperties()
			.addProperty("canvasBaseColor", "FE6E54")
			.addProperty("numberPrefix", "$");
		
		
		SeriesPropertiesMap sPropertiesMap = config.getSeriesPropertiesMap();
		
		sPropertiesMap.createSeriesProperties(0)
			.addProperty("color", "9ACCF6").addProperty("alpha", "90");
		sPropertiesMap.createSeriesProperties(1)
			.addProperty("color", "82CF27").addProperty("alpha", "90")
			.addProperty("parentYAxis", "S");
		
		return config;
	}
	
	private static GanttChartConfig createGanttChartConfig() {
		GanttChartConfig config = new GanttChartConfig();
		config.getChartProperties()
			.addProperty("canvasBorderColor", "024455")
			.addProperty("canvasBorderThickness", "0");
		
		GanttChartCategoriesProperties cProps = 
			config.getCategoriesConfig().createCategoriesProperties();
		cProps.addProperty("bgColor", "4567aa");
		
		cProps.createCategoryProperties("Months", 
			date(2008, 4, 1), date(2008, 9, 30))
				.addProperty("align", "center")
				.addProperty("font", "Verdana")
				.addProperty("fontColor", "ffffff")
				.addProperty("isBold", "1")
				.addProperty("fontSize", "16");
		
		config.getHeaderConfig()
			.addProperty("bgColor", "ffffff")
			.addProperty("fontColor", "1288dd")
			.addProperty("fontSize", "10");
		
		config.getProcessConfig()
			.addProperty("bgColor", "4567aa")
			.addProperty("fontColor", "ffffff")
			.addProperty("fontSize", "10")
			.addProperty("headerVAlign", "right")
			.addProperty("headerbgColor", "4567aa")
			.addProperty("headerFontColor", "ffffff")
			.addProperty("headerFontSize", "16")
			.addProperty("width", "80")
			.addProperty("align", "left");
		
		config.getTasksProperties().addProperty("width", "10");
		
		GanttChartSeriesPropertiesMap sConfig = config.getSeriesConfig();
		sConfig.createSeriesProperties("Scheduled")
			.addProperty("color", "4567aa");
		sConfig.createSeriesProperties("Actual")
			.addProperty("color", "cccccc");
		
		
		defineGanttTable(config.getTableConfig());
		
		
		return config;
	}
	
	
	
	private static void defineGanttTable(GanttTableConfig config) {
		config.getChartProperties()
			.addProperty("nameAlign", "left")
			.addProperty("fontColor", "000000")
			.addProperty("fontSize", "10")
			.addProperty("headerBgColor", "00ffff")
			.addProperty("headerFontColor", "4567aa")
			.addProperty("headerFontSize", "11")
			.addProperty("vAlign", "right")
			.addProperty("align", "left");
	
		GanttTableColumnPropertiesMap cConfig = config.getColumnConfig();
		cConfig.createColumnProperties(0)
			.addProperty("headerText", "Dur")
			.addProperty("align", "center")
			.addProperty("headerfontcolor", "ffffff")
			.addProperty("headerbgColor", "4567aa")
			.addProperty("bgColor", "eeeeee");
		
		cConfig.createColumnProperties(1)
			.addProperty("headerText", "Cost")
			.addProperty("align", "right")
			.addProperty("headerfontcolor", "ffffff")
			.addProperty("headerbgColor", "4567aa")
			.addProperty("bgColor", "4567aa")
			.addProperty("bgAlpha", "25");
	}

	private static ListModel createGanttTableModel() {
		ListModelList model = new ListModelList();
		model.add(new String[]{"150","$400"});
		model.add(new String[]{"340","$890"});
		model.add(new String[]{"60","$1234"});
		model.add(new String[]{"20","$230"});
		return model;
	}
	
	private static GanttTableRenderer createGanttTableRenderer() {
		return new GanttTableRenderer() {
			public void render(GanttRowPropertiesList row, Object data) throws Exception {
				String[] s = (String[])data;
				row.createLabel(s[0]);
				row.createLabel(s[1]);
			}
		};
	}
	
	
	public void onSelect$typeList() {
		if (Chart.GANTT.equals(typeList.getSelectedItem().getValue())) {
			chart.setWidth("750");
			chart.setHeight("450");
			fchart.setWidth("750");
			fchart.setHeight("450");
		} else {
			chart.setWidth("500");
			chart.setHeight("250");
			fchart.setWidth("500");
			fchart.setHeight("250");
		}
	}
	
	public void onShowTable(ForwardEvent evt) {
		CheckEvent event = (CheckEvent) evt.getOrigin();
		if (event.isChecked()) {
			fchart.setTableModel(createGanttTableModel());
			fchart.setTableRenderer(createGanttTableRenderer());
		} else {
			fchart.setTableModel(null);
			fchart.setTableRenderer(null);
		}
		
	}
	
	public void onChartClick(ForwardEvent evt) {
		doCategoryChartClick(evt);
	}

	private void doCategoryChartClick(ForwardEvent evt) {
		Event event = (Event) evt.getOrigin();
		if (event instanceof MouseEvent) {
			Component obj = ((MouseEvent) event).getAreaComponent();

			if (obj != null) {
				Area area = (Area) obj;
				System.out.print(area.getAttribute("series") + " = "
						+ area.getTooltiptext());
			}
		} else {
			Map data = (Map) event.getData();
			ChartModel model = ((Fusionchart) event.getTarget()).getModel();
			if (model instanceof CategoryModel) {
				System.out.print(data.get("series") + ", "
						+ data.get("category") + " = " + data.get("value"));
			} else if (model instanceof PieModel) {
				System.out.print(data.get("category") + " = "
						+ data.get("value"));
			} else if (model instanceof XYModel) {
				System.out.print(data.get("series") + " = " + data.get("x")
						+ ", " + data.get("y"));
			} else if (model instanceof GanttModel) {
				GanttModel.GanttTask task = (GanttTask) data.get("task");
				System.out.print(data.get("series") + ", "
						+ task.getDescription() + " = " + task.getStart()
						+ " ~ " + task.getEnd());
			}
		}

		System.out.println();
	}
	
	public class AttrController {
		private boolean threeD = false;
		private String orient = "vertical";
		private String type = "pie";
		private boolean stacked = false;
		private ChartModel model;
		private boolean useChartConfig = false;
		private ChartConfig chartConfig;

		public AttrController() {
			super();
			this.model = (ChartModel) DEFAULT_MODEL.get(type);
		}

		public boolean isThreeD() {
			return threeD;
		}

		public void setThreeD(boolean threeD) {
			this.threeD = threeD;
		}

		public void setOrient(String orient) {
			//"vertical" : "horizontal"
			this.orient = orient;
		}
		
		public String getOrient() {
			return orient;
		}

		public boolean isStacked() {
			return stacked;
		}

		public void setStacked(boolean stacked) {
			this.stacked = stacked;
		}

		public void setType(String type) {
			this.type = type;
			setModel((ChartModel) DEFAULT_MODEL.get(type));
			
			if (useChartConfig)
				setChartConfig((ChartConfig) DEFAULT_CONF.get(type));
		}
		
		public String getType() {
			return type;
		}
		
		public void setModel(ChartModel model) {
			this.model = model;
		}
		
		public ChartModel getModel() {
			return model;
		}
		
		public boolean isUseChartConfig() {
			return useChartConfig;
		}

		public void setUseChartConfig(boolean useChartConfig) {
			this.useChartConfig = useChartConfig;
			setChartConfig(useChartConfig ? 
					(ChartConfig) DEFAULT_CONF.get(type): null);
		}

		public ChartConfig getChartConfig() {
			return chartConfig;
		}

		public void setChartConfig(ChartConfig chartConfig) {
			this.chartConfig = chartConfig;
		}
	}
	
}
