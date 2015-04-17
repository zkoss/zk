package org.zkoss.zktest.zats.zuti.vm;

public class WizardStep {

	private String template;
	private String title;
	private Class<?>[] validationGroups;
	private String id;

	public WizardStep(String template, String title, Class<?>[] validationGroups) {
		this.template = template;
		this.id = template;
		this.title = title;
		this.validationGroups = validationGroups;
	}

	public String getId() {
		return id;
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
