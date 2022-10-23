package demo.data;

import java.util.ArrayList;
import java.util.List;

import demo.data.pojo.Contributor;
import demo.data.pojo.LanguageContribution;

public class ContributorData {

	private List<String> titles = new ArrayList<String>();
	private List<Contributor> contributors = new ArrayList<Contributor>();
	private List<LanguageContribution> languageContributions = new ArrayList<LanguageContribution>();

	public ContributorData() {
		titles.add("Code");
		titles.add("Bug");
		titles.add("Docs");
		titles.add("Arts");

		contributors.add(new Contributor("Kaleb", "Leonel", titles.get(0).toString(), 321));
		contributors.add(new Contributor("Balu", "Haben", titles.get(0).toString(), 321));
		contributors.add(new Contributor("Trey", "Wyatt", titles.get(0).toString(), 323));
		contributors.add(new Contributor("Balu", "Chen", titles.get(0).toString(), 324));
		contributors.add(new Contributor("Terry", "Tornado", titles.get(0).toString(), 711));
		contributors.add(new Contributor("Jesse", "Miles", titles.get(1).toString(), 712));
		contributors.add(new Contributor("Sadira", "Jobs", titles.get(1).toString(), 713));
		contributors.add(new Contributor("Jaquan", "Frederick", titles.get(2).toString(), 451));
		contributors.add(new Contributor("Avery", "Katrina", titles.get(2).toString(), 453));
		contributors.add(new Contributor("Heidi", "Nikolas", titles.get(2).toString(), 455));
		contributors.add(new Contributor("Katelyn", "Clara", titles.get(2).toString(), 457));
		contributors.add(new Contributor("Branden", "Shane", titles.get(2).toString(), 459));
		contributors.add(new Contributor("Dacey", "Obert", titles.get(2).toString(), 450));
		contributors.add(new Contributor("Julianna", "Allison", titles.get(3).toString(), 643));
		contributors.add(new Contributor("Rachel", "Elisabeth", titles.get(3).toString(), 644));
		contributors.add(new Contributor("Clarissa", "Francesca", titles.get(3).toString(), 645));
		contributors.add(new Contributor("Gabby", "Taffy", titles.get(3).toString(), 646));
		
		languageContributions.add(new LanguageContribution("Arabic (ar)", "Ayman Elgharabawy", "iso-8859-6"));
		languageContributions.add(new LanguageContribution("Hungarian (hu)", "Andreas Klein", "iso-8859-2"));
		languageContributions.add(new LanguageContribution("Italian (it)", "Matteo Barbieri", "iso-8859-1"));
		languageContributions.add(new LanguageContribution("Indonesian (id)", "James Liam Supangkat", "iso-8859-1"));
		languageContributions.add(new LanguageContribution("Japanese (ja)", "Poli Lee", " iso-2022-jp"));
		languageContributions.add(new LanguageContribution("Korean (ko)", "Deok-su Lee", "euc-kr"));
		languageContributions.add(new LanguageContribution("Dutch (nl)", "Wido Jansen", "iso-8859-1"));
		languageContributions.add(new LanguageContribution("Portuguese (pt)", "Airton Carrara", "iso-8859-1"));
		languageContributions.add(new LanguageContribution("Brazilian Portugese (pt_BR)", "Airton Carrara", "iso-8859-1"));
		languageContributions.add(new LanguageContribution("Romanian (ro)", "Calin Vaida", "iso-8859-2"));
		languageContributions.add(new LanguageContribution("Russian (ru)", "Denis Yarkovoy", "iso-8859-1"));
		languageContributions.add(new LanguageContribution("French (fr)", "Jêrôme Vergereau", "iso-8859-1"));
		languageContributions.add(new LanguageContribution("Spanish (es)", "Daniel Octavio Ruiz Rodriguez", "iso-8859-1"));
		languageContributions.add(new LanguageContribution("German (de)", "H.-Dirk Schmitt", "iso-8859-1"));
		languageContributions.add(new LanguageContribution("Czech (cs)", "Pavel Mica", "iso-8859-2"));
		languageContributions.add(new LanguageContribution("Catalan (ca)", "Xavier Covas O'Ryan", "iso-8859-1"));
		languageContributions.add(new LanguageContribution("Bulgarian (bg) ", "Boril Yonchev", "iso-8859-5"));
		languageContributions.add(new LanguageContribution("Ukrainian (uk)", "Denis Yarkovoy", "iso-8859-5"));
		languageContributions.add(new LanguageContribution("Turkish (tr)", "Kursat Kurt", "iso-8859-9"));
		languageContributions.add(new LanguageContribution("Swedish (sv)", "Easit AB", "iso-8859-1"));
		languageContributions.add(new LanguageContribution("Slovenian (sl) ", "Ziga", "iso-8859-2"));
	}

	public List<String> getTitles() {
		return titles;
	}

	public List<Contributor> getContributors() {
		return contributors;
	}

	public List<LanguageContribution> getLanguageContributors() {
		return languageContributions;
	}

}
