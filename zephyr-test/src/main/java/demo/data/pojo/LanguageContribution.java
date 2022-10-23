package demo.data.pojo;

public class LanguageContribution {
	private String language, name, charset;

	public LanguageContribution(String language, String name, String charset) {
		this.language = language;
		this.name = name;
		this.charset = charset;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}
