package company.pluginName.Hooks.ProtocolLib.Hook;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedParticle;

import company.pluginName.MainPluginClass;
import company.pluginName.API.RoyaleProtectionBlocksAPIImpl;
import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.FilePckg.Settings;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.ProtectionsPckg.Utils.ProtectionUtilities;
import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.SimpleLocation;
import royale.RoyaleProtectionBlocks.Plugin.API.Services.PlayerInteractions.Objects.Inventories.OpenProtectionManagementInventoryRequestInput;

public class ProtocolLibHook extends PandaAbstractHook {

	private static final List<WrappedDataValue> DATA_VALUES;

	@PandaInject
	private static MainPluginClass plugin;

	static {
		DATA_VALUES = Arrays.asList(new WrappedDataValue(0, Registry.get(Byte.class), (byte) 0x60),
				new WrappedDataValue(16, Registry.get(Integer.class), 2));
	}

	private HashMap<Integer, SimpleLocation> locationEntities = new HashMap<>();
	private HashMap<UUID, Integer> spawnedEntities = new HashMap<>();

	@Override
	public void load() throws Throwable {
		if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
			ProtocolLibrary.getProtocolManager()
					.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {

						@Override
						public void onPacketReceiving(PacketEvent event) {
							if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
								int entityId = event.getPacket().getIntegers().read(0);

								SimpleLocation simpleLocation = locationEntities.get(entityId);

								if (simpleLocation != null) {
									event.setCancelled(true);

									Protection protection = RoyaleProtectionBlocksAPIImpl.getInstance()
											.getProtectionsService()
											.findProtectionBySourceLocation(simpleLocation.toLocation());
									if (protection != null
											&& Settings.SETTINGS_PROTECTION_OPENINVENTORYONINTERACT.getContent()
											&& ProtectionUtilities.canManage(protection, event.getPlayer())) {
										try {
											RoyaleProtectionBlocksAPIImpl.getInstance().getPlayerInteractionsService()
													.openProtectionManagementInventoryRequest(
															OpenProtectionManagementInventoryRequestInput
																	.inst(event.getPlayer(), protection));
										} catch (RoyaleProtectionBlocksExceptionImpl e) {
											e.sendError(event.getPlayer());
										}
									}
								}
							}
						}

					});

			hooked = true;
		} else {
			throw new Exception("ProtocolLib not found as plugin in the server.");
		}
	}

	@Override
	public void unload() throws Throwable {
	}

	public UUID spawnGlowingMagmaCube(Player player, Location location) {
		UUID uuid = UUID.randomUUID();
		int entityId = getOrCreateEntityId(location);

		PacketContainer packet = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);

		packet.getIntegers().write(0, entityId);
		packet.getUUIDs().write(0, uuid);
		packet.getEntityTypeModifier().write(0, EntityType.MAGMA_CUBE);

		packet.getDoubles().write(0, location.getX()).write(1, location.getY()).write(2, location.getZ());

		ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);

		PacketContainer metadata = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

		metadata.getIntegers().write(0, entityId);
		metadata.getDataValueCollectionModifier().write(0, DATA_VALUES);

		ProtocolLibrary.getProtocolManager().sendServerPacket(player, metadata);

		this.spawnedEntities.put(uuid, entityId);

		return uuid;
	}

	public void removeGlowingMagmaCubes(Player player, List<UUID> uuids) {
		List<Integer> ids = uuids.stream().map(uuid -> this.spawnedEntities.remove(uuid)).filter(Objects::nonNull)
				.collect(Collectors.toList());
		if (ids.size() > 0) {
			PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);

			packet.getIntLists().write(0, ids);

			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		}
	}

	public void spawnParticle(Player player, Location location, Color color) {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.WORLD_PARTICLES);

		packet.getDoubles().write(0, location.getX());
		packet.getDoubles().write(1, location.getY());
		packet.getDoubles().write(2, location.getZ());
		packet.getFloat().write(0, 0F);
		packet.getFloat().write(1, 0F);
		packet.getFloat().write(2, 0F);
		packet.getFloat().write(3, 0.5F);
		packet.getIntegers().write(0, 5);
		packet.getBooleans().write(0, false);
		packet.getNewParticles().write(0,
				WrappedParticle.create(Particle.REDSTONE, new Particle.DustOptions(color, 1.0F)));

		ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
	}

	private Integer getOrCreateEntityId(Location location) {
		SimpleLocation simpleLocation = SimpleLocation.of(location);

		Integer entityId = this.locationEntities.entrySet().stream()
				.filter(entry -> entry.getValue().equals(simpleLocation)).map(Entry::getKey).findFirst().orElse(null);

		if (entityId == null) {
			FutureTask<Integer> generateIdTask = new FutureTask<Integer>(() -> {
				Location worldCenter = new Location(location.getWorld(), 0, 0, 0);
				Entity entity = location.getWorld().spawnEntity(worldCenter, EntityType.MAGMA_CUBE);
				entity.remove();
				return entity.getEntityId();
			});

			if (Bukkit.isPrimaryThread()) {
				try {
					generateIdTask.run();
				} catch (IllegalStateException e) {
					TasksUtils.execute(() -> generateIdTask.run());
				}
			} else {
				TasksUtils.execute(() -> generateIdTask.run());
			}

			try {
				entityId = generateIdTask.get();
				this.locationEntities.put(entityId, simpleLocation);
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		}

		return entityId;
	}

}
