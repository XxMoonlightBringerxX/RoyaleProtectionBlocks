package company.pluginName.Modules.FilePckg.Settings;

import relampagorojo93.LibsCollection.SpigotPlugin.Defaults.FileModuleObjects.FileObjFieldsEnum;

public enum SettingInt implements FileObjFieldsEnum<Integer> {
	SETTINGS_PROTECTION_BOUNDARIESVIEWDURATIONINSECONDS("Settings.Protection.Boundaries-view-duration-in-seconds", 30);

	// Methods
	String oldpath, path;
	Integer content, defaultcontent;

	SettingInt(String path, Integer defaultcontent) {
		this(path, path, defaultcontent);
	}

	SettingInt(String path, String oldpath, Integer defaultcontent) {
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
	public Integer getDefaultContent() {
		return defaultcontent;
	}

	@Override
	public Integer getContent() {
		return content != null ? content : defaultcontent;
	}

	@Override
	public void setContent(Integer content) {
		this.content = content;
	}

	@Override
	public Type getType() {
		return Type.INTEGER;
	}
}