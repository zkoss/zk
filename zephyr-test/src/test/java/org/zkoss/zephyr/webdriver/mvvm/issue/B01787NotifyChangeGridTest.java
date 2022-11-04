package org.zkoss.zephyr.webdriver.mvvm.issue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class B01787NotifyChangeGridTest extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery updatePath = jq("$updatePath");
		JQuery updateA = jq("$updateA");
		JQuery updateAName = jq("$updateAName");
		JQuery updateB = jq("$updateB");
		JQuery updateBName = jq("$updateBName");
		List<JQuery> btns = new ArrayList<JQuery>();
		btns.add(updatePath);
		btns.add(updateA);
		btns.add(updateAName);
		btns.add(updateB);
		btns.add(updateBName);

		String[] answer0 = {"Item A:0", "Item B:0", "Item A:0", "Item B:0"};
		String[] answer1 = {"Item A:0", "Item B:0", "Item A:1", "Item B:1"};
		String[] answer2 = {"Item A.*:x:0", "Item B:1", "Item A.*:0", "Item B:1"};
		String[] answer3 = {"Item A.name:x:0", "Item B:1", "Item A.name:0", "Item B:1"};
		String[] answer4 = {"Item A.name:x:1", "Item B.*:x:0", "Item A.name:0", "Item B.*:0"};
		String[] answer5 = {"Item A.name:x:1", "Item B.name:x:0", "Item A.name:0", "Item B.name:0"};
		List<String[]> answers = new ArrayList<String[]>();
		answers.add(answer0);
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);
		answers.add(answer4);
		answers.add(answer5);

		JQuery comp1s;
		JQuery box1s;
		for (int i = 0; i < answers.size(); i++) {
			if (i > 0) {
				click(btns.get(i - 1));
				waitResponse();
			}
			comp1s = jq("$grid @label");
			box1s = jq("$box1 @label");
			assertEquals(2, comp1s.length());
			assertEquals(answers.get(i)[0], comp1s.eq(0).text());
			assertEquals(answers.get(i)[1], comp1s.eq(1).text());
			assertEquals(2, box1s.length());
			assertEquals(answers.get(i)[2], box1s.eq(0).text());
			assertEquals(answers.get(i)[3], box1s.eq(1).text());
		}
	}
}
