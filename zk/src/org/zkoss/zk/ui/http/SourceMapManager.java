/* SourceMapManager.java

	Purpose:

	Description:

	History:
		Thu Jul 13 10:47:11 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.SourceMap;
import com.google.javascript.jscomp.WarningLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.lang.Strings;
import org.zkoss.util.Pair;

/**
 * A source map manager can handle source map info during the wpd file generating.
 * Internal used only.
 * @author jameschu
 * @since 8.5.0
 */
public class SourceMapManager implements Serializable {
	static final Logger log = LoggerFactory.getLogger(SourceMapManager.class);
	private final String _name;
	private final String _sourceRoot;
	private final String _sourceMappingURL;
	private String _preScript = null;
	private String _postScript = null;
	private Map<Pair<String, String>, String> _jsContentMap; //js <src path, real path> -> content
	private Map<Pair<String, String>, String> _postJsContentMap; //js <src path, real path> -> content (for merging package)
	private Pair<String, String> _jsCursor = null;
	private static final SourceFile.Builder _builder = new SourceFile.Builder();

	public SourceMapManager(String name, String sourceRoot, String id) {
		_name = name;
		_sourceRoot = sourceRoot;
		_sourceMappingURL = _sourceRoot + "js/" + id + "/" + _name + ".map";
		_jsContentMap = new LinkedHashMap<>();
		_postJsContentMap = new LinkedHashMap<>();
	}

	public void setPreScript(String preScript) {
		this._preScript = preScript;
	}

	public void setPostScript(String postScript) {
		this._postScript = postScript;
	}

	public void updateCursorRealPath(String realPath) {
		if (!isJsCursorValid())
			return;
		if (this._jsCursor.getY() == null) { //only do once
			String jsContent = _jsContentMap.remove(_jsCursor);
			// add /src/
			Pair<String, String> newKey = new Pair<>(_jsCursor.getX(), realPath.replace("/js/", "/js/src/"));
			_jsCursor = newKey;
			_jsContentMap.put(newKey, jsContent);
		}
	}

	public void appendJsContent(String... scripts) {
		if (!isJsCursorValid())
			return;
		String jsContent = _jsContentMap.get(_jsCursor);
		if (jsContent == null) jsContent = "";
		for (String script : scripts)
			jsContent += script;
		_jsContentMap.put(_jsCursor, jsContent);
	}

	public void startJsCursor(String jsPath) {
		this._jsCursor = new Pair<>(jsPath, null);
	}

	public void clearJsCursor() {
		this._jsCursor = null;
	}

	public void closeJsCursor(ByteArrayOutputStream out) {
		String realPath = _jsCursor.getY();
		if (!Strings.isEmpty(realPath)) {
			String key = "$zk$" + realPath;
			String loadJsScript = "window['" + key + "']();delete window['" + key + "'];";
			final byte[] bs;
			bs = loadJsScript.getBytes();
			out.write(bs, 0, bs.length);
		}
		clearJsCursor();
	}

	public void appendPostScript(String... scripts) {
		for (String script : scripts)
			_postScript += script;
	}

	public void mergeWpd(SourceMapManager sourceMapManager, byte[] data) {
		String wpdFileName = sourceMapManager._name + ".wpd.src.js";
		String wpdPath = "/js/src/" + wpdFileName;
		String wpdFileContent = sourceMapManager._preScript + new String(data) + sourceMapManager._postScript;
		for (Map.Entry<Pair<String, String>, String> en : sourceMapManager._jsContentMap.entrySet())
			_postJsContentMap.put(en.getKey(), en.getValue());
		_postJsContentMap.put(new Pair<>(wpdFileName, wpdPath), wpdFileContent);
	}

	public String generateFinalWpd(ConcurrentMap<String, String> sourceMapContentMap, String sourceMapKey, ConcurrentMap sourceCache, byte[] data) throws IOException {
		List<SourceFile> jsSourceFiles = new LinkedList<>();
		//handle xxx.src.js
		handleSourceFiles(sourceCache, jsSourceFiles, _jsContentMap);

		//handle zk.wpd (config file)
		String wpdFileName = "js/src/" + _name + ".wpd.src.js";
		String wpdFilePath = _sourceRoot + wpdFileName;
		String wpdFileContent = this._preScript + new String(data) + this._postScript;
		SourceFile wpdSourceFile = _builder.buildFromCode(wpdFilePath, wpdFileContent);
		cacheJsSource(sourceCache, "/" + wpdFileName, wpdFileContent);
		jsSourceFiles.add(wpdSourceFile);

		//handle merged javascript (ex. js files in zul.wpd)
		handleSourceFiles(sourceCache, jsSourceFiles, _postJsContentMap);

		//compile and merge
		Compiler compiler = new Compiler();
		CompilerOptions compilerOptions = new CompilerOptions();
		CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(compilerOptions);
		compilerOptions.setSourceMapDetailLevel(SourceMap.DetailLevel.ALL);
		compilerOptions.setSourceMapOutputPath(_sourceMappingURL);
		compilerOptions.setStrictModeInput(false);
		WarningLevel.QUIET.setOptionsForWarningLevel(compilerOptions);
		compilerOptions.setEmitUseStrict(false);
		compilerOptions.sourceMapFormat = SourceMap.Format.V3;
		compiler.compile(Collections.emptyList(), jsSourceFiles, compilerOptions);
		String finalWpdContent = compiler.toSource();

		//add mapping url
		finalWpdContent += "//# sourceMappingURL=" + _sourceMappingURL;

		//generate source map
		StringBuilder sourceMapBuilder = new StringBuilder();
		compiler.getSourceMap().appendTo(sourceMapBuilder, _sourceMappingURL);
		sourceMapContentMap.putIfAbsent(sourceMapKey, sourceMapBuilder.toString());

		//clear
		_jsContentMap.clear();
		_postJsContentMap.clear();

		return finalWpdContent;
	}

	private void handleSourceFiles(ConcurrentMap sourceCache, List<SourceFile> sourceFiles, Map<Pair<String, String>, String> jsContentMap) {
		for (Map.Entry<Pair<String, String>, String> en : jsContentMap.entrySet()) {
			String jsContent = en.getValue();
			Pair<String, String> jsKey = en.getKey();
			String jsRealPath = jsKey.getY();
			if (!jsRealPath.contains("wpd.src.js")) { // not wpd
				jsContent = "window['$zk$" + jsRealPath + "'] = function () {\n" + jsContent + "\n};";
				if (jsContent.contains("$mold$")) {
					jsContent = jsContent.replaceAll("([a-zA-Z]+)\\$mold\\$", "window['$1\\$mold\\$']");
				}
			}
			cacheJsSource(sourceCache, jsRealPath.substring(jsRealPath.indexOf("/js/")), jsContent); // set /js/src/*/*.src.js
			if (jsRealPath.startsWith(File.separator)) {
				jsRealPath = _sourceRoot + jsRealPath.substring(1);
			}
			SourceFile sourceFile = _builder.buildFromCode(jsRealPath, jsContent);
			sourceFiles.add(sourceFile);
		}
	}

	private boolean isJsCursorValid() {
		if (this._jsCursor == null) {
			log.debug("({}) invalid _jsCursor", _name);
			return false;
		}
		return true;
	}

	private void cacheJsSource(ConcurrentMap<String, String> sourceCache, String key, String content) {
		sourceCache.putIfAbsent(key, content);
	}
}
