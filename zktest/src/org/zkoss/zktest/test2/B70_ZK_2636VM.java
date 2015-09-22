/**
 * 
 */
package org.zkoss.zktest.test2;

import java.util.Arrays;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;

/**
 * @author jumperchen
 *
 */
public class B70_ZK_2636VM {
		ListModelList<String> letters = new ListModelList<String>();
		Bean bean = new Bean();
		Validator validator;

		@Init
		public void init() {
			letters.addAll(Arrays.asList(new String[] { "a", "b", "c", "d", "e", "f", "g" }));
		}
		
		@Command
		public void onChangeLetter() {
			letters.add("h");
		}
		
		@Command
		public void save() {
		}
		
		public ListModelList<String> getLetters() {
			return letters;
		}
		
		public Bean getBean() {
			return bean;
		}
		
		public Validator getBeanValidator() {
			if (validator == null) {
				//validator = new CascadingFormBeanValidatorService();
				validator = new Validator() {
					public void validate(ValidationContext ctx) {
						String value = (String) ctx.getProperties("letter")[0].getValue();
						Clients.log("Validate value: " + value);
					}
				};
			}
			return validator;
		}

		public static class Bean {

			private String letter;

			public String getLetter() {
				return letter;
			}

			public void setLetter(String letter) {
				this.letter = letter;
			}

		}
	}
