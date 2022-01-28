/* EmulatorClient.java

	Purpose:

	Description:

	History:
		Mar 20, 2012 Created by pao

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zats.mimic.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.util.UrlEncoded;

import org.zkoss.zats.ZatsException;
import org.zkoss.zats.common.json.JSONValue;
import org.zkoss.zats.common.json.parser.ParseException;
import org.zkoss.zats.mimic.Client;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.EchoEventMode;
import org.zkoss.zats.mimic.exception.ZKExceptionHandler;
import org.zkoss.zats.mimic.impl.au.AuUtility;
import org.zkoss.zats.mimic.impl.emulator.Emulator;
import org.zkoss.zats.mimic.impl.operation.AbstractUploadAgentBuilder;
import org.zkoss.zk.ui.Desktop;

/**
 * The default server emulator client implementation.
 * @author pao
 */
public class EmulatorClient implements Client, ClientCtrl {
	private static Logger logger = Logger.getLogger(EmulatorClient.class.getName());
	private Emulator emulator;
	private Map<String, DesktopAgent> desktopAgents = new HashMap<String, DesktopAgent>();
	private Map<String, String> cookies = new HashMap<String, String>();
	private DestroyListener destroyListener;
	private Map<String, List<UpdateEvent>> auQueues; // AU queues of desktops
	private Map<String, List<UpdateEvent>> auQueues4piggyback; // AU queues for piggyback events
	private EchoEventMode echoEventMode = EchoEventMode.IMMEDIATE;

	public EmulatorClient(Emulator emulator) {
		this.emulator = emulator;
		this.auQueues = new ConcurrentHashMap<String, List<UpdateEvent>>();
		this.auQueues4piggyback = new ConcurrentHashMap<String, List<UpdateEvent>>();
	}

	public DesktopAgent connectAsIncluded(String zulPath, Map<String, Object> args) {
		if(zulPath == null)
			throw new IllegalArgumentException("the path of ZUL can't be null");
		if (args == null)
			args = new HashMap<String, Object>();

		// generate key and map for transferring data into server side
		String key = "zats_" + Long.toString(Thread.currentThread().getId(), 36);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("url", zulPath);
		data.put("args", args);
		emulator.getServletContext().setAttribute(key, data);

		// connect to adapter with key
		String adapter = "/~./zats/includingAdapter.zul?id=" + key;
		DesktopAgent desktop = connect(adapter);

		// clean resources
		emulator.getServletContext().removeAttribute(key);
		data.clear();

		return desktop;
	}

	public DesktopAgent connectWithContent(String content, String ext,
			ComponentAgent parent, Map<String, Object> args) {
		if (content == null)
			throw new IllegalArgumentException(
					"the content of ZUL can't be null");
		if (args == null)
			args = new HashMap<String, Object>();

		// generate key and map for transferring data into server side
		String key = "zats_"
				+ Long.toString(Thread.currentThread().getId(), 36);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("content", content);
		data.put("ext", ext);
		data.put("args", args);
		if (parent != null)
			data.put("parent", parent.getOwner());
		emulator.getServletContext().setAttribute(key, data);

		// connect to adapter with key
		String adapter = "/~./zats/createComponentsDirectlyAdapter.zul?id=" + key;
		DesktopAgent desktop = connect(adapter);

		// clean resources
		emulator.getServletContext().removeAttribute(key);
		data.clear();

		return desktop;
	}

