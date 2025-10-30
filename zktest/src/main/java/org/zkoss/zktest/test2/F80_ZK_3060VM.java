package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * Created by wenninghsu on 7/6/16.
 */
public class F80_ZK_3060VM {

    private boolean _pagingDisabled = true;

    public boolean isPagingDisabled() {
        return _pagingDisabled;
    }

    public void setPagingDisabled(boolean pagingDisabled) {
        _pagingDisabled = pagingDisabled;
    }

    @Command
    @NotifyChange("pagingDisabled")
    public void select() {
        _pagingDisabled = !_pagingDisabled;
    }
}
