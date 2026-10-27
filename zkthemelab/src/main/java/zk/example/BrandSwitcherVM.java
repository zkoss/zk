package zk.example;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.theme.MarbleBrand;

/**
 * Drives the Brand Presets showcase page ({@code usecase/brand-switcher.zul}).
 *
 * <p>Two paths are demonstrated:
 * <ul>
 * <li><b>Built-in presets</b> — delegate to the theme's own {@link MarbleBrand}
 *     helper, which sets {@code data-brand} on the document root (the same knob
 *     Marble ships in {@code tokens/_colors.css}). The demo dogfoods the real
 *     mechanism, mirroring how {@link UseCaseVM#toggleCompactMode} drives
 *     {@code MarbleDensity}.</li>
 * <li><b>Custom color</b> — sets an inline {@code --zk-color-primary} on the
 *     document root at runtime. This is only the demo's live illustration; a real
 *     app's own brand color is a static {@code :root { --zk-color-primary: … }}
 *     rule loaded after the theme CSS (no Java, no FOUC — see brand-override.md).
 *     The single-seed cascade re-derives the whole palette either way.</li>
 * </ul>
 */
public class BrandSwitcherVM {

    /** Preset token, or "custom" while an arbitrary picked color is active. */
    private String currentBrand = "default";

    public String getCurrentBrand() {
        return currentBrand;
    }

    @Command
    @NotifyChange("currentBrand")
    public void switchBrand(@BindingParam("brand") String brand) {
        MarbleBrand.Brand target;
        try {
            target = MarbleBrand.Brand.valueOf(brand.toUpperCase());
        } catch (IllegalArgumentException e) {
            target = MarbleBrand.Brand.DEFAULT;
        }
        this.currentBrand = target.token();
        // Drop any custom inline seed first — an inline style wins over the
        // [data-brand] rule, so leaving it would mask the preset.
        Clients.evalJavaScript(
                "document.documentElement.style.removeProperty('--zk-color-primary')");
        MarbleBrand.apply(target);
    }

    @Command
    @NotifyChange("currentBrand")
    public void applyCustomBrand(@BindingParam("color") String color) {
        if (!isSafeCssColor(color)) {
            return;
        }
        // Clear any preset and pin the picked seed inline. Whole palette derives
        // from it via the oklch(from …) cascade, exactly as a static :root rule would.
        this.currentBrand = "custom";
        Clients.evalJavaScript(
                "document.documentElement.removeAttribute('data-brand');"
                + "document.documentElement.style.setProperty('--zk-color-primary','" + color + "')");
    }

    /** Allowlist so the value can be inlined into the JS string literal safely
        (no quote/backslash/semicolon/angle-bracket escape). Covers #hex, rgb()/hsl()/oklch(). */
    private static boolean isSafeCssColor(String c) {
        return c != null && c.matches("[#()%.,\\-\\s0-9a-zA-Z]{1,64}");
    }
}
