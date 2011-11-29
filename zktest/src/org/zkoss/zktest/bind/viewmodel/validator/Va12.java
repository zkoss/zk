package org.zkoss.zktest.bind.viewmodel.validator;

import java.util.Map;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;


public class Va12{

	private Integer quantityA = 1;
	private Integer quantityB = 1;
	private Integer subtotalA = 10;
	private Integer subtotalB = 20;
	private long total = 0;

	

	public Integer getQuantityA() {
		return quantityA;
	}

	@NotifyChange
	public void setQuantityA(Integer quantityA) {
		this.quantityA = quantityA;
	}


	@DependsOn("quantityA")
	public Integer getSubtotalA() {
		subtotalA =  10 * quantityA;
		return subtotalA;
	}

//	@DependsOn("quantityA")
	public void setSubtotalA(Integer subtotalA) {
		this.subtotalA =  subtotalA;
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
		subtotalB = 20 *quantityB;
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

	// ------ validator ------------

	public class UpperBoundValidator implements Validator {

		public void validate(ValidationContext ctx) {
			Number upperBound = (Number)ctx.getBindContext().getValidatorArg("max");
			if (ctx.getProperty().getValue() instanceof Number){
				Number value = (Number)ctx.getProperty().getValue();
				if (value.longValue() > upperBound.longValue()){
					ctx.setInvalid();
				}
			}else{
				ctx.setInvalid();
			}
		}
	}
	public Validator getUpperBoundValidator(){
		return new UpperBoundValidator();
	}
	


	// -----------command -----------------
	public void compute(Map<String, Object> args){
		total = (subtotalA+subtotalB);
		Object offObject = args.get("off");
		if (offObject !=null){
			Long off = Long.parseLong(args.get("off").toString());
			total = total*off.longValue()/100;
		}
	}
}
