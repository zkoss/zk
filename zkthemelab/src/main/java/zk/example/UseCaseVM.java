package zk.example;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.theme.MarbleDensity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UseCaseVM {

    private static final String DEFAULT_PAGE = "~./usecase/inventory-table.zul";
    private static final String DEFAULT_NAV = "Use Cases";

    private static final Map<String, String> PAGE_TO_NAV;
    static {
        PAGE_TO_NAV = new HashMap<>();
        for (String p : new String[]{"usecase/inventory-table", "usecase/item-editor",
                "usecase/ticket-inbox", "usecase/ops-dashboard", "usecase/onboarding-wizard",
                "usecase/account-settings", "usecase/sign-in", "usecase/brand-switcher"}) {
            PAGE_TO_NAV.put(p, "Use Cases");
        }
        for (String p : new String[]{"button", "combobutton", "checkbox", "radiogroup",
                "textbox", "intbox", "longbox", "decimalbox", "doublebox", "combobox", "selectbox",
                "chosenbox", "searchbox", "cascader", "datebox", "daterangebox", "timebox", "timepicker", "spinner",
                "doublespinner", "colorbox", "bandbox", "slider", "rangeslider", "multislider",
                "rating", "inputgroup", "inputs", "inputs-basic", "a",
                "calendar"}) {
            PAGE_TO_NAV.put(p, "Inputs");
        }
        for (String p : new String[]{"grid", "grid-header", "grid-grouping", "grid-livegrouping",
                "grid-detail", "grid-paging", "listbox", "listbox-header", "listbox-grouping",
                "tree", "tree-header", "paging", "biglistbox", "organigram", "badge", "chip"}) {
            PAGE_TO_NAV.put(p, "Data");
        }
        for (String p : new String[]{"menubar", "toolbar", "tabbox", "tabbox-misc",
                "navbar", "anchornav", "breadcrumb",
                "fisheyebar", "coachmark", "stepbar", "drawer"}) {
            PAGE_TO_NAV.put(p, "Navigation");
        }
        for (String p : new String[]{"window", "panel", "groupbox", "popup", "caption"}) {
            PAGE_TO_NAV.put(p, "Containers");
        }
        for (String p : new String[]{"borderlayout", "borderlayout-misc", "hlayout", "vlayout",
                "splitter", "splitlayout", "anchorlayout", "absolutelayout", "columnlayout",
                "rowlayout", "linelayout", "tablelayout", "cardlayout", "goldenlayout",
                "portallayout", "scrollview", "space", "area"}) {
            PAGE_TO_NAV.put(p, "Layout");
        }
        for (String p : new String[]{"progressmeter", "notification", "toast", "loadingbar",
                "messagebox", "confirmpopup", "loading", "separator", "scrollbar", "error", "errorbox"}) {
            PAGE_TO_NAV.put(p, "Feedback");
        }
        for (String p : new String[]{"fileupload", "dropupload", "audio", "video", "camera",
                "barcode", "barcodescanner", "imagemap", "pdfviewer", "cropper", "signature",
                "tbeditor", "captcha", "html", "iframe", "avatar", "carousel"}) {
            PAGE_TO_NAV.put(p, "Media & Upload");
        }
        for (String p : new String[]{
                "utility/colors", "utility/typography",
                "utility/spacing", "utility/stack",
                "utility/layout", "utility/borders",
                "utility/elevation", "utility/surface",
                "utility/opacity", "utility/visibility", "utility/interactions"}) {
            PAGE_TO_NAV.put(p, "Utility CSS");
        }
    }

    /** HEAD commit date + short hash, stamped onto the classpath at build time by
        scripts/stamp-commit.sh and shown beside the sidebar wordmark, so a design
        reviewer can pin a screenshot to a commit. Captured at build time rather than
        read from git here because the review WAR ships without a .git directory.
        Empty when the tree was built outside a git repo. */
    private static final String COMMIT_STAMP = readCommitStamp();

    private static String readCommitStamp() {
        try (InputStream in = UseCaseVM.class.getResourceAsStream("commit-stamp.txt")) {
            return in == null ? "" : new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
        } catch (IOException e) {
            return "";
        }
    }

    private String currentPage = DEFAULT_PAGE;
    private String openNavLabel = DEFAULT_NAV;
    private boolean compactMode = false;

    private static boolean isValidBookmark(String bookmark) {
        return bookmark != null && bookmark.matches("[a-zA-Z0-9/_-]+");
    }

    private void updateOpenNav(String bookmark) {
        String group = PAGE_TO_NAV.get(bookmark);
        if (group != null) {
            openNavLabel = group;
        }
    }

    @Init
    public void init() {
        String bookmark = Executions.getCurrent().getDesktop().getBookmark();
        if (isValidBookmark(bookmark)) {
            this.currentPage = "~./" + bookmark + ".zul";
            updateOpenNav(bookmark);
        }
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public String getOpenNavLabel() {
        return openNavLabel;
    }

    public String getCommitStamp() {
        return COMMIT_STAMP;
    }

    public boolean isCompactMode() {
        return compactMode;
    }

    /** Toggles the whole-app data-dense mode. Delegates to the theme's own
        MarbleDensity helper, which sets data-density on the document root — the
        same [data-density="compact"] knob Marble ships (tokens/_sizing.css), so the
        demo dogfoods the real mechanism rather than a demo-only class.
        See doc/spec/data-dense-mode.md. */
    @Command
    @NotifyChange("compactMode")
    public void toggleCompactMode(@BindingParam("on") boolean on) {
        this.compactMode = on;
        MarbleDensity.apply(on ? MarbleDensity.Density.COMPACT : MarbleDensity.Density.COMFORTABLE);
    }

    @Command
    @NotifyChange({"currentPage", "openNavLabel"})
    public void navigate(@BindingParam("page") String page) {
        this.currentPage = page;
        String bookmark = page.replace("~./", "").replace(".zul", "");
        Executions.getCurrent().getDesktop().setBookmark(bookmark);
        updateOpenNav(bookmark);
    }

    @Command
    @NotifyChange({"currentPage", "openNavLabel"})
    public void handleBookmarkChange(@BindingParam("bookmark") String bookmark) {
        if (isValidBookmark(bookmark)) {
            this.currentPage = "~./" + bookmark + ".zul";
            updateOpenNav(bookmark);
        } else {
            this.currentPage = DEFAULT_PAGE;
            this.openNavLabel = DEFAULT_NAV;
        }
    }
}
