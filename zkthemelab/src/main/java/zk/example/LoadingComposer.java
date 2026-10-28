package zk.example;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

public class LoadingComposer extends SelectorComposer<Component> {

    @Wire
    private Window demoWin;
    @Wire
    private Timer clearGlobalTimer;

    @Listen("onClick = #showGlobalBusyBtn")
    public void showGlobalBusy() {
        Clients.showBusy("Loading...");
        clearGlobalTimer.setRunning(true);
    }

    @Listen("onTimer = #clearGlobalTimer")
    public void onGlobalTimer() {
        Clients.clearBusy();
        clearGlobalTimer.setRunning(false);
    }

    @Listen("onClick = #showBusyBtn")
    public void showBusy() {
        Clients.showBusy(demoWin, "Processing...");
    }

    @Listen("onClick = #clearBusyBtn")
    public void clearBusy() {
        Clients.clearBusy(demoWin);
    }
}
