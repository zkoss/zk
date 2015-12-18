package org.zkoss.zktest.zats.zuti.vm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

public class Order {

	private String shippingAddress;

	private String paymentDetails;

	@Size(min=4, max=20, groups={Shipping.class, Default.class})
	@NotNull(groups={Shipping.class, Default.class})
	public String getShippingAddress() {
		return shippingAddress;
	}
	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	@NotNull(groups={Payment.class, Default.class})
	@Size(min = 8, max=10, groups={Payment.class, Default.class})
	public String getPaymentDetails() {
		return paymentDetails;
	}
	public void setPaymentDetails(String paymentDetails) {
		this.paymentDetails = paymentDetails;
	}
	
	public static interface Shipping {};
	public static interface Payment {};
}
