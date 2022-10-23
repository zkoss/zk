package demo.grid.data_binding;

import java.util.ArrayList;
import java.util.List;

import demo.data.ContributorData;
import demo.data.pojo.Contributor;

import org.zkoss.bind.annotation.Init;

public class ContributorViewModel {
	private Contributor selected;
	private List<String> titles = new ArrayList<String>(new ContributorData().getTitles());
	private List<Contributor> contributors = new ArrayList<Contributor>(new ContributorData().getContributors());

	@Init
	public void init() {	// Initialize
		selected = contributors.get(0); // Selected First One
	}

	public List<String> getContributorTitles() {
		return titles;
	}

	public List<Contributor> getContributorList() {
		return contributors;
	}

	public void setSelectedContributor(Contributor selected) {
		this.selected = selected;
	}

	public Contributor getSelectedContributor() {
		return selected;
	}
}
