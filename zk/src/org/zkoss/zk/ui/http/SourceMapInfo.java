/* SourceMapInfo.java

	Purpose:

	Description:

	History:
		Thu Jul 13 10:47:11 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.http;

/**
 * @author jameschu
 */
public class SourceMapInfo {
	/** The content of source map. */
	private String _content;
	/** The line count of source map. */
	private int _lineCount;
	/** The source path of source map. */
	private String _sourcePath;
	/** Mark the source map resolved or not. (in WpdContent toByteArray)*/
	private boolean _resolved;

	public int getLineCount() {
		return _lineCount;
	}

	public void setLineCount(int lineCount) {
		this._lineCount = lineCount;
	}

	public String getContent() {
		return _content;
	}

	public void setContent(String content) {
		this._content = content;
	}

	public String getSourcePath() {
		return _sourcePath;
	}

	public SourceMapInfo(String content, int lineCount, String sourcePath) {
		this(content, lineCount, sourcePath, true);
	}

	public SourceMapInfo(String content, int lineCount, String sourcePath, boolean resolved) {
		this._lineCount = lineCount;
		this._content = content;
		this._sourcePath = sourcePath;
		this._resolved = resolved;
	}

	public void setSourcePath(String sourcePath) {
		this._sourcePath = sourcePath;
	}

	public boolean isResolved() {
		return _resolved;
	}

	public void setResolved(boolean resolved) {
		this._resolved = resolved;
	}
}
