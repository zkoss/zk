package zk.example;

import org.zkoss.util.Pair;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zkmax.zul.*;
import org.zkoss.zul.*;

import java.util.*;

public class BiglistboxComposer extends SelectorComposer<Component> {

    @Wire
    private Biglistbox biglist;
    @Wire
    private Selectbox modelBox;
    @Wire
    private Selectbox frozenColsBox;
    @Wire
    private Selectbox colsSizeBox;
    @Wire
    private Selectbox rowsSizeBox;
    private ListModelList<String> tenSize;
    private ListModelList<Integer> colsSize;
    private ListModelList<Integer> rowsSize;
    private ListModelList<Pair<String, FakerMatrixModel>> modelList;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        tenSize = new ListModelList(Arrays.asList(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9" }));
        tenSize.addToSelection(tenSize.get(0));
        frozenColsBox.setModel(tenSize);

        colsSize = new ListModelList(Arrays.asList(new Integer[]{5, 10, 15, 20}));
        colsSizeBox.setModel(colsSize);

        rowsSize = new ListModelList(Arrays.asList(new Integer[]{5, 10, 15, 20}));
        rowsSizeBox.setModel(rowsSize);

        FakerMatrixModel NonHeader = new FakerMatrixModel(10, 10, 0);
        FakerMatrixModel MultipleHeader = new FakerMatrixModel(10, 10, 3);
        FakerMatrixModel SingleColumn = new FakerMatrixModel(1, 10);
        FakerMatrixModel MultipleColumn = new FakerMatrixModel(100, 10);
        FakerMatrixModel HugeColumn = new FakerMatrixModel(10000, 10);
        FakerMatrixModel SingleRow = new FakerMatrixModel(10, 10);
        FakerMatrixModel MultipleRow = new FakerMatrixModel(10, 100);
        FakerMatrixModel HugeRow = new FakerMatrixModel(10, 10000);
        FakerMatrixModel BigData = new FakerMatrixModel(1000000, 1000000);


        MyMatrixComparatorProvider ascCmpr = new MyMatrixComparatorProvider(true);
        MyMatrixComparatorProvider dscCmpr = new MyMatrixComparatorProvider(false);
        biglist.setSortAscending(ascCmpr);
        biglist.setSortDescending(dscCmpr);

        Pair<String, FakeListModel>[] models = new Pair[] {
                new Pair("NonHeader", NonHeader),
                new Pair("MultipleHeader", MultipleHeader),
                new Pair("SingleColumn", SingleColumn),
                new Pair("MultipleColumn", MultipleColumn),
                new Pair("HugeColumn", HugeColumn),
                new Pair("SingleRow", SingleRow),
                new Pair("MultipleRow", MultipleRow),
                new Pair("HugeRow", HugeRow),
                new Pair("BigData", BigData)
        };
        modelList = new ListModelList(Arrays.asList(models));
        modelList.addToSelection(modelList.get(0));

        biglist.setModel(MultipleColumn);
        modelBox.setModel(modelList);
    }

    @Listen("onSelect = #modelBox")
    public void selectModel() {
        biglist.setFrozenCols(0);
        tenSize.addToSelection(tenSize.get(0));
        biglist.setModel(modelList.getSelection().iterator().next().y);
    }

    @Listen("onSelect = #frozenColsBox")
    public void changeFrozenCols() {
        if (frozenColsBox.getSelectedIndex() > biglist.getCols() -1) {
            frozenColsBox.setSelectedIndex(frozenColsBox.getSelectedIndex() - 1);
            alert("FrozenCols cannot be greater than Cols");
        } else
            biglist.setFrozenCols(frozenColsBox.getSelectedIndex());
    }

    @Listen("onSelect = #rowsSizeBox")
    public void changeRowsSize() {
        biglist.setAutoRows(false);
        biglist.setRows(rowsSize.getSelection().iterator().next());
    }

    @Listen("onSelect = #colsSizeBox")
    public void changeColsSize() {
        int i = colsSize.getSelection().iterator().next();
        if (biglist.getFrozenCols() > i-1) {
            colsSizeBox.setSelectedIndex(colsSizeBox.getSelectedIndex() - 1);
            alert("FrozenCols cannot be greater than Cols");
        } else {
            biglist.setAutoCols(false);
            biglist.setCols(i);
        }
    }


    class MyMatrixComparatorProvider implements MatrixComparatorProvider {
        private int _x = -1;
        private boolean _acs;
        private MyComparator _cmpr;

        public MyMatrixComparatorProvider(boolean asc) {
            _acs = asc;
            _cmpr = new MyComparator(this);
        }

        public Comparator getColumnComparator(int columnIndex) {
            this._x = columnIndex;
            return _cmpr;
        }

        public class MyComparator implements Comparator<List<String>> {
            private MyMatrixComparatorProvider _mmc;
            public MyComparator(MyMatrixComparatorProvider mmc) {
                _mmc = mmc;
            }

            @Override
            public int compare(List<String> o1, List<String> o2) {
                return o1.get(_mmc._x).compareTo(o2.get(_mmc._x))
                        * (_acs ? 1 : -1);
            }
        }
    }


}
