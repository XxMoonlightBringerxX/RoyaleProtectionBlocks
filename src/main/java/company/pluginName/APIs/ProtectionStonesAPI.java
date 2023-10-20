package company.pluginName.APIs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;

import company.pluginName.MainPluginClass;
import company.pluginName.Exceptions.Protection.Save.ProtectionSaveException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveException;
import company.pluginName.Exceptions.ProtectionMembers.Save.ProtectionMembersSaveCannotAddProtectionOwnerException;
import company.pluginName.Exceptions.ProtectionMembers.Save.ProtectionMembersSaveException;
import company.pluginName.Exceptions.ProtectionOwners.Save.ProtectionOwnersSaveCannotAddProtectionOwnerException;
import company.pluginName.Exceptions.ProtectionOwners.Save.ProtectionOwnersSaveException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionBlocks.ProtectionBlockInformation;
import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import dev.espi.protectionstones.FlagHandler;
import dev.espi.protectionstones.PSGroupRegion;
import dev.espi.protectionstones.PSMergedRegion;
import dev.espi.protectionstones.PSProtectBlock;
import dev.espi.protectionstones.PSRegion;
import dev.espi.protectionstones.ProtectionStones;
import lombok.AllArgsConstructor;
import lombok.Data;
import relampagorojo93.LibsCollection.Utils.Bukkit.APIs.AbstractAPI;

public class ProtectionStonesAPI extends AbstractAPI {

	public ProtectionStonesAPI() {
		super(MainPluginClass.getPlugin());
		try {
			MessageBuilder.createMessage(getPrefix().concat("<ProtectionStones> Finding ProtectionStones."))
					.sendMessage(Bukkit.getConsoleSender());

			Class.forName("dev.espi.protectionstones.ProtectionStones");

			if (Bukkit.getPluginManager().isPluginEnabled("ProtectionStones")) {
				this.hooked = true;

				MessageBuilder.createMessage(getPrefix().concat("<ProtectionStones> Done!.")).sendMessage(Bukkit.getConsoleSender());
			} else {
				MessageBuilder.createMessage(getPrefix().concat("<ProtectionStones> ProtectionStones is not enabled. ")
						.concat(isOptional() ? "Ignoring its implementation." : "")).sendMessage(Bukkit.getConsoleSender());
			}
		} catch (Exception e) {
			MessageBuilder.createMessage(getPrefix().concat("<ProtectionStones> ProtectionStones could not be loaded. ")
					.concat(isOptional() ? "Ignoring its implementation." : "")).sendMessage(Bukkit.getConsoleSender());
			if (!isOptional()) {
				e.printStackTrace();
			}
		}
	}

	public TransferResult<ProtectionBlock> transferProtectionBlocks() {
		List<ProtectionBlock> successList = new ArrayList<>();
		List<Throwable> exceptionsList = new ArrayList<>();
		AtomicReference<Boolean> errorsInConsole = new AtomicReference<>(false);

		if (isHooked()) {
			ProtectionStones.getInstance().getConfiguredBlocks().forEach(pb -> {
				try {
					ProtectionBlock block = new ProtectionBlock(new ProtectionBlockInformation(pb.alias, pb.createItem(), pb.xRadius,
							pb.yRadius, pb.zRadius, pb.permission != null && !pb.permission.isEmpty() ? pb.permission : null));

					if (pb.worlds.size() > 0 && pb.worldListType != null && !pb.worldListType.isBlank()) {
						Bukkit.getWorlds().stream()
								.filter(world -> pb.worldListType.equalsIgnoreCase("whitelist") == pb.worlds.contains(world.getName()));
					}

					block.save();

					successList.add(block);
				} catch (ProtectionBlocksSaveException e) {
					MessageBuilder
							.createMessage(MessageString.applyPrefix(String
									.format("&cUnable to create protection block using config from '%s': %s", pb.alias, e.getMessage())))
							.sendMessage(Bukkit.getConsoleSender());
					e.printStackTrace();
					exceptionsList.add(e);
					errorsInConsole.set(true);
				}
			});
		}

		return new TransferResult<ProtectionBlock>(successList, exceptionsList, errorsInConsole.get()) {
		};
	}

	public TransferResult<Protection> transferProtections() {
		List<Protection> successList = new ArrayList<>();
		List<Throwable> exceptionsList = new ArrayList<>();
		AtomicReference<Boolean> errorsInConsole = new AtomicReference<>(false);

		if (isHooked()) {
			Bukkit.getWorlds().stream().forEach(world -> {
				try {
					RegionManager regionManager = MainPluginClass.getWorldGuardAPI().getInternalWorldGuard().getRegionManager(world);
					regionManager.getRegions().entrySet().stream().filter((entry) -> ProtectionStones.isPSRegion(entry.getValue()))
							.map((entry) -> {
								List<PSRegion> regions = ProtectionStones.getPSRegions(world, entry.getKey());

								return regions.size() > 0 ? this.transferRegion(regions.get(0)) : null;
							}).filter(Objects::nonNull).forEach(result -> {
								successList.addAll(result.getSuccessList());
								exceptionsList.addAll(result.getExceptionsList());

								if (!errorsInConsole.get() && result.isErrorsInConsole()) {
									errorsInConsole.set(true);
								}
							});
				} catch (Throwable e) {
					MessageBuilder
							.createMessage(MessageString.applyPrefix(
									String.format("&cUnable to process regions for world '%s': %s", world.getName(), e.getMessage())))
							.sendMessage(Bukkit.getConsoleSender());
					e.printStackTrace();
					errorsInConsole.set(true);
				}
			});
		}

		return new TransferResult<Protection>(successList, exceptionsList, errorsInConsole.get()) {
		};
	}

