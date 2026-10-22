/* B110_ZK_6199Test.java

        Purpose:
                
        Description:
                
        History:
                Mon Oct 19 14:32:44 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B110_ZK_6199Test extends WebDriverTestCase {
	@Test
	public void styleUploadRowsAndRemoveCompletedFiles(@TempDir Path directory) throws IOException {
		Path file = Files.write(directory.resolve("ZK-6199.txt"), "upload".getBytes(StandardCharsets.UTF_8));
		connect();
		click(jq("$open"));
		waitResponse();

		// ZK-6199: hold the first upload so the progress row survives until its styles are checked.
		eval("(function () {var start = zul.Uploader.prototype.start;"
				+ "zul.Uploader.prototype.start = function () {"
				+ "zul.Uploader.prototype.start = start; window.zk6199Uploader = this; return true;};})()");
		driver.findElement(By.cssSelector("input[type=file]")).sendKeys(file.toAbsolutePath().toString());
		waitResponse();

		assertTrue(jq(".z-fileupload-file").exists(), "the upload row must expose a stylesheet hook");
		assertFalse(jq(".z-fileupload-file[style], .z-fileupload-file [style]").exists(),
				"row styles must not override application stylesheets");
		assertEquals("rgb(244, 248, 255)", jq(".z-fileupload-file").css("background-color"));
		assertEquals("rgb(15, 59, 130)", jq(".z-fileupload-file").css("color"));
		assertEquals("rgb(153, 170, 189)", jq(".z-fileupload-file").css("border-top-color"));
		assertEquals("1px", jq(".z-fileupload-file").css("border-top-width"));
		assertEquals("2px", jq(".z-fileupload-file").css("padding-left"));
		assertEquals("16px", jq(".z-fileupload-file").css("min-height"));
		assertEquals("content-box", jq(".z-fileupload-file").css("box-sizing"));
		assertEquals("11px", jq(".z-fileupload-file").css("font-size"));
		assertEquals("arial, sans-serif", jq(".z-fileupload-file").css("font-family").toLowerCase(Locale.ROOT));
		assertEquals("left", jq(".z-fileupload-cancel").css("float"));
		assertEquals("2px", jq(".z-fileupload-cancel").css("padding-left"));
		assertEquals("rgb(3, 102, 172)", jq(".z-fileupload-cancel").css("color"));
		assertEquals("700", jq(".z-fileupload-cancel").css("font-weight"));
		assertEquals("pointer", jq(".z-fileupload-cancel").css("cursor"));
		assertEquals("left", jq(".z-fileupload-icon").css("float"));
		assertEquals("24px", jq(".z-fileupload-size").css("padding-left"));
		assertFalse(jq("$fileupload").isVisible(), "the browse button must hide at the one-file limit");
		new WebDriverWait(driver, Duration.ofSeconds(5)).until(d ->
				"true".equals(getEval("document.querySelector('.z-fileupload-icon').naturalWidth > 0")));

		eval("(function () {var style = document.documentElement.style;"
				+ "style.setProperty('--zk-fileupload-font-family', 'monospace');"
				+ "style.setProperty('--zk-fileupload-background-color', 'rgb(21,22,23)');"
				+ "style.setProperty('--zk-fileupload-border-color', 'rgb(24,25,26)');"
				+ "style.setProperty('--zk-fileupload-color', 'rgb(27,28,29)');"
				+ "style.setProperty('--zk-fileupload-cancel-color', 'rgb(30,31,32)');})()");
		assertEquals("monospace", jq(".z-fileupload-file").css("font-family"));
		assertEquals("rgb(21, 22, 23)", jq(".z-fileupload-file").css("background-color"));
		assertEquals("rgb(24, 25, 26)", jq(".z-fileupload-file").css("border-top-color"));
		assertEquals("rgb(27, 28, 29)", jq(".z-fileupload-file").css("color"));
		assertEquals("rgb(30, 31, 32)", jq(".z-fileupload-cancel").css("color"));
		eval("['font-family', 'background-color', 'border-color', 'color', 'cancel-color'].forEach(function (name) {"
				+ "document.documentElement.style.removeProperty('--zk-fileupload-' + name);})");

		eval("(function () {var style = document.createElement('style'); style.id = 'zk6199Style';"
				+ "style.textContent = '.z-fileupload-file {background:rgb(1,2,3);color:rgb(4,5,6);"
				+ "border:3px solid rgb(7,8,9);padding:5px;font:15px serif;}"
				+ ".z-fileupload-cancel {float:right;padding-left:7px;color:rgb(10,11,12);font-weight:400;cursor:default;}"
				+ ".z-fileupload-icon {float:right;} .z-fileupload-size {padding-left:31px;}';"
				+ "document.head.appendChild(style);})()");
		assertEquals("rgb(1, 2, 3)", jq(".z-fileupload-file").css("background-color"));
		assertEquals("rgb(4, 5, 6)", jq(".z-fileupload-file").css("color"));
		assertEquals("rgb(7, 8, 9)", jq(".z-fileupload-file").css("border-top-color"));
		assertEquals("3px", jq(".z-fileupload-file").css("border-top-width"));
		assertEquals("5px", jq(".z-fileupload-file").css("padding-left"));
		assertEquals("15px", jq(".z-fileupload-file").css("font-size"));
		assertEquals("serif", jq(".z-fileupload-file").css("font-family"));
		assertEquals("right", jq(".z-fileupload-cancel").css("float"));
		assertEquals("7px", jq(".z-fileupload-cancel").css("padding-left"));
		assertEquals("rgb(10, 11, 12)", jq(".z-fileupload-cancel").css("color"));
		assertEquals("400", jq(".z-fileupload-cancel").css("font-weight"));
		assertEquals("default", jq(".z-fileupload-cancel").css("cursor"));
		assertEquals("right", jq(".z-fileupload-icon").css("float"));
		assertEquals("31px", jq(".z-fileupload-size").css("padding-left"));
		eval("(function () {document.getElementById('zk6199Style').remove(); window.zk6199Uploader.start();"
				+ "delete window.zk6199Uploader;})()");
		waitForUpload();
		assertFalse(jq(".z-fileupload-size").exists(), "completion must remove the progress size row");
		assertEquals(1, jq(".z-fileupload-file").length());

		click(jq(".z-fileupload-cancel"));
		waitResponse();
		assertFalse(jq(".z-fileupload-file").exists(), "cancel must remove the completed row");
		assertTrue(jq("$fileupload").isVisible(), "cancel must allow choosing a replacement file");
		driver.findElement(By.cssSelector("input[type=file]")).sendKeys(file.toAbsolutePath().toString());
		waitForUpload();
		assertEquals(1, jq(".z-fileupload-file").length());
		click(jq("$submit"));
		waitResponse();
		assertEquals("1:ZK-6199.txt", jq("$result").text(), "only the replacement file must reach the callback");
	}

	private void waitForUpload() {
		new WebDriverWait(driver, Duration.ofSeconds(10)).until(d ->
				jq(".z-fileupload-icon[src$=\"attachment.gif\"]").exists());
		waitResponse();
	}
}
