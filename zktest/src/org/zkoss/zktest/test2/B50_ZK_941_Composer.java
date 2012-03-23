package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.ArithmeticWrongValueException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.CustomConstraint;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;

public class B50_ZK_941_Composer extends GenericForwardComposer {
	private Decimalbox dec ;
	private Label label;
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		dec.setConstraint(new MyConstraint());
	}
	
	private class MyConstraint implements CustomConstraint,Constraint{
		public void showCustomError(Component comp, WrongValueException ex) {
			Label label = ((Label)(comp.getFellow("label")));
			if(ex instanceof ArithmeticWrongValueException){
				ArithmeticWrongValueException aex = (ArithmeticWrongValueException)ex;
				label.setValue("The value your input( "+ aex.getValue() +") is not avaiable for the input, " +
						" please enter numbers with format #.## .");
			}else{
				label.setValue("");
			}
			
		}

		public void validate(Component comp, Object value)
				throws WrongValueException { //We didn't do any job for validate.
		}
	}

}
