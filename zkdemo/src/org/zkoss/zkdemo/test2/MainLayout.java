/* MainLayout.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/12/11 2007, Created by Ian Tsai
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.zkoss.io.Files;
import org.zkoss.util.logging.Log;
import org.zkoss.web.fn.ServletFns;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * @author Ian Tsai
 * @author jumperchen
 */
public class MainLayout extends Borderlayout {
	private static final Log log = Log.lookup(MainLayout.class);

	ListModelList fileModel = new ListModelList();
	Map relatedFileModel = new LinkedHashMap();

	final static String INDEX = "index.zul";
	final static String CONFIG = "config.properties";
	final static String TEMP_FILE = "tempXYZ.zul";
	final static String[] SKIP_LIST = new String[] { INDEX, CONFIG, TEMP_FILE};
	final static String PATH = "/test2/";
	
	private Properties prop;

	public MainLayout() {
		fileModel = new ListModelList();
	}

	Iframe iframe;
	Textbox codeView;
	Listbox lb;

	public void onCreate() {
		try {
			ServletContext context = ServletFns.getCurrentServletContext();
			prop = new Properties();
			prop.load(new FileInputStream(new File(context.getRealPath(PATH + CONFIG))));
		} catch (IOException ex) {
			log.warning("Ingored: failed to load a properties file, \nCause: "
					+ ex.getMessage());
		}
		iframe = (Iframe) getFellow("ifr");
		lb = (Listbox) getFellow("w1").getFellow("lb");
		lb.setItemRenderer(new FileitemRenderer());
		lb.setModel(fileModel);
		updateModel();

		lb.addEventListener("onSelect", new EventListener() {

			public void onEvent(Event event) throws Exception {
				int index = lb.getSelectedIndex();
				String disFileStr = getFileName(index);

				if (((Checkbox) getFellow("w1").getFellow("newb")).isChecked()) {
					Clients.evalJavaScript("newWindow(\"" + disFileStr + "\")");
				}

				iframe.setSrc(PATH + disFileStr);
				if (codeView != null) {
					ServletContext context = ServletFns
							.getCurrentServletContext();
					InputStream in = context.getResourceAsStream(PATH + disFileStr);

					byte[] bytes = Files.readAll(in);
					codeView.setValue(new String(bytes));
				}
			}
		});
		getFellow("w1").getFellow("fnf").addEventListener(Events.ON_CHANGE,
				new EventListener() {

					public void onEvent(Event arg0) throws Exception {
						updateModel();
					}
				});
		getFellow("w1").getFellow("fnf").addEventListener(Events.ON_OK,
				new EventListener() {

					public void onEvent(Event arg0) throws Exception {
						//nothing to do, because it has registered onChange event.
					}
				});

		getFellow("w1").getFellow("reg").addEventListener(Events.ON_CHECK,
				new EventListener() {
					public void onEvent(Event arg0) throws Exception {
						updateModel();
					}
				});

		getFellow("w1").getFellow("fnt").addEventListener(Events.ON_CHANGE,
				new EventListener() {

					public void onEvent(Event arg0) throws Exception {
						updateModelByTag();
					}
				});
		getFellow("w1").getFellow("fnt").addEventListener(Events.ON_OK,
				new EventListener() {

					public void onEvent(Event arg0) throws Exception {
						// nothing to do, because it has registered onChange event.
					}
				});
	}

	public void reload() {
		String src = iframe.getSrc();
		iframe.setSrc(src + "?tid=" + (new Date()).getTime());

	}

