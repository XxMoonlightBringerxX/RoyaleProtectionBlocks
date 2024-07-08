package company.pluginName.Modules.ProtectionParticlesPckg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import company.pluginName.Modules.ProtectionsPckg.ProtectionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionUtils.SimpleLocation;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionUtils.SimpleLocation.SimpleLocationArea;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Message;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.NMS.Packets.PacketPlayOutParticle;
import darkpanda73.PandaUtils.PandaUtilities.NMS.Packets.PacketPlayOutParticle.DustParticleOptions;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaDoubleField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaIntegerField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;

@PandaService
public class ProtectionParticlesService {

	@RegisteredPandaField("config")
	public static final PandaIntegerField SETTINGS_PROTECTION_VIEW_RADIUS = new PandaIntegerField(
			"Settings.Protection.View.Radius", 16);

	@RegisteredPandaField("config")
	public static final PandaDoubleField SETTINGS_PROTECTION_VIEW_SPEED = new PandaDoubleField(
			"Settings.Protection.View.Speed", 1.5D);

	@RegisteredPandaField("config")
	public static final PandaIntegerField SETTINGS_PROTECTION_VIEW_DURATIONINMILLIS = new PandaIntegerField(
			"Settings.Protection.View.Duration-in-seconds", 60);

	@RegisteredPandaField("lang")
	public static final PandaStringField MESSAGE_PROTECTION_VIEW_VIEWACTIVEACTIONBAR = new PandaStringField(
			"Message.Protection.View.View-active-action-bar", "&eProtection view: &aActive");

	@PandaInject
	private ProtectionsService protectionsService;

	private HashMap<UUID, Pair<Long, BukkitTask>> tasks = new HashMap<>();
	private BukkitTask checkerTask;

	@LoadMethod
	private void load() {
		checkerTask = TasksUtils.executeWithTimer(() -> {
			tasks.entrySet().removeIf(entry -> {
				if ((entry.getValue().getFirst()
						+ (SETTINGS_PROTECTION_VIEW_DURATIONINMILLIS.getContent() * 1000)) < System
								.currentTimeMillis()) {
					entry.getValue().getSecond().cancel();
					return true;
				}
				return false;
			});
		}, 20, 20);
	}

	@UnloadMethod
	private void unload() {
		if (checkerTask != null) {
			checkerTask.cancel();
			checkerTask = null;
			tasks.entrySet().removeIf(entry -> {
				entry.getValue().getSecond().cancel();
				return true;
			});
		}
	}

	@ReloadMethod
	private void reload() {
		unload();
		load();
	}

	public void toggleView(Player player, boolean showAll) {
		if (this.tasks.containsKey(player.getUniqueId())) {
			deactivateView(player.getUniqueId());
		} else {
			activateView(player, showAll);
		}
	}

	private void activateView(Player player, boolean showAll) {
		ProtectionParticlePlayerContainer playerContainer = new ProtectionParticlePlayerContainer(this, player,
				showAll);

		tasks.put(player.getUniqueId(), Pair.of(System.currentTimeMillis(),
				TasksUtils.executeOnAsyncWithTimer(() -> playerContainer.processTick(), 1, 1)));
	}

	private void deactivateView(UUID playerUuid) {
		Pair<Long, BukkitTask> pair = tasks.remove(playerUuid);

		if (pair != null) {
			pair.getSecond().cancel();
		}
	}

	private static class ProtectionParticlePlayerContainer {

		private ProtectionParticlesService service;
		private UUID playerUuid;
		private boolean showAll;

		private long radius = SETTINGS_PROTECTION_VIEW_RADIUS.getContent();
		private double speed = 1D / SETTINGS_PROTECTION_VIEW_SPEED.getContent();

		private long length = (radius * 2) + 1;
		private long diagonalLength = (length * 2) - 1;
		private double diagonalPortion = ((double) diagonalLength * 0.05D);

		private Location currentLocation;
		private Location currentMinLocation;
		private Location currentMaxLocation;
		private List<Protection> currentProtections;
		private int currentTick;

