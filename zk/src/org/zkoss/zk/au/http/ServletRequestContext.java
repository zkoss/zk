/* ServletRequestContext.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 16 16:56:46 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zkoss.zk.au.http;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.UploadContext;

/**
 * An implementation of RequestContext, for commons-fileupload.
 * A concrete servlet request object will be wrapped.
 *
 * @author rudyhuang
 * @since 9.6.0
 */
class ServletRequestContext implements UploadContext {
	private final HttpServletRequest _request;

	public ServletRequestContext(HttpServletRequest request) {
		this._request = request;
	}

	@Override
	public String getCharacterEncoding() {
		return _request.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return _request.getContentType();
	}

	@Override
	@Deprecated
	public int getContentLength() {
		return _request.getContentLength();
	}

	@Override
	public long contentLength() {
		long size;
		try {
			size = Long.parseLong(_request.getHeader(FileUploadBase.CONTENT_LENGTH));
		} catch (NumberFormatException e) {
			size = _request.getContentLength();
		}
		return size;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return _request.getInputStream();
	}

	@Override
	public String toString() {
		return String.format("ContentLength=%s, ContentType=%s",
				this.contentLength(),
				this.getContentType());
	}
}
