package org.zkoss.zktest.zats.zuti.vm;

import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.DependsOn;

public class WizardModel {
	private String backCommand;
	private String nextCommand;
	private String submitCommand;
	private String backLabel;
	private String nextLabel;
	private String submitLabel;
	private int currentStepIndex;
	private List<WizardStep> availableSteps;
	
	public WizardModel(List<WizardStep> availableSteps, String backCommand, String nextCommand, String submitCommand) {
		this.availableSteps = availableSteps;
		this.currentStepIndex = 0;
		this.backCommand = backCommand;
		this.nextCommand = nextCommand;
		this.submitCommand = submitCommand;
	}

	public void step(int direction) {
		int newIndex = currentStepIndex + direction;
		if (newIndex < 0) throw new IllegalStateException("cannot go back from first step");
		if (newIndex >= availableSteps.size()) throw new IllegalStateException("no next step, already on last");
		currentStepIndex = newIndex;
		BindUtils.postNotifyChange(null, null, this, "currentStep");
	}
	
	public String getBackCommand() {
		return backCommand;
	}

	public String getNextCommand() {
		return nextCommand;
	}
	
	public String getSubmitCommand() {
		return submitCommand;
	}
	
	@DependsOn("currentStep")
	public boolean isFirstStep() {
		return currentStepIndex == 0;
	}
	
	@DependsOn("currentStep")
	public boolean isLastStep() {
		return currentStepIndex == availableSteps.size() - 1;
	}

	@DependsOn("currentStep")
	public int getProgress() {
		return 100 * currentStepIndex / (availableSteps.size() - 1);
	}
	
	public WizardStep getCurrentStep() {
		return availableSteps.get(currentStepIndex);
	}

	public String getBackLabel() {
		return backLabel;
	}

	public void setBackLabel(String backLabel) {
		this.backLabel = backLabel;
	}

	public String getNextLabel() {
		return nextLabel;
	}

	public void setNextLabel(String nextLabel) {
		this.nextLabel = nextLabel;
	}

	public String getSubmitLabel() {
		return submitLabel;
	}

	public void setSubmitLabel(String submitLabel) {
		this.submitLabel = submitLabel;
	}
}
