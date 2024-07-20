package company.pluginName.Bukkit.Inventories.ProtectionBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import company.pluginName.Bukkit.Inventories.ProtectionBlocks.ProtectionBlockRecipeInventory.SimpleRecipe;
import company.pluginName.Bukkit.Inventories.ProtectionBlocks.BannedWorlds.ProtectionBlockAllowedWorldsInventory;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Reference.ReferencedProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.RecipesPckg.RecipesService;
import company.pluginName.Modules.RecipesPckg.Objects.Recipe;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Defaults.Messages.Events.MessagesListener;
import darkpanda73.PandaUtils.PandaPlugin.Defaults.Messages.Exceptions.PlayerAlreadyListeningException;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Annotations.ItemGenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.ChestInventoryObject;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.GeneratedItem;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.Objects.ChestInventory.Item;
import relampagorojo93.LibsCollection.Utils.Bukkit.Enums.Material;
import relampagorojo93.LibsCollection.Utils.Shared.Java.StringsHelper;

@Inventory("protectionblocks_manage")
public class ProtectionBlockManagerInventory extends ChestInventoryObject {

	private static final String PROTECTIONBLOCKITEM_NOTSETITEM_PATH = "Not-set-item";
	private static final String PROTECTIONBLOCKITEM_REPLACEITEMLORELINE_PATH = "Replace-item-lore-line";
	private static final String PROTECTIONBLOCKITEM_TAKEITEMLORELINE_PATH = "Take-item-lore-line";
	private static final String IDBUTTON_NOTSETITEM_PATH = "Not-set-item";
	private static final String IDBUTTON_NOTMODIFIABLEITEM_PATH = "Not-modifiable-item";
	private static final String PERMISSIONBUTTON_NOTSETITEM_PATH = "Not-set-item";
	private static final String PRICEBUTTON_NOTSETITEM_PATH = "Not-set-item";

	private static final String MESSAGES_IDSPECIFYINFO_PATH = "Messages.Id-specify-info";
	private static final String MESSAGES_PERMISSIONSPECIFYINFO_PATH = "Messages.Permission-specify-info";
	private static final String MESSAGES_BLOCKSSPECIFYINFO_PATH = "Messages.Blocks-specify-info";
	private static final String MESSAGES_PRICESPECIFYINFO_PATH = "Messages.Price-specify-info";

	@PandaInject
	private static MessagesListener messagesListener;

	@PandaInject
	private static RecipesService recipesService;

	@PandaInject
	private static ProtectionsService protectionsService;

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	private ProtectionBlock originalProtectionBlock;
	private ProtectionBlock copyOriginalProtectionBlock;
	private ProtectionBlock newProtectionBlock;

	private SimpleRecipe originalRecipe;
	private SimpleRecipe newRecipe;

	public ProtectionBlockManagerInventory(Player player) {
		this(player, null);
	}

	public ProtectionBlockManagerInventory(Player player, ProtectionBlock protectionBlock) {
		super(player);
		this.originalProtectionBlock = protectionBlock;
		this.copyOriginalProtectionBlock = protectionBlock != null ? new ProtectionBlock(originalProtectionBlock)
				: null;
		this.newProtectionBlock = protectionBlock != null ? new ProtectionBlock(originalProtectionBlock)
				: new ProtectionBlock();

		Recipe recipe = originalProtectionBlock != null
				? recipesService.findRecipeByProtectionBlock(originalProtectionBlock)
				: null;
		if (recipe != null) {
			this.originalRecipe = new SimpleRecipe();
			for (int i = 0; i < 9; i++) {
				this.originalRecipe.getRecipe()[i] = recipe.getRecipe()[i] != null ? recipe.getRecipe()[i].clone()
						: null;
			}
			this.originalRecipe.setPermission(recipe.getPermission());
		}
	}

	@Override
	protected String getTitle() {
		return MessageTemplate.inst(super.getTitle())
				.setReplacements(new Replacement("{block}",
						() -> newProtectionBlock.getInformation().getId() != null
								? newProtectionBlock.getInformation().getId()
								: "???"))
				.process().toString();
	}

	@ItemGenerator("Protection-block-item")
	private ItemStack generateProtectionBlockItem(Item item) {
		if (newProtectionBlock.getInformation().getItem() != null) {
			ItemBuilder builder = ItemBuilder.inst().fromItem(newProtectionBlock.getInformation().getItem());

			List<String> lore = builder.getLore();
			lore.add("&0");
			lore.add(item.getData().get(PROTECTIONBLOCKITEM_REPLACEITEMLORELINE_PATH).toString());
			lore.add(item.getData().get(PROTECTIONBLOCKITEM_TAKEITEMLORELINE_PATH).toString());
			builder.setLore(lore);

			return builder.apply(newProtectionBlock.getInformation().getItem().clone());
		} else {
			return ItemBuilder.inst().fromMap(item.getData(), PROTECTIONBLOCKITEM_NOTSETITEM_PATH).build();
		}
	}

