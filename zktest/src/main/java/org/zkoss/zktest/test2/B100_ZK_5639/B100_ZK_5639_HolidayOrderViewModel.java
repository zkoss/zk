/* B100_ZK_5639_HolidayOrderViewModel.java

	Purpose:
		
	Description:
		
	History:
		11:45 AM 2024/2/17, Created by jumperchen

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2.B100_ZK_5639;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author jumperchen
 */
public class B100_ZK_5639_HolidayOrderViewModel {
	private StepBarModel stepBarModel;
	private boolean carAdded = false;
	@Init
	public void init() {
		stepBarModel = new StepBarModel();
		addStep("Destination", "z-icon-globe", "destination.zul");
		addStep("Accommodation", "z-icon-hotel", "accommodation.zul");
		addStep("Personal Details", "z-icon-user", "personal-details.zul");
		addStep("Payment", "z-icon-credit-card", "payment.zul");
		addStep("Enjoy Holiday", "z-icon-smile-o", "finish.zul");
		stepBarModel.getItems().addToSelection(stepBarModel.getItems().get(0));
	}

	@Command
	public void gotoStep(@BindingParam("step") StepBarModel.Step step) {
		stepBarModel.navigateTo(step);
	}

	@Command
	public void next() {
		stepBarModel.next();
	}

	@Command
	public void back() {
		stepBarModel.back();
	}

	@Command
	@NotifyChange("carAdded")
	public void addCar() {
		addStep(2, "Rent Car", "z-icon-car", "rent-car.zul");
		this.carAdded = true;
	}

	@Command
	@NotifyChange("carAdded")
	public void removeCar() {
		StepBarModel.Step carStep = stepBarModel.getCurrent();
		stepBarModel.next();
		stepBarModel.getItems().remove(carStep);
		this.carAdded = false;
	}

	public StepBarModel getStepBarModel() {
		return stepBarModel;
	}

	public boolean isCarAdded() {
		return carAdded;
	}

	public void addStep(String label, String icon, String uri) {
		addStep(stepBarModel.getItems().size(), label, icon, uri);
	}

	public void addStep(int index, String label, String icon, String uri) {
		String stepsFolder = "B100-ZK-5639/steps";
		stepBarModel.getItems().add(index, stepBarModel.new Step(label, icon, stepsFolder + "/" + uri));
	}
}

