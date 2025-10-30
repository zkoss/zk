package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F60_ZK_469Test extends WebDriverTestCase {
	int w1x = 60;
	int w1y = 100;
	int w2x = 160;
	int w2y = 200;
	int w3x = 260;
	int w3y = 300;

	@Test
	public void test() {
		connect();
		JQuery parent = jq("@absolutelayout");
		JQuery w1 = jq("$w1");
		JQuery w2 = jq("$w2");
		JQuery w3 = jq("$w3");

		int parentLeft = parent.offsetLeft();
		int parentTop = parent.offsetTop();
		int[] addX = new int[] {100, 0, -100, 0};
		int[] addY = new int[] {0, 100, 0, -100};
		int[] btnOrder = new int[] {0, 2, 1, 3};
		for (int i = 0; i < 4; i++) {
			click(jq("@button").eq(btnOrder[i]));
			waitResponse();
			w1x += addX[i];
			w2x += addX[i];
			w3x += addX[i];
			w1y += addY[i];
			w2y += addY[i];
			w3y += addY[i];
			assertTrue(Math.abs((jq(w1).offsetLeft() - parentLeft - w1x)) < 2
										&& Math.abs(jq(w1).offsetTop() - parentTop - w1y) < 2
										&& Math.abs((jq(w2).offsetLeft() - parentLeft - w2x)) < 2
										&& Math.abs(jq(w2).offsetTop() - parentTop - w2y) < 2
										&& Math.abs((jq(w3).offsetLeft() - parentLeft - w3x)) < 2
										&& Math.abs(jq(w3).offsetTop() - parentTop - w3y) < 2,
					"The position should be changed correctly");
		}
	}
}
