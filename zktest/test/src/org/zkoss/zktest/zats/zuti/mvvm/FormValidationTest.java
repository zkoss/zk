/** ManipulateInsertion2Test.java.

	Purpose:
		
	Description:
		
	History:
		5:23:24 PM Nov 10, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.zats.zuti.mvvm;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.zkoss.zats.mimic.AgentException;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zats.mimic.impl.ClientCtrl;
import org.zkoss.zats.mimic.impl.EventDataManager;
import org.zkoss.zats.mimic.impl.OperationAgentBuilder;
import org.zkoss.zats.mimic.impl.OperationAgentManager;
import org.zkoss.zats.mimic.impl.au.AuUtility;
import org.zkoss.zats.mimic.impl.operation.AgentDelegator;
import org.zkoss.zats.mimic.impl.operation.input.AbstractInputAgentBuilder;
import org.zkoss.zats.mimic.operation.InputAgent;
import org.zkoss.zats.mimic.operation.OperationAgent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.impl.InputElement;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.mvvm.FormValidationTest.TypeInputAgentBuilder.InputAgentImpl;
import org.zkoss.zktest.zats.zuti.verifier.HierarchyVerifier;

/**
 * @author jumperchen
 */
public class FormValidationTest extends ZutiBasicTestCase {
	@Test
	public void testResult() {
		OperationAgentManager.getInstance().registerBuilder(
						"8.0.0",
						"*",
						Textbox.class,
						new TypeInputAgentBuilder());
		DesktopAgent desktop = connect();

		ComponentAgent host = desktop.query("#host");
		ComponentAgent next = host.getFirstChild().getFirstChild();
		assertEquals(1, next.getChildren().size());

		next.getFirstChild().as(MyInputAgent.class).type("aa");

		assertEquals(2, next.getChildren().size());
		checkVerifier(host.getOwner(), HierarchyVerifier.class);
	}
	public interface MyInputAgent extends OperationAgent {
		public void type(String text);
	}
	public static class TypeInputAgentBuilder implements OperationAgentBuilder<ComponentAgent,MyInputAgent> {
		public MyInputAgent getOperation(ComponentAgent agent) {
			return new InputAgentImpl(agent);
		}
		public Class<MyInputAgent> getOperationClass() {
			return MyInputAgent.class;
		}

		static class InputAgentImpl extends AgentDelegator<ComponentAgent> implements MyInputAgent {

			public InputAgentImpl(ComponentAgent target) {
				super(target);
			}

			protected void putValue(ComponentAgent target, String raw,
					Map<String, Object> data) {
				data.put("value", raw);
			}

			protected String toRawString(ComponentAgent target, Object value) {
				return value == null ? "" : value.toString();
			}
			public void type(String value) {
				try {
					ClientCtrl cctrl = (ClientCtrl) target.getClient();
					String cmd = Events.ON_CHANGE;
					InputEvent event = new InputEvent(cmd, (Component) target.getDelegatee(), value, null);
					Map<String, Object> data = EventDataManager.getInstance().build(event);
					putValue(target, value, data); // parse value and put into data collection
					String desktopId = target.getDesktop().getId();
					cctrl.postUpdate(desktopId, target.getUuid(), cmd, data, false);
					
					ComponentAgent et = AuUtility.lookupEventTarget(target,Events.ON_OK);
									
					cmd = Events.ON_OK;
					data = EventDataManager.getInstance().build(new KeyEvent(cmd, (Component)et.getDelegatee(), 13, false, false,
							false, (Component)target.getDelegatee()));
					((ClientCtrl)et.getClient()).postUpdate(desktopId, et.getUuid(), cmd, data, false);
					cctrl.flush(desktopId);
				} catch (Exception e) {
					throw new AgentException("value \"" + value
							+ "\" is invalid for the component: "
							+ target, e);
				}
			}

			public void input(Object value) {
				// TODO Auto-generated method stub
				
			}

			public void typing(String value) {
				// TODO Auto-generated method stub
				
			}

			public void select(int start, int end) {
				// TODO Auto-generated method stub
				
			}

		}

		
	}

}
