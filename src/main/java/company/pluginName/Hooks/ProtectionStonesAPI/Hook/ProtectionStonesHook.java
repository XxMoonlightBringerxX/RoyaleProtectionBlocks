package company.pluginName.Hooks.ProtectionStonesAPI.Hook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;

import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.FilePckg.Messages;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Components.ProtectionBlockInformation;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsServiceImpl;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.Java.StringsHelper;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import dev.espi.protectionstones.FlagHandler;
import dev.espi.protectionstones.PSGroupRegion;
import dev.espi.protectionstones.PSMergedRegion;
import dev.espi.protectionstones.PSProtectBlock;
import dev.espi.protectionstones.PSRegion;
import dev.espi.protectionstones.ProtectionStones;
import lombok.Data;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.CreationCause;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException;
import royale.RoyaleProtectionBlocks.Plugin.API.Exceptions.RoyaleProtectionBlocksException.Type;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.Protections.Objects.ProtectionCreationData;

public class ProtectionStonesHook extends PandaAbstractHook {

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private static ProtectionsServiceImpl protectionsService;

	@Override
	public void load() throws Throwable {
		Class.forName("dev.espi.protectionstones.ProtectionStones");

		if (Bukkit.getPluginManager().isPluginEnabled("ProtectionStones")) {
			this.hooked = true;
		}

	}

	@Override
	public void unload() throws Throwable {
	}

	public TransferResult<ProtectionBlock> transferProtectionBlocks() {
		TransferResult<ProtectionBlock> transferResult = new TransferResult<>(0);

		if (isHooked()) {
			transferResult.setTotalAmount(ProtectionStones.getInstance().getConfiguredBlocks().size());

			ProtectionStones.getInstance().getConfiguredBlocks().forEach(pb -> {
				if (protectionBlocksService.getProtectionBlockById(pb.alias) == null) {
					try {
						ProtectionBlock block = new ProtectionBlock(new ProtectionBlockInformation(pb.alias,
								pb.createItem(), pb.xRadius, pb.yRadius, pb.zRadius,
								pb.permission != null && !pb.permission.isEmpty() ? pb.permission : null, pb.price));

						if (pb.worlds.size() > 0 && pb.worldListType != null && !pb.worldListType.isEmpty()) {
							Bukkit.getWorlds().stream()
									.filter(world -> pb.worldListType.equalsIgnoreCase("whitelist") == pb.worlds
											.contains(world.getName()))
									.map(World::getName).forEach(block.getBlockAllowedWorlds()::add);
						}

						block.save();

						transferResult.addSuccess();
					} catch (RoyaleProtectionBlocksExceptionImpl e) {
						MessageTemplate
								.inst(PandaPrefixedStringField.applyPrefix(
										String.format("&cUnable to create protection block using config from '%s': %s",
												pb.alias, e.getMessage())))
								.process().sendMessage(Bukkit.getConsoleSender());
						e.printStackTrace();
						transferResult.addException();
						transferResult.setErrorsInConsole(true);
					}
				} else {
					transferResult.addProcessed();
				}
			});

			MessageTemplate.inst(Messages.MESSAGE_TRANSFER_PROTECTIONBLOCKSPROGRESS.applyPrefix())
					.setReplacements(new Replacement("{current_percent}",
							() -> StringsHelper.toPercentage(
									(transferResult.getProcessedAmount() / transferResult.getTotalAmount()), 1)))
					.process().sendMessage(Bukkit.getConsoleSender());

			return transferResult;
		}

		return transferResult;
	}