	@ItemGenerator("Blocks-x-button")
	private ItemStack generateBlocksXButton(Item item) {
		return ItemBuilder.inst().fromMap(item.getData(), Item.DISPLAYITEM_KEY)
				.setReplacements(new Replacement("{blocks_x}",
						() -> String.valueOf((newProtectionBlock.getInformation().getBlocksX() * 2) + 1)))
				.build();
	}

	@ItemGenerator("Blocks-y-button")
	private ItemStack generateBlocksYButton(Item item) {
		return ItemBuilder.inst().fromMap(item.getData(), Item.DISPLAYITEM_KEY)
				.setReplacements(new Replacement("{blocks_y}",
						() -> newProtectionBlock.getInformation().getBlocksY() == -1
								? Messages.MESSAGE_GENERAL_NOLIMIT.toString()
								: String.valueOf((newProtectionBlock.getInformation().getBlocksY() * 2) + 1)))
				.build();
	}

	@ItemGenerator("Blocks-z-button")
	private ItemStack generateBlocksZButton(Item item) {
		return ItemBuilder.inst().fromMap(item.getData(), Item.DISPLAYITEM_KEY)
				.setReplacements(new Replacement("{blocks_z}",
						() -> String.valueOf((newProtectionBlock.getInformation().getBlocksZ() * 2) + 1)))
				.build();
	}

	@ItemGenerator("Id-button")
	private ItemStack generateIdButton(Item item) {
		return ItemBuilder.inst()
				.fromMap(item.getData(),
						(this.originalProtectionBlock != null ? IDBUTTON_NOTMODIFIABLEITEM_PATH
								: (this.newProtectionBlock.getInformation().getId() != null ? Item.DISPLAYITEM_KEY
										: IDBUTTON_NOTSETITEM_PATH)))
				.setReplacements(new Replacement("{block_id}",
						() -> newProtectionBlock.getInformation().getId() != null
								? newProtectionBlock.getInformation().getId()
								: ""))
				.build();
	}

	@ItemGenerator("Permission-button")
	private ItemStack generatePermissionButton(Item item) {
		return ItemBuilder.inst()
				.fromMap(item.getData(),
						(this.newProtectionBlock.getInformation().getPermission() != null ? Item.DISPLAYITEM_KEY
								: PERMISSIONBUTTON_NOTSETITEM_PATH))
				.setReplacements(new Replacement("{block_permission}",
						() -> newProtectionBlock.getInformation().getPermission() != null
								? newProtectionBlock.getInformation().getPermission()
								: ""))
				.build();
	}

	@ItemGenerator("Price-button")
	private ItemStack generatePriceButton(Item item) {
		return ItemBuilder.inst().fromMap(item.getData(),
				(this.newProtectionBlock.getInformation().getPrice() != null ? Item.DISPLAYITEM_KEY
						: PRICEBUTTON_NOTSETITEM_PATH))
				.setReplacements(new Replacement("{block_price}",
						() -> newProtectionBlock.getInformation().getPrice() != null
								? StringsHelper.toCurrency(newProtectionBlock.getInformation().getPrice())
								: "---"))
				.build();
	}

	@ItemExecutor("Cancel-button")
	private void executeCancelButton() {
		goToPreviousHolder();
	}

