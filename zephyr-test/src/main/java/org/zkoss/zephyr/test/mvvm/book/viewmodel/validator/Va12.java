package org.zkoss.zephyr.test.mvvm.book.viewmodel.validator;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.lang.Strings;

public class Va12 {
	class Bean {
		private String name;
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	private Bean bean = null;
	private Integer quantityA = 1;
	private Integer quantityB = 1;
	private Integer subtotalA = 10;
	private Integer subtotalB = 20;
	private long total = 0;
	private String selectedItem = "";

	public Integer getQuantityA() {
		return quantityA;
	}

	@NotifyChange
	public void setQuantityA(Integer quantityA) {
		this.quantityA = quantityA;
	}

	@DependsOn("quantityA")
	public Integer getSubtotalA() {
		subtotalA = 10 * quantityA;
		return subtotalA;
	}

	//	@DependsOn("quantityA")
	public void setSubtotalA(Integer subtotalA) {
		this.subtotalA = subtotalA;
	}

	public Integer getQuantityB() {
		return quantityB;
	}

	@NotifyChange
	public void setQuantityB(Integer quantityB) {
		this.quantityB = quantityB;
	}

	@DependsOn("quantityB")
	public Integer getSubtotalB() {
		subtotalB = 20 * quantityB;
		return subtotalB;
	}

	public void setSubtotalB(Integer subtotalB) {
		this.subtotalB = subtotalB;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public String getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(String selectedItem) {
		this.selectedItem = selectedItem;
	}

	// ------ validator ------------

	public class UpperBoundValidator implements Validator {
		public void validate(ValidationContext ctx) {
			Number upperBound = (Number) ctx.getBindContext().getValidatorArg("max");
			if (ctx.getProperty().getValue() instanceof Number) {
				Number value = (Number) ctx.getProperty().getValue();
				if (value.longValue() > upperBound.longValue()) {
					ctx.setInvalid();
				}
			} else {
				ctx.setInvalid();
			}
		}
	}

	public Validator getUpperBoundValidator() {
		return new UpperBoundValidator();
	}

	// -----------command -----------------
	@Command
	@NotifyChange("total")
	public void compute(@BindingParam("off") String offString) {
		total = (subtotalA + subtotalB);
		if (!Strings.isEmpty(offString)) {
			Long off = Long.parseLong(offString);
			total = total * off.longValue() / 100;
		}
	}
}