	public TransferResult<Protection> transferProtections() {
		TransferResult<Protection> transferResult = new TransferResult<>(0);

		if (isHooked()) {
			Map<World, RegionManager> regionManagers = new HashMap<>();
			Set<PSRegion> regionsToTransfer = new HashSet<>();

			Bukkit.getWorlds().stream().forEach(world -> {
				try {
					regionManagers
							.computeIfAbsent(world,
									(key) -> worldGuardApi.getHook().getInternalWorldGuard()
											.getRegionManagerSafely(world))
							.getRegions().entrySet().stream()
							.filter((entry) -> ProtectionStones.isPSRegion(entry.getValue()))
							.forEach((entry) -> ProtectionStones.getPSRegions(world, entry.getKey()).stream()
									.findFirst().ifPresent(regionsToTransfer::add));
				} catch (Throwable e) {
					MessageTemplate
							.inst(PandaPrefixedStringField.applyPrefix(String.format(
									"&cUnable to process regions for world '%s': %s", world.getName(), e.getMessage())))
							.process().sendMessage(Bukkit.getConsoleSender());
					e.printStackTrace();
					transferResult.setErrorsInConsole(true);
				}
			});

			transferResult.setTotalAmount(regionsToTransfer.size());

			TasksUtils.executeOnAsync(() -> {
				List<PSRegion> regionsToRemoveOnTask = new ArrayList<>();

				regionsToTransfer.removeIf(regionToTransfer -> {
					try {
						transferRegion(transferResult, regionToTransfer);

						regionsToRemoveOnTask.add(regionToTransfer);

						if (regionsToRemoveOnTask.size() >= 10 || regionsToTransfer.size() == 1) {
							FutureTask<Void> task = new FutureTask<Void>(() -> {
								regionsToRemoveOnTask.forEach(region -> {
									region.deleteRegion(false);
								});
								regionsToRemoveOnTask.clear();
								return null;
							});

							TasksUtils.execute(task::run);

							try {
								task.get();

								Thread.sleep(10);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					return true;
				});
			});

			Replacement replacement = new Replacement("{current_percent}",
					() -> StringsHelper.toPercentage(
							((float) transferResult.getProcessedAmount() / (float) transferResult.getTotalAmount()),
							1));
			replacement.getMessageFragment().setCacheText(false);
			MessageTemplate template = MessageTemplate.inst(Messages.MESSAGE_TRANSFER_PROTECTIONSPROGRESS.applyPrefix())
					.setReplacements(replacement);

			while (!regionsToTransfer.isEmpty()) {
				try {
					Thread.sleep(1000);

					template.process().sendMessage(Bukkit.getConsoleSender());
				} catch (InterruptedException e) {
				}
			}
		}
		return transferResult;
	}

	private void transferRegion(TransferResult<Protection> transferResult, PSRegion protectionStonesRegion) {
		Map<Flag<?>, Object> flags = new HashMap<>(protectionStonesRegion.getWGRegion().getFlags());

		List<UUID> owners = protectionStonesRegion.getOwners();
		List<UUID> members = protectionStonesRegion.getMembers();

		String displayName = protectionStonesRegion.getName();
		Location home = protectionStonesRegion.getHome();

		flags.keySet().removeIf(flag -> flag.getName().startsWith("ps-") || flag == FlagHandler.GREET_ACTION
				|| flag == FlagHandler.FAREWELL_ACTION);

		List<PSRegion> regions = new ArrayList<>();

		if (protectionStonesRegion instanceof PSGroupRegion) {
			((PSGroupRegion) protectionStonesRegion).getMergedRegions().forEach(regions::add);
			transferResult.setTotalAmount(transferResult.getTotalAmount() + (regions.size() - 1));
		} else {
			regions.add(protectionStonesRegion);
		}

		PSProtectBlock protectBlock = ProtectionStones.getBlockOptions(protectionStonesRegion.getType());

		if (protectBlock != null) {
			AtomicBoolean exception = new AtomicBoolean(false);

			regions.forEach(region -> {
				ProtectionBlock protectionBlock = protectionBlocksService.getProtectionBlockById(protectBlock.alias);

				if (protectionBlock != null) {
					try {

						Protection protection = protectionsService.create(ProtectionCreationData
								.inst(Bukkit.getConsoleSender(), owners.get(0), protectionBlock,
										region.getProtectBlock().getLocation(), CreationCause.IMPORT)
								.setCheckOverlap(false).setCheckWorldGuardOverlap(false));

						if (displayName != null) {
							protection.setDisplayName(displayName);
						}

						if (!(protectionStonesRegion instanceof PSMergedRegion)) {
							try {
								protection.setHome(home);
							} catch (Exception e) {
								MessageTemplate
										.inst(PandaPrefixedStringField.applyPrefix(
												String.format("&cUnable to set home for protection '%s': %s",
														protection.getProtectionId(), e.getMessage())))
										.process().sendMessage(Bukkit.getConsoleSender());
								e.printStackTrace();
								transferResult.setErrorsInConsole(true);
							}
						}

						owners.forEach(owner -> {
							try {
								protection.addOwnerAndSave(owner);
							} catch (RoyaleProtectionBlocksException e) {
								if (e.getType() != Type.PROTECTIONS_OWNERS_SAVE_CANNOTADDPROTECTIONOWNER) {
									MessageTemplate
											.inst(PandaPrefixedStringField.applyPrefix(
													String.format("&cUnable to add owner on protection '%s': %s",
															protection.getProtectionId(), e.getMessage())))
											.process().sendMessage(Bukkit.getConsoleSender());
									e.printStackTrace();
									transferResult.setErrorsInConsole(true);
								}
							}
						});

						members.forEach(member -> {
							try {
								protection.addMemberAndSave(member);
							} catch (RoyaleProtectionBlocksException e) {
								if (e.getType() != Type.PROTECTIONS_MEMBERS_SAVE_CANNOTADDPROTECTIONOWNER) {
									MessageTemplate
											.inst(PandaPrefixedStringField.applyPrefix(
													String.format("&cUnable to add member on protection '%s': %s",
															protection.getProtectionId(), e.getMessage())))
											.process().sendMessage(Bukkit.getConsoleSender());
									e.printStackTrace();
									transferResult.setErrorsInConsole(true);
								}
							}
						});
					} catch (RoyaleProtectionBlocksException e) {
						if (e.getType() != Type.PROTECTIONS_SAVE_CANCELLED) {
							MessageTemplate
									.inst(PandaPrefixedStringField.applyPrefix(
											String.format("&cUnable to save information for protection '%s': %s",
													region.getId(), e.getMessage())))
									.process().sendMessage(Bukkit.getConsoleSender());
							e.printStackTrace();
						}

						exception.set(true);
						transferResult.setErrorsInConsole(true);
					}
				}
			});

			if (!exception.get()) {
				regions.forEach(region -> {
					if (region.getParent() != null) {
						Protection childProtection = protectionsService.findProtectionById(region.getId());
						if (childProtection != null) {
							Protection protection = protectionsService
									.findProtectionById(protectionStonesRegion.getId());
							if (protection != null) {
								try {
									childProtection.setParentProtectionAndSave(protection);
								} catch (RoyaleProtectionBlocksExceptionImpl e) {
									e.sendError(Bukkit.getConsoleSender());
									transferResult.setErrorsInConsole(true);
								}
							}
						}
					}
				});

				transferResult.addSuccess();
			} else {
				transferResult.addException();
			}
		}
	}

	@Data
	public static class TransferResult<T> {

		private int totalAmount;
		private int processedAmount = 0;
		private int successAmount = 0;
		private int failedAmount = 0;
		private boolean errorsInConsole = false;

		public TransferResult(int totalAmount) {
			this.totalAmount = totalAmount;
		}

		public synchronized void addProcessed() {
			this.processedAmount++;
		}

		public synchronized void addSuccess() {
			this.successAmount++;
			this.processedAmount++;
		}

		public synchronized void addException() {
			this.failedAmount++;
			this.processedAmount++;
		}

	}

}
