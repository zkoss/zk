package demo.grid.inline_row_editing;

import java.util.List;

import demo.data.ContributorData;
import demo.data.pojo.LanguageContribution;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;

public class InplaceEditingViewModel {
	private final ContributorData contributorData = new ContributorData();
	private final ListModelList<LanguageContributionStatus> contributionStatuses = generateStatusList(contributorData.getLanguageContributors());
	private boolean displayEdit = true;

	public boolean isDisplayEdit() {
		return displayEdit;
	}

	@NotifyChange({"languageContributions", "displayEdit"})
	public void setDisplayEdit(boolean displayEdit) {
		this.displayEdit = displayEdit;
	}

	public List<LanguageContributionStatus> getLanguageContributions() {
		return contributionStatuses;
	}

	@Command
	@NotifyChange("contributionStatuses")
	public void changeEditableStatus(@BindingParam("languageContributionStatus") LanguageContributionStatus lcs) {
		lcs.setEditingStatus(!lcs.getEditingStatus());
		refreshRowTemplate(lcs);
	}

	@Command
	@NotifyChange("contributionStatuses")
	public void confirm(@BindingParam("languageContributionStatus") LanguageContributionStatus lcs) {
		changeEditableStatus(lcs);
		refreshRowTemplate(lcs);
	}

	public void refreshRowTemplate(LanguageContributionStatus lcs) {
		//replace the element in the collection by itself to trigger a model update
		contributionStatuses.set(contributionStatuses.indexOf(lcs), lcs);
	}

	private static ListModelList<LanguageContributionStatus> generateStatusList(List<LanguageContribution> contributions) {
		ListModelList<LanguageContributionStatus> contribs = new ListModelList<LanguageContributionStatus>();
		for (LanguageContribution lc : contributions) {
			contribs.add(new LanguageContributionStatus(lc, false));
		}
		return contribs;
	}
}
