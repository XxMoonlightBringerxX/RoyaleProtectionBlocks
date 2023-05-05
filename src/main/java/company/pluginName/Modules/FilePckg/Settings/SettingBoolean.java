package company.pluginName.Modules.FilePckg.Settings;

import relampagorojo93.LibsCollection.SpigotPlugin.Defaults.FileModuleObjects.FileObjFieldsEnum;

public enum SettingBoolean implements FileObjFieldsEnum<Boolean> {
	;

	// Methods
	String oldpath, path;
	Boolean content, defaultcontent;

	SettingBoolean(String path, Boolean defaultcontent) {
		this(path, path, defaultcontent);
	}

	SettingBoolean(String path, String oldpath, Boolean defaultcontent) {
		this.path = path;
		this.oldpath = oldpath;
		this.defaultcontent = defaultcontent;
	}

	@Override
	public String toString() {
		return String.valueOf(getContent());
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getOldPath() {
		return oldpath;
	}

	@Override
	public Boolean getDefaultContent() {
		return defaultcontent;
	}

	@Override
	public Boolean getContent() {
		return content != null ? content : defaultcontent;
	}

	@Override
	public void setContent(Boolean content) {
		this.content = content;
	}

	@Override
	public Type getType() {
		return Type.BOOLEAN;
	}

}