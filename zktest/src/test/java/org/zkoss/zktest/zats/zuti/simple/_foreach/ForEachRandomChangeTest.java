package org.zkoss.zktest.zats.zuti.simple._foreach;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;

/**
 * @author jumperchen
 *
 */
public class ForEachRandomChangeTest extends ZutiBasicTestCase {
	
	private String seed;
	private int iteration;

	@Test
	public void test() {
		DesktopAgent desktop = connect();
		seed = desktop.query("#seed").as(Label.class).getValue();
		ComponentAgent result = desktop.query("#result");
		ComponentAgent testLabel = desktop.query("#testLabel");
		
		ComponentAgent button = desktop.query("button");

		ListModelList<Integer> debugListModel = (ListModelList<Integer>) result.getAttribute("debugListModel");
		
		verifyModel(debugListModel, testLabel, result.getChildren());
		
		iteration = 0;
		for(iteration = 0; iteration < 1000; iteration++) {
			button.click();
			verifyModel(debugListModel, testLabel, result.getChildren());
			//System.out.println(debugListModel);
		}
	}

	private void verifyModel(ListModelList<Integer> model, ComponentAgent testLabel, List<ComponentAgent> children) {
		String testStepInfo = ", Iteration: " + iteration + " Seed:" + seed;
		Assertions.assertEquals(model.toString(), testLabel.as(Label.class).getValue(), "testLabel does not match model.toString()" + testStepInfo);
		int index = 0;
		for (ComponentAgent componentAgent : children) {
			String itemLabel = componentAgent.as(Label.class).getValue();
			Assertions.assertEquals( model.get(index).toString(), itemLabel, "forEach item label mismatch at position:" + index + testStepInfo);
			index++;
		}
	}

}