	public DesktopAgent connect(String zulPath) {
		if(zulPath == null)
			throw new IllegalArgumentException("the path of ZUL can't be null");

		InputStream is = null;
		try {
			// load zul page
			HttpURLConnection huc = getConnection(zulPath, "GET");
			huc.connect();

			// read response
			fetchCookies(huc);

			// check if there exists any exception during connect
			List l;
			if ((l = ZKExceptionHandler.getInstance().getExceptions()).size() > 0) {
				//only throw the first exception, and clear all once thrown
				throw (Throwable)l.get(0);
			}

			is = huc.getInputStream();
			String raw = getReplyString(is, huc.getContentEncoding());

			// get specified objects such as Desktop
			Desktop desktop = (Desktop) emulator.getRequestAttributes().get("javax.zkoss.zk.ui.desktop");
			// TODO, what if a non-zk(zul) page, throw exception?
			DesktopAgent desktopAgent = new DefaultDesktopAgent(this, desktop);
			desktopAgents.put(desktopAgent.getId(), desktopAgent);

			// pass layout response to response handlers
			List<LayoutResponseHandler> handlers = ResponseHandlerManager.getInstance().getLayoutResponseHandlers();
			for (LayoutResponseHandler h : handlers) {
				try {
					h.process(desktopAgent, raw);
				} catch (Throwable e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
			if (logger.isLoggable(Level.FINEST)) {
				logger.finest("HTTP response header: " + huc.getHeaderFields());
				logger.finest("HTTP response content: " + raw);
			}

			// ZATS-11: must flush AU requests after layout
			flush(desktopAgent.getId());

			return desktopAgent;
		} catch (Throwable t) {
			throw new ZatsException(t.getMessage(), t);
		} finally {
			close(is);
			//clear exceptions once thrown out
			ZKExceptionHandler.getInstance().destroy();
		}
	}

	public void destroy() {
		if (destroyListener != null) {
			destroyListener.willDestroy(this);
		}

		for (DesktopAgent d : desktopAgents.values()) {
			destroy(d);
		}
		desktopAgents.clear();

		// should be after cleaning desktop
		cookies.clear();
		auQueues.clear();
		auQueues4piggyback.clear();
	}

	public void destroy(DesktopAgent desktopAgent) {
		postUpdate(desktopAgent.getId(), null, "rmDesktop", null, true);
		flush(desktopAgent.getId());
	}

	public void postUpdate(String desktopId, String targetUUID, String command, Map<String, Object> data, boolean ignorable) {
		postUpdate(desktopId, targetUUID, command, data, ignorable, false);
	}

	public void postPiggyback(String desktopId, String targetUUID, String command, Map<String, Object> data, boolean ignorable) {
		postUpdate(desktopId, targetUUID, command, data, ignorable, true);
	}

	private void postUpdate(String desktopId, String targetUUID, String command, Map<String, Object> data, boolean ignorable, boolean piggyback) {
		if(desktopId==null){
			throw new IllegalArgumentException("desktop id is null");
		}else if(command == null){
			throw new IllegalArgumentException("command is null");
		}

		// get or create AU queue
		Map<String, List<UpdateEvent>> queues = piggyback ? auQueues4piggyback : auQueues;
		List<UpdateEvent> queue = queues.get(desktopId);
		if (queue == null) {
			queue = new LinkedList<UpdateEvent>();
			queues.put(desktopId, queue);
		}
		// queue such AU event
		queue.add(new UpdateEvent(targetUUID, command, data, ignorable));
	}

	private Map<String, Object> deconstructPacket(Map<String, Object> data, List<AbstractUploadAgentBuilder.FileItem> files) {
			if (data.isEmpty()) {
				return data;
			}
			Map newData = new LinkedHashMap(data.size());
			for (Map.Entry<String, Object> me : data.entrySet()) {
				if (me.getValue() instanceof AbstractUploadAgentBuilder.FileItem) {
					newData.put(me.getKey(), org.zkoss.util.Maps.of("_placeholder", true, "num", files.size()));
					files.add(
							(AbstractUploadAgentBuilder.FileItem) me.getValue());
				} else {
					newData.put(me.getKey(), me.getValue());
				}
			}
			return newData;
	}

	private String getCombinedEventString(String desktopId, List<AbstractUploadAgentBuilder.FileItem> files) {

		// collect all events
		List<UpdateEvent> queue = new LinkedList<UpdateEvent>();
		if (auQueues4piggyback.containsKey(desktopId)) {
			queue.addAll(auQueues4piggyback.remove(desktopId));
		}
		if (auQueues.containsKey(desktopId)) {
			queue.addAll(auQueues.remove(desktopId));
		}
		if (queue.size() <= 0) {
			return null;// do nothing
		}

		// combine AU events from queue into single request content
		int index = 0;
		StringBuilder sb = new StringBuilder();
		while (!queue.isEmpty()) {
			UpdateEvent event = queue.remove(0);
			// command
			sb.append("&cmd_").append(index).append("=").append(event.cmd);
			// UUID
			if (event.uuid != null) {
				String uuid = UrlEncoded.encodeString(event.uuid);
				sb.append("&uuid_").append(index).append("=").append(uuid);
			}
			// event data
			if (event.data != null && event.data.size() > 0) {
				String jsonData = UrlEncoded.encodeString(JSONValue.toJSONString(deconstructPacket(event.data, files)));
				sb.append("&data_").append(index).append("=").append(jsonData);
			}
			// ignorable
			if (event.ignorable) {
				sb.append("&opt_").append(index).append("=").append("i");
			}
			++index;
		}

		// debug log
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Desktop " + desktopId + " perform AU: "
					+ UrlEncoded.decodeString(sb.toString(), 0, sb.length(), StandardCharsets.UTF_8));
		}

		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public void flush(String desktopId) {
		OutputStream os = null;

		// #ZATS-11: when post-flush, handlers process AU responses.
		// They might require posting more AU requests immediately, so repeat posting.
		while(auQueues.containsKey(desktopId) && auQueues.get(desktopId).size() > 0) {
			try {
				// for fileupload
				List<AbstractUploadAgentBuilder.FileItem> files = new ArrayList<>();
				String combinedEvents = getCombinedEventString(desktopId, files); // this will clean such desktop's event queue
				if (combinedEvents == null) { // do nothing
					return;
				}

				// create http request and perform it
				HttpURLConnection c = getConnection("/zkau", "POST");
				c.setDoOutput(true);
				c.setDoInput(true);

				// combine AU events from queue into single request
				StringBuilder sb = new StringBuilder();
				sb.append("dtid=").append(UrlEncoded.encodeString(desktopId)); // desktop ID.
				final String content = sb.append(combinedEvents).toString();

				if (files.isEmpty()) {

					c.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded;charset=UTF-8");
					if (logger.isLoggable(Level.FINEST)) {
						logger.finest("HTTP request header: " + c.getRequestProperties());
						logger.finest("HTTP request content: " + content);
					}
					c.connect();
					os = c.getOutputStream();
					os.write(content.getBytes("utf-8"));
					close(os);
				} else {
					FormDataUtility formData = new FormDataUtility(c,
							"utf-8");
					formData.addFormField("data", content);
					formData.addFormField("attachments", files.size() + "");
					for (int i = 0, j = files.size(); i < j; i++) {
						formData.addFilePart("files_" + i, files.get(i));
					}
					formData.finish();
				}

				// read and parse response, and pass to response handlers
				fetchCookies(c);

				// check if there exists any exception during auRequest
				List l;
				if ((l = ZKExceptionHandler.getInstance().getExceptions()).size() > 0) {
					//only throw the first exception, but can check in ZKExceptionHandler
					throw (Throwable)l.get(0);
				}

				String raw = getReplyString(c.getInputStream(), parseCharset(c));

				// ZATS-25: filter non-JSON part (i.e. real JS code)
				raw = AuUtility.filterNonJSON(raw);

				Map<String, Object> json = (Map<String, Object>) JSONValue.parseWithException(raw);
				List<UpdateResponseHandler> handlers = ResponseHandlerManager.getInstance().getUpdateResponseHandlers();
				for (UpdateResponseHandler h : handlers) {
					try {
						h.process(desktopAgents.get(desktopId), json);
					} catch (Throwable e) {
						logger.log(Level.SEVERE, e.getMessage(), e);
					}
				}
				if (logger.isLoggable(Level.FINEST)) {
					logger.finest("HTTP response header: " + c.getHeaderFields());
					logger.finest("HTTP response content: " + raw);
				}

			} catch (ParseException e) {
				logger.log(Level.SEVERE, "unexpect exception when parsing JSON", e);
			} catch (Exception e) {
				throw new ZatsException(e.getMessage(), e);
			} catch (Throwable t) {
				throw new ZatsException(t.getMessage(), t);
			} finally {
				close(os);
				//clear exceptions once thrown out
				ZKExceptionHandler.getInstance().destroy();
			}
		}
	}

	private static Pattern CHARSET_PATTERN = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");
	private String parseCharset(HttpURLConnection c) {
		Matcher matcher = CHARSET_PATTERN.matcher(c.getContentType());
		String charset = null;
		if(matcher.find() ) {
			charset = matcher.group(1).trim().toUpperCase(Locale.ENGLISH);
		}
		return charset;
	}

	public HttpURLConnection getConnection(String path, String method) {
		try {
			URL url = new URL(emulator.getAddress() + path);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			huc.setRequestMethod(method);
			huc.setUseCaches(false);
			huc.addRequestProperty("Host", emulator.getHost() + ":" + emulator.getPort());
			huc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)");
			huc.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			huc.addRequestProperty("Accept-Language", "zh-tw,en-us;q=0.7,en;q=0.3");
			// handle cookies
			for (Entry<String, String> cookie : cookies.entrySet()) {
				String value = cookie.getValue() != null ? cookie.getValue() : "";
				huc.addRequestProperty("Cookie", cookie.getKey() + "=" + value);
			}
			return huc;
		} catch (Exception e) {
			throw new ZatsException(e.getMessage(), e);
		}
	}

