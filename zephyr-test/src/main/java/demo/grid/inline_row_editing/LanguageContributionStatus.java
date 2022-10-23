package demo.grid.inline_row_editing;

import demo.data.pojo.LanguageContribution;

public class LanguageContributionStatus {
	private LanguageContribution lc;
	private boolean editingStatus;
	
	public LanguageContributionStatus(LanguageContribution lc, boolean editingStatus) {
		this.lc = lc;
		this.editingStatus = editingStatus;
	}
	
	public LanguageContribution getLanguageContribution() {
		return lc;
	}
	
	public boolean getEditingStatus() {
		return editingStatus;
	}
	
	public void setEditingStatus(boolean editingStatus) {
		this.editingStatus = editingStatus;
	}
}
