/* F90_ZK_4375Composer.java

	Purpose:
		
	Description:
		
	History:
		Thu Sep 19 12:26:36 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.zul.DefaultStepModel;
import org.zkoss.zkmax.zul.Step;
import org.zkoss.zkmax.zul.StepModel;
import org.zkoss.zkmax.zul.Stepbar;
import org.zkoss.zul.*;

/**
 * @author jameschu
 */
public class F90_ZK_4375Composer extends SelectorComposer {
	@Wire
	private Stepbar stepbar;
	@Wire
	private Label answer;
	private StepModel<Integer> model;
	private int count = 1;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		model = new DefaultStepModel<>();
		while (count <= 3)
			model.add(count++);
		model.setActiveIndex(0);
		stepbar.setModel(model);
	}

	@Listen("onClick = #modelAdd")
	public void modelAdd() {
		model.add(count++);
	}

	@Listen("onClick = #modelRemove")
	public void modelRemove() {
		ListModel<Integer> steps = model.getSteps();
		model.remove(steps.getSize() - 1);
		count--;
	}

	@Listen("onClick = #modelNext")
	public void modelNext() {
		model.next();
	}

	@Listen("onClick = #modelBack")
	public void modelBack() {
		model.back();
	}

	@Listen("onClick = #modelIndex2")
	public void modelIndex2() {
		model.setActiveIndex(2);
	}

	@Listen("onClick = #modelGetIndex")
	public void modelGetIndex() {
		answer.setValue(String.valueOf(model.getActiveIndex()));
	}

	@Listen("onClick = #wrappedLabels")
	public void changeWrappedLabels() {
		stepbar.setWrappedLabels(!stepbar.isWrappedLabels());
	}

	@Listen("onClick = #linear")
	public void linear() {
		stepbar.setLinear(!stepbar.isLinear());
	}

	@Listen("onClick = #back")
	public void back() {
		stepbar.back();
	}

	@Listen("onClick = #next")
	public void next() {
		stepbar.next();
	}

	@Listen("onClick = #getActiveIndex")
	public void getActiveIndex() {
		answer.setValue(String.valueOf(stepbar.getActiveIndex()));
	}

	@Listen("onClick = #isComplete1")
	public void isComplete1() {
		answer.setValue(String.valueOf(stepbar.getSteps().get(0).isComplete()));
	}

	@Listen("onClick = #isComplete2")
	public void isComplete2() {
		answer.setValue(String.valueOf(stepbar.getSteps().get(1).isComplete()));
	}

	@Listen("onClick = #isComplete3")
	public void isComplete3() {
		answer.setValue(String.valueOf(stepbar.getSteps().get(2).isComplete()));
	}

	@Listen("onClick = #setActiveIndex0")
	public void setActiveIndex0() {
		stepbar.setActiveIndex(0);
	}

	@Listen("onClick = #setActiveIndex1")
	public void setActiveIndex1() {
		stepbar.setActiveIndex(1);
	}

	@Listen("onClick = #setActiveIndex2")
	public void setActiveIndex2() {
		stepbar.setActiveIndex(2);
	}

	@Listen("onClick = #setComplete1")
	public void setComplete1() {
		Step step = stepbar.getSteps().get(0);
		step.setComplete(!step.isComplete());
	}

	@Listen("onClick = #setComplete2")
	public void setComplete2() {
		Step step = stepbar.getSteps().get(1);
		step.setComplete(!step.isComplete());
	}

	@Listen("onClick = #setComplete3")
	public void setComplete3() {
		Step step = stepbar.getSteps().get(2);
		step.setComplete(!step.isComplete());
	}

	@Listen("onClick = #addStep")
	public void addStep() {
		List<Step> steps = stepbar.getSteps();
		int index = steps.size() + 1;
		Step step = new Step("Step " + index);
		stepbar.appendChild(step);
	}

	@Listen("onClick = #removeStep")
	public void removeStep() {
		stepbar.removeChild(stepbar.getLastChild());
	}

	@Listen("onClick = #detach")
	public void detach() {
		stepbar.detach();
	}

	@Listen("onClick = #setError1")
	public void setError1() {
		Step step = stepbar.getSteps().get(0);
		step.setError(!step.isError());
	}

	@Listen("onClick = #setError2")
	public void setError2() {
		Step step = stepbar.getSteps().get(1);
		step.setError(!step.isError());
	}

	@Listen("onClick = #setError3")
	public void setError3() {
		Step step = stepbar.getSteps().get(2);
		step.setError(!step.isError());
	}
}
