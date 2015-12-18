package org.zkoss.zktest.zats.bind.issue;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;

public class B01787NotifyChangeListboxTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		 
		ComponentAgent updatePath = desktop.query("#updatePath");
		ComponentAgent updateA = desktop.query("#updateA");
		ComponentAgent updateAName = desktop.query("#updateAName");
		ComponentAgent updateB = desktop.query("#updateB");
		ComponentAgent updateBName = desktop.query("#updateBName");
		List<ComponentAgent> btns = new ArrayList<ComponentAgent>();
		btns.add(updatePath);
		btns.add(updateA);
		btns.add(updateAName);
		btns.add(updateB);
		btns.add(updateBName);
		
		String[] answer0 = { "Item A:0","Item B:0","Item A:0","Item B:0" };
		String[] answer1 = { "Item A:1","Item B:1","Item A:1","Item B:1" };
		String[] answer2 = { "Item A.*:x:0","Item B:2","Item A.*:0","Item B:1" };
		String[] answer3 = { "Item A.name:x:0","Item B:2","Item A.name:0","Item B:1" };
		String[] answer4 = { "Item A.name:x:1","Item B.*:x:0","Item A.name:0","Item B.*:0" };
		String[] answer5 = { "Item A.name:x:1","Item B.name:x:0","Item A.name:0","Item B.name:0" };
		List<String[]> answers = new ArrayList<String[]>();
		answers.add(answer0);
		answers.add(answer1);
		answers.add(answer2);
		answers.add(answer3);
		answers.add(answer4);
		answers.add(answer5);
		
		List<ComponentAgent> comp1s;
		List<ComponentAgent> box1s;
		for (int i = 0; i < answers.size(); i++) {
			if (i > 0) {
				btns.get(i-1).click();
			}
			comp1s = desktop.queryAll("#listbox listitem");
			box1s = desktop.queryAll("#box1 label");
			assertEquals(2, comp1s.size());
			assertEquals(answers.get(i)[0], comp1s.get(0).as(Listitem.class).getLabel());
			assertEquals(answers.get(i)[1], comp1s.get(1).as(Listitem.class).getLabel());
			assertEquals(2, box1s.size());
			assertEquals(answers.get(i)[2], box1s.get(0).as(Label.class).getValue());
			assertEquals(answers.get(i)[3], box1s.get(1).as(Label.class).getValue());
		}
	}
}
