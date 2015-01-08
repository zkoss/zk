package org.zkoss.zktest.bind.issue.parser;

import org.zkoss.bind.Binder;
import org.zkoss.bind.DefaultBinder;
import org.zkoss.bind.impl.BinderUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class F80_Parser_StoreSubAnnotCntComposer  extends SelectorComposer<Window> {
 
	@Wire
    Window root;
	@Wire
    Window w3;

	//add comp
	Window w;
	Label l;
	
	
    @Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
	}
    
	@Listen("onClick=#add")
	public void add() {
	   	Executions.createComponentsDirectly("<label id=\"l5\" value=\"@load(vm3.label1)\" />", null, w3, null);
	}
	
	@Listen("onClick=#add_bind")
    public void add_b() {
		Binder binder = BinderUtil.getBinder(w3);
    	binder.addPropertyLoadBindings(this.getSelf().getChildren().get(1).getChildren().get(1), "label", "vm3.label3", null, null, null, null, null);
    }
	
    @Listen("onClick=#remove_binding_one")
    public void remove_binding_one() {
    	Binder binder = BinderUtil.getBinder(w3);
		binder.removeBindings(this.getSelf().getChildren().get(1).getChildren().get(2), "onClick");
    }
    
    @Listen("onClick=#remove_binding_all")
    public void remove_binding_all() {
    	Binder binder = BinderUtil.getBinder(w3);
		binder.removeBindings(this.getSelf().getChildren().get(1).getChildren().get(3));
    }
    
	@Listen("onClick=#add_bindfirst")
    public void add_bf() {
    	w = new Window();
    	Binder binder = new DefaultBinder(); 
    	binder.init(w, new F80_Parser_StoreSubAnnotCntVM(), null);
    	l = new Label();
    	binder.addPropertyLoadBindings(l, "label", "vm.orders", null, null, null, null, null);
    	l.setParent(w);
    	this.getSelf().appendChild(w);
    }
    
	@Listen("onClick=#add_bindlast")
    public void add_bl() {
    	w = new Window();
    	Binder binder = new DefaultBinder(); 
    	binder.init(w, new F80_Parser_StoreSubAnnotCntVM(), null);
    	l = new Label();
    	l.setParent(w);
    	binder.addPropertyLoadBindings(l, "label", "vm.orders", null, null, null, null, null);
    	this.getSelf().appendChild(w);
    }
	
    @Listen("onClick=#move_append")
    public void move_a() {
    	//move w2 to root as last child
    	Component c = this.getSelf().getChildren().get(0).getLastChild();
    	this.getSelf().appendChild(c);
    }
    
    @Listen("onClick=#move_setParent")
    public void move_sp() {
    	//move w2 to w1 as last child
    	Component c = this.getSelf().getLastChild();
    	c.setParent(this.getSelf().getFirstChild());
    }
    
    @Listen("onClick=#remove")
    public void remove() {
    	root.removeChild(w3);
    }
}
