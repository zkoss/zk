/* MultipartUtility.java

	Purpose:

	Description:

	History:
		12:41 PM 2022/1/28, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zats.mimic.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import org.zkoss.zats.mimic.impl.operation.AbstractUploadAgentBuilder;

public class FormDataUtility {

	private final String boundary;
	private static final String LINE_FEED = "\r\n";
	private HttpURLConnection httpConn;
	private String charset;
	private OutputStream outputStream;
	private PrintWriter writer;

	public FormDataUtility(HttpURLConnection httpConn, String charset)
			throws IOException {
		this.charset = charset;

		// creates a unique boundary based on time stamp
		boundary = "===" + System.currentTimeMillis() + "===";
		this.httpConn = httpConn;
		httpConn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + boundary);
		outputStream = httpConn.getOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
				true);
	}

	public void addFormField(String name, String value) {
		writer.append("--" + boundary).append(LINE_FEED);
		writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
				.append(LINE_FEED);
		writer.append("Content-Type: text/plain; charset=" + charset).append(
				LINE_FEED);
		writer.append(LINE_FEED);
		writer.append(value).append(LINE_FEED);
		writer.flush();
	}

	public void addFilePart(String fieldName, AbstractUploadAgentBuilder.FileItem uploadFile)
			throws IOException {
		String fileName = uploadFile.getFileName();
		writer.append("--" + boundary).append(LINE_FEED);
		writer.append(
						"Content-Disposition: form-data; name=\"" + fieldName
								+ "\"; filename=\"" + fileName + "\"")
				.append(LINE_FEED);
		writer.append(
						"Content-Type: " + uploadFile.getContentType())
				.append(LINE_FEED);
		writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
		writer.append(LINE_FEED);
		writer.flush();

		InputStream inputStream = uploadFile.getInputStream();
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		outputStream.flush();
		inputStream.close();

		writer.append(LINE_FEED);
		writer.flush();
	}

	public void finish() {
		writer.append(LINE_FEED).flush();
		writer.append("--" + boundary + "--").append(LINE_FEED);
		writer.close();
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}