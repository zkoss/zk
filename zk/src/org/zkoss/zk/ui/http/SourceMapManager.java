/* SourceMapManager.java

	Purpose:

	Description:

	History:
		Thu Jul 13 10:47:11 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.debugging.sourcemap.SourceMapGeneratorV3;
import com.google.debugging.sourcemap.SourceMapParseException;

/**
 * A source map manager can handle source map info during the wpd file generating.
 * @author jameschu
 * @since 8.5.0
 */
public class SourceMapManager {
	private final SourceMapGeneratorV3 _generator;
	private final String _name;
	private final String _sourceRoot;
	private final String _sourceMappingURL;
	private final List<SourceMapInfo> _sourceMapInfoList;
	private String _generatedSourceMapContent;

	public SourceMapManager(String name, String sourceRoot, String id) {
		_generator = new SourceMapGeneratorV3();
		_name = name;
		_sourceRoot = sourceRoot;
		_sourceMapInfoList = new ArrayList<SourceMapInfo>(16);
		_sourceMappingURL = _sourceRoot + "js/" + id + "/" + _name + ".map";
	}

	/**
	 * Returns a list of source map info
	 * @return a list of source map info
	 */
	public List<SourceMapInfo> getSourceMapInfoList() {
		return _sourceMapInfoList;
	}

	/**
	 * Returns the source mapping url
	 * @return the source mapping url
	 */
	public String getSourceMappingURL() {
		return _sourceMappingURL;
	}

	/**
	 * Append a empty source map which would be ignored in mapping
	 * @param jsLineCount the number of javascript file lines
	 */
	public void appendEmptySourceMap(int jsLineCount) {
		insertEmptySourceMap(-1, jsLineCount);
	}

	/**
	 * Insert a empty source map which would be ignored in mapping
	 * @param index of source map info list
	 * @param jsLineCount the number of javascript file lines
	 */
	public void insertEmptySourceMap(int index, int jsLineCount) {
		try {
			insertSourceMap(index, "", jsLineCount, null);
		} catch (SourceMapParseException e) {
			//Should not error
		}
	}

	/**
	 * Append a source map
	 * @param newSourceMapManager
	 */
	public void appendSourceMap(SourceMapManager newSourceMapManager) throws IOException {
		if (newSourceMapManager != null)
			_sourceMapInfoList.addAll(newSourceMapManager.getSourceMapInfoList());
	}

	/**
	 * Append a source map by content
	 * @param sourceMapContent the content of source map
	 * @param jsLineCount the number of javascript file lines
	 * @param sourcePath the javascript source path
	 */
	public void appendSourceMap(String sourceMapContent, int jsLineCount, String sourcePath) throws SourceMapParseException {
		insertSourceMap(-1, sourceMapContent, jsLineCount, sourcePath);
	}

	/**
	 * Insert a source map by content
	 * @param index of source map info list
	 * @param sourceMapContent the content of source map
	 * @param jsLineCount the number of javascript file lines
	 * @param sourcePath the javascript source path
	 */
	public void insertSourceMap(int index, String sourceMapContent, int jsLineCount, String sourcePath) throws SourceMapParseException {
		if (sourceMapContent == null || sourceMapContent.length() == 0) //add Empty
			sourceMapContent = getEmptySourceMap(jsLineCount);
		if (index > -1) //if -1, append
			_sourceMapInfoList.add(index, new SourceMapInfo(sourceMapContent, jsLineCount, sourcePath));
		else
			_sourceMapInfoList.add(new SourceMapInfo(sourceMapContent, jsLineCount, sourcePath));
	}

	private String getEmptySourceMap(int lineCount) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n\"version\":3,\n\"file\":\"empty\",\n\"lineCount\":");
		sb.append(lineCount + "");
		sb.append(",\n\"mappings\":\"");
		for (int i = 1; i < lineCount; i++)
			sb.append(";");
		sb.append("\",\n\"_sources\":[],\n\"names\":[]\n}");
		return sb.toString();
	}

	/**
	 * Generate the source map of this mananger.
	 * @return the merged source map content
	 */
	public String getSourceMapContent() throws IOException, SourceMapParseException {
		String mapContentStr = _generatedSourceMapContent;
		if (mapContentStr != null && mapContentStr.length() != 0)
			return mapContentStr;

		int lineCount = 0;
		List<String> sourcePathList = new ArrayList<String>(16);
		for (int i = 0; i < _sourceMapInfoList.size(); i++) {
			SourceMapInfo sourceMapInfo = _sourceMapInfoList.get(i);
			String content = sourceMapInfo.getContent();
			if (content == null || content.length() == 0) //skip empty map
				continue;
			_generator.mergeMapSection(lineCount, 0, content);
			lineCount += sourceMapInfo.getLineCount();
			String sourcePath = sourceMapInfo.getSourcePath();
			if (sourcePath != null && sourcePath.length() != 0)
				sourcePathList.add(sourcePath);
		}
		StringBuilder mapContents = new StringBuilder();
		_generator.appendTo(mapContents, _name + ".wpd");
		mapContentStr = mapContents.toString();
		StringBuilder sourcesBuilder = new StringBuilder();
		sourcesBuilder.append("\"sources\":[");
		for (int i = 0, max = sourcePathList.size() - 1; i <= max; i++) {
			sourcesBuilder.append("\"");
			sourcesBuilder.append(sourcePathList.get(i));
			sourcesBuilder.append("\"");
			if (i != max)
				sourcesBuilder.append(",");
		}
		sourcesBuilder.append("],\"sourceRoot\":\"");
		sourcesBuilder.append(_sourceRoot);
		sourcesBuilder.append("\",");
		mapContentStr = mapContentStr.replaceAll("\"sources\".*\\]\\,",  sourcesBuilder.toString());
		_generatedSourceMapContent = mapContentStr;
		return mapContentStr;
	}


	//Add a source map which would be handled later (in WpdContent toByteArray)
	void addNonResolvedSourceMap() {
		_sourceMapInfoList.add(new SourceMapInfo("", 0, null, false));
	}

	//Return a list of unresolved source map (in WpdContent toByteArray)
	List<Integer> getUnresolvedSourceMapInfoIndexList() {
		List<Integer> indexList = new ArrayList<Integer>();
		for (int i = 0; i < _sourceMapInfoList.size(); i++) {
			if (!_sourceMapInfoList.get(i).isResolved())
				indexList.add(i);
		}
		return indexList;
	}

	//Resolve the source maps by insert empty map (in WpdContent toByteArray)
	void resolveSourceMapByIndex(int index, int jsLineCount) {
		SourceMapInfo info = _sourceMapInfoList.remove(index);
		info.setContent(getEmptySourceMap(jsLineCount));
		info.setLineCount(jsLineCount);
		info.setResolved(true);
		_sourceMapInfoList.add(index, info);
	}
}
