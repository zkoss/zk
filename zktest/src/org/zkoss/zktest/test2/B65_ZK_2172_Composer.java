package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Biglistbox;
import org.zkoss.zkmax.zul.MatrixComparatorProvider;
import org.zkoss.zkmax.zul.MatrixRenderer;
import org.zkoss.zktest.test2.big.FakerMatrixModel;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * A demo of Big listbox to handle 1 trillion data.
 * @author jumperchen
 */
public class B65_ZK_2172_Composer extends SelectorComposer<Window> {

	@Wire
	private Biglistbox myComp;

	@Wire
	private Div tip;

	@Wire
	private Textbox content;
	
	// images marks
	private String[] images = { "aim", "amazon", "android", "apple", "bebo",
			"bing", "blogger", "delicious", "digg", "facebook", "flickr",
			"friendfeed", "google", "linkedin", "netvibes", "newsvine",
			"reggit", "rss", "sharethis", "stumbleupon", "technorati",
			"twitter", "utorrent", "vimeo", "vkontakte", "wikipedia", "windows",
			"yahoo" };

	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		// specify a trillion faker model
		myComp.setModel(new FakerMatrixModel(4 * 5, 4 * 5));
		myComp.setColWidth("130px");
		myComp.setMatrixRenderer(new MatrixRenderer<List<String>>() {

			public String renderCell(Component owner, List<String> data,
					int rowIndex, int colIndex) throws Exception {
				String d = data.get(colIndex);
				d = d.replace("ZK", "<span class='red' title='ZK'>ZK</span>")
						.replace("Hello", "<span class='blue' title='Hello'>Hello</span>");
				return "<div class='images_" + (colIndex%28) + "' title='x=" + 
				colIndex + ",y=" + rowIndex + "'>" + d + "</div>";
			}

			public String renderHeader(Component owner, List<String> data,
					int rowIndex, int colIndex) throws Exception {
				return "<div class='images_" + (colIndex % 28) + "' title='"
						+ images[colIndex % 28] + "'>" + data.get(colIndex)
						+ "</div>";
			}
		});
		myComp.setSortAscending(new MyMatrixComparatorProvider<List<String>>(true));
		myComp.setSortDescending(new MyMatrixComparatorProvider<List<String>>(false));
	}

	@Listen("onClick=#myComp; onSort=#myComp")
	public void onClick() {
		tip.setVisible(true); // reset first, if the tip is shown at client only.
		tip.setVisible(false);
	}

	@SuppressWarnings("unchecked")
	@Listen("onCellClick=#myComp")
	public void onCellClick(MouseEvent evt) {
		Integer[] axis = (Integer[]) evt.getData();
		
		// shift some pixels to make it look better
		tip.setStyle("left: " + (evt.getPageX() - 30) + "px; top:"
				+ (evt.getPageY() + 20) + "px");
		tip.setVisible(true);
		FakerMatrixModel fmm = (FakerMatrixModel) myComp.getModel();
		content.setValue(String.valueOf(fmm.getCellAt(
				fmm.getElementAt(axis[1]), axis[0])));
		content.setAttribute("axis", axis); // store the change for update
		Clients.evalJavaScript("doPosition()"); // resync the tooltip position
	}

	@Listen("onClick=#update; onOK=#content")
	public void onClick$update() {
		Integer[] axis = (Integer[]) content.getAttribute("axis");
		FakerMatrixModel fmm = (FakerMatrixModel) myComp.getModel();
		fmm.update(axis, content.getValue());
		tip.setVisible(false);
		myComp.focus(); // pass focus to the big listbox
	}

	@Listen("onSelect=#myComp")
	public void onSelect(SelectEvent evt) {
		System.out.println("You listen onSelect: "
				+ Arrays.asList(((Integer[]) evt.getData())));
	}
	
	// Matrix comparator provider 
	private class MyMatrixComparatorProvider<T> implements
			MatrixComparatorProvider<List<String>> {
		private int _x = -1;

		private boolean _acs;

		private MyComparator _cmpr;

		public MyMatrixComparatorProvider(boolean asc) {
			_acs = asc;
			_cmpr = new MyComparator(this);
		}

		public Comparator<List<String>> getColumnComparator(int columnIndex) {
			this._x = columnIndex;
			return _cmpr;

		}

		// a real String comparator
		private class MyComparator implements Comparator<List<String>> {
			private MyMatrixComparatorProvider _mmc;

			public MyComparator(MyMatrixComparatorProvider mmc) {
				_mmc = mmc;
			}

			public int compare(List<String> o1, List<String> o2) {
				return o1.get(_mmc._x).compareTo(o2.get(_mmc._x))
						* (_acs ? 1 : -1);
			}
		}
	}
}
