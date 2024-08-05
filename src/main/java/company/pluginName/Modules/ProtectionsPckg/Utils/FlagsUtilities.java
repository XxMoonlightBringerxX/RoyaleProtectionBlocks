package company.pluginName.Modules.ProtectionsPckg.Utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.OfflinePlayer;

import com.sk89q.worldguard.protection.flags.Flag;

import company.pluginName.Hooks.PlaceholderAPI.PlaceholderAPI;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.PlaceholdersPckg.PlaceholdersService;
import company.pluginName.Modules.ProtectionFlagsPckg.ProtectionFlagsService;
import company.pluginName.Modules.ProtectionFlagsPckg.Objects.ProtectionFlag;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;

public class FlagsUtilities {

	@RegisteredPandaField("flags")
	private static final PandaStringListField SETTINGS_IGNOREFLAGSONRESET = new PandaStringListField(
			"Settings.Ignore-flags-on-reset", Arrays.asList("banned-players", "teleport"));

	@PandaInject
	private static ProtectionFlagsService protectionFlagsService;

	@PandaInject
	private static PlaceholderAPI placeholderApi;

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	@PandaInject
	private static PlaceholdersService placeholdersService;

	@SuppressWarnings("unchecked")
	public static void resetFlags(Protection protection, UUID ownerUuid) {
		OfflinePlayer owner = OfflinePlayerUtilities.getOfflinePlayer(ownerUuid);

		Map<Flag<?>, Object> flags = new HashMap<>();
		SETTINGS_IGNOREFLAGSONRESET.getContent().forEach(flagId -> {
			protection.getProtectedRegion().getFlags().entrySet().stream()
					.filter(entry -> entry.getKey().getName().equalsIgnoreCase(flagId)).findFirst()
					.ifPresent(entry -> flags.put(entry.getKey(), entry.getValue()));
		});
		protection.getProtectedRegion().setFlags(flags);

		protectionFlagsService.getFlags().forEach(flag -> {
			Object value = flag.getDefaultValue();

			if (placeholderApi.getHook().isHooked()) {
				if (value instanceof String) {
					value = MessageTemplate
							.inst(placeholderApi.getHook().applyPlaceholders(value.toString(), owner).replaceAll("\\%",
									"%"))
							.setReplacements(placeholdersService.getProtectionReplacements(protection)).process()
							.toString();
				} else if (value instanceof Collection) {
					value = ((Collection<String>) value).stream().map(string -> MessageTemplate
							.inst(placeholderApi.getHook().applyPlaceholders(string, owner).replaceAll("\\%", "%"))
							.setReplacements(placeholdersService.getProtectionReplacements(protection)).toString())
							.collect(Collectors.toSet());
				}
			}

			if (value != null) {
				flag.modifyValue(protection.getProtectedRegion(), value);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public static void resetFlags(Protection protection, UUID ownerUuid, Collection<String> flagsToReset) {
		OfflinePlayer owner = OfflinePlayerUtilities.getOfflinePlayer(ownerUuid);

		flagsToReset.forEach(flagId -> {
			ProtectionFlag flag = protectionFlagsService.getFlag(flagId);

			if (flag != null) {
				Object value = flag.getDefaultValue();

				if (placeholderApi.getHook().isHooked()) {
					if (value instanceof String) {
						value = MessageTemplate
								.inst(placeholderApi.getHook().applyPlaceholders(value.toString(), owner)
										.replaceAll("\\%", "%"))
								.setReplacements(placeholdersService.getProtectionReplacements(protection)).process()
								.toString();
					} else if (value instanceof Collection) {
						value = ((Collection<String>) value).stream().map(string -> MessageTemplate
								.inst(placeholderApi.getHook().applyPlaceholders(string, owner).replaceAll("\\%", "%"))
								.setReplacements(placeholdersService.getProtectionReplacements(protection)).toString())
								.collect(Collectors.toSet());
					}
				}

				Object fValue = value;

				flag.modifyValue(protection.getProtectedRegion(), fValue);
				protection.getChildProtections()
						.forEach(child -> flag.modifyValue(((Protection) child).getProtectedRegion(), fValue));
			} else {
				protection.getProtectedRegion().getFlags().keySet().stream()
						.filter(wgFlag -> wgFlag.getName().equals(flagId)).findFirst()
						.ifPresent(wgFlag -> protection.getProtectedRegion().setFlag(wgFlag, null));
			}
		});
	}

}
