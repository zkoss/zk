/* UiManager.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 5 10:12:21     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.zkoss.zkmob.factory.AlertFactory;
import org.zkoss.zkmob.factory.ChoiceGroupFactory;
import org.zkoss.zkmob.factory.CommandFactory;
import org.zkoss.zkmob.factory.DateFieldFactory;
import org.zkoss.zkmob.factory.FormFactory;
import org.zkoss.zkmob.factory.GaugeFactory;
import org.zkoss.zkmob.factory.ImageItemFactory;
import org.zkoss.zkmob.factory.ListFactory;
import org.zkoss.zkmob.factory.ListItemFactory;
import org.zkoss.zkmob.factory.SpacerFactory;
import org.zkoss.zkmob.factory.StringItemFactory;
import org.zkoss.zkmob.factory.TextBoxFactory;
import org.zkoss.zkmob.factory.TextFieldFactory;
import org.zkoss.zkmob.factory.TickerFactory;
import org.zkoss.zkmob.factory.ZkFactory;
import org.zkoss.zkmob.ui.ZkDesktop;
import org.zkoss.zkmob.ui.ZkForm;

/** 
 * The static facade for UI component management. 
 */
public class UiManager {
	private static Hashtable _uiFactoryMap = new Hashtable(16);
	//predefined a SAXParserFactory
	private static SAXParserFactory _factory = SAXParserFactory.newInstance();
	
	//predefined Ui component factory
	static {
		//virtual components
		new ZkFactory("zk");
		new TickerFactory("tc");
		new ListItemFactory("li");
		new CommandFactory("cm");
		
		//top level window
		new FormFactory("fr");
		new AlertFactory("al");
		new ListFactory("ls");
		new TextBoxFactory("tb");
		
		//items (MIDP 1.0)
		new DateFieldFactory("df");
		new GaugeFactory("gg");
		new ImageItemFactory("ii");
		new StringItemFactory("si");
		new TextFieldFactory("tf");
		new ChoiceGroupFactory("cg");
		
		//items (MIDP 2.0)
		new SpacerFactory("sp");
	}
	
	/** register an UiFactory to a Ui tag name. 
	 * @param name the Ui tag name
	 * @param uiFactory an UiFactory used to create an Ui component
	 */
	public static void registerUiFactory(String name, UiFactory uiFactory) {
		_uiFactoryMap.put(name, uiFactory);
	}

