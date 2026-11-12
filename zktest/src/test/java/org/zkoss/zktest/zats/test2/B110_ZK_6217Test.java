/* B110_ZK_6217Test.java

		Purpose:

		Description:

		History:
				Thu Nov 12 14:20:05 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openqa.selenium.By;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B110_ZK_6217Test extends WebDriverTestCase {
	@Test
	public void test(@TempDir Path directory) throws IOException {
		String name = "<img src=x onerror=zk6217=1>.txt";
		Path file = Files.write(directory.resolve(name), "upload".getBytes(StandardCharsets.UTF_8));
		connect();
		click(jq("$open"));
		waitResponse();
		driver.findElement(By.cssSelector("input[type=file]")).sendKeys(file.toAbsolutePath().toString());
		waitResponse();

		JQuery row = jq(".z-fileupload-file");
		assertEquals(1, row.find("img").length(), "the file name must not create an element");
		assertTrue(row.text().contains(name), "the file name must be shown as plain text: " + row.text());
	}
}
