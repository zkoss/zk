/* B103_ZK_5684VM.java

        Purpose:
                
        Description:
                
        History:
                Thu Dec 11 10:13:56 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

public class B103_ZK_5684VM {
    public class GridRow {

        private Integer number;
        private String name;

        public GridRow(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private List<String> details = Arrays.asList("One", "Two", "Three", "Four");

        public List<String> getDetails() {
            return details;
        }

        public void setDetails(List<String> details) {
            this.details = details;
        }

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }


    }

    private ListModelList<GridRow> gridRows = new ListModelList<>();

    private List<String> details = Arrays.asList("One", "Two", "Three", "Four");

    @Init
    public void init()
    {
        for (int i = 1; i < 100; i++) {
            GridRow gridRow = new GridRow("Entry #" + i);
            gridRow.setNumber(i);
            gridRows.add(gridRow);
        }

    }

    @Command("update-row-2")
    public void onForceUpdate() {
        GridRow row = new GridRow("Row updated");
        gridRows.set(1, row);
    }

    public ListModelList<GridRow> getGridRows() {
        return gridRows;
    }

    public void setGridRows(ListModelList<GridRow> gridRows) {
        this.gridRows = gridRows;
    }

    public List<String> getDetails() {
        return details;
    }

    public String concat(String str, int i) {
        return str + i;
    }
}
