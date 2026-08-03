/* B100_ZK_6062Test.java

	Purpose:
		Test that WpdExtendlet.writeAppInfo() correctly handles javascript-module
		entries from multiple LanguageDefinitions without missing commas.

	Description:
		Uses Mockito to mock all dependencies of writeAppInfo(), then calls the
		real method via reflection to verify the produced output has proper comma
		separation in the javascript-module section.

	History:
		Mon Mar 31 10:00:00 CST 2026, Created by hawkchen

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.zkoss.lang.Library;
import org.zkoss.web.servlet.http.Encodes;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.web.util.resource.ExtendletContext;
import org.zkoss.zk.ui.http.WpdExtendlet;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.util.Configuration;

/**
 * ZK-6062: Missing comma in generated wpd for javascript-module in lang-addon.xml.
 * <p>Calls the real WpdExtendlet.writeAppInfo() via reflection with mocked
 * dependencies to verify the javascript-module comma generation.</p>
 */
public class B100_ZK_6062Test {

	@Test
	public void testWriteAppInfoWithMultipleLanguageDefinitions() throws Exception {
		// Mock LanguageDefinitions with javascript-module entries
		Map<String, String> mods1 = new LinkedHashMap<>();
		mods1.put("zktest.mod1", "1.0.0");
		LanguageDefinition langdef1 = mock(LanguageDefinition.class);
		when(langdef1.getJavaScriptModules()).thenReturn(Collections.unmodifiableMap(mods1));

		Map<String, String> mods2 = new LinkedHashMap<>();
		mods2.put("zktest.mod2", "2.0.0");
		LanguageDefinition langdef2 = mock(LanguageDefinition.class);
		when(langdef2.getJavaScriptModules()).thenReturn(Collections.unmodifiableMap(mods2));

		// Mock WebApp and Configuration
		WebApp wapp = mock(WebApp.class);
		when(wapp.getVersion()).thenReturn("10.0.0");
		when(wapp.getBuild()).thenReturn("test");
		when(wapp.getUpdateURI(anyBoolean())).thenReturn("/zkau");
		when(wapp.getResourceURI(anyBoolean())).thenReturn("/zkres");
		Configuration config = mock(Configuration.class);
		when(config.getProcessingPromptDelay()).thenReturn(900);
		when(config.getTooltipDelay()).thenReturn(800);
		when(config.getAutoResendTimeout()).thenReturn(200);
		when(wapp.getConfiguration()).thenReturn(config);

		// Mock servlet objects
		ServletContext servletCtx = mock(ServletContext.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);

		// Create WpdExtendlet instance
		WpdExtendlet extendlet = new WpdExtendlet();

		// Inject a mock ExtendletContext into AbstractExtendlet._webctx
		// so getServletContext() returns our mock
		Class<?> absExtClass = extendlet.getClass().getSuperclass(); // AbstractExtendlet
		Field webctxField = absExtClass.getDeclaredField("_webctx");
		webctxField.setAccessible(true);

		ExtendletContext webCtx = mock(ExtendletContext.class);
		when(webCtx.getServletContext()).thenReturn(servletCtx);
		webctxField.set(extendlet, webCtx);

		// Create RequestContext via reflection (it's a package-private inner class)
		Class<?> reqCtxClass = Class.forName("org.zkoss.zk.ui.http.AbstractExtendlet$RequestContext");
		Constructor<?> reqCtxCtor = reqCtxClass.getDeclaredConstructors()[0];
		reqCtxCtor.setAccessible(true);
		Object reqCtx = reqCtxCtor.newInstance(extendlet, request, response);

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try (MockedStatic<LanguageDefinition> ldMock = Mockito.mockStatic(LanguageDefinition.class);
			 MockedStatic<Encodes> encMock = Mockito.mockStatic(Encodes.class);
			 MockedStatic<WebApps> waMock = Mockito.mockStatic(WebApps.class);
			 MockedStatic<Library> libMock = Mockito.mockStatic(Library.class)) {

			ldMock.when(() -> LanguageDefinition.getByDeviceType("ajax"))
					.thenReturn(Arrays.asList(langdef1, langdef2));
			encMock.when(() -> Encodes.encodeURL(
					any(ServletContext.class), any(), any(), anyString()))
					.thenAnswer(inv -> inv.getArgument(3));
			waMock.when(() -> WebApps.getFeature(anyString()))
					.thenReturn(false);
			libMock.when(() -> Library.getProperty(anyString(), anyString()))
					.thenAnswer(inv -> inv.getArgument(1));

			// Call the real writeAppInfo via reflection
			Method writeAppInfo = WpdExtendlet.class.getDeclaredMethod(
					"writeAppInfo", reqCtxClass, OutputStream.class, WebApp.class);
			writeAppInfo.setAccessible(true);
			writeAppInfo.invoke(extendlet, reqCtx, out, wapp);
		}

		String output = out.toString("UTF-8");

		// Verify the output contains properly comma-separated javascript-modules
		assertTrue(output.contains("'zktest.mod1':'1.0.0'"),
				"Should contain mod1: " + output);
		assertTrue(output.contains("'zktest.mod2':'2.0.0'"),
				"Should contain mod2: " + output);
		assertTrue(output.contains("'zktest.mod1':'1.0.0','zktest.mod2':'2.0.0'"),
				"Modules should be comma-separated: " + output);
		assertFalse(output.contains("'1.0.0''"),
				"Must not have adjacent quotes (missing comma): " + output);
	}
}