	public void reloadCodeView() {
		if (codeView != null) {
			try {
				saveToTemp(codeView.getValue());
				iframe.setSrc(PATH + TEMP_FILE + "?tid=" + (new Date()).getTime());
			} catch (WrongValueException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getFileName(int index) {
		return ((File) fileModel.get(index)).getName();
	}

	public void saveBack() throws WrongValueException, IOException {
		if (codeView == null)
			return;
		reloadCodeView();
		File file = getCurrentRealFile();
		Files.copy(file, new ByteArrayInputStream(codeView.getValue()
				.getBytes()));
	}

	private void saveToTemp(String zulContent) throws IOException {
		ServletContext context = ServletFns.getCurrentServletContext();
		File file = new File(context.getRealPath(PATH + TEMP_FILE));
		Files.copy(file, new ByteArrayInputStream(zulContent.getBytes()));
	}

	public File getCurrentRealFile() {
		int index = lb.getSelectedIndex();
		String disFileStr = PATH + ((File) fileModel.get(index)).getName();
		ServletContext context = ServletFns.getCurrentServletContext();
		return new File(context.getRealPath(disFileStr));
	}

	public void updateModel() {
		fileModel.clear();
		final String r = getDesktop().getWebApp().getRealPath("/");
		final File test2 = new File(r, PATH);
		final String pattern = ((Textbox) getFellow("w1").getFellow("fnf"))
				.getValue();
		final boolean reg = ((Checkbox) getFellow("w1").getFellow("reg")).isChecked();
		fileModel.addAll(Arrays.asList(test2.listFiles(new MyFilenameFilter(pattern, reg))));
	}
	public void exportFileName() throws SuspendNotAllowedException, InterruptedException {
		if(fileModel.isEmpty()) return;
		final StringBuffer sb = new StringBuffer();
		for(Iterator it = fileModel.iterator();it.hasNext();)
			sb.append(((File)it.next()).getName()).append("\n");
		Window w = new Window();
		w.setTitle("Export File Name - ["+ fileModel.size() +"]");
		w.setWidth("300px");
		w.setClosable(true);
		w.setPage(this.getPage());
		Textbox t = new Textbox();
		t.setWidth("98%");
		t.setMultiline(true);
		t.setRows(20);
		t.setParent(w);
		t.setValue(sb.toString());
		t.focus();
		w.doModal();
	}
	public void importFileName() throws SuspendNotAllowedException, InterruptedException {
		final Window w = new Window();
		w.setTitle("Import File Name");
		w.setWidth("300px");
		w.setClosable(true);
		w.setPage(this.getPage());
		final Textbox t = new Textbox();
		t.setWidth("98%");
		t.setMultiline(true);
		t.setRows(20);
		t.setParent(w);
		t.setConstraint("no empty");
		final Button ok = new Button("OK");
		final Button cancel = new Button("Cancel");
		ok.setParent(w);
		ok.addEventListener(Events.ON_CLICK,
				new EventListener() {
			public void onEvent(Event e) throws Exception {
				String val = t.getValue();
				String[] vals = val.trim().split("\n");
				final StringBuffer sb = new StringBuffer();
				fileModel.clear();
				final String r = getDesktop().getWebApp().getRealPath("/");
				final File test2 = new File(r, PATH);
				final File[] files = test2.listFiles(new MyFilenameFilter("", false));
				for(int j = 0; j < vals.length; j++) {
					boolean exist = false;
					for (int i = 0; i < files.length; i++) {
						if (vals[j].trim().equalsIgnoreCase(files[i].getName())) {
							fileModel.add(files[i]);
							exist = true;
							break;
						}
					}
					if (!exist) sb.append(vals[j].trim()).append("\n");
				}
				if (sb.toString().trim().length() > 0) t.setValue("Failed File Name:\n" + sb.toString());
				w.insertBefore(new Label(" \nsuccess : [" + fileModel.size() + "] failed : [" + (vals.length - fileModel.size())+ "]"),
						(Component)w.getChildren().get(0));
				ok.detach();
				cancel.detach();
			}
		});
		cancel.setParent(w);
		cancel.addEventListener(Events.ON_CLICK,
				new EventListener() {
			public void onEvent(Event e) throws Exception {
				w.detach();
			}
		});
		t.focus();
		w.doModal();
	}
	public void updateModelByTag() {
		fileModel.clear();
		final String r = getDesktop().getWebApp().getRealPath("/");
		final File test2 = new File(r, PATH);
		final String pattern = ((Textbox) getFellow("w1").getFellow("fnt"))
				.getValue();
		final String[] ptns = pattern.split(",");
		final LinkedList linkedList = new LinkedList();
		for (Iterator it = prop.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry) it.next();
			final String[] vals = me.getValue().toString().split(",");
			boolean match = true;
			for (int j = 0; j < ptns.length; j++) {
				boolean m = false;				
				if (!ptns[j].contains("!")) {
					for (int k = 0; k < vals.length; k++) {												
						if (ptns[j].trim().length() > 1 && vals[k].trim().length() > 1) {
							if (vals[k].trim().toLowerCase().indexOf(ptns[j].trim().toLowerCase()) > -1) {
								m = true;
								break;
							}
						} else if (ptns[j].trim().equalsIgnoreCase(vals[k].trim())) {
							m = true;
							break;
						}										
					}
				}
				else
					m = true;				
				if (!m) {
					match = false;
					break;
				}
			}
			if (match)
				linkedList.add(me.getKey().toString());
			
			for (int j = 0; j < ptns.length; j++) {
				boolean m = true;
				if (ptns[j].contains("!")) {
					for (int k = 0; k < vals.length; k++) {										
						if (ptns[j].trim().length() > 1 && (vals[k].trim().length()-1 > 1)) {
							if (vals[k].trim().toLowerCase().indexOf(ptns[j].trim().toLowerCase().substring(1)) > -1) {
								m = false;
								break;
							}
						} else if (ptns[j].trim().substring(1).equalsIgnoreCase(vals[k].trim())) {
							m = false;
							break;
						}										
					}
				}				
				if (!m) {
					match = false;
					break;
				}
			}
			if (!match)
				linkedList.remove(me.getKey().toString());
		}
		final File[] files = test2.listFiles(new MyFilenameFilter("", false));
		for (Iterator it = linkedList.iterator(); it.hasNext();) {
			final String fn = (String) it.next();
			for (int i = 0; i < files.length; i++) {
				if (fn.equalsIgnoreCase(files[i].getName())) {
					fileModel.add(files[i]);
					break;
				}
			}
		}
	}
	
	public void showUncategorized() {
		fileModel.clear();
		final String r = getDesktop().getWebApp().getRealPath("/");
		final File test2 = new File(r, PATH);
		final File[] files = test2.listFiles(new MyFilenameFilter("", false));
		for (int j = 0; j < files.length; j++)
			if(!prop.containsKey(files[j].getName()))
				fileModel.add(files[j]);
		
	}

	static class MyFilenameFilter implements FilenameFilter {
		String str;
		boolean reg;
		Pattern pattern;

		public MyFilenameFilter(String pattern, boolean reg) {
			if (pattern == null)
				pattern = "";
			pattern = pattern.trim();
			this.str = pattern;
			this.reg = reg;
			if (reg) {
				this.pattern = Pattern.compile(str, Pattern.CASE_INSENSITIVE);
			}
		}

		public boolean accept(File dir, String name) {
			for (int i = 0; i < SKIP_LIST.length; i++)
				if (name.equals(SKIP_LIST[i]))
					return false;
			final String n = name; 
			if (name.endsWith(".zul") || name.endsWith(".jsp"))
				name = name.substring(0, name.length() - 4);
			else if (name.endsWith(".zhtml"))
				name = name.substring(0, name.length() - 6);
			else if (!n.equalsIgnoreCase(str))
				return false; // unsupported file type
			
			if (n.equalsIgnoreCase(str))
				return true;
			else if(!name.matches("[a-zA-z0-9]*-[^_]*"))
				return false; // unsupported file pattern
			
			if (reg) {
				Matcher matcher = pattern.matcher(n);
				return matcher.matches();
			} else {
				return n.toUpperCase().indexOf(str.toUpperCase()) >= 0;
			}
		}

	}

	static class FileitemRenderer implements ListitemRenderer {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd mm:ss");

		public void render(Listitem item, Object data) throws Exception {
			File file = (File) data;
			new Listcell(file.getName()).setParent(item);
			new Listcell(format.format(new Date(file.lastModified())))
					.setParent(item);
		}

	}

	public Textbox getCodeView() {
		return codeView;
	}

	public void setCodeView(Textbox codeView) {
		this.codeView = codeView;
	}
}
