package company.pluginName.TemporaryModules.FilePckg.FileModuleObjects;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map.Entry;

import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlNamedObject;
import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;

public class FileObj {

	private File file;
	private PandaYaml defaultContent;
	private FileObjFieldsEnum<?>[][] enums;

	public FileObj(String path) {
		this.file = new File(path);
		this.defaultContent = null;
		this.enums = null;
	}

	public FileObj(String path, PandaYaml defaultContent, FileObjFieldsEnum<?>[]... enums) {
		this.file = new File(path);
		this.defaultContent = defaultContent;
		this.enums = enums;
	}

	public File getFile() {
		return this.file;
	}

	public boolean createFile() {
		PandaYaml yFile = null;

		try {
			if (defaultContent != null) {
				if (!this.file.exists()) {
					this.defaultContent.saveYAML(this.file);
				}

				yFile = new PandaYaml(this.file);

				if (this.defaultContent.getRoot().has("Version")) {
					yFile = fileVersionCheck(yFile, defaultContent);
				}

			} else if (!this.file.exists()) {
				this.file.getParentFile().mkdirs();
				this.file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				Files.move(this.file.toPath(), new File(getFile().getPath() + ".backup").toPath());

				this.defaultContent.saveYAML(this.file);

				yFile = new PandaYaml(this.file);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		if (yFile != null && enums.length > 0) {
			try {
				for (FileObjFieldsEnum<?>[] fields : enums) {
					for (FileObjFieldsEnum<?> field : fields) {
						YamlNamedObject nw = yFile.getRoot().get(field.getPath());
						if (!field.getPath().equals(field.getOldPath())) {
							YamlNamedObject old = yFile.getRoot().get(field.getOldPath());
							if (old != null) {
								if (nw == null) {
									nw = yFile.getRoot().set(field.getPath(), old.asYamlData().getData());
								}
								yFile.getRoot().remove(field.getOldPath());
							}
						}

						if (nw == null) {
							nw = yFile.getRoot().set(field.getPath(), field.getDefaultContent());
						}

						field.setObjectContent(nw.asYamlData().getData() != null ? nw.asYamlData().getData() : field.getDefaultContent());
					}
				}

				yFile.saveYAML(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	protected PandaYaml fileVersionCheck(PandaYaml oldyaml, PandaYaml newyaml) {
		return fileVersionCheck(oldyaml, newyaml, new HashMap<>());
	}

	private PandaYaml fileVersionCheck(PandaYaml oldyaml, PandaYaml newyaml, HashMap<String, String> oldtonew) {
		try {
			int currentVersion = oldyaml.getRoot().getDataOrDefault("Version", 0).getInteger();
			int defaultVersion = newyaml.getRoot().getDataOrDefault("Version", 0).getInteger();

			if (currentVersion < defaultVersion) {
				PandaYaml defaultYaml = newyaml.cloneYaml();

				for (YamlData<?> currentSection : oldyaml.getRoot().getAllNamedChilds()) {
					if (!currentSection.toPath().equals("Version")) {
						defaultYaml.getRoot().set(currentSection.toPath(), currentSection);
					}
				}

				YamlData<?> currentSection;
				for (Entry<String, String> entry : oldtonew.entrySet()) {
					currentSection = defaultYaml.getRoot().getData(entry.getKey());
					if (currentSection != null) {
						defaultYaml.getRoot().set(entry.getValue(), currentSection.getData());
						defaultYaml.getRoot().remove(entry.getValue());
					}
				}

				return defaultYaml;
			}
		} catch (Exception e) {
			System.out.println(String.format("Unable to update file '%s' with new default file content:", getFile().getPath()));
			e.printStackTrace();
		}
		return oldyaml;
	}

}
