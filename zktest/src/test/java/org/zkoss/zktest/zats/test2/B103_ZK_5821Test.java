/* B103_ZK_5821Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Dec 15 14:55:51 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.JsContentRenderer;

public class B103_ZK_5821Test extends WebDriverTestCase{
    @Test
    public void test() {
        connect();
        assertNoAnyError();
    }

    @Test
    public void testValidateJSExpression() {
        JsContentRenderer renderer = new JsContentRenderer();
        Map<String, String> overrides = new HashMap<>();

        overrides.put("test1", "");
        assertDoesNotThrow(() -> renderer.renderWidgetOverrides(overrides));
        overrides.clear();

        overrides.put("test2", "alert('test');");
        assertThrows(UiException.class, () -> renderer.renderWidgetOverrides(overrides));
        overrides.clear();

        overrides.put("test3", "alert('test'),");
        assertThrows(UiException.class, () -> renderer.renderWidgetOverrides(overrides));
        overrides.clear();

        overrides.put("test4", "function() { var x = 1; return x; }");
        assertDoesNotThrow(() -> renderer.renderWidgetOverrides(overrides));
        overrides.clear();

        overrides.put("test5", "alert('test')");
        assertDoesNotThrow(() -> renderer.renderWidgetOverrides(overrides));
        overrides.clear();

        overrides.put("test6", "var x = 1; var y = 2");
        assertThrows(UiException.class, () -> renderer.renderWidgetOverrides(overrides));
        overrides.clear();

        overrides.put("test7", "var x = 1; var y = 2");
        assertThrows(UiException.class, () -> renderer.renderWidgetOverrides(overrides));
        overrides.clear();

        overrides.put("test8", "'var x = 1'; 'var y = 2'");
        assertThrows(UiException.class, () -> renderer.renderWidgetOverrides(overrides));
        overrides.clear();

        overrides.put("test9", "var x = 1; 'some string'");
        assertThrows(UiException.class, () -> renderer.renderWidgetOverrides(overrides));
        overrides.clear();

        overrides.put("test10", "'some string' var x = 1;");
        assertThrows(UiException.class, () -> renderer.renderWidgetOverrides(overrides));
        overrides.clear();

        overrides.put("test11", "'normal string containing a semicolon ; nothing special here'");
        assertDoesNotThrow(() -> renderer.renderWidgetOverrides(overrides));
        overrides.clear();

        overrides.put("test12", "'hello world'");
        assertDoesNotThrow(() -> renderer.renderWidgetOverrides(overrides));
        overrides.clear();

        overrides.put("test13", null);
        assertDoesNotThrow(() -> renderer.renderWidgetOverrides(overrides));
        overrides.clear();

        overrides.put("test14", "'{key: value, anotherKey: anotherValue}'");
        assertDoesNotThrow(() -> renderer.renderWidgetOverrides(overrides));
        overrides.clear();

        overrides.put("test15", "{ \"key\": \"value\", \"anotherKey\": \"another; Value\" }");
        assertDoesNotThrow(() -> renderer.renderWidgetOverrides(overrides));
        overrides.clear();
    }

}
