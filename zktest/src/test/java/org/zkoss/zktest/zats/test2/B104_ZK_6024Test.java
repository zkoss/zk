/* B104_ZK_6024Test.java

        Purpose:
                
        Description:
                
        History:
                Thu Jan 22 11:44:00 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.web.fn.ThemeFns;
import org.zkoss.web.servlet.xel.RequestContext;
import org.zkoss.web.servlet.xel.RequestContexts;
import org.zkoss.web.theme.StandardTheme;
import org.zkoss.web.theme.Theme;
import org.zkoss.web.theme.ThemeRegistry;
import org.zkoss.web.theme.ThemeResolver;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zul.theme.StandardThemeProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class B104_ZK_6024Test extends WebDriverTestCase {

    private ThemeRegistry _originalRegistry;
    private ThemeResolver _originalResolver;
    private final static String TEST_THEME_NAME = "iceblue_c";

    @BeforeEach
    public void setUp() {
        _originalRegistry = ThemeFns.getThemeRegistry();
        _originalResolver = ThemeFns.getThemeResolver();

        RequestContext context = mock(RequestContext.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(context.getRequest()).thenReturn(request);
        RequestContexts.push(context);

        ThemeFns.setThemeRegistry(new ThemeRegistry() {
            @Override
            public boolean register(Theme theme) {
                return true;
            }

            @Override
            public boolean deregister(Theme theme) {
                return true;
            }

            @Override
            public Theme[] getThemes() {
                return new Theme[0];
            }

            @Override
            public Theme getTheme(String themeName) {
                if (TEST_THEME_NAME.equals(themeName)) {
                    return new StandardTheme(TEST_THEME_NAME, TEST_THEME_NAME, 100, StandardTheme.ThemeOrigin.JAR);
                }
                return null;
            }

            @Override
            public boolean hasTheme(String themeName) {
                return TEST_THEME_NAME.equals(themeName);
            }
        });

        ThemeFns.setThemeResolver(new ThemeResolver() {
            @Override
            public String getTheme(HttpServletRequest request) {
                return TEST_THEME_NAME;
            }

            @Override
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
    public void testBeforeWidgetCSS() {
        StandardThemeProvider provider = new StandardThemeProvider();
        Execution exec = null;

        // Test case 1: Font Awesome URI (The target of the fix)
        String fontUri = "~./zul/font/font-awesome.css.dsp";
        String resolvedFont = provider.beforeWidgetCSS(exec, fontUri);
        assertEquals("~./iceblue_c/zul/font/font-awesome.css.dsp", resolvedFont);

        // Test case 2: Normal CSS URI (Existing functionality)
        String cssUri = "~./zul/css/norm.css.dsp";
        String resolvedCss = provider.beforeWidgetCSS(exec, cssUri);
        assertEquals("~./iceblue_c/zul/css/norm.css.dsp", resolvedCss);
    }
}