	public static SAXParser getSAXParser() {
		try {
			return _factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e.toString()); 
		} catch (SAXException e) {
			throw new IllegalStateException(e.toString()); 
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static void alert(Display disp, Throwable e) {
		final Alert alert = new Alert("Exception:", e.toString(), null, AlertType.ERROR);
		alert.setTimeout(Alert.FOREVER);
		disp.setCurrent(alert);
	}
	
	/**
	 * given Ui tag name and create an associated Ui component.
	 * @param parent the parent component
	 * @param tag the Ui tag name
	 * @param attrs the Ui tag attributes 
	 * @param hostURL the host URL
	 */
	public static ZkComponent create(ZkComponent parent, String tag, Attributes attrs, String hostURL, String pathURL) {
		UiFactory uiFactory = (UiFactory) _uiFactoryMap.get(tag);

		if (uiFactory != null) {
			return uiFactory.create(parent, tag, attrs, hostURL, pathURL);
		}
		
		throw new IllegalArgumentException("Cannot find the UiFactory of the RMIL tag: "+ tag); 
	}

	public static void loadPageOnThread(Browser browser, String url) {
		new Thread(new PageRequest(browser, url)).start();
	}
	
	/* package */ static void loadPage(Browser browser, String url) {
		try {
			myLoadPage(browser, url);
		} catch (Throwable e) {
			e.printStackTrace();
			alert(browser.getDisplay(), e);
		}
	}
	
	public static String getHostURL(HttpConnection conn) {
		return conn.getProtocol()+"://"+conn.getHost()+(conn.getPort() != 80 ? (":"+conn.getPort()) : "");
	}
	
	public static String getPathURL(HttpConnection conn) {
		String url = conn.getURL();
		int j = url.lastIndexOf('/');
		if (j >= 0) {
			url = url.substring(0, j+1); //include the '/'
		}
		return url;
	}

	public static String prefixURL(String hostURL, String pathURL, String url) {
		if (url != null && !url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("~.")) {
			url = (url.startsWith("/") ? hostURL : pathURL) + url;
		}
		return url;
	}
	
	public static Vector createComponents(ZkComponent parent, InputStream is, String hostURL, String pathURL) throws IOException, SAXException {
	    // Load the responsed page, the current Displayable is put in _current
	    final PageHandler handler = new PageHandler(parent, hostURL, pathURL);
	    getSAXParser().parse(is, handler);
	    return handler.getRoots();
	}

	private static void myLoadPage(Browser browser, String url) throws IOException, SAXException {
		HttpConnection conn = null;
		InputStream is = null;
		
		try {
		    conn = (HttpConnection)Connector.open(url);
		    is = request(conn, null);
		    
		    loadPage(browser, is, url, getHostURL(conn), getPathURL(conn));
		} finally {
			if (is != null)	is.close();
			if (conn != null) conn.close();
		}
	}

	public static ZkDesktop loadPage(Browser browser, InputStream is, String url, String hostURL, String pathURL) 
	throws IOException, SAXException {
	    final ZkDesktop zk = createComponents(browser, is, hostURL, pathURL);
	    
	    //notify server to remove old desktop
	    final Object current = browser.getDisplay().getCurrent();
	    if (current instanceof ZkComponent) {
	    	final ZkDesktop oldZk = ((ZkComponent)current).getZkDesktop();
	    	if (oldZk != null && !"zkMobile".equals(oldZk.getId())) {
	    		oldZk.removeDesktop(); //notify server to remove desktop
	    	}
	    }

	    //associate new desktop to browser
	    browser.setDesktop(zk, url);
	    return zk;
    }

	private static ZkDesktop createComponents(Browser browser, InputStream is, String hostURL, String pathURL) 
	throws IOException, SAXException {
	    // Load the responsed page, the current Displayable is put in _current
	    final PageHandler handler = new PageHandler(hostURL, pathURL);
	    getSAXParser().parse(is, handler);
	    return handler.getZk();
	}

	/** utility to load Image on a separate thread.
	 * 
	 * @param item Imageable item to be setup the loaded image
	 * @param url the Image url
	 */
	public static void loadImageOnThread(Imageable item, String hostURL, String pathURL, String url) {
		if (url != null) {
			final ZkDesktop zk = ((ZkComponent)item).getZkDesktop();
			if (zk != null) {
				final String imagesrc = UiManager.prefixURL(hostURL, pathURL, url); //image
System.out.println("imagesrc="+imagesrc);				
				new Thread(new ImageRequest(item, imagesrc)).start();
			}
		}
	}
	
	/** utility to be called by {@link ImageRequest} only.
	 * 
	 * @param url The image url
	 * @return the Image object
	 */
	/*package*/ static Image loadImage(String url) {
		try {
			return myLoadImage(url);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static Image myLoadImage(String url) throws IOException {
		HttpConnection conn = null;
		InputStream is = null;

		try {
			if (url.startsWith("~.")) {
				url = url.substring(2);
				is = UiManager.class.getResourceAsStream(url);
			} else {
			    conn = (HttpConnection) Connector.open(url);
			    is = request(conn, null);
			}
		    // Load the response
		    return Image.createImage(is);
		} finally {
			if (is != null)	is.close();
			if (conn != null) conn.close();
		}
	}		

	public static InputStream request(HttpConnection conn, String request) throws IOException {
		OutputStream os = null;
		int rc;
		
		try {
		    // Set the request method and headers
		    conn.setRequestMethod(HttpConnection.POST);
		    conn.setRequestProperty("Content-Type",
            	"application/x-www-form-urlencoded");
		    conn.setRequestProperty("User-Agent",
		        "ZK Mobile/1.0 (RMIL)");
		    conn.setRequestProperty("Content-Language", "en-US");
		
		    // Getting the output stream may flush the headers
		    os = conn.openOutputStream();
		    if (request != null) {
		    	os.write(request.getBytes());
		    }
		    // Getting the response code will open the connection,
		    // send the request, and read the HTTP response headers.
		    // The headers are stored until requested.
		    rc = conn.getResponseCode();
		    if (rc != HttpConnection.HTTP_OK) {
		        throw new IOException("HTTP response code: " + rc);
		    }
		
		    return conn.openInputStream();
		} catch (ClassCastException e) {
		    throw new IllegalArgumentException("Not an HTTP URL");
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (os != null) os.close();
		}
	}

	/**
	 * parse a "width,height" into int[] where int[0] is width, int[1] is height.
	 * @param sizestr The size in string format of "width, height"
	 * @return int[] where int[0] is width, int[1] is height.
	 */
	public static int[] parseSize(String sizestr) {
		int[] size = new int[2]; //[0]: width; [1]: height
		int j = sizestr.indexOf(",");
		if (j < 0) { //not found
			return null;
		} else {
			String wstr = sizestr.substring(0, j).trim();
			String hstr = sizestr.substring(j+1).trim();
			size[0] = Integer.parseInt(wstr);
			size[1] = Integer.parseInt(hstr);
		}
		return size;
	}
	
	/**
	 * Utility to apply common properties to Item component. 
	 */
	public static void applyItemProperties(ZkComponent parent, Item component, Attributes attrs) {
		setItemAttr(component, "lo", attrs.getValue("lo")); //layout
		setItemAttr(component, "ps", attrs.getValue("ps")); //preferredSize
		if (parent instanceof ZkForm) {
			((ZkForm)parent).appendChild((ZkComponent)component);
		}
	}
	
	/**
	 * Given Item, attribute name, and value; then set the attribute of the Item if match. 
	 * @param item The Item to be set
	 * @param attr The attribute name
	 * @param val The value
	 */
	public static void setItemAttr(Item item, String attr, String val) {
		if ("lb".equals(attr)) {
			item.setLabel(val);
		} else if ("lo".equals(attr)) {
			item.setLayout(val == null ? Item.LAYOUT_DEFAULT : Integer.parseInt(val));
		} else if ("ps".equals(attr)) {
			int[] sz = val == null ? new int[] {-1, -1} : parseSize(val);
			item.setPreferredSize(sz[0], sz[1]);
		}
	}
	
	public static void registerCommand(ZkComponent owner, Command cmd, ZkDesktop zk) {
		owner.addCommand(cmd); //add into owner
		if (owner instanceof Item) {
			((Item)owner).setItemCommandListener(zk.getItemCommandListener());
		} else if (owner instanceof Displayable) {
			((Displayable)owner).setCommandListener(zk.getCommandListener());
		}
	}
}
