package company.pluginName.Hooks.ProtectionStonesAPI.Hook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;

import company.pluginName.Debugger;
import company.pluginName.Debugger.MessageType;
import company.pluginName.Exceptions.Exceptions;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.ProtectionBlocksPckg.ProtectionBlocksService;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionBlocksPckg.Objects.Components.ProtectionBlockInformation;
import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import dev.espi.protectionstones.FlagHandler;
import dev.espi.protectionstones.PSGroupRegion;
import dev.espi.protectionstones.PSMergedRegion;
import dev.espi.protectionstones.PSProtectBlock;
import dev.espi.protectionstones.PSRegion;
import dev.espi.protectionstones.ProtectionStones;
import lombok.AllArgsConstructor;
import lombok.Data;
import royale.RoyaleProtectionBlocks.Plugin.API.Events.Protection.ProtectionCreationAttemptEvent;

public class ProtectionStonesHook extends PandaAbstractHook {

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	@PandaInject
	private static ProtectionBlocksService protectionBlocksService;

	@PandaInject
	private static ProtectionsService protectionsService;

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
		List<ProtectionBlock> successList = new ArrayList<>();
		List<Throwable> exceptionsList = new ArrayList<>();
		AtomicReference<Boolean> errorsInConsole = new AtomicReference<>(false);

		if (isHooked()) {
			ProtectionStones.getInstance().getConfiguredBlocks().forEach(pb -> {
				if (protectionBlocksService.getProtectionBlockById(pb.alias) == null) {
					try {
						ProtectionBlock block = new ProtectionBlock(new ProtectionBlockInformation(pb.alias,
								pb.createItem(), pb.xRadius, pb.yRadius, pb.zRadius,
								pb.permission != null && !pb.permission.isEmpty() ? pb.permission : null, pb.price));

						if (pb.worlds.size() > 0 && pb.worldListType != null && !pb.worldListType.isBlank()) {
							Bukkit.getWorlds().stream()
									.filter(world -> pb.worldListType.equalsIgnoreCase("whitelist") == pb.worlds
											.contains(world.getName()))
									.map(World::getName).forEach(block.getAllowedWorlds()::add);
						}

						block.save();

						successList.add(block);
					} catch (RoyaleProtectionBlocksExceptionImpl e) {
						MessageTemplate
								.inst(PandaPrefixedStringField.applyPrefix(
										String.format("&cUnable to create protection block using config from '%s': %s",
												pb.alias, e.getMessage())))
								.process().sendMessage(Bukkit.getConsoleSender());
						e.printStackTrace();
						exceptionsList.add(e);
						errorsInConsole.set(true);
					}
				}
			});
		}

