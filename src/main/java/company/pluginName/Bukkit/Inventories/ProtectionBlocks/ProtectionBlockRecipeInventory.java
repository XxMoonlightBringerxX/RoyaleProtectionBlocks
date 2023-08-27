package company.pluginName.Bukkit.Inventories.ProtectionBlocks;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.MainPluginClass;
import company.pluginName.Bukkit.Inventories.Abstracts.PluginChestInventory;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.Objects.ReferencedObjects.ReferencedProtectionBlock;
import company.pluginName.Modules.RecipesPckg.Objects.Recipe;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Objects.TextInput;
import darkpanda73.PandaUtils.PandaColors.Objects.TextReplacement;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Button;
import relampagorojo93.LibsCollection.Utils.Bukkit.Inventories.Objects.Modifiable;
import relampagorojo93.LibsCollection.Utils.Bukkit.ItemStacks.ItemStacksUtils;
import relampagorojo93.LibsCollection.Utils.Bukkit.Messages.Exceptions.PlayerAlreadyListeningException;

public class ProtectionBlockRecipeInventory extends PluginChestInventory {

	private Recipe originalRecipe;
	private Recipe newRecipe;

	public ProtectionBlockRecipeInventory(Player pl, ProtectionBlock protectionBlock, Consumer<Recipe> onRecipeUpdate) {
		super(pl);

		this.originalRecipe = MainPluginClass.getPlugin().getRecipesModule()
				.findRecipeByProtectionBlock(protectionBlock);
		this.newRecipe = new Recipe(new ReferencedProtectionBlock(protectionBlock.getInformation().getId()));

		if (this.originalRecipe != null) {
			this.newRecipe.copy(this.originalRecipe);
		}

		setName(MessageBuilder.createMessage(
				TextInput.inst().text(MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_RECIPE_TITLE.toString())
						.replacements(new TextReplacement("{block}",
								() -> protectionBlock.getInformation().getId() != null
										? protectionBlock.getInformation().getId()
										: "???")))
				.toString());
		setSize(45);

		for (int i = 0; i < getSize(); i++) {
			setSlot(i, GRAY_STAINED_GLASS_PANE);
		}

		for (int i = 0; i < 9; i++) {
			ItemStack it = null;

			if (this.newRecipe.getRecipe() != null) {
				it = newRecipe.getRecipe()[i];
			}

			setSlot(11 + i + i / 3 * 6, new Modifiable(it) {
				@Override
				public void onModify(InventoryClickEvent e) {
				}
			});
		}

		setSlot(36, new Button(CANCEL_ITEM) {
			@Override
			public void onClick(InventoryClickEvent e) {
				goToPreviousHolder();
			}
		});

		setSlot(44, new Button(CONFIRM_ITEM) {
			@Override
			public void onClick(InventoryClickEvent e) {
				for (int j = 0; j < 9; j++) {
					newRecipe.getRecipe()[j] = e.getInventory().getItem(11 + j + j / 3 * 6);
				}

				onRecipeUpdate.accept(newRecipe);

				goToPreviousHolder();
			}
		});
	}

	@Override
	public void updateContent() {
		MessageString permissionName = newRecipe.getPermission() != null
				? MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_RECIPE_PERMISSIONNAME
				: MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_RECIPE_PERMISSIONNOTSETNAME;

		setSlot(24,
				new Button(ItemStacksUtils.createItemStack(Material.PAPER, MessageBuilder
						.createMessage(TextInput.inst().text(permissionName.toString())
								.replacements(new TextReplacement("{block_permission}",
										() -> newRecipe.getPermission() != null ? newRecipe.getPermission() : "")))
						.toString())) {
					@Override
					public void onClick(InventoryClickEvent e) {
						if (e.getClick() == ClickType.LEFT) {
							try {
								MainPluginClass.getPlugin().getMessagesListener()
										.startListening(getPlayer().getUniqueId(), (message) -> {
											if (!message.equalsIgnoreCase("cancel")) {
												newRecipe.setPermission(!message.isEmpty() ? message : null);
											}
											openInventory();
											return true;
										});
								closeInventory();
								MessageBuilder.createMessage(
										MessageString.INVENTORY_PROTECTIONBLOCKS_MANAGE_RECIPE_PERMISSIONSPECIFYINFO
												.applyPrefix())
										.sendMessage(e.getWhoClicked());
							} catch (PlayerAlreadyListeningException e1) {
								MessageBuilder
										.createMessage(MessageString.ERROR_CHATPROMPT_ALREADYPROMPTED.applyPrefix())
										.sendMessage(e.getWhoClicked());
							}
						} else if (e.getClick() == ClickType.RIGHT && newRecipe.getPermission() != null) {
							newRecipe.setPermission(null);
							updateInventory();
						}
					}
				});
	}

}