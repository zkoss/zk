package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * Created by wenninghsu on 7/6/16.
 */
public class F80_ZK_3060VM {

    private boolean _disablePaging = true;

    public boolean isDisablePaging() {
        return _disablePaging;
    }

    public void setDisablePaging(boolean disablePaging) {
        _disablePaging = disablePaging;
    }

    @Command
    @NotifyChange("disablePaging")
    public void select() {
        _disablePaging = !_disablePaging;
    }
}
