package company.pluginName.Modules.FilePckg.Messages;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import relampagorojo93.LibsCollection.SpigotPlugin.Defaults.FileModuleObjects.FileObjFieldsEnum;

@RequiredArgsConstructor
@Getter
public enum MessageList implements FileObjFieldsEnum<List<String>> {
	// Messages
	MESSAGE_HELPER_HEADER("Message.Helper.Header",
			Arrays.asList(" ", "&c・。・。・。・。・。・。・。%left_arrow%&r %current_page%/%max_page% %right_arrow%&c。・。・。・。・。・。・。・",
					" ")),
	MESSAGE_HELPER_BODY("Message.Helper.Body", Arrays.asList("&6%command_usage%", "  &8%command_description%")),
	MESSAGE_HELPER_FOOTER("Message.Helper.Footer", Arrays.asList(" ", "&c・。・。・。・。・。・。・。・。・。・。・。・。・。・。・。・。・", " ")),

	INVENTORY_PROTECTIONBLOCKS_LIST_BLOCKLORE("Inventory.Protection-blocks.List.Block-lore",
			Arrays.asList("&0", "&a&oBlock ID: &e{block_id}", "&a&oBlock size: &e{blocks_x}x{blocks_y}x{blocks_z}")),

	INVENTORY_PROTECTION_LIST_PROTECTIONLORE("Inventory.Protection.List.Protection-lore", Arrays.asList("&0",
			"&a&oWorld: &e{world}", "&a&oLocation: &ex={location_x},y={location_y},z={location_z}"));

	// Methods
	private @NonNull String oldPath, path;
	private @NonNull List<String> defaultContent;
	private @Setter List<String> content;

	MessageList(String path, List<String> defaultContent) {
		this(path, path, defaultContent);
	}

	@Override
	public String toString() {
		return String.valueOf(getContent());
	}

	@Override
	public List<String> getContent() {
		return content != null ? content : defaultContent;
	}

	@Override
	public Type getType() {
		return Type.STRING_LIST;
	}

	public String[] toArray() {
		return getContent().toArray(new String[getContent().size()]);
	}
}
