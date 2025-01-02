package company.pluginName.Modules.ProtectionBlocksPckg;

import java.util.HashMap;

import org.bukkit.Material;

import company.pluginName.Modules.ProtectionBlocksPckg.Objects.ProtectionBlock;
import company.pluginName.Modules.SQLPckg.SQLService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import lombok.Getter;

@PandaService(priority = 998)
public class ProtectionBlocksService {

	@PandaInject
	private SQLService sqlService;

	private @Getter HashMap<String, ProtectionBlock> protectionBlocks = new HashMap<>();

	@LoadMethod
	private void load() {
		sqlService.getProtectionBlocks().stream()
				.filter(block -> block.getItem() != null && block.getItem().getType() != Material.AIR)
				.forEach(block -> protectionBlocks.put(block.getId().toLowerCase(), block));
	}

	@UnloadMethod
	private void unload() {
		this.protectionBlocks.clear();
	}

	@ReloadMethod
	private void reload() {
		unload();
		load();
	}

	/*
	 * Register/Unregister methods
	 */

	public synchronized void registerProtectionBlock(ProtectionBlock protectionBlock) {
		protectionBlocks.putIfAbsent(protectionBlock.getId().toLowerCase(), protectionBlock);
	}

	public synchronized void unregisterProtectionBlock(ProtectionBlock protectionBlock) {
		protectionBlocks.remove(protectionBlock.getId());
	}

	/*
	 * Search methods
	 */

	public ProtectionBlock getProtectionBlockById(String id) {
		return protectionBlocks.get(id.toLowerCase());
	}

}
