package org.zkoss.zktest.zats.zuti.vm;

import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;

public class OrderViewModel {
	private static final String SHIPPING = "shipping";
	private static final String PAYMENT = "payment";
	private static final String CONFIRMATION = "confirmation";
	private static final String FEEDBACK = "feedback";

	private Order order;
	private WizardViewModel2 wizardModel;
	
	@Init
	public void init() {
		List<WizardStep> availableSteps = Arrays.asList(
				new WizardStep(SHIPPING, "Shipping Details", new Class<?>[] {Order.Shipping.class}),
				new WizardStep(PAYMENT, "Payment Details", new Class<?>[] {Order.Payment.class}),
				new WizardStep(CONFIRMATION, "Order Confirmation", null),
				new WizardStep(FEEDBACK, "Order Processed", null)
				);
		wizardModel = new WizardViewModel2(availableSteps) {
			@Override
			protected String getNextLabelFor(WizardStep wizardStep) {
				if (wizardStep.getId().equals(CONFIRMATION)) {
					return "Send Order Now";
				} else if (wizardStep.getId().equals(FEEDBACK)) {
					return "New Order";
				}
				return super.getNextLabelFor(wizardStep);
			}
			
			@Override
			protected void onFinish(WizardStep currentStep) {
				Executions.sendRedirect("");
			}
		};
		order = new Order();
	}

	public WizardViewModel2 getWizardModel() {
		return wizardModel;
	}

	public Order getOrder() {
		return order;
	}
}
