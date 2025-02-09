package company.pluginName.Modules.ProtectionsPckg.Objects.Components.Permissions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import company.pluginName.Exceptions.RoyaleProtectionBlocksExceptionImpl;
import company.pluginName.Modules.ProtectionSettingsPckg.ProtectionPermissionsService;
import company.pluginName.Modules.ProtectionsPckg.Objects.Protection;
import company.pluginName.Modules.SQLPckg.SQLService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import royale.RoyaleProtectionBlocks.Plugin.API.Enums.PermissionGroup;
import royale.RoyaleProtectionBlocks.Plugin.API.Objects.Permissions.AbstractPermission;

@Data
@AllArgsConstructor
public class ProtectionPermissionManager {

	@PandaInject
	private static ProtectionPermissionsService protectionPermissionsService;

	@PandaInject
	private static SQLService sqlService;

	private Protection protection;

	private Map<String, ProtectionPermission> permissions = new HashMap<>();

	public ProtectionPermissionManager(Protection protection) {
		this.protection = protection;
	}

	public void resetPermissions() {
		this.permissions.clear();
	}

	public void setValue(AbstractPermission permission, PermissionGroup group, Boolean value) {
		ProtectionPermission protectionPermission = permissions.computeIfAbsent(permission.getId(),
				(id) -> new ProtectionPermission(id));

		switch (group) {
		case NON_MEMBERS:
			protectionPermission.setNonMembersValue(value);
			break;
		case MEMBERS:
			protectionPermission.setMembersValue(value);
			break;
		case OWNERS:
			protectionPermission.setOwnersValue(value);
			break;
		}

		TasksUtils.executeOnAsync(() -> {
			try {
				sqlService.saveProtectionPermission(protection.getProtectionId(), protectionPermission);
			} catch (RoyaleProtectionBlocksExceptionImpl e) {
				e.sendError(Bukkit.getConsoleSender());
			}
		});
	}

	public Boolean getValue(AbstractPermission setting, Player player) {
		if (protection.isMainOwner(player.getUniqueId()) || protection.isOwner(player.getUniqueId())) {
			return getValue(setting, PermissionGroup.OWNERS);
		} else if (protection.isMember(player.getUniqueId())) {
			return getValue(setting, PermissionGroup.MEMBERS);
		} else {
			return getValue(setting, PermissionGroup.NON_MEMBERS);
		}
	}

	public Boolean getValue(AbstractPermission permission, PermissionGroup group) {
		ProtectionPermission protectionPermission = permissions.get(permission.getId());

		switch (group) {
		case NON_MEMBERS:
			return protectionPermission != null && protectionPermission.getNonMembersValue() != null
					? protectionPermission.getNonMembersValue()
					: permission.getNonMembersValue();
		case MEMBERS:
			return protectionPermission != null && protectionPermission.getMembersValue() != null
					? protectionPermission.getMembersValue()
					: permission.getMembersValue();
		case OWNERS:
			return protectionPermission != null && protectionPermission.getOwnersValue() != null
					? protectionPermission.getOwnersValue()
					: permission.getOwnersValue();
		}

		return permission.getNonMembersValue();
	}

}