	public InputStream openConnection(String path) throws IOException {
		HttpURLConnection c = getConnection(path, "GET");
		c.setDoOutput(false);
		c.setDoInput(true);
		return c.getInputStream();
	}

	private void close(Closeable c) {
		try {
			c.close();
		} catch (Throwable e) {
		}
	}

	private String getReplyString(InputStream is, String encoding) {
		String reply = null;
		Reader r = null;
		try {
			StringBuilder sb = new StringBuilder();
			r = new BufferedReader(new InputStreamReader(is, encoding != null ? encoding : "ISO-8859-1"));
			while (true) {
				int c = r.read();
				if (c < 0)
					break;
				sb.append((char) c);
			}
			reply = sb.toString();
		} catch (Exception e) {
			logger.log(Level.WARNING, "", e);
		} finally {
			close(r);
		}
		return reply;
	}

	public void setDestroyListener(DestroyListener l) {
		destroyListener = l;
	}

	@SuppressWarnings("deprecation")
	private void fetchCookies(HttpURLConnection connection)
	{
		// fetch and parse cookies from connection
		List<String> list = connection.getHeaderFields().get("Set-Cookie");
		if (list == null)
			return;
		for (String raw : list) {
			try {

				// parse cookie and append to the collection of cookies
				String[] tokens = raw.trim().split(";"); // fetch cookie
				tokens = tokens[0].split("="); // fetch key and value from cookie
				cookies.put(tokens[0], tokens.length < 2 ? "" : tokens[1]); // value can be null

				// get cookie attributes
				byte[] data = raw.replaceAll(";", "\n").getBytes("ASCII");
				Properties attr = new Properties();
				attr.load(new ByteArrayInputStream(data));

				// check expired time and remove cookie if necessary
				String expired = attr.getProperty("Expires");
				if (expired != null && expired.length() > 0) {
					try {
						long time = Date.parse(expired); // W3C Datetime Format
						if (time < System.currentTimeMillis())
							cookies.remove(tokens[0]);
					} catch (Throwable e) {
						logger.log(Level.WARNING, "unexpect exception when parsing HTTP Datetime string", e);
					}
				}
			} catch (Exception e) {
				new ZatsException("unexpected exception", e);
			}
		}
	}

	public void setCookie(String key, String value) {
		if (key == null || key.startsWith("$"))
			throw new IllegalArgumentException(key == null ? "cookie key name can't be null"
					: "cookie key name can't be start with '$'");
		if (value != null)
			cookies.put(key, value);
		else
			cookies.remove(key);
	}

	public String getCookie(String key) {
		if (key == null)
			throw new IllegalArgumentException("cookie key name can't be null");
		return cookies.get(key);
	}

	public Map<String, String> getCookies() {
		return new HashMap<String, String>(cookies); // a copy of cookies
	}

	public void setEchoEventMode(EchoEventMode mode) {
		if(mode != null) {
			echoEventMode = mode;
		}
	}

	public EchoEventMode getEchoEventMode() {
		return echoEventMode;
	}

	private static class UpdateEvent {
		String uuid;
		String cmd;
		Map<String, Object> data;
		boolean ignorable = false;

		UpdateEvent(String uuid, String cmd, Map<String, Object> data, boolean ignorable) {
			this.uuid = uuid;
			this.cmd = cmd;
			this.data = data;
			this.ignorable = ignorable;
		}
	}
}
