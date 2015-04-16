package org.zkoss.zktest.zats.zuti.vm;

public class WizardStep {

	private String template;
	private String title;
	private Class<?>[] validationGroups;

	public WizardStep(String template, String title, Class<?>[] validationGroups) {
		this.template = template;
		this.title = title;
		this.validationGroups = validationGroups;
	}

	public String getTemplate() {
		return template;
	}

	public String getTitle() {
		return title;
	}

	public Class<?>[] getValidationGroups() {
		return validationGroups;
	}
}
