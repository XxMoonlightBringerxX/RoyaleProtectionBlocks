package company.pluginName.Modules.ProtectionFlagsPckg.Objects;

import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import company.pluginName.Enums.EconomyService;
import company.pluginName.Hooks.WorldGuard.WorldGuardAPI;
import company.pluginName.Modules.ProtectionFlagsPckg.Utils.ProtectionFlagUtilities;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import lombok.Data;
import lombok.Setter;

@Data
public class ProtectionFlag {

	@PandaInject
	private static WorldGuardAPI worldGuardApi;

	private @Setter(lombok.AccessLevel.NONE) Flag<?> worldGuardFlag;
	protected boolean editable;
	protected boolean hidden;
	protected ItemStack displayItem;
	protected boolean hideIfNoValue;
	protected boolean hideIfNoPermission;
	protected Object defaultValue;
	protected RegionGroup defaultGroup;
	protected String permission;
	protected Double costPerChange;
	protected EconomyService economyService;

	public ProtectionFlag(String id) throws NullPointerException {
		try {
			this.worldGuardFlag = worldGuardApi.getHook().getInternalWorldGuard().getAllFlags().stream()
					.filter(wgFlag -> wgFlag.getName().equalsIgnoreCase(id)).findFirst()
					.orElseThrow(() -> new NullPointerException(String.format("Unable to find flag '%s'", id)));
		} catch (Exception e) {
			throw new NullPointerException(String.format("Unable to find flag '%s'", id));
		}

		this.defaultGroup = worldGuardFlag.getRegionGroupFlag() != null
				? worldGuardFlag.getRegionGroupFlag().getDefault()
				: null;
	}

	public void modifyValue(ProtectedRegion protectedRegion, Object value)
			throws ClassCastException, IllegalStateException {
		ProtectionFlagUtilities.setValue(protectedRegion, worldGuardFlag, value, defaultGroup);
	}

	public Object retrieveValue(ProtectedRegion protectedRegion) throws ClassCastException {
		return ProtectionFlagUtilities.getValue(protectedRegion, worldGuardFlag);
	}

}
