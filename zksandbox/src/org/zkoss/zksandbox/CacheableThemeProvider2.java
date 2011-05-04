package org.zkoss.zksandbox;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ThemeProvider;
import org.zkoss.zk.ui.util.ThemeProvider.Aide;
import org.zkoss.zksandbox.Themes2;
import org.zkoss.zul.Messagebox;

public class CacheableThemeProvider2  implements ThemeProvider{
	
	public final static String DEFAULT_WCS = "~./zul/css/zk.wcs";
	public final static String DEFAULT_MSGBOX_TEMPLATE_URI = "~./zul/html/messagebox.zul";
	
	public Collection getThemeURIs(Execution exec, List uris) {
		String suffix = getThemeFileSuffix();
		String fsc = Themes2.getFontSizeCookie(exec);
		boolean isSilvergray = Themes2.isSilvergray(exec) && Themes2.hasSilvergrayLib();
		processSilverAndFontURI(isSilvergray, uris, fsc);
		
		if ("lg".equals(fsc)) {
			uris.add("/css/fontlg.css.dsp");
			if (isSilvergray)
				uris.add("/css/silvergraylg.css.dsp");
		} else if ("sm".equals(fsc)) {
			uris.add("/css/fontsm.css.dsp");
			if (isSilvergray)
				uris.add("/css/silvergraysm.css.dsp");
		}
		if (Strings.isEmpty(suffix)) {
			Messagebox.setTemplate(DEFAULT_MSGBOX_TEMPLATE_URI);
			return uris;
		}
		if (isUsingDefaultTemplate(suffix))
			Messagebox.setTemplate(getThemeMsgBoxURI(suffix));
		
		uris.add(getExtCSS(suffix));
		uris.add(getNormCSS(suffix));
		
		bypassURI(uris, suffix);
		return uris;
	}
	
	private static String getThemeFileSuffix() {
		String suffix = Themes2.getCurrentTheme();
		return Themes2.getProperty("CLASSICBLUE_THEME").equals(suffix) ? null : suffix;
	}
	
	private static String getExtCSS(String suffix) {
		return "~./zul/css/ext." + suffix + ".css.dsp";
	}
	
	private static String getNormCSS(String suffix) {
		return "~./zul/css/norm." + suffix + ".css.dsp";
	}
	
	private static String getThemeMsgBoxURI(String suffix) {
		return "~./zul/html/messagebox." + suffix + ".zul";
	}
	
	private static boolean isUsingDefaultTemplate(String suffix){
		return getThemeMsgBoxURI(suffix).equals(Messagebox.getTemplate()) ||
			DEFAULT_MSGBOX_TEMPLATE_URI.equals(Messagebox.getTemplate());
	}
	
	private void bypassURI(List uris, String suffix) {
		for (ListIterator it = uris.listIterator(); it.hasNext();) {
			final String uri = (String)it.next();
			if (uri.startsWith(DEFAULT_WCS)) {
				it.set(Aide.injectURI(uri, suffix));
				break;
			} 
		}
	}

	/**
	 * Setup inject URI for font and silvergray
	 * @param isSilver
	 * @param uris
	 * @param fsn
	 */
	private static void processSilverAndFontURI (boolean isSilver, List uris, String fsn) {
		for (ListIterator it = uris.listIterator(); it.hasNext();) {
			final String uri = (String)it.next();
			if (isSilver) {
				if (uri.startsWith(Themes2.getProperty("DEFAULT_WCS_URI"))) {
					String injectURI = Themes2.getProperty("SILVERGRAY_THEME") + ((fsn != null && fsn.length() > 0) ? "-" + fsn : "");
					it.set(Aide.injectURI(uri, injectURI));
				}
			} else {
				/*Remove silvergray URI*/
				if (uri.startsWith(Themes2.getProperty("DEFAULT_SILVERGRAY_URI")))
					it.remove();
				else if (fsn != null && fsn.length() > 0 && uri.startsWith(Themes2.getProperty("DEFAULT_WCS_URI")))
					it.set(Aide.injectURI(uri, fsn));
			}
		}
	}

	public int getWCSCacheControl(Execution exec, String uri) {
		return 8760; // a year. (JVM will utilize it, don't have to count the answer)
	}
	
	public String beforeWCS(Execution exec, String uri) {
		final String[] dec = Aide.decodeURI(uri);
		if (dec != null) {
			if ("lg".equals(dec[1])) {
				exec.setAttribute("fontSizeM", "15px");
				exec.setAttribute("fontSizeMS", "13px");
				exec.setAttribute("fontSizeS", "13px");
				exec.setAttribute("fontSizeXS", "12px");
			} else if ("sm".equals(dec[1])) {
				exec.setAttribute("fontSizeM", "10px");
				exec.setAttribute("fontSizeMS", "9px");
				exec.setAttribute("fontSizeS", "9px");
				exec.setAttribute("fontSizeXS", "8px");
			}
			return dec[0];
		}
		return uri;
	}
	
	public String beforeWidgetCSS(Execution exec, String uri) {
		String suffix = getThemeFileSuffix();
		if (Strings.isEmpty(suffix)) return uri;
		
		if(uri.startsWith("~./js/zul/") || 
				uri.startsWith("~./js/zkex/") || 
				uri.startsWith("~./js/zkmax/")){
			return uri.replaceFirst(".css.dsp", getWidgetCSSName(suffix));
		}
		return uri;
	}
	
	private static String getWidgetCSSName(String suffix) {
		return "." + suffix + ".css.dsp";
	}
	
}