	@ItemExecutor("Confirm-button")
	private void executeConfirmButton() {
		List<Protection> relatedProtections = new ArrayList<>();
		try {
			if (originalProtectionBlock != null) {
				protectionsService.getProtectionsByWorld().values().forEach(protections -> protections.stream()
						.filter(protection -> protection.getProtectionBlock().getIdentifier()
								.equals(originalProtectionBlock.getInformation().getId())
								&& protection.getUtils().isProtectionBlockShown())
						.forEach(relatedProtections::add));

				relatedProtections.forEach(prot -> prot.getUtils().hideProtectionBlock());

				originalProtectionBlock.copy(newProtectionBlock);
				originalProtectionBlock.save(getPlayer());
			} else {
				newProtectionBlock.save(getPlayer());
			}

			if (newRecipe != null) {
				Recipe recipeToModify = recipesService.findRecipeByProtectionBlock(newProtectionBlock);
				if (recipeToModify == null) {
					recipeToModify = new Recipe(
							new ReferencedProtectionBlock(newProtectionBlock.getInformation().getId()));
				}
				for (int i = 0; i < 9; i++) {
					recipeToModify.getRecipe()[i] = newRecipe.getRecipe()[i];
				}
				recipeToModify.setPermission(newRecipe.getPermission());
				recipeToModify.save(getPlayer());
			}

			if (originalProtectionBlock != null) {
				MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_BLOCKS_SAVEDSUCCESSFULLY.applyPrefix()).process()
						.sendMessage(getPlayer());
			} else {
				MessageTemplate.inst(Messages.MESSAGE_PROTECTIONS_BLOCKS_CREATEDSUCCESSFULLY.applyPrefix()).process()
						.sendMessage(getPlayer());
			}
			goToPreviousHolder();
		} catch (RoyaleProtectionBlocksExceptionImpl ex) {
			if (originalProtectionBlock != null) {
				originalProtectionBlock.copy(copyOriginalProtectionBlock);
			}
			ex.sendError(getPlayer());
		} finally {
			relatedProtections.forEach(prot -> {
				prot.getUtils().showProtectionBlock();

				try {
					prot.getUtils().regenerateProtectionArea();
					if (!prot.getProtectedRegion().getMinimumPoint()
							.equals(worldGuardApi.getHook().getInternalWorldGuard()
									.asBlockVector(prot.getUtils().getProtectionArea().getMinLocation().toLocation()))
							|| !prot.getProtectedRegion().getMaximumPoint()
									.equals(worldGuardApi.getHook().getInternalWorldGuard().asBlockVector(
											prot.getUtils().getProtectionArea().getMaxLocation().toLocation()))) {
						prot.regenerateProtectedRegion();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}

	}

	@ItemExecutor("Protection-block-item")
	private void executeProtectionBlockItem(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		try {
			if (e.getClick() == ClickType.LEFT) {
				if (e.getCursor() != null && e.getCursor().getType() != Material.AIR.getMaterial()) {
					newProtectionBlock.getInformation().setItem(e.getCursor().clone());
					e.getWhoClicked().getOpenInventory().setCursor(null);
					updateInventory();
				} else if (newProtectionBlock.getInformation().getItem() != null) {
					e.getWhoClicked().getOpenInventory().setCursor(newProtectionBlock.getInformation().getItem());
					newProtectionBlock.getInformation().setItem(null);
					updateInventory();
				}
			}
		} catch (RoyaleProtectionBlocksExceptionImpl ex) {
			ex.sendError(getPlayer());
		}
	}

	@ItemExecutor("Blocks-x-button")
	private void executeBlocksXButton(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		setBlocks(e, () -> newProtectionBlock.getInformation().getBlocksX(),
				(blocks) -> newProtectionBlock.getInformation().setBlocksX(blocks));
	}

	@ItemExecutor("Blocks-y-button")
	private void executeBlocksYButton(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		setBlocks(e, () -> newProtectionBlock.getInformation().getBlocksY(),
				(blocks) -> newProtectionBlock.getInformation().setBlocksY(blocks), true);
	}

	@ItemExecutor("Blocks-z-button")
	private void executeBlocksZButton(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		setBlocks(e, () -> newProtectionBlock.getInformation().getBlocksZ(),
				(blocks) -> newProtectionBlock.getInformation().setBlocksZ(blocks));
	}

	@ItemExecutor("Allowed-worlds-button")
	private void executeAllowedWorldsButton() {
		new ProtectionBlockAllowedWorldsInventory(getPlayer(), newProtectionBlock).openInventory();
	}

	@ItemExecutor("Id-button")
	private void executeIdButton() {
		if (originalProtectionBlock == null) {
			try {
				messagesListener.startListening(getPlayer().getUniqueId(), (message) -> {
					if (!message.equalsIgnoreCase("cancel")) {
						newProtectionBlock.getInformation().setId(!message.isEmpty() ? message : null);
					}
					openInventory();
					return true;
				});
				closeInventory();
				MessageTemplate
						.inst(PandaPrefixedStringField.applyPrefix(
								getChestInventoryData().getCustomFields().get(MESSAGES_IDSPECIFYINFO_PATH).toString()))
						.process().sendMessage(getPlayer());
			} catch (PlayerAlreadyListeningException e1) {
				MessageTemplate.inst(Messages.ERROR_CHATPROMPT_ALREADYPROMPTED.applyPrefix()).process()
						.sendMessage(getPlayer());
			}
		}
	}

	@ItemExecutor("Recipe-button")
	private void executeRecipeButton() {
		new ProtectionBlockRecipeInventory(getPlayer(), newProtectionBlock,
				newRecipe != null ? newRecipe : originalRecipe, (recipe) -> newRecipe = recipe).openInventory();
	}

	@ItemExecutor("Permission-button")
	private void executePermissionButton(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		if (e.getClick() == ClickType.LEFT) {
			try {
				messagesListener.startListening(getPlayer().getUniqueId(), (message) -> {
					if (!message.equalsIgnoreCase("cancel")) {
						newProtectionBlock.getInformation().setPermission(!message.isEmpty() ? message : null);
					}
					openInventory();
					return true;
				});
				closeInventory();
				MessageTemplate
						.inst(PandaPrefixedStringField.applyPrefix(getChestInventoryData().getCustomFields()
								.get(MESSAGES_PERMISSIONSPECIFYINFO_PATH).toString()))
						.process().sendMessage(e.getWhoClicked());
			} catch (PlayerAlreadyListeningException e1) {
				MessageTemplate.inst(Messages.ERROR_CHATPROMPT_ALREADYPROMPTED.applyPrefix()).process()
						.sendMessage(e.getWhoClicked());
			}
		} else if (e.getClick() == ClickType.RIGHT && newProtectionBlock.getInformation().getPermission() != null) {
			newProtectionBlock.getInformation().setPermission(null);
			updateInventory();
		}
	}

	@ItemExecutor("Price-button")
	private void executePriceButton(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		if (e.getClick() == ClickType.LEFT) {
			try {
				messagesListener.startListening(getPlayer().getUniqueId(), (message) -> {
					if (!message.equalsIgnoreCase("cancel")) {
						try {
							double price = Double.parseDouble(message);

							if (price <= 0D) {
								newProtectionBlock.getInformation().setPrice(null);
							} else {
								newProtectionBlock.getInformation().setPrice(price);
							}
						} catch (NumberFormatException e1) {
							MessageTemplate.inst(Messages.ERROR_INVALIDNUMBER.applyPrefix()).process()
									.sendMessage(e.getWhoClicked());
						}
					}
					openInventory();
					return true;
				});
				closeInventory();
				MessageTemplate
						.inst(PandaPrefixedStringField.applyPrefix(getChestInventoryData().getCustomFields()
								.get(MESSAGES_PRICESPECIFYINFO_PATH).toString()))
						.process().sendMessage(e.getWhoClicked());
			} catch (PlayerAlreadyListeningException e1) {
				MessageTemplate.inst(Messages.ERROR_CHATPROMPT_ALREADYPROMPTED.applyPrefix()).process()
						.sendMessage(e.getWhoClicked());
			}
		} else if (e.getClick() == ClickType.RIGHT && newProtectionBlock.getInformation().getPermission() != null) {
			newProtectionBlock.getInformation().setPrice(null);
			updateInventory();
		}
	}

	public void setBlocks(InventoryClickEvent e, IntSupplier getBlocks, IntConsumer setBlocks) {
		this.setBlocks(e, getBlocks, setBlocks, false);
	}

	public void setBlocks(InventoryClickEvent e, IntSupplier getBlocks, IntConsumer setBlocks, boolean allowUnlimited) {
		int cur = getBlocks.getAsInt();
		if (e.getClick() == ClickType.SHIFT_LEFT) {
			try {
				messagesListener.startListening(getPlayer().getUniqueId(), (message) -> {
					if (!message.equalsIgnoreCase("cancel")) {
						try {
							int value = Integer.parseInt(message);

							if (value < 0 && (value != -1 || !allowUnlimited)) {
								MessageTemplate.inst(Messages.ERROR_NUMBERBELOWZERO.applyPrefix()).process()
										.sendMessage(e.getWhoClicked());
								return true;
							}

							setBlocks.accept(value == -1 ? value : value / 2);
						} catch (NumberFormatException e1) {
							MessageTemplate.inst(Messages.ERROR_INVALIDNUMBER.applyPrefix()).process()
									.sendMessage(e.getWhoClicked());
						}
					}
					openInventory();
					return true;
				});
				closeInventory();
				MessageTemplate
						.inst(PandaPrefixedStringField.applyPrefix(getChestInventoryData().getCustomFields()
								.get(MESSAGES_BLOCKSSPECIFYINFO_PATH).toString()))
						.process().sendMessage(e.getWhoClicked());
			} catch (PlayerAlreadyListeningException e1) {
				MessageTemplate.inst(Messages.ERROR_CHATPROMPT_ALREADYPROMPTED.applyPrefix()).process()
						.sendMessage(e.getWhoClicked());
			}
		} else if (e.getClick() == ClickType.LEFT) {
			setBlocks.accept(++cur);
			updateInventory();
		} else if (e.getClick() == ClickType.RIGHT && (cur > (allowUnlimited ? -1 : 0))) {
			setBlocks.accept(--cur);
			updateInventory();
		}
	}

}