	private TransferResult<Protection> transferRegion(PSRegion protectionStonesRegion) {
		Map<Flag<?>, Object> flags = new HashMap<>(protectionStonesRegion.getWGRegion().getFlags());

		World world = protectionStonesRegion.getWorld();
		String id = protectionStonesRegion.getId();

		List<UUID> owners = protectionStonesRegion.getOwners();
		List<UUID> members = protectionStonesRegion.getMembers();

		String displayName = protectionStonesRegion.getName();
		Location home = protectionStonesRegion.getHome();

		flags.keySet().removeIf(
				flag -> flag.getName().startsWith("ps-") || flag == FlagHandler.GREET_ACTION || flag == FlagHandler.FAREWELL_ACTION);

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
				ProtectionBlock protectionBlock = MainPluginClass.getPlugin().getProtectionsModule()
						.getProtectionBlockById(protectBlock.alias);

				if (protectionBlock != null) {
					try {
						Protection protection = MainPluginClass.getPlugin().getProtectionsModule().createProtection(owners.get(0), null,
								protectionBlock, region.getProtectBlock().getLocation());

						protection.getProtectedRegion().getFlags().forEach((flag, value) -> {
							if (MainPluginClass.getWorldGuardAPI().getBannedPlayersFlag().getWorldGuardFlag() == flag
									|| MainPluginClass.getWorldGuardAPI().getProtectionBlockLocationFlag().getWorldGuardFlag() == flag) {
								flags.put(flag, value);
							}
						});

						protection.getProtectedRegion().setFlags(flags);

						if (displayName != null) {
							protection.setDisplayName(displayName);
						}

						if (!(protectionStonesRegion instanceof PSMergedRegion)) {
							try {
								protection.setHome(home);
							} catch (Exception e) {
								MessageBuilder.createMessage(MessageString.applyPrefix(String
										.format("&cUnable to set home for protection '%s': %s", protection.getRegionId(), e.getMessage())))
										.sendMessage(Bukkit.getConsoleSender());
								e.printStackTrace();
								errorsInConsole.set(true);
							}
						}

						owners.forEach(owner -> {
							try {
								protection.getOwners().add(owner);
							} catch (ProtectionOwnersSaveCannotAddProtectionOwnerException e) {
							} catch (ProtectionOwnersSaveException e) {
								MessageBuilder.createMessage(MessageString.applyPrefix(String
										.format("&cUnable to add owner on protection '%s': %s", protection.getRegionId(), e.getMessage())))
										.sendMessage(Bukkit.getConsoleSender());
								e.sendError(Bukkit.getConsoleSender());
								e.printStackTrace();
								errorsInConsole.set(true);
							}
						});

						members.forEach(member -> {
							try {
								protection.getMembers().add(member);
							} catch (ProtectionMembersSaveCannotAddProtectionOwnerException e) {
							} catch (ProtectionMembersSaveException e) {
								MessageBuilder.createMessage(MessageString.applyPrefix(String
										.format("&cUnable to add member on protection '%s': %s", protection.getRegionId(), e.getMessage())))
										.sendMessage(Bukkit.getConsoleSender());
								e.sendError(Bukkit.getConsoleSender());
								e.printStackTrace();
								errorsInConsole.set(true);
							}
						});

						successList.add(protection);
					} catch (ProtectionSaveException e) {
						MessageBuilder
								.createMessage(MessageString.applyPrefix(String
										.format("&cUnable to save information for protection '%s': %s", region.getId(), e.getMessage())))
								.sendMessage(Bukkit.getConsoleSender());
						e.sendError(Bukkit.getConsoleSender());
						exceptionsList.add(e);
						errorsInConsole.set(true);
					}
				}
			});
		}

		ProtectionStones.removePSRegion(world, id);

		return new TransferResult<Protection>(successList, exceptionsList, errorsInConsole.get()) {
		};
	}

	@Override
	public String getPrefix() {
		return MainPluginClass.getPlugin().getPrefix();
	}

	@Override
	public boolean isOptional() {
		return true;
	}

	@Data
	@AllArgsConstructor
	public abstract static class TransferResult<T> {

		private List<T> successList;
		private List<Throwable> exceptionsList;
		private boolean errorsInConsole;

	}

}
