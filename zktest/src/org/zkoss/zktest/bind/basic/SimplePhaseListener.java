package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Phase;
import org.zkoss.bind.PhaseListener;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

/**
 * a 
 * @author dennis
 *
 */
public class SimplePhaseListener implements PhaseListener{

	@Override
	public void prePhase(Phase phase, BindContext ctx) {
		if(phase == Phase.COMMAND){
			Execution ex = Executions.getCurrent();
			String page = ex.getDesktop().getRequestPath();
			Object viewModel = ctx.getBinder().getViewModel();
			String command = ctx.getCommandName();
			
			System.out.println("prePhase "+phase+","+viewModel.getClass().getSimpleName()+":"+command+","+page);
		}
	}

	@Override
	public void postPhase(Phase phase, BindContext ctx) {
	}

}
