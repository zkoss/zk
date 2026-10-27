package zk.example;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.zkoss.zk.ui.WebApps;

/**
 * Backs index.zul — a link index of every preview page.
 *
 * The list is scanned off the filesystem rather than curated in code: UseCaseVM's PAGE_TO_NAV is a
 * "which nav group should open" lookup, not an inventory, and it has already drifted from the pages
 * on disk. Grouping therefore follows the directory layout under /web, which cannot go stale.
 */
public class PageIndexVM {

	/** One page: the href index.zul links to, and the name shown for it. */
	public static class Page {
		private final String href;
		private final String name;

		Page(String href, String name) {
			this.href = href;
			this.name = name;
		}

		public String getHref() {
			return href;
		}

		public String getName() {
			return name;
		}
	}

	public static class Group {
		private final String label;
		private final String note;
		private final List<Page> pages;

		Group(String label, String note, List<Page> pages) {
			this.label = label;
			this.note = note;
			this.pages = pages;
		}

		public String getLabel() {
			return label;
		}

		public String getNote() {
			return note;
		}

		public List<Page> getPages() {
			return pages;
		}

		public int getCount() {
			return pages.size();
		}
	}

	private final List<Group> groups = new ArrayList<>();
	private int totalCount;

	public PageIndexVM() {
		// The pages live in the webapp's /web directory (served as class web resources through
		// org.zkoss.web.util.resource.dir); scan that directory, not the class path.
		final File web = new File(WebApps.getCurrent().getRealPath("/web"));
		addGroup("Use cases", "Whole screens — judge whether the theme hangs together", scan(web, "usecase"));
		addGroup("Components", "One widget per page, across its states and variants", scan(web, null));
		addGroup("Utility CSS", "The token and utility-class catalogues", scan(web, "utility"));
		addGroup("Input snippets", "The popup content the input pages load; out of context on their own", scan(web, "pv"));
	}

	private void addGroup(String label, String note, List<Page> pages) {
		if (!pages.isEmpty()) {
			groups.add(new Group(label, note, pages));
			totalCount += pages.size();
		}
	}

	/** Lists the .zul files directly in /web (subDir null) or in /web/&lt;subDir&gt;. */
	private List<Page> scan(File web, String subDir) {
		final File dir = subDir == null ? web : new File(web, subDir);
		final File[] files = dir.listFiles((d, name) -> name.endsWith(".zul"));
		final List<Page> pages = new ArrayList<>();
		if (files != null) {
			for (File f : files) {
				final String href = subDir == null ? f.getName() : subDir + "/" + f.getName();
				pages.add(new Page(href, f.getName().substring(0, f.getName().length() - 4)));
			}
		}
		Collections.sort(pages, (a, b) -> a.getName().compareTo(b.getName()));
		return pages;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public int getTotalCount() {
		return totalCount;
	}
}
