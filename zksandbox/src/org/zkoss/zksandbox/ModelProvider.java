/* ModelProvider.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 14 20:16:19 TST 2011, Created by jimmy

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zksandbox;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.zkoss.lang.reflect.Fields;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.ArrayComparator;
import org.zkoss.zul.Column;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Group;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
/**
 * Used for create test data.
 * @author jimmy
 *
 */
public class ModelProvider {
	private static DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
	
	public static List getList(int count) {
		return getList(count, "item");
	}
	
	public static List getList(int count, int range) {
		return getList(count, "item", range);
	}
	
	public static List getList(int count, String label) {
		return getList(count, label, count);
	}
	
	public static List getList(int count, String label, int range) {
		List list = new ArrayList(count);
		Random rand = new Random(new Random().nextLong());
		for (int i = 0; i < count; i++) {
			list.add(label + " " + rand.nextInt(range));
		}
		return list;
	}
	
	public static String[][] getMultiColumnArray(int count, int column) {
		String[][] data = new String[count][column];
		Random rand = new Random(new Random().nextLong());
		for (int i = 0; i < count; i++) {
			data[i] = new String[column];
			for (int j = 0; j < count; j++) {
				data[i][j] = "item " + rand.nextInt(count) + " - " + rand.nextInt(column);
			}
		}
		return data;
	}
	
	public static Random getRandom() {
		return new Random(new Random().nextLong());
	}
	
