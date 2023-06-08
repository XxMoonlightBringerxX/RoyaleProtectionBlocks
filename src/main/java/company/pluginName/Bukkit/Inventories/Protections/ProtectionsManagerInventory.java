package company.pluginName.Bukkit.Inventories.Protections;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Bukkit.Inventories.Protections.Flags.ProtectionFlagsInventory;
import company.pluginName.Bukkit.Inventories.Protections.Members.ProtectionMembersInventory;
import company.pluginName.Bukkit.Inventories.Protections.Owners.ProtectionOwnersInventory;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveException;
import company.pluginName.Modules.FilePckg.Messages.MessageString;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import relampagorojo93.LibsCollection.SpigotMessages.NMS.MessageBuilder;
import relampagorojo93.LibsCollection.SpigotMessages.Objects.TextInput;
import relampagorojo93.LibsCollection.SpigotMessages.Objects.TextReplacement;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Button;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;
import relampagorojo93.LibsCollection.Utils.Bukkit.Messages.Exceptions.PlayerAlreadyListeningException;

public class ProtectionsManagerInventory extends PluginChestInventory {

	private Protection protection;

	public ProtectionsManagerInventory(Player player, Protection protection) {
		super(player);

		this.protection = protection;

		setSize(27);
	}

	@Override
	public Inventory getInventory() {
		setName(MessageBuilder
				.createMessage(TextInput.inst().text(MessageString.INVENTORY_PROTECTION_TITLE.toString())
						.replacements(new TextReplacement("{protection}", () -> protection.getDisplayName())))
				.toString());
		return super.getInventory();
	}

	@Override
	public void updateContent() {
		clearSlots();

		for (int i = 0; i < 27; i++) {
			setSlot(i, GRAY_STAINED_GLASS_PANE);
		}

		if (getPreviousHolder() != null && !(getPreviousHolder() instanceof ProtectionsManagerInventory)) {
			setSlot(18, new Button(CLOSE_ITEM) {
				@Override
				public void onClick(InventoryClickEvent e) {
					goToPreviousHolder();
				}
			});
		}

		setSlot(11, new Button(ItemStacksUtils.createItemStack(Material.ENDER_CHEST,
				MessageBuilder.createMessage(MessageString.INVENTORY_PROTECTION_OWNERSNAME.toString()).toString())) {

			@Override
			public void onClick(InventoryClickEvent e) {
				new ProtectionOwnersInventory(getPlayer(), protection).setPreviousHolder(getHolder())
						.openInventory(MainPluginClass.getPlugin());
			}

		});

		setSlot(12, new Button(ItemStacksUtils.createItemStack(Material.CHEST,
				MessageBuilder.createMessage(MessageString.INVENTORY_PROTECTION_MEMBERSNAME.toString()).toString())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				new ProtectionMembersInventory(getPlayer(), protection).setPreviousHolder(getHolder())
						.openInventory(MainPluginClass.getPlugin());
			}
		});

		setSlot(14, new Button(ItemStacksUtils.createItemStack(Material.COMMAND_BLOCK,
				MessageBuilder.createMessage(MessageString.INVENTORY_PROTECTION_FLAGSNAME.toString()).toString())) {
			@Override
			public void onClick(InventoryClickEvent e) {
				new ProtectionFlagsInventory(getPlayer(), protection).setPreviousHolder(getHolder())
						.openInventory(MainPluginClass.getPlugin());
			}
		});

		setSlot(15, new Button(ItemStacksUtils.createItemStack(Material.NAME_TAG,
				MessageBuilder.createMessage(MessageString.INVENTORY_PROTECTION_RENAMENAME.toString()).toString())) {

			@Override
			public void onClick(InventoryClickEvent e) {
				try {
					MainPluginClass.getPlugin().getMessagesListener().startListening(e.getWhoClicked().getUniqueId(),
							(message) -> {
								if (!message.equalsIgnoreCase("cancel")) {
									try {
										MainPluginClass.getPlugin().getProtectionsModule().renameProtection(getPlayer(),
												protection, message);
										MessageBuilder.createMessage(
												MessageString.MESSAGE_PROTECTIONS_RENAMEDSUCCESSFULLY.applyPrefix())
												.sendMessage(getPlayer());
									} catch (ProtectionSaveException e1) {
										e1.sendError(getPlayer());
									}
								}
								openInventory();
								return true;
							});
					closeInventory();
					MessageBuilder.createMessage(MessageString.INVENTORY_PROTECTION_TYPENEWNAMEINFO.applyPrefix())
							.sendMessage(e.getWhoClicked());
				} catch (PlayerAlreadyListeningException ex) {
					MessageBuilder.createMessage(MessageString.ERROR_CHATPROMPT_ALREADYPROMPTED.toString())
							.sendMessage(e.getWhoClicked());
				}
			}
		});
	}

}