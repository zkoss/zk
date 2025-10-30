package org.zkoss.zktest.test2;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.CustomConstraint;
import org.zkoss.zul.SimpleConstraint;


public class ZK837BasicConstraint
   extends SimpleConstraint
   implements CustomConstraint
{

   /**
    * Constructor
    * 
    * @param constraints comma-separated list of constraints
    */
   public ZK837BasicConstraint(String constraints)
   {
      super(constraints);
   }


   /**
    * @see org.zkoss.zul.CustomConstraint#showCustomError(org.zkoss.zk.ui.Component, org.zkoss.zk.ui.WrongValueException)
    */
   public void showCustomError(Component comp, WrongValueException ex)
   {
	System.out.println(ex.getLocalizedMessage());
   }

}
