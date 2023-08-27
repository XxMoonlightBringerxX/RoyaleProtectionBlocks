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

						field.setObjectContent(nw.asYamlData().getData() != null ? nw.asYamlData().getData()
								: field.getDefaultContent());
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
			int oldversion = oldyaml.getRoot().getOrDefault("Version", 0).asYamlData().getInteger(),
					newversion = newyaml.getRoot().getOrDefault("Version", 0).asYamlData().getInteger();
			YamlData<?> oldsection;
			if (oldversion < newversion) {
				for (YamlData<?> newsection : newyaml.getRoot().getAllNamedChilds()) {
					if (!newsection.getName().equals("Version")
							&& (oldsection = oldyaml.getRoot().getData(newsection.toPath())) != null
							&& oldsection.isYamlData()) {
						newyaml.getRoot().set(newsection.toPath(), oldsection.getData());
					}
				}

				for (Entry<String, String> entry : oldtonew.entrySet()) {
					if ((oldsection = oldyaml.getRoot().getData(entry.getKey())) != null) {
						newyaml.getRoot().set(entry.getValue(), oldsection.getData());
					}
				}

				return newyaml;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oldyaml;
	}

}
