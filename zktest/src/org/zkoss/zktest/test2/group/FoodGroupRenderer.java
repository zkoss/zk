package org.zkoss.zktest.test2.group;

import org.zkoss.zul.Group;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

/**
 * For B50-ZK-699 test case
 * @author jumperchen
 *
 */
public class FoodGroupRenderer implements RowRenderer, java.io.Serializable {
    public void render(Row row, java.lang.Object obj) {
        if (row instanceof Group) {
            row.appendChild(new Label(obj.toString()));
        } else {
            Object[] data = (Object[]) obj;
            row.appendChild(new Label(data[0].toString()));
            row.appendChild(new Label(data[1].toString()));
            row.appendChild(new Label(data[2].toString()));
            row.appendChild(new Label(data[3].toString()));
            row.appendChild(new Label(data[4].toString()));
        }
    }
};
