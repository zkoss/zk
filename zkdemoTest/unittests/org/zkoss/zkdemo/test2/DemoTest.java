package org.zkoss.zkdemo.test2;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;
import com.thoughtworks.selenium.Selenium;

public class DemoTest extends ZKTestCase {

	private Selenium browser;

	public void setUp() {
		browser = Server;
		browser.start();
	}

	public void testTodo() {
		try {
			Thread.sleep(5000);
			browser.open(url + "/SeleniumHowTo/todo.zul");
			System.out.println("Enter first thing");
			browser.focus("zk-comp-13");
			browser.type("zk-comp-13", "Somthing impotant");
			Thread.sleep(1000);
			browser.focus("zk-comp-15");
			browser.type("zk-comp-15", "1");
			Thread.sleep(1000);
			browser.focus("zk-comp-17!real");
			browser.type("zk-comp-17!real", "Feb 14, 2009");
			Thread.sleep(1000);
			browser.focus("zk-comp-18!real");
			browser.click("zk-comp-18!real");
			Thread.sleep(1000);
			System.out.println("Enter second thing");
			browser.focus("zk-comp-13");
			browser.type("zk-comp-13", "Somthing nothing");
			Thread.sleep(1000);
			browser.focus("zk-comp-15");
			browser.type("zk-comp-15", "3");
			Thread.sleep(1000);
			browser.focus("zk-comp-17!real");
			browser.type("zk-comp-17!real", "Feb 15, 2009");
			Thread.sleep(1000);
			browser.focus("zk-comp-18!real");
			browser.click("zk-comp-18!real");

			Thread.sleep(1500);

			assertEquals("Somthing impotant", browser
					.getText("zk-comp-22!cave").trim());
			assertEquals("1", browser.getText("zk-comp-23!cave").trim());
			assertEquals("2009-02-14", browser.getText("zk-comp-24!cave")
					.trim());

			if (!browser.getText("zk-comp-26!cave").trim().equals(
					"Somthing nothing")
					|| !browser.getText("zk-comp-27!cave").trim().equals("3")
					|| !browser.getText("zk-comp-28!cave").trim().equals(
							"2009-02-15"))
				fail("Update error!");
			browser.close();
			System.out.print("done!");

			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void tearDown() {
		browser.stop();
	}
}