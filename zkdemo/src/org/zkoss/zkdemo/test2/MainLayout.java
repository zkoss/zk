/* MainLayout.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/12/11 2007, Created by Ian Tsai
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

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
	final static String[] skipList = new String[] { INDEX, CONFIG };
	final static String path = "/test2";
	final static String tempFile = "/test2/tempXYZ.zul";
	
	static Properties prop;

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
			prop.load(new FileInputStream(new File(context.getRealPath(path
					+ "/" + CONFIG))));
		} catch (IOException ex) {
			log.warning("Ingored: failed to load a properties file, \nCause: "
					+ ex.getMessage());
		}
		iframe = (Iframe) getFellow("w2").getFellow("ifr");
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

				iframe.setSrc(path + "/" + disFileStr);
				if (codeView != null) {
					ServletContext context = ServletFns
							.getCurrentServletContext();
					InputStream in = context.getResourceAsStream(path + "/"
							+ disFileStr);

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
				iframe.setSrc(tempFile + "?tid=" + (new Date()).getTime());
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
		File file = new File(context.getRealPath(tempFile));
		Files.copy(file, new ByteArrayInputStream(zulContent.getBytes()));
	}

	public File getCurrentRealFile() {
		int index = lb.getSelectedIndex();
		String disFileStr = path + "/"
				+ ((File) fileModel.get(index)).getName();
		ServletContext context = ServletFns.getCurrentServletContext();
		return new File(context.getRealPath(disFileStr));
	}

	public void updateModel() {
		fileModel.clear();
		final String r = getDesktop().getWebApp().getRealPath("/");
		final File test2 = new File(r, path);
		final String pattern = ((Textbox) getFellow("w1").getFellow("fnf"))
				.getValue();
		final boolean reg = ((Checkbox) getFellow("w1").getFellow("reg")).isChecked();
		fileModel.addAll(Arrays.asList(test2.listFiles(new MyFilenameFilter(pattern, reg))));
	}

	public void updateModelByTag() {
		fileModel.clear();
		final String r = getDesktop().getWebApp().getRealPath("/");
		final File test2 = new File(r, path);
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
				for (int k = 0; k < vals.length; k++)
					if (ptns[j].trim().equalsIgnoreCase(vals[k].trim())) {
						m = true;
						break;
					}
				if (!m) {
					match = false;
					break;
				}
			}
			if (match)
				linkedList.add(me.getKey().toString());
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
		final File test2 = new File(r, path);
		final File[] files = test2.listFiles(new MyFilenameFilter("", false));
		for (int j = 0; j < files.length; j++) {
			if(!prop.containsKey(files[j].getName()))
				fileModel.add(files[j]);
		}
		
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
			for (int i = 0; i < skipList.length; i++)
				if (name.equals(skipList[i]))
					return false;

			if (name.endsWith(".zul")) {
				name = name.substring(0, name.length() - 4);
			} else if (name.endsWith(".zhtml")) {
				name = name.substring(0, name.length() - 5);
			} else if (name.endsWith(".jsp")) {
				name = name.substring(0, name.length() - 4);
			} else {
				return false;
			}

			if (reg) {
				Matcher matcher = pattern.matcher(name);
				return matcher.matches();
			} else {
				return name.toUpperCase().indexOf(str.toUpperCase()) >= 0;
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
