/**
 * 
 */
package org.zkoss.zktest.bind.issue;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.zkoss.bind.Binder;
import org.zkoss.bind.DefaultBinder;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.lang.Strings;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * @author Ian YT Tsai (Zanyking)
 *
 */
public class B01196BinderUiLcConfusion implements Composer<Window> {
	
	private static HashMap<String, Object> formatedNumberArg = new HashMap<String, Object>();
	private static HashMap<String, Object> formatedDateArg = new HashMap<String, Object>();
	private static final String SYS_DATE_CONVERTER = "'formatedDate'"; 
	private static final String SYS_NUMBER_CONVERTER = "'formatedNumber'";
	
	static {
		formatedNumberArg.put("format", "###,##0.00");
		formatedDateArg.put("format","yyyy/MM/dd");
	}
	/**
	 * 
	 * @author Ian YT Tsai (Zanyking)
	 *
	 */
	public static class OrderVM4{
		ListModelList<Order> orders;

		public ListModelList<Order> getOrders() {
			if (orders == null) {
				//init the list
				orders = new ListModelList<Order>(OrderService.getInstance().list());
			}
			return orders;
		}
	}
	
	public void doAfterCompose(Window window) throws Exception {
		//initialize binder, use DefaultBinder
		Binder binder = new DefaultBinder(); 
		  
		
		binder.init(window, new OrderVM4(), null);
		window.setAttribute("vm", binder.getViewModel());

		window.appendChild(buildOrderListbox(binder));

		binder.loadComponent(window,true); 
	}
	

	private static class ListboxTemplate implements Template{
		Binder binder;
		
		public ListboxTemplate(Binder binder) {
			this.binder = binder;
		}
		@SuppressWarnings("rawtypes")
		public Component[] create(Component parent, Component insertBefore,
				VariableResolver resolver, Composer composer){
			
			//create template components & add binding expressions
			Listitem listitem = new Listitem();
			Listcell idCell = new Listcell();
			listitem.appendChild(idCell);
			binder.addPropertyLoadBindings(idCell, "label", "item.id", null, null, null, null, null);
			Listcell quantityCell = new Listcell();
			listitem.appendChild(quantityCell);
			binder.addPropertyLoadBindings(quantityCell, "label", "item.quantity", null, null, null, null, null);
			Listcell priceCell = new Listcell();
			listitem.appendChild(priceCell);
			binder.addPropertyLoadBindings(priceCell, "label", "item.price", null, null, null, SYS_NUMBER_CONVERTER, formatedNumberArg);
			Listcell creationDateCell = new Listcell();
			listitem.appendChild(creationDateCell);
			binder.addPropertyLoadBindings(creationDateCell, "label", "item.creationDate", null, null, null, SYS_DATE_CONVERTER, formatedDateArg);
			Listcell shippingDateCell = new Listcell();
			listitem.appendChild(shippingDateCell);
			binder.addPropertyLoadBindings(shippingDateCell, "label", "item.shippingDate", null, null, null, SYS_DATE_CONVERTER, formatedDateArg);

			//append to the parent
			if (insertBefore ==null){
				parent.appendChild(listitem);
			}else{
				parent.insertBefore(listitem, insertBefore);
			}
			
			Component[] components = new Component[1];
			components [0] = listitem;
			binder.loadComponent(listitem,true); 
			return components;
		}
		public Map<String, Object> getParameters(){
			Map<String,Object> parameters = new HashMap<String, Object>();
			//set binding variable
			parameters.put("var","item");
			
			return parameters;
		}
		
	}

	private Component buildOrderListbox(Binder binder){
		Listbox listbox = new Listbox();
		listbox.setHeight("200px");
		listbox.setHflex("true");
		Listhead head = new Listhead();
		listbox.appendChild(head);
		head.appendChild(new Listheader("Id"));
		head.appendChild(new Listheader("Quantity"));
		head.appendChild(new Listheader("Price"));
		head.appendChild(new Listheader("Creation Date"));
		head.appendChild(new Listheader("Shipping Date"));

		binder.addPropertyLoadBindings(listbox, "model", "vm.orders", null, null, null, null, null);

		listbox.setTemplate("model", new ListboxTemplate(binder));		
		return listbox;
	}
	
	
	

	/**
	 * 
	 * @author Ian YT Tsai (Zanyking)
	 *
	 */
	public static class Order {

		String id;
		String description;
		Date creationDate;
		Date shippingDate;
		double price;
		int quantity;

		Order(String id, String description, double price, int quantity, Date creationDate) {
			super();
			this.id = id;
			this.description = description;
			this.price = price;
			this.quantity = quantity;
			this.creationDate = creationDate;
			// default shipping date is 3 day after.
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, 3);
			shippingDate = cal.getTime();
		}

		public String getId() {
			return id;
		}

		@NotifyChange
		public void setId(String id) {
			this.id = id;
		}

		public Date getCreationDate() {
			return creationDate;
		}
		@NotifyChange
		public void setCreationDate(Date creationDate) {
			this.creationDate = creationDate;
		}

		public Date getShippingDate() {
			return shippingDate;
		}
		@NotifyChange
		public void setShippingDate(Date shippingDate) {
			this.shippingDate = shippingDate;
		}

		public String getDescription() {
			return description;
		}

		@NotifyChange
		public void setDescription(String description) {
			this.description = description;
		}

		public double getPrice() {
			return price;
		}

		@NotifyChange
		public void setPrice(double price) {
			this.price = price;
		}

		public int getQuantity() {
			return quantity;
		}

		@NotifyChange
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		@DependsOn( { "price", "quantity" })
		public double getTotalPrice() {
			return price * quantity;
		}

		public String toString(){
			return "test";
		}
	}
	/**
	 * 
	 * @author Ian YT Tsai (Zanyking)
	 *
	 */
	static class OrderService {

		List<Order> allItems = new ArrayList<Order>();

		Random r = new Random(System.currentTimeMillis());
		
		private OrderService() {
			Date now = new Date();
			allItems.add(new Order(nextOid(), "part AF2 order", nextPrice(), nextQuantity(), now));
			allItems.add(new Order(nextOid(), "part BB2 order", nextPrice(), nextQuantity(), now));
			allItems.add(new Order(nextOid(), "part CX1 order", nextPrice(), nextQuantity(), now));
			allItems.add(new Order(nextOid(), "part DS34 order", nextPrice(), nextQuantity(), now));
			allItems.add(new Order(nextOid(), "part ZK99 order", nextPrice(), nextQuantity(), now));
		}
		
		static OrderService instance;
		
		static synchronized public OrderService getInstance(){
			if(instance==null){
				instance = new OrderService();
			}
			return instance;
		}
		
		public List<Order> list() {
			return new ArrayList<Order>(allItems);
		}
		
		long oid = 0;
		
		String nextOid(){
			return new DecimalFormat("00000").format(++oid);
		}

		double nextPrice() {
			return r.nextDouble()*300;
		}
		
		int nextQuantity() {
			return r.nextInt(9)+1;
		}

		public void delete(Order order) {
			allItems.remove(order);
		}

		public void save(Order order) {
			if(Strings.isBlank(order.getId())){
				order.setId(nextOid());
				allItems.add(order);
			}
		}

	}
}




