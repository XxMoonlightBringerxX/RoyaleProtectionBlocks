package company.pluginName.Modules.FilePckg.Settings;

import relampagorojo93.LibsCollection.SpigotPlugin.Defaults.FileModuleObjects.FileObjFieldsEnum;

public enum SettingBoolean implements FileObjFieldsEnum<Boolean> {
	SETTINGS_DEBUG_ENABLED("Settings.Debug.Enabled", false),
	SETTINGS_DEBUG_MESSAGES_BLOCKPLACE("Settings.Debug.Messages.Block-place", true),
	SETTINGS_DEBUG_MESSAGES_BLOCKPLACECANCELLED("Settings.Debug.Messages.Block-place-cancelled", true),
	SETTINGS_DEBUG_MESSAGES_BLOCKBREAK("Settings.Debug.Messages.Block-break", true),
	SETTINGS_DEBUG_MESSAGES_BLOCKBREAKCANCELLED("Settings.Debug.Messages.Block-break-cancelled", true),
	SETTINGS_DEBUG_MESSAGES_PROTECTIONCREATIONATTEMPT("Settings.Debug.Messages.Protection-creation-attempt", true),
	SETTINGS_DEBUG_MESSAGES_PROTECTIONCREATION("Settings.Debug.Messages.Protection-creation", true),
	SETTINGS_DEBUG_MESSAGES_PROTECTIONREMOVALATTEMPT("Settings.Debug.Messages.Protection-removal-attempt", true),
	SETTINGS_DEBUG_MESSAGES_PROTECTIONREMOVAL("Settings.Debug.Messages.Protection-removal", true);

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