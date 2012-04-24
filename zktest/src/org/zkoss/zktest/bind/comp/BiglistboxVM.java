package org.zkoss.zktest.bind.comp;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zkmax.zul.MatrixModel;
import org.zkoss.zul.AbstractListModel;

public class BiglistboxVM {

	static int rowCount = 200;
	static int colCount = 200;
	
	
	MyMatrixModel model = new MyMatrixModel();
	Item selected = null;
	int selectedIndex;
	
	public BiglistboxVM(){
		for(int i=0;i<rowCount;i++){
			model.add(new Item("Row "+i,colCount));
		}
		selected = model.getElementAt(0);
		selectedIndex = 0;
//		System.out.println("Total Item "+model.getSize());
	}
	
	public Item getSelected() {
		return selected;
	}

	public void setSelected(Item selected) {
		this.selected = selected;
	}

	@Command @NotifyChange({"selected","selectedIndex"})
	public void clear(){
		selected=null;
		selectedIndex = -1;
	}



	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public MyMatrixModel getModel(){
		System.out.println("getModel:"+model.getSize());
		return model;
	}
	
	class MyMatrixModel extends AbstractListModel<Item> implements MatrixModel<Item, Object, Field, Object>{

		List<Item> rows = new ArrayList<Item>();
		
		public void add(Item item){
			rows.add(item);
		}

		@Override
		public Item getElementAt(int index) {
			return rows.get(index);
		}

		@Override
		public int getSize() {
			return rows.size();
		}

		@Override
		public int getColumnSize() {
			// TODO Auto-generated method stub
			return colCount;
		}

		@Override
		public int getHeadSize() {
			return 1;
		}

		@Override
		public Object getHeadAt(int rowIndex) {
//			System.out.println("getHeadAt:"+rowIndex);
			return "Head"+rowIndex;
		}

		@Override
		public Field getCellAt(Item rowData, int columnIndex) {
//			System.out.println("getCellAt:"+rowData+","+columnIndex);
			return rowData.getFields()[columnIndex];
		}

		@Override
		public Object getHeaderAt(Object headData, int columnIndex) {
//			System.out.println("getHeaderAt:"+headData+","+columnIndex);
			return headData+":Header"+columnIndex;
		}
	}
	
	
	public static class Item{
		String name;
		Field[] fields;
		
		public Item(String name, int nfield){
			this.name = name;
			fields = new Field[nfield];
			for(int i=0;i<nfield;i++){
				fields[i] = new Field("Field "+i);
			}
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Field[] getFields() {
			return fields;
		}

		public String toString(){
			return name;
		}
	}
	
	public static class Field {
		String value;
		public Field(String value){
			this.value = value;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		
	}
}
