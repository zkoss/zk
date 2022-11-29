package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

public class B65_ZK_2428OuterVM {

	public B65_ZK_2428InnerVM innerVM = new B65_ZK_2428InnerVM() {

		@Init(superclass = true)
		public void init2() {
			System.out.println("init2");
		}

		@Override
		public void change() {
			System.out.println("override change");
		}

		public void myCommand() {
			Clients.log("myCommand called!");
		}
	};

	public B65_ZK_2428InnerVM getInnerVm() {
		return innerVM;
	}
}