		private Message actionBarMessage;

		public ProtectionParticlePlayerContainer(ProtectionParticlesService service, Player player, boolean showAll) {
			this.service = service;
			this.playerUuid = player.getUniqueId();
			this.showAll = showAll;
			this.currentTick = 0;
			this.actionBarMessage = MessageTemplate.inst(MESSAGE_PROTECTION_VIEW_VIEWACTIVEACTIONBAR.getContent())
					.process();

			updateLocation(player.getLocation().getBlock().getLocation());
			actionBarMessage.sendActionBar(player);
		}

		public void processTick() {
			Player pl = Bukkit.getPlayer(playerUuid);

			if (pl == null || !pl.isOnline()) {
				service.deactivateView(playerUuid);
				return;
			}

			currentTick++;

			if (!currentProtections.isEmpty()) {
				long min = (int) ((diagonalPortion * (currentTick - 1)) / speed);
				long max = (int) ((diagonalPortion * currentTick) / speed);

				if (currentTick >= (20 * speed)) {
					max = diagonalLength + 1;
				}

				if (currentTick > (20 * speed)) {
					min = diagonalLength;
				}

				Location diagonalLocation = currentMinLocation.clone().add(min, 0, 0);
				try {
					for (long i = min; i <= max; i++) {
						for (long j = i; j >= 0; j--) {
							if (diagonalLocation.getY() > currentMaxLocation.getY()) {
								break;
							}

							if (j <= length) {
								for (long k = 0; k <= length; k++) {
									if (currentProtections.stream()
											.anyMatch(prot -> prot.isInside(diagonalLocation, true))) {
										Optional<Pair<Protection, Long>> coincidences = currentProtections.stream()
												.filter(prot -> prot.isInside(diagonalLocation, true))
												.map(prot -> Pair.of(prot, Arrays.asList(((diagonalLocation
														.getY() == currentLocation.getY()
														|| diagonalLocation.getY() == prot.getLocation().getY())
														|| (diagonalLocation.getY() == prot.getMinLocation().getY()
																|| diagonalLocation
																		.getY() == (prot.getMaxLocation().getY() + 1))),
														(diagonalLocation.getX() == prot.getMinLocation().getX()),
														(diagonalLocation.getZ() == prot.getMinLocation().getZ()),
														(diagonalLocation.getX() == (prot.getMaxLocation().getX() + 1)),
														(diagonalLocation.getZ() == (prot.getMaxLocation().getZ() + 1)))
														.stream().filter(Boolean.TRUE::equals).count()))
												.filter(pair -> pair.getSecond() >= 2)
												.collect(Collectors.maxBy((pair1, pair2) -> {
													if (pair1.getSecond() == pair2.getSecond()) {
														return Integer.compare(pair1.getFirst().getPriority(),
																pair2.getFirst().getPriority());
													}
													return Long.compare(pair1.getSecond(), pair2.getSecond());
												}));

										if (coincidences.isPresent() && coincidences.get().getSecond() >= 2L) {
											if (coincidences.get().getSecond() >= 3L
													|| (diagonalLocation.getY() != currentLocation.getY()
															&& diagonalLocation.getY() != coincidences.get().getFirst()
																	.getLocation().getY())) {
												if (coincidences.get().getFirst().isMember(playerUuid)
														|| coincidences.get().getFirst().isOwner(playerUuid)) {
													// Green
													new PacketPlayOutParticle(diagonalLocation.getX(),
															diagonalLocation.getY(), diagonalLocation.getZ(), 0, 0, 0,
															0.5F, 5, false,
															new DustParticleOptions(Color.fromRGB(50, 255, 55), 1.0F))
															.send(pl);
												} else {
													// Gray
													new PacketPlayOutParticle(diagonalLocation.getX(),
															diagonalLocation.getY(), diagonalLocation.getZ(), 0, 0, 0,
															0.5F, 5, false,
															new DustParticleOptions(Color.fromRGB(180, 180, 180), 1.0F))
															.send(pl);
												}
											}

											if (diagonalLocation.getY() == coincidences.get().getFirst().getLocation()
													.getY()) {
												// Yellow
												new PacketPlayOutParticle(diagonalLocation.getX(),
														diagonalLocation.getY() + 0.5D, diagonalLocation.getZ(), 0, 0,
														0, 0.5F, 5, false,
														new DustParticleOptions(Color.fromRGB(225, 230, 0), 1.0F))
														.send(pl);
											} else if (diagonalLocation.getY() == currentLocation.getY()) {
												// Brown
												new PacketPlayOutParticle(diagonalLocation.getX(),
														diagonalLocation.getY() + 0.5D, diagonalLocation.getZ(), 0, 0,
														0, 0.5F, 5, false,
														new DustParticleOptions(Color.fromRGB(200, 130, 0), 1.0F))
														.send(pl);
											}
										}

										SimpleLocation diagonalExtraLocation = SimpleLocation.of(diagonalLocation)
												.add(0.5D, 0.5D, 0.5D);
										coincidences = currentProtections
												.stream().filter(
														prot -> diagonalLocation.getY() == prot.getLocation().getY()
																&& prot.isInside(
																		new SimpleLocationArea(
																				diagonalLocation.getWorld().getName(),
																				diagonalLocation, diagonalLocation),
																		true))
												.map(prot -> Pair.of(prot, Arrays
														.asList((diagonalLocation.getX() == prot.getLocation().getX()),
																(diagonalLocation.getZ() == prot.getLocation().getZ()))
														.stream().filter(Boolean.TRUE::equals).count()))
												.filter(pair -> pair.getSecond() >= 1)
												.collect(Collectors.maxBy((pair1, pair2) -> {
													if (pair1.getSecond() == pair2.getSecond()) {
														return Integer.compare(pair1.getFirst().getPriority(),
																pair2.getFirst().getPriority());
													}
													return Long.compare(pair1.getSecond(), pair2.getSecond());
												}));

										if (coincidences.isPresent()) {
											if (coincidences.get().getSecond() == 1) {
												// Yellow
												new PacketPlayOutParticle(diagonalExtraLocation.getX(),
														diagonalExtraLocation.getY(), diagonalExtraLocation.getZ(), 0,
														0, 0, 0.5F, 5, false,
														new DustParticleOptions(Color.fromRGB(225, 230, 0), 1.0F))
														.send(pl);
											} else {
												// Purple
												new PacketPlayOutParticle(diagonalExtraLocation.getX(),
														diagonalExtraLocation.getY(), diagonalExtraLocation.getZ(), 0,
														0, 0, 0.5F, 5, false,
														new DustParticleOptions(Color.fromRGB(195, 70, 255), 1.0F))
														.send(pl);
											}
										}
									}
									diagonalLocation.add(0, 0, 1);
								}
								diagonalLocation.setZ(currentMinLocation.getZ());
								diagonalLocation.add(-1, 1, 0);
							} else {
								diagonalLocation.add(-1, 1, 0);
							}
						}
						diagonalLocation.setX(currentMinLocation.getX() + i);
						diagonalLocation.setY(currentMinLocation.getY());
						diagonalLocation.setZ(currentMinLocation.getZ());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (currentTick >= (20 * speed)) {
				currentTick = 0;

				updateLocation(pl.getLocation().getBlock().getLocation());
				actionBarMessage.sendActionBar(pl);
			}
		}

		private void updateLocation(Location location) {
			currentLocation = location;
			currentMinLocation = currentLocation.clone().subtract(radius, radius, radius);
			currentMaxLocation = currentLocation.clone().add(radius + 1, radius + 1, radius + 1);
			currentProtections = service.protectionsService
					.findProtectionsByArea(currentMinLocation, currentMaxLocation)
					.filter(prot -> showAll || prot.isMember(playerUuid) || prot.isOwner(playerUuid))
					.collect(Collectors.toList());
		}

	}

}
