package org.zkoss.zktest.zats.zuti.vm;

import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

public class WizardViewModel {
	private static final String BACK = "back";
	private static final String NEXT = "next";
	private static final String CONFIRM = "confirm";
	
	private Order order;
	private WizardModel wizardModel;
	
	@Init
	public void init() {
		List<WizardStep> availableSteps = Arrays.asList(
				new WizardStep("shipping", "Shipping Details", new Class<?>[] {Order.Shipping.class}),
				new WizardStep("payment", "Payment Details", new Class<?>[] {Order.Payment.class}),
				new WizardStep("confirmation", "Order Confirmation", null)
				);
		wizardModel = new WizardModel(availableSteps, BACK, NEXT, CONFIRM);
		wizardModel.setBackLabel("Back");
		wizardModel.setNextLabel("Next");
		wizardModel.setSubmitLabel("Confirm");
		order = new Order();
	}

	@Command(BACK) 
	public void back() {
		wizardModel.step(-1);
	}

	@Command(NEXT) 
	public void next() {
		wizardModel.step(1);
	}
	
	@Command(CONFIRM) 
	public void confirm() {
		Clients.showNotification("Order sent successfully!");
	}

	public WizardModel getWizardModel() {
		return wizardModel;
	}

	public Order getOrder() {
		return order;
	}
}
