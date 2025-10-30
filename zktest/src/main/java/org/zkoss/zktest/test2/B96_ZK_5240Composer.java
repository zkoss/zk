package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

public class B96_ZK_5240Composer extends SelectorComposer<Component> {

    @Wire
    private Grid gd;


    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        gd.setModel(new ListModelList<>());
    }

    private ListModel<?> generateModel() {
        List Items = new ArrayList();
        for (int i = 0; i < 2500; i++) {
            Items.add("data "+i);
        }
        ListModelList model = new ListModelList(Items);
        return model;
    }

    @Listen("onClick=#btn")
    public void doBtnClick() {
        ((ListModelList)gd.getModel()).addAll((Collection) generateModel());
        gd.invalidate();
    }

}
