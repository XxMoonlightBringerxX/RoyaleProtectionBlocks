package company.pluginName.TemporaryModules.FilePckg.Messages;

import java.util.Arrays;
import java.util.List;

import company.pluginName.TemporaryModules.FilePckg.FileModuleObjects.FileObjFieldsEnum;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public enum MessageList implements FileObjFieldsEnum<List<String>> {
	// Messages
	MESSAGE_HELPER_HEADER("Message.Helper.Header",
			Arrays.asList(" ", "&c・。・。・。・。・。・。・。%left_arrow%&r %current_page%/%max_page% %right_arrow%&c。・。・。・。・。・。・。・",
					" ")),
	MESSAGE_HELPER_BODY("Message.Helper.Body", Arrays.asList("&6%command_usage%", "  &8%command_description%")),
	MESSAGE_HELPER_FOOTER("Message.Helper.Footer", Arrays.asList(" ", "&c・。・。・。・。・。・。・。・。・。・。・。・。・。・。・。・。・", " ")),

	INVENTORY_PROTECTION_PROTECTIONINFOLORE("Inventory.Protection.Protection-info-lore",
			Arrays.asList("&0", "&a&oWorld: &e{world}", "&a&oLocation: &ex={location_x},y={location_y},z={location_z}",
					"&a&oSize: &e{size_x}x{size_y}x{size_z}")),

	INVENTORY_PROTECTION_LIST_PROTECTIONLORE("Inventory.Protection.List.Protection-lore",
			Arrays.asList("&0", "&a&oWorld: &e{world}",
					"&a&oLocation: &ex={location_x},y={location_y},z={location_z}")),

	INVENTORY_PROTECTIONBLOCKS_LIST_BLOCKLORE("Inventory.Protection-blocks.List.Block-lore",
			Arrays.asList("&0", "&a&oBlock ID: &e{block_id}", "&a&oBlock size: &e{blocks_x}x{blocks_y}x{blocks_z}")),

	INVENTORY_PROTECTIONBLOCKS_MANAGE_ITEMLORE("Inventory.Protection-blocks.Manage.Item-lore",
			Arrays.asList("&0", "&8 - &aLeft click with item: &eReplace current item",
					"&8 - &aLeft click without item: &eTake current item")),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_ITEMNOTSETLORE("Inventory.Protection-blocks.Manage.Item-not-set-lore",
			Arrays.asList("&0", "&8 - &aLeft click with item: &eSet as current item")),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_BLOCKSXLORE("Inventory.Protection-blocks.Manage.Blocks-x-lore",
			Arrays.asList("&0", "&8 - &aLeft click: &eIncrease two blocks", "&8 - &aRight click: &eDecrease two blocks",
					"&8 - &aLeft click + Shift: &eSpecify current blocks")),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_BLOCKSYLORE("Inventory.Protection-blocks.Manage.Blocks-y-lore",
			Arrays.asList("&0", "&8 - &aLeft click: &eIncrease two blocks", "&8 - &aRight click: &eDecrease two blocks",
					"&8 - &aLeft click + Shift: &eSpecify current blocks")),
	INVENTORY_PROTECTIONBLOCKS_MANAGE_BLOCKSZLORE("Inventory.Protection-blocks.Manage.Blocks-z-lore",
			Arrays.asList("&0", "&8 - &aLeft click: &eIncrease two blocks", "&8 - &aRight click: &eDecrease two blocks",
					"&8 - &aLeft click + Shift: &eSpecify current blocks"));

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
