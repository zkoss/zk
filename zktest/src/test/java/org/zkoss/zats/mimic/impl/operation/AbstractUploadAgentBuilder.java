/* ButtonUploadAgent.java

	Purpose:

	Description:

	History:
		Jun 19, 2012 Created by pao

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zats.mimic.impl.operation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.util.IO;

import org.zkoss.zats.common.util.MultiPartOutputStream;
import org.zkoss.zats.mimic.AgentException;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.impl.ClientCtrl;
import org.zkoss.zats.mimic.impl.OperationAgentBuilder;
import org.zkoss.zats.mimic.impl.Util;
import org.zkoss.zats.mimic.operation.UploadAgent;
import org.zkoss.zk.ui.event.Events;

/**
 * The abstract builder of upload agent.
 * @author pao
 * @since 1.1.0
 */
public abstract class AbstractUploadAgentBuilder implements OperationAgentBuilder<ComponentAgent, UploadAgent> {
	private final static Logger logger = Logger.getLogger(AbstractUploadAgentBuilder.class.getName());

	public Class<UploadAgent> getOperationClass() {
		return UploadAgent.class;
	}

	public static class FileItem {
		String contentType;
		String fileName;
		InputStream inputStream;

		public FileItem(File file, String contentType)
				throws FileNotFoundException {
			this.inputStream = new FileInputStream(file);
			this.fileName = file.getName();
			this.contentType = contentType;
		}

		public FileItem(String fileName, InputStream file, String contentType) {
			this.fileName = fileName;
			this.inputStream = file;
			this.contentType = contentType;
		}
		public String getFileName() {
			return fileName;
		}
		public String getContentType() {
			return contentType;
		}
		public InputStream getInputStream() {
			return inputStream;
		}
	}
	abstract class AbstractUploadAgentImpl extends AgentDelegator<ComponentAgent> implements UploadAgent {

		private HttpURLConnection conn;
		private MultiPartOutputStream multipartStream;
		private boolean isMultiple;

		public AbstractUploadAgentImpl(ComponentAgent target) {
			super(target);
		}

		public void upload(File file, String contentType) {
			if (file == null)
				throw new NullPointerException("file can't be null.");
			InputStream is = null;
			try {
				is = new BufferedInputStream(new FileInputStream(file));
				upload(file.getName(), is, contentType);
			} catch (IOException e) {
				throw new AgentException(e.getMessage(), e);
			} finally {
				Util.close(is);
			}
		}

		protected abstract String getUploadFlag();

		public void upload(String fileName, InputStream content, String contentType) {
			if (fileName == null)
				throw new NullPointerException("file name can't be null.");
			if (content == null)
				throw new NullPointerException("content stream can't be null.");

			Map data = new HashMap<>();
			data.put("file", new FileItem(fileName, content, contentType));
			((ClientCtrl)target.getClient()).postUpdate(target.getDesktop().getId(), target.getUuid(),
					Events.ON_UPLOAD, data , false);
//			// first time upload
//			if (multipartStream == null) {
//
//				// fetch and check upload flag
//				String flag = getUploadFlag();
//				if (flag == null || flag.length() == 0)
//					throw new AgentException("upload feature doesn't turn on.");
//				else {
//					Map<String, String> attr = new HashMap<String, String>();
//					for (String token : flag.split("\\s*,\\s*")) {
//						if (token.trim().length() <= 0)
//							continue;
//						String[] tokens = token.split("[\\s=]+");
//						if (tokens.length == 1)
//							attr.put("", tokens[0]);
//						else if (tokens.length >= 2)
//							attr.put(tokens[0], tokens[1]);
//					}
//					String value = attr.get("");
//					if ("false".equals(value))
//						throw new AgentException("upload feature doesn't turn on.");
//					isMultiple = Boolean.parseBoolean(attr.get("multiple"));
//				}
//
//				try {
//					// parameters
//					String param = "?uuid={0}&dtid={1}&sid=0&maxsize=undefined";
//					param = MessageFormat.format(param, target.getUuid(), target.getDesktop().getId());
//					// open connection
//					String boundary = Util.generateRandomString(); // boundary for multipart
//					ClientCtrl cc = (ClientCtrl) getClient();
//					conn = cc.getConnection("/zkau/upload" + param, "POST");
//					conn.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
//					conn.setDoInput(true);
//					conn.setDoOutput(true);
//					OutputStream os = conn.getOutputStream();
//					multipartStream = new MultiPartOutputStream(os, boundary);
//				} catch (IOException e) {
//					clean();
//					throw new AgentException(e.getMessage(), e);
//				}
//			} else if (!isMultiple) { // check allow of multiple
//				throw new AgentException("multiple upload feature doesn't turn on.");
//			}
//
//			try {
//				// additional headers
//				String contentDisposition = "Content-Disposition: form-data; name=\"file\"; filename=\"{0}\"";
//				contentDisposition = MessageFormat.format(contentDisposition, fileName);
//				String[] headers = new String[] { contentDisposition };
//				// upload multipart data
//				multipartStream.startPart(contentType != null ? contentType : "application/octet-stream", headers); // default content type
//				int b;
//				while ((b = content.read()) >= 0)
//					multipartStream.write(b);
//			} catch (IOException e) {
//				clean();
//				throw new AgentException(e.getMessage(), e);
//			}
		}

		public void finish() {
			((ClientCtrl)target.getClient()).flush(target.getDesktop().getId());
//			if (multipartStream == null)
//				return;
//
//			// finish upload first and get the correct key
//			clean();
//			int key = ((DesktopCtrl) target.getDesktop().getDelegatee()).getNextKey() - 1;
//			String contentId = Strings.encode(new StringBuffer(12).append("z__ul_"), key).toString(); // copy from AuUploader
//			// perform AU
//			String cmd = "updateResult";
//			String desktopId = target.getDesktop().getId();
//			Event event = new Event(cmd, (Component) target.getDelegatee());
//			Map<String, Object> data = EventDataManager.getInstance().build(event);
//			data.put("wid", target.getUuid());
//			data.put("contentId", contentId);
//			data.put("sid", "0");
//			((ClientCtrl) target.getClient()).postUpdate(desktopId, target.getUuid(), cmd, data, false);
//			((ClientCtrl) target.getClient()).flush(desktopId);
		}

		private void clean() {
			// close output
			Util.close(multipartStream);
			multipartStream = null;

			// close input
			InputStream is = null;
			try {
				String respMsg = conn.getResponseMessage();
				is = conn.getInputStream();
				String resp = IO.toString(is);
				if (logger.isLoggable(Level.FINEST)) {
					logger.finest("response message: " + respMsg);
					logger.finest("response content: " + resp);
				}
			} catch (IOException e) {
				throw new AgentException(e.getMessage(), e);
			} finally {
				Util.close(is);
				conn = null;
			}
		}
	}
}
