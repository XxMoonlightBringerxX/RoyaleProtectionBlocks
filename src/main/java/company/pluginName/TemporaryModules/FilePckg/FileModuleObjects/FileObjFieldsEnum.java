package company.pluginName.TemporaryModules.FilePckg.FileModuleObjects;

public interface FileObjFieldsEnum<T extends Object> {

	public String getPath();

	public String getOldPath();

	public T getDefaultContent();

	public T getContent();

	public void setContent(T content);
	
	@SuppressWarnings("unchecked")
	public default void setObjectContent(Object content) {
		try {
			T result = null;
			switch (getType()) {
				case BOOLEAN:
					result = (T) ((Boolean) Boolean.parseBoolean(content.toString()));
					break;
				case DOUBLE:
					result = (T) ((Double) Double.parseDouble(content.toString()));
					break;
				case INTEGER:
					result = (T) ((Integer) Integer.parseInt(content.toString()));
					break;
				case STRING:
					result = (T) content.toString();
					break;
				case STRING_LIST:
					result = (T) content;
					break;
			}
			setContent(result);
		} catch (Exception e) {}
	}
	
	public Type getType();
	
	public static enum Type {
		INTEGER,
		DOUBLE,
		BOOLEAN,
		STRING,
		STRING_LIST,
		
	}
}