		return new TransferResult<ProtectionBlock>(successList, exceptionsList, errorsInConsole.get());
	}

	public TransferResult<Protection> transferProtections() {
		List<Protection> successList = new ArrayList<>();
		List<Throwable> exceptionsList = new ArrayList<>();
		AtomicReference<Boolean> errorsInConsole = new AtomicReference<>(false);

		if (isHooked()) {
			Set<PSRegion> regionsToTransfer = new HashSet<>();

			Bukkit.getWorlds().stream().forEach(world -> {
				try {
					RegionManager regionManager = worldGuardApi.getHook().getInternalWorldGuard()
							.getRegionManager(world);
					regionManager.getRegions().entrySet().stream()
							.filter((entry) -> ProtectionStones.isPSRegion(entry.getValue()))
							.forEach((entry) -> ProtectionStones.getPSRegions(world, entry.getKey()).stream()
									.findFirst().ifPresent(regionsToTransfer::add));
				} catch (Throwable e) {
					MessageTemplate
							.inst(PandaPrefixedStringField.applyPrefix(String.format(
									"&cUnable to process regions for world '%s': %s", world.getName(), e.getMessage())))
							.process().sendMessage(Bukkit.getConsoleSender());
					errorsInConsole.set(true);
				}
			});

			regionsToTransfer.stream().filter(Objects::nonNull).forEach(psRegion -> {
				if (protectionsService.findProtectionById(psRegion.getId()) == null
						&& !psRegion.getOwners().isEmpty()) {
					TransferResult<Protection> result = transferRegion(psRegion);

					successList.addAll(result.getSuccessList());
					exceptionsList.addAll(result.getExceptionsList());

					if (!errorsInConsole.get() && result.isErrorsInConsole()) {
						errorsInConsole.set(true);
					}
				}
			});
		}

		return new TransferResult<Protection>(successList, exceptionsList, errorsInConsole.get());
	}

	private TransferResult<Protection> transferRegion(PSRegion protectionStonesRegion) {
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
		} else {
			regions.add(protectionStonesRegion);
		}

		List<Protection> successList = new ArrayList<>();
		List<Throwable> exceptionsList = new ArrayList<>();
		AtomicReference<Boolean> errorsInConsole = new AtomicReference<>(false);

		PSProtectBlock protectBlock = ProtectionStones.getBlockOptions(protectionStonesRegion.getType());

		if (protectBlock != null) {
			regions.forEach(region -> {
				ProtectionBlock protectionBlock = protectionBlocksService.getProtectionBlockById(protectBlock.alias);

				if (protectionBlock != null) {
					try {
						Protection protection = new Protection(owners.get(0), region.getProtectBlock().getLocation(),
								protectionBlock);

						ProtectionCreationAttemptEvent attemptEvent = new ProtectionCreationAttemptEvent(null,
								protection);
						Bukkit.getPluginManager().callEvent(attemptEvent);

						if (attemptEvent.isCancelled()) {
							throw Exceptions.Protections.Save.CANCELLED.generateException();
						}

						protection.create().subscribe((createdProtection) -> {
							try {
								createdProtection.getProtectedRegion().getFlags().forEach((flag, value) -> {
									if (worldGuardApi.getHook().getBannedPlayersFlag().getWorldGuardFlag() == flag) {
										flags.put(flag, value);
									}
								});

								createdProtection.getProtectedRegion().setFlags(flags);

								if (displayName != null) {
									createdProtection.setDisplayName(displayName);
								}

								if (!(protectionStonesRegion instanceof PSMergedRegion)) {
									try {
										createdProtection.setHome(home);
									} catch (Exception e) {
										MessageTemplate
												.inst(PandaPrefixedStringField.applyPrefix(
														String.format("&cUnable to set home for protection '%s': %s",
																createdProtection.getRegionId(), e.getMessage())))
												.process().sendMessage(Bukkit.getConsoleSender());
										e.printStackTrace();
										errorsInConsole.set(true);
									}
								}

								owners.forEach(owner -> {
									try {
										createdProtection.getOwners().add(owner);
									} catch (RoyaleProtectionBlocksExceptionImpl e) {
										if (e.getExceptionType() != Exceptions.Protections.Owners.Save.CANNOTADDPROTECTIONOWNER) {
											MessageTemplate
													.inst(PandaPrefixedStringField.applyPrefix(String.format(
															"&cUnable to add owner on protection '%s': %s",
															createdProtection.getRegionId(), e.getMessage())))
													.process().sendMessage(Bukkit.getConsoleSender());
											e.sendError(Bukkit.getConsoleSender());
											e.printStackTrace();
											errorsInConsole.set(true);
										}
									}
								});

								members.forEach(member -> {
									try {
										createdProtection.getMembers().add(member);
									} catch (RoyaleProtectionBlocksExceptionImpl e) {
										if (e.getExceptionType() != Exceptions.Protections.Members.Save.CANNOTADDPROTECTIONOWNER) {
											MessageTemplate
													.inst(PandaPrefixedStringField.applyPrefix(String.format(
															"&cUnable to add member on protection '%s': %s",
															createdProtection.getRegionId(), e.getMessage())))
													.process().sendMessage(Bukkit.getConsoleSender());
											e.sendError(Bukkit.getConsoleSender());
											e.printStackTrace();
											errorsInConsole.set(true);
										}
									}
								});

								successList.add(createdProtection);

								ProtectionStones.removePSRegion(protection.getLocation().getWorld(),
										protectionStonesRegion.getId());
							} catch (RoyaleProtectionBlocksExceptionImpl e) {
								MessageTemplate
										.inst(PandaPrefixedStringField.applyPrefix(
												String.format("&cUnable to save information for protection '%s': %s",
														region.getId(), e.getMessage())))
										.process().sendMessage(Bukkit.getConsoleSender());
								e.sendError(Bukkit.getConsoleSender());
								exceptionsList.add(e);
								errorsInConsole.set(true);
							}
						}, (e) -> {
							MessageTemplate
									.inst(PandaPrefixedStringField.applyPrefix(
											String.format("&cUnable to save information for protection '%s': %s",
													region.getId(), e.getMessage())))
									.process().sendMessage(Bukkit.getConsoleSender());
							exceptionsList.add(e);
							errorsInConsole.set(true);
						});
					} catch (RoyaleProtectionBlocksExceptionImpl e) {
						if (e.getExceptionType() == Exceptions.Protections.Save.CANCELLED) {
							Debugger.log(MessageType.PROTECTION_CREATION_ATTEMPT_CANCELLED,
									() -> new Object[] { String.valueOf(region.getProtectBlock().getLocation().getX()),
											String.valueOf(region.getProtectBlock().getLocation().getY()),
											String.valueOf(region.getProtectBlock().getLocation().getZ()) });
						} else {
							MessageTemplate
									.inst(PandaPrefixedStringField.applyPrefix(
											String.format("&cUnable to save information for protection '%s': %s",
													region.getId(), e.getMessage())))
									.process().sendMessage(Bukkit.getConsoleSender());
							e.sendError(Bukkit.getConsoleSender());
						}

						exceptionsList.add(e);
						errorsInConsole.set(true);
					}
				}
			});
		}

		return new TransferResult<Protection>(successList, exceptionsList, errorsInConsole.get());
	}

	@Data
	@AllArgsConstructor
	public static class TransferResult<T> {

		private List<T> successList;
		private List<Throwable> exceptionsList;
		private boolean errorsInConsole;

	}

}
