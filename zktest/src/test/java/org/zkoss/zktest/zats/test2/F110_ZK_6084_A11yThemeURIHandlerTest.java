/* F110_ZK_6084_A11yThemeURIHandlerTest.java

	Purpose:

	Description:

	History:
		Thu Oct 29 16:58:29 CST 2026, Created by peggypeng

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.zkoss.html.StyleSheet;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.web.fn.ThemeFns;
import org.zkoss.web.servlet.xel.RequestContext;
import org.zkoss.web.servlet.xel.RequestContexts;
import org.zkoss.web.theme.StandardTheme;
import org.zkoss.web.theme.Theme;
import org.zkoss.web.theme.ThemeRegistry;
import org.zkoss.web.theme.ThemeResolver;
import org.zkoss.za11y.theme.A11yThemeURIHandler;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.ThemeURIModifier;

@Tag("WcagTestOnly")
public class F110_ZK_6084_A11yThemeURIHandlerTest extends WebDriverTestCase {
	private static final String THEME_NAME = "iceblue_c";
	private static final String ZA11Y_SHEET = "~./za11y/css/iceblue-c.css.dsp";
	private static final String THEME_SHEET = "~./iceblue_c/za11y/css/iceblue-c.css.dsp";

	private ThemeRegistry _originalRegistry;
	private ThemeResolver _originalResolver;

	@BeforeEach
	public void setUp() {
		_originalRegistry = ThemeFns.getThemeRegistry();
		_originalResolver = ThemeFns.getThemeResolver();

		RequestContext context = mock(RequestContext.class);
		when(context.getRequest()).thenReturn(mock(HttpServletRequest.class));
		RequestContexts.push(context);

		ThemeFns.setThemeRegistry(new ThemeRegistry() {
			public boolean register(Theme theme) {
				return true;
			}

			public boolean deregister(Theme theme) {
				return true;
			}

			public Theme[] getThemes() {
				return new Theme[0];
			}

			public Theme getTheme(String themeName) {
				return THEME_NAME.equals(themeName)
						? new StandardTheme(THEME_NAME, THEME_NAME, 100, StandardTheme.ThemeOrigin.JAR) : null;
			}

			public boolean hasTheme(String themeName) {
				return THEME_NAME.equals(themeName);
			}
		});

		ThemeFns.setThemeResolver(new ThemeResolver() {
			public String getTheme(HttpServletRequest request) {
				return THEME_NAME;
			}

			public void setTheme(HttpServletRequest request, HttpServletResponse response, String themeName) {
			}
		});
	}

	@AfterEach
	public void tearDown() {
		ThemeFns.setThemeRegistry(_originalRegistry);
		ThemeFns.setThemeResolver(_originalResolver);
		RequestContexts.pop();
	}

	@Test
	public void test() throws Exception {
		Assertions.assertEquals(ZA11Y_SHEET, addedSheet(false));
		Assertions.assertEquals(THEME_SHEET, addedSheet(true));
	}

	private String addedSheet(boolean themeShipsSheet) throws Exception {
		URL found = new URL("file:/found.css.dsp");
		WebApp wapp = mock(WebApp.class);
		Configuration config = mock(Configuration.class);
		when(wapp.getConfiguration()).thenReturn(config);
		when(config.getDisabledThemeURIs()).thenReturn(Collections.emptySet());
		when(wapp.getResource(ZA11Y_SHEET)).thenReturn(found);
		when(wapp.getResource(THEME_SHEET)).thenReturn(themeShipsSheet ? found : null);

		Desktop desktop = mock(Desktop.class);
		when(desktop.getWebApp()).thenReturn(wapp);
		Execution exec = mock(Execution.class);
		when(exec.getDesktop()).thenReturn(desktop);

		ThemeURIModifier modifier = mock(ThemeURIModifier.class);
		when(modifier.getURIs()).thenReturn(new ArrayList<>());
		new A11yThemeURIHandler().modifyThemeURIs(exec, modifier);

		ArgumentCaptor<StyleSheet> sheet = ArgumentCaptor.forClass(StyleSheet.class);
		verify(modifier).add(anyInt(), sheet.capture());
		return sheet.getValue().getHref();
	}
}