	public static class ListModelFactory {
		public static ListModelList getBeanListModelList(int count) {
			return getBeanListModelList(count, "item");
		}
		public static ListModelList getBeanListModelList(int count, String label) {
			List list = new ArrayList(count);
			Random rand = new Random(new Random().nextLong());
			
			Calendar cal = Calendar.getInstance();
			for (int i = 0; i < count; i++) {
				list.add(new SampleBean(label + " " + rand.nextInt(count),
						rand.nextInt(count), cal.getTime()));
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
			return new ListModelList(list);
		}
		public static ListModelList getArrayListModelList(int count) {
			return getArrayListModelList(count, "item");
		}
		public static ListModelList getArrayListModelList(int count, String label) {
			List list = new ArrayList(count);
			Random rand = new Random(new Random().nextLong());
			
			Calendar cal = Calendar.getInstance();
			
			for (int i = 0; i < count; i++) {
				list.add(new String[]{label + " " + rand.nextInt(count),
						rand.nextInt(count)+"", df.format(cal.getTime())});
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
			return new ListModelList(list);
		}
		public static void addListModelListBean(ListModelList model,String label) {
			model.add(new SampleBean(label, getRandom().nextInt(10), new Date()));
		}
		public static void addListModelListAry(ListModelList model,String label) {
			model.add(new String[]{label,
					getRandom().nextInt(10)+"", df.format(new Date())});
		}
		public static void updateListModelListBean(ListModelList model, int index, String label) {
			model.set(index, new SampleBean(label, getRandom().nextInt(10), new Date()));
		}
		public static void updateListModelListAry(ListModelList model, int index,String label) {
			model.set(index, new String[]{label,
					getRandom().nextInt(10)+"", df.format(new Date())});
		}
	}
	
	public static class GroupModelFactory {
		public static GroupsModelArray getBeanGroupsModelArray(int count) {
			return getBeanGroupsModelArray(count, "item");
		}
		public static GroupsModelArray getBeanGroupsModelArray(int count, String label) {
			List list = new ArrayList(count);
			Random rand = new Random(new Random().nextLong());
			
			Calendar cal = Calendar.getInstance();
			for (int i = 0; i < count; i++) {
				list.add(new SampleBean(label + " " + rand.nextInt(count),
						rand.nextInt(count), cal.getTime()));
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
			
			return new GroupsModelArray(list, new FieldComparator("name", true)) {
				protected Object createGroupHead(
						Object[] groupdata, int index, int col) {
					return new Object[] { groupdata[0], new Integer(col) };
				}
			};
		}
		public static GroupsModelArray getArrayGroupsModelArray(int count) {
			return getArrayGroupsModelArray(count, "item");
		}
		public static GroupsModelArray getArrayGroupsModelArray(int count, String label) {
			List list = new ArrayList(count);
			Random rand = new Random(new Random().nextLong());
			
			Calendar cal = Calendar.getInstance();
			
			for (int i = 0; i < count; i++) {
				list.add(new String[]{label + " " + rand.nextInt(count),
						rand.nextInt(count)+"", df.format(cal.getTime())});
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
			return new GroupsModelArray(list, new ArrayComparator(0, true));
		}
	}
	
	public static class TreeModelFactory {
		public static DefaultTreeModel getSingleColDefaultTreeModel(int level1, int level2, int level3) {
			return getSingleColDefaultTreeModel(level1 ,level2, level3,"item");
		}
		public static DefaultTreeModel getSingleColDefaultTreeModel(int level1, int level2, int level3, String label) {
			List list = getList(10, label);
			List list2 = new ArrayList();
			for (int i =0 ,j = list.size(); i < j; i++)
				list2.add(new DefaultTreeNode(list.get(i)));
			return new DefaultTreeModel(new DefaultTreeNode(null, list2));
		}
		public static DefaultTreeModel getBeanDefaultTreeModel(int level1, int level2, int level3) {
			return getBeanDefaultTreeModel(level1 ,level2, level3,"item");
		}
		public static DefaultTreeModel getBeanDefaultTreeModel(int level1, int level2, int level3, String label) {
			Random rand = new Random(new Random().nextLong());
			Calendar cal = Calendar.getInstance();
			List list = new ArrayList(level1);
			for (int i = 0; i < level1; i++) {
				List subList = new ArrayList(level2);
				for (int j = 0; j < level2; j++) {
					List subSubList = new ArrayList(level3);
					for (int k = 0; k < level3; k++) {
						subSubList.add(new DefaultTreeNode(new SampleBean(label + " " + rand.nextInt(level3),
								rand.nextInt(level3), cal.getTime())));
						cal.add(Calendar.MINUTE, 1);
					}
					subList.add(new DefaultTreeNode(new SampleBean(label + " " + rand.nextInt(level2),
							rand.nextInt(level2), cal.getTime()), subSubList));
					cal.add(Calendar.HOUR_OF_DAY, 1);
				}
				
				list.add(new DefaultTreeNode(new SampleBean(label + " " + rand.nextInt(level1),
						rand.nextInt(level1), cal.getTime()), subList));
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
			return new DefaultTreeModel(new DefaultTreeNode(null, list));
		}
		public static DefaultTreeModel getArrayDefaultTreeModel(int level1, int level2, int level3) {
			return getArrayDefaultTreeModel(level1 ,level2, level3,"item");
		}
		public static DefaultTreeModel getArrayDefaultTreeModel(int level1, int level2, int level3, String label) {
			Random rand = new Random(new Random().nextLong());
			Calendar cal = Calendar.getInstance();
			List list = new ArrayList(level1);
			for (int i = 0; i < level1; i++) {
				List subList = new ArrayList(level2);
				for (int j = 0; j < level2; j++) {
					List subSubList = new ArrayList(level3);
					for (int k = 0; k < level3; k++) {
						subSubList.add(new DefaultTreeNode(new String[]{label + " " + rand.nextInt(level3),
								rand.nextInt(level3)+"", df.format(cal.getTime())}));
						cal.add(Calendar.MINUTE, 1);
					}
					subList.add(new DefaultTreeNode(new String[]{label + " " + rand.nextInt(level2),
							rand.nextInt(level2)+"", df.format(cal.getTime())}, subSubList));
					cal.add(Calendar.HOUR_OF_DAY, 1);
				}
				
				list.add(new DefaultTreeNode(new String[]{label + " " + rand.nextInt(level1),
						rand.nextInt(level1)+"", df.format(cal.getTime())}, subList));
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
			return new DefaultTreeModel(new DefaultTreeNode(null, list));
		}
		public static void addLDefaultTreeModelBean(Treeitem item, String label) {
			if (item == null || item.getValue() == null)
				return;
			DefaultTreeNode node = (DefaultTreeNode) ((DefaultTreeNode) item.getValue()).getParent();
			node.add(new DefaultTreeNode(new SampleBean(label, getRandom().nextInt(10), new Date())));
		}
		public static void addDefaultTreeModelAry(Treeitem item, String label) {
			if (item == null || item.getValue() == null)
				return;
			DefaultTreeNode node = (DefaultTreeNode) ((DefaultTreeNode) item.getValue()).getParent();
			node.add(new DefaultTreeNode(new String[]{label, getRandom().nextInt(10)+"", 
					df.format(new Date())}));
		}
		public static void updateDefaultTreeModelBean(Treeitem item, int index, String label) {
			if (item == null || item.getValue() == null)
				return;
			DefaultTreeNode node = (DefaultTreeNode) item.getValue();
			node.setData(new SampleBean(label, getRandom().nextInt(10), new Date()));
		}
		public static void updateDefaultTreeModelAry(Treeitem item, int index,String label) {
			if (item == null || item.getValue() == null)
				return;
			DefaultTreeNode node = (DefaultTreeNode) item.getValue();
			node.setData(new String[]{label, getRandom().nextInt(10)+"", df.format(new Date())});
		}
	}
	
	public static class GridRendererFactory {
		public static RowRenderer getBeanRowRenderer() {
			return new RowRenderer() {
				public void render(Row row, Object data) throws Exception {
					SampleBean b = (SampleBean) data;
					row.appendChild(new Label(b.getName()));
					row.appendChild(new Label(b.getNumber()+""));
					row.appendChild(new Label(df.format(b.getDate())));
				}
			};
		}
		public static RowRenderer getArrayRowRenderer() {
			return new RowRenderer() {
				public void render(Row row, Object data) throws Exception {
					String[] ary = (String[]) data;
					row.appendChild(new Label(ary[0]));
					row.appendChild(new Label(ary[1]));
					row.appendChild(new Label(ary[2]));
				}
			};
		}
		public static RowRenderer getGroupBeanRowRenderer() {
			return new RowRenderer() {
				public void render(Row row, Object data) {
					if (row instanceof Group) {
						Object[] obj = (Object[]) data; // prepared by
						// createGroupHead()
						row.appendChild(new Label(
								getGroupHead(row, (SampleBean) obj[0], ((Integer) obj[1]).intValue())));
					} else {
						SampleBean b = (SampleBean) data;
						row.appendChild(new Label(b.getName()));
						row.appendChild(new Label(b.getNumber()+""));
						row.appendChild(new Label(df.format(b.getDate())));
					}
				}
				
				private String getGroupHead(Row row, SampleBean bean, int index) {
					Column column = 
						(Column) row.getGrid().getColumns().getChildren().get(index);
					String field =  //print: category
						((FieldComparator) column.getSortAscending()).getRawOrderBy();
					try {
						return Fields.get(bean, field).toString();
					} catch (NoSuchMethodException ex) {
						throw UiException.Aide.wrap(ex);
					}
				}
			};
		}
		public static RowRenderer getGroupArrayRowRenderer() {
			return new RowRenderer () {
				public void render(Row row, java.lang.Object obj) {
					if (row instanceof Group) {
						row.appendChild(new Label(obj.toString()));
					} else {
						Object[] data = (Object[]) obj;
						row.appendChild(new Label(data[0].toString()));
						row.appendChild(new Label(data[1].toString()));
						row.appendChild(new Label(data[2].toString()));
					}
				}
			};
		}

	}
	
	public static class ListboxRendererFactory {
		public static ListitemRenderer getBeanItemRenderer() {
			return new ListitemRenderer() {
				public void render(Listitem item, Object data) throws Exception {
					SampleBean b = (SampleBean) data;
					item.appendChild(new Listcell(b.getName()));
					item.appendChild(new Listcell(b.getNumber()+""));
					item.appendChild(new Listcell(df.format(b.getDate())));
				}
			};
		}
		public static ListitemRenderer getArrayItemRenderer() {
			return new ListitemRenderer() {
				public void render(Listitem item, Object data) throws Exception {
					String[] ary = (String[]) data;
					item.appendChild(new Listcell(ary[0]));
					item.appendChild(new Listcell(ary[1]));
					item.appendChild(new Listcell(ary[2]));
				}
			};
		}
		public static ListitemRenderer getGroupBeanItemRenderer() {
			return new ListitemRenderer() {
				public void render(Listitem item, Object data) throws Exception {
					if (item instanceof Listgroup) {
						Object[] obj = (Object[]) data; // prepared by
						// createGroupHead()
						item.appendChild(new Listcell(
								getGroupHead(item, (SampleBean) obj[0], ((Integer) obj[1]).intValue())));
					} else {
						SampleBean b = (SampleBean) data;
						item.appendChild(new Listcell(b.getName()));
						item.appendChild(new Listcell(b.getNumber()+""));
						item.appendChild(new Listcell(df.format(b.getDate())));
					}
				}
				private String getGroupHead(Listitem item, SampleBean bean, int index) {
					Listheader hd = 
						(Listheader) item.getListbox().getListhead().getChildren().get(index);
					String field =  //print: category
						((FieldComparator) hd.getSortAscending()).getRawOrderBy();
					try {
						return Fields.get(bean, field).toString();
					} catch (NoSuchMethodException ex) {
						throw UiException.Aide.wrap(ex);
					}
				}
			};
		}
		public static ListitemRenderer getGroupArrayItemRenderer() {
			return new ListitemRenderer() {
				public void render(Listitem item, Object obj) throws Exception {
					if (item instanceof Listgroup) {
						item.appendChild(new Listcell(obj.toString()));
					} else {
						Object[] data = (Object[]) obj;
						item.appendChild(new Listcell(data[0].toString()));
						item.appendChild(new Listcell(data[1].toString()));
						item.appendChild(new Listcell(data[2].toString()));
					}
				}
			};
		}

	}
	
	public static class TreeRendererFactory {
		public static TreeitemRenderer getBeanTreeitemRenderer() {
			return new TreeitemRenderer() {
				public void render(Treeitem item, Object data) throws Exception {
					SampleBean b = (SampleBean) ((DefaultTreeNode) data).getData();
					Treerow tr;
					if (item.getTreerow() == null) {
						tr = new Treerow();
						tr.setParent(item);
					} else {
						tr = item.getTreerow();
						tr.getChildren().clear();
					}
					tr.appendChild(new Treecell(b.getName()));
					tr.appendChild(new Treecell(b.getNumber()+""));
					tr.appendChild(new Treecell(df.format(b.getDate())));
					item.setValue(data);
					item.setOpen(true);
				}
			};
		}
		public static TreeitemRenderer getArrayTreeitemRenderer() {
			return new TreeitemRenderer() {
				public void render(Treeitem item, Object data) throws Exception {
					String[] ary = (String[]) ((DefaultTreeNode) data).getData();
					Treerow tr;
					if (item.getTreerow() == null) {
						tr = new Treerow();
						tr.setParent(item);
					} else {
						tr = item.getTreerow();
						tr.getChildren().clear();
					}
					tr.appendChild(new Treecell(ary[0]));
					tr.appendChild(new Treecell(ary[1]));
					tr.appendChild(new Treecell(ary[2]));
					item.setValue(data);
					item.setOpen(true);
				}
			};
		}
		public static TreeitemRenderer getSingleColTreeitemRenderer() {
			return new TreeitemRenderer() {
				public void render(Treeitem item, Object data) throws Exception {
					Treerow tr;
					if (item.getTreerow() == null) {
						tr = new Treerow();
						tr.setParent(item);
					} else {
						tr = item.getTreerow();
						tr.getChildren().clear();
					}
					tr.appendChild(new Treecell((String) ((DefaultTreeNode) data).getData()));
					item.setValue(data);
					item.setOpen(true);
				}
			};
		}
	}
	
	public static class SampleBean {
		private String name;
		private int number;
		private Date date;

		public SampleBean(String name, int number, Date date) {
			super();
			this.name = name;
			this.number = number;
			this.date = date;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getNumber() {
			return number;
		}

		public void setNumber(int number) {
			this.number = number;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

	}
}



