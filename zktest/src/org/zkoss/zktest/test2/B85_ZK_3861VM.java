package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ext.Pageable;

import java.util.List;
import java.util.Locale;

/**
 * @author charles qiu
 */
public class B85_ZK_3861VM {

    private ListModelList model = new ListModelList(Locale.getAvailableLocales());

    @Init
    public void init() {
        ((Pageable) model).setPageSize(15);
    }

    @Command
    public void next() {
        int activePage = ((Pageable) model).getActivePage();
        ((Pageable) model).setActivePage(activePage + 1);
    }

    @Command
    public void previous() {
        int activePage = ((Pageable) model).getActivePage();
        if (--activePage < 0) {
            activePage = 0;
        }
        ((Pageable) model).setActivePage(activePage);
    }

    public List getModel() {
        return model;
    }
}