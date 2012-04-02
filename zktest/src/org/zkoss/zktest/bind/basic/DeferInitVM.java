package org.zkoss.zktest.bind.basic;

import org.zkoss.bind.Binder;
import org.zkoss.bind.DefaultBinder;
import org.zkoss.bind.Form;
import org.zkoss.bind.SimpleForm;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.sys.BinderCtrl;
import org.zkoss.bind.tracker.impl.TrackerImpl;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

public class DeferInitVM implements Composer<Window>{

	public void doAfterCompose(Window comp) throws Exception {
		Window w1 = new Window();
		w1.setId("w1");
		w1.setTitle("window 1");
		
		w1.setParent(comp);
		init(w1,"A","B","C");
		
		Window w2 = new Window();
		w2.setId("w2");
		w2.setTitle("window 2");
		
		init(w2,"X","Y","Z");
		w2.setParent(comp);
	}

	@SuppressWarnings("deprecation")
	private void init(Window window,String value1,String value2,String value3) {
		final Binder binder = new DefaultBinder(); 
		binder.init(window, new MyViewModel(value1,value2,value3));  
		window.setAttribute("vm", binder.getViewModel());
		
		Vbox vbox = new Vbox();

		Label label1 = new Label();
		label1.setId("l11");
		Label label2 = new Label();
		label2.setId("l12");

		Textbox textbox = new Textbox();
		textbox.setId("t11");
		Button button1 = new Button("cmd1");
		button1.setId("btn11");
		Button button2 = new Button("cmd2 person change");
		button2.setId("btn12");
		
		Button button3 = new Button("cmd3 person change");
		button3.setId("btn13");
		
		binder.addPropertyInitBinding(label1, "value", "vm.value1", null,null,null);
		binder.addPropertyLoadBindings(label1, "value","vm.value2", null,new String[]{"cmd1"},null,null,null);
		
		binder.addPropertyLoadBindings(label2, "value","vm.value2", null,null,null,null,null);
		
		binder.addPropertyLoadBindings(textbox, "value","vm.value2", null,null,null,null,null);
		binder.addPropertySaveBindings(textbox, "value","vm.value2", null,null,null,null,null,null,null);
		
		
		
		binder.addCommandBinding(button1, "onClick", "'cmd1'", null);
		binder.addCommandBinding(button2, "onClick", "'cmd2'", null);
		binder.addCommandBinding(button3, "onClick", "'cmd3'", null);
		
		label1.setParent(vbox);
		label2.setParent(vbox);

		
		textbox.setParent(vbox);
		button1.setParent(vbox);
		button2.setParent(vbox);
		button3.setParent(vbox);
		
		initForm(vbox,binder);
		
		vbox.setParent(window);
		Button btn = new Button("tracker");
		btn.setParent(vbox);
		btn.addEventListener("onClick", new EventListener<Event>() {

			public void onEvent(Event event) throws Exception {
				((TrackerImpl)((BinderCtrl)binder).getTracker()).dump();
			}
		});
		binder.loadComponent(window,true);
	}
	
	private void initForm(Vbox parent,Binder binder) {
		Vbox vbox1 = new Vbox();
		binder.addFormInitBinding(vbox1, "fx", "vm.form", null);
		binder.addFormLoadBindings(vbox1, "fx", "vm.person",  null,null,null);
		binder.addFormLoadBindings(vbox1, "fx", "vm.person",  null,null,null);
		binder.addFormLoadBindings(vbox1, "fx", "vm.person",  null,new String[]{"cmd3"},null);
		binder.addFormSaveBindings(vbox1, "fx", "vm.person", new String[]{"cmd4"}, null, null,null,null);
		Label label1 = new Label();
		label1.setId("l21");
		Textbox textbox = new Textbox();
		textbox.setId("t21");
		Button button1 = new Button("save forom");
		button1.setId("btn21");
		label1.setParent(vbox1);
		textbox.setParent(vbox1);
		button1.setParent(vbox1);
		
		binder.addPropertyLoadBindings(label1, "value","fx.name", null,null,null,null,null);
		
		binder.addPropertyLoadBindings(textbox, "value","fx.name", null,null,null,null,null);
		binder.addPropertySaveBindings(textbox, "value","fx.name", null,null,null,null,null,null,null);
		
		binder.addCommandBinding(button1, "onClick", "'cmd4'", null);
		vbox1.setParent(parent);
		
	}

	public static class MyViewModel{
		String value1;
		String value2;
		Person person;
		Form form;
		
		public MyViewModel(String value1,String value2,String value3){
			this.value1 = value1;
			this.value2 = value2;
			this.person = new Person(value3);
			form = new SimpleForm(){
				public void setField(String field, Object value) {
					super.setField(field, value+":byForm");
				}
			};
		}

		public String getValue1() {
			return value1;
		}

		public void setValue1(String value1) {
			this.value1 = value1;
		}

		public String getValue2() {
			return value2;
		}

		public void setValue2(String value2) {
			this.value2 = value2;
		}

		public Form getForm() {
			return form;
		}

		public void setForm(Form form) {
			this.form = form;
		}

		public Person getPerson() {
			return person;
		}

		@Command @NotifyChange({"value1","value2"})
		public void cmd1(){
			value1 = value1+":cmd1";
			value2 = value2+":cmd1";
		}
		
		@Command @NotifyChange({"person"})
		public void cmd2(){
			person.setName(person.name+":cmd2");
		}
		
		@Command 
		public void cmd3(){
			person.setName(person.name+":cmd3");
		}
		
		@Command @NotifyChange({"value1","value2"})
		public void cmd4(){
			value1 = person.name+":cmd4";
			value2 = person.name+":cmd4";
		}
	}
	
	public static class Person {
		String name;

		
		
		public Person(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
}
