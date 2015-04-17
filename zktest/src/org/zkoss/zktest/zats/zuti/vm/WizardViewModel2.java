package org.zkoss.zktest.zats.zuti.vm;

import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;

public class WizardViewModel2 {
	private int currentStepIndex;
	private List<WizardStep> availableSteps;
	
	public WizardViewModel2(List<WizardStep> availableSteps) {
		this.availableSteps = availableSteps;
		this.currentStepIndex = 0;
	}

	@Command("back") 
	public void back() {
		WizardStep previousStep = getCurrentStep();
		step(-1);
		onBack(getCurrentStep(), previousStep);
	}
	
	@Command("next")
	public void next() {
		if(isLastStep()) {
			onFinish(getCurrentStep());
		} else {
			WizardStep previousStep = getCurrentStep();
			step(1);
			onNext(getCurrentStep(), previousStep);
		}
	}
	
	protected void onBack(WizardStep currentStep, WizardStep previousStep) {}
	protected void onNext(WizardStep currentStep, WizardStep previousStep) {}
	protected void onFinish(WizardStep currentStep) {}

	protected String getBackLabelFor(WizardStep wizardStep) {
		return "Back";
	}
	
	protected String getNextLabelFor(WizardStep wizardStep) {
		return isLastStep() ? "Finish" : "Next";
	}
	
	private void step(int direction) {
		int newIndex = currentStepIndex + direction;
		if (newIndex < 0) throw new IllegalStateException("cannot go back from first step");
		if (newIndex >= availableSteps.size()) throw new IllegalStateException("no next step, already on last");
		currentStepIndex = newIndex;
		BindUtils.postNotifyChange(null, null, this, "currentStep");
	}
	
	public WizardStep getCurrentStep() {
		return availableSteps.get(currentStepIndex);
	}
	
	@DependsOn("currentStep")
	public String getCurrentStepTemplate() {
		return getCurrentStep().getId();
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
		return 100 * (currentStepIndex + 1) / (availableSteps.size());
	}
	
	@DependsOn("currentStep")
	public boolean isBackVisible() {
		return !isFirstStep() && !isLastStep();
	}

	@DependsOn("currentStep")
	public boolean isNextVisible() {
		return true;
	}
	
	@DependsOn("currentStep")
	public String getBackLabel() {
		return getBackLabelFor(getCurrentStep());
	}

	@DependsOn("currentStep")
	public String getNextLabel() {
		return getNextLabelFor(getCurrentStep());
	}
}
