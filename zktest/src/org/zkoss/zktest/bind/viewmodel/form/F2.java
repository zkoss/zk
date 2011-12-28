package org.zkoss.zktest.bind.viewmodel.form;

import java.util.Iterator;
import java.util.Map;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.BindingParam;


public class F2{

	
	public class Order{
		private Integer quantityA = 1;
		private Integer quantityB = 1;
		private Integer subtotalA = 10;
		private Integer subtotalB = 20;
		private long total = 0;
		private int off = 100;
		
		public int getQuantity(){
			return 10;
		}
		
		public Integer getQuantityA() {
			return quantityA;
		}
		
		public void setQuantityA(Integer quantityA) {
			this.quantityA = quantityA;
		}
		
		public Integer getSubtotalA() {
			subtotalA =  10 * quantityA;
			return subtotalA;
		}
		
		public void setSubtotalA(Integer subtotalA) {
			this.subtotalA =  subtotalA;
		}
		
		public Integer getQuantityB() {
			return quantityB;
		}
		
		public void setQuantityB(Integer quantityB) {
			this.quantityB = quantityB;
		}
		
		public Integer getSubtotalB() {
			subtotalB = 20 *quantityB;
			return subtotalB;
		}
		
		public void setSubtotalB(Integer subtotalB) {
			this.subtotalB = subtotalB;
		}
		
		public long getTotal() {
			total = (getSubtotalA()+getSubtotalB()) * off /100;
			return total;
		}
		
		public void setTotal(long total) {
			this.total = total;
		}

		public int getOff() {
			return off;
		}

		public void setOff(int off) {
			this.off = off;
		}
	}
	
	private Order order = new Order();
	
	public Order getOrder() {
		return order;
	}

	class F2Validator implements Validator{
		public void validate(ValidationContext ctx) {
//			Map properties = (Map)ctx.getProperty().getValue()
			Iterator<String> keyIt = ctx.getProperties().keySet().iterator();
			while (keyIt.hasNext()){
				String propertyName = keyIt.next();
				if (propertyName.length()>1){
					Integer quantity = (Integer)ctx.getProperties(propertyName)[0].getValue();
					if (quantity > 10){
						ctx.setInvalid();
						break;
					}
				}
			}
		}
	}
	
	// ------ validator ------------
	public Validator getF2Validator(){
		return new F2Validator();
	}
	
	// -----------command -----------------
	@Command @NotifyChange("order")
	public void compute(@BindingParam("off") Object offObject){
		if (offObject !=null){
			Integer off = Integer.parseInt(offObject.toString());
			order.setOff(off);
		}
	}
}
