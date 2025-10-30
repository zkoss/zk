package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

public class B96_ZK_5149VM {
	private List<Article> article;

	public List<Article> getArticle() {
		return article;
	}

	public void setArticle(List<Article> article) {
		this.article = article;
	}

	public B96_ZK_5149VM() {
		this.loadArticle();
	}

	public void loadArticle() {
		this.article = new ArrayList<>();
		Article art;
		for (int i = 0; i < 30; i++) {
			art = new Article();
			art.setCode("A");
			this.article.add(art);
			art = new Article();
			art.setCode("B");
			this.article.add(art);
		}
	}

	public class Article {
		private String code;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}
}