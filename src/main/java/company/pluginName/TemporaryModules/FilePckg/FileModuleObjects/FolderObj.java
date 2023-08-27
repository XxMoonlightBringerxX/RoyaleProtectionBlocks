package company.pluginName.TemporaryModules.FilePckg.FileModuleObjects;

import java.io.File;

public class FolderObj {
	
	private File folder;
	
	public FolderObj(String path) {
		this.folder = new File(path);
	}
	
	public File getFolder() {
		return this.folder;
	}
	
	public boolean createFolder() {
		if (!folder.exists() && !folder.mkdirs()) {
			return false;
		}
		return true;
	}
	
}
