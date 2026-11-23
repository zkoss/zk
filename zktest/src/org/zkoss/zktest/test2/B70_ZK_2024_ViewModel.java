package org.zkoss.zktest.test2;

import java.util.Arrays;
import java.util.List;

import org.zkoss.bind.annotation.Init;

public class B70_ZK_2024_ViewModel {
	private String code;
	private String name;

	public B70_ZK_2024_ViewModel() {
	}
			
			
	public B70_ZK_2024_ViewModel(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	private List<B70_ZK_2024_ViewModel> countries;
	private B70_ZK_2024_ViewModel country;

	public List<B70_ZK_2024_ViewModel> getCountries() {
		return countries;
	}

	public B70_ZK_2024_ViewModel getCountry() {
		return country;
	}

	@Init
	public void init() {
		countries = Arrays.asList(new B70_ZK_2024_ViewModel[] {
				//
				new B70_ZK_2024_ViewModel("AT", "\u00D6sterreich"), //
				new B70_ZK_2024_ViewModel("BE", "Belgi\u00EB"), //
				new B70_ZK_2024_ViewModel("BG",
						"\u0411\u044A\u043B\u0433\u0430\u0440\u0438\u044F"), //
				new B70_ZK_2024_ViewModel("CH", "Suisse"), //
				new B70_ZK_2024_ViewModel("CY",
						"\u039A\u03CD\u03C0\u03C1\u03BF\u03C2"), //
				new B70_ZK_2024_ViewModel("CZ", "\u010Cesk\u00E1 republika"), //
				new B70_ZK_2024_ViewModel("DE", "Deutschland"), //
				new B70_ZK_2024_ViewModel("DK", "Danmark"), //
				new B70_ZK_2024_ViewModel("EE", "Eesti"), //
				new B70_ZK_2024_ViewModel("ES", "Espa\u00F1a"), //
				new B70_ZK_2024_ViewModel("FI", "Suomi"), //
				new B70_ZK_2024_ViewModel("FR", "France"), //
				new B70_ZK_2024_ViewModel("GB", "United Kingdom"), //
				new B70_ZK_2024_ViewModel("GR",
						"\u0395\u03BB\u03BB\u03AC\u03B4\u03B1"), //
				new B70_ZK_2024_ViewModel("HU", "Magyarorsz\u00E1g"), //
				new B70_ZK_2024_ViewModel("IE", "\u00C9ire"), //
				new B70_ZK_2024_ViewModel("IS", "\u00CDsland"), //
				new B70_ZK_2024_ViewModel("IT", "Italia"), //
				new B70_ZK_2024_ViewModel("LT", "Lietuva"), //
				new B70_ZK_2024_ViewModel("LU", "L\u00EBtzebuerg"), //
				new B70_ZK_2024_ViewModel("LV", "Latvija"), //
				new B70_ZK_2024_ViewModel("MT", "Malta"), //
				new B70_ZK_2024_ViewModel("NL", "Nederland"), //
				new B70_ZK_2024_ViewModel("NO", "Norge"), //
				new B70_ZK_2024_ViewModel("PL", "Polska"), //
				new B70_ZK_2024_ViewModel("PT", "Portugal"), //
				new B70_ZK_2024_ViewModel("RO", "Rom\u00E2nia"), //
				new B70_ZK_2024_ViewModel("SE", "Sverige"), //
				new B70_ZK_2024_ViewModel("SI", "Slovenija"), //
				new B70_ZK_2024_ViewModel("SK", "Sloensk\u00E1 republika") //
				});
	}

	public void setCountry(
			B70_ZK_2024_ViewModel country) {
		this.country = country;
	}
}