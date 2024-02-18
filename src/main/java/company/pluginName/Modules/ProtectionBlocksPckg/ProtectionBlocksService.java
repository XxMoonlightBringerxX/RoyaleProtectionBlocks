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
				.filter(block -> block.getInformation().getItem() != null
						&& block.getInformation().getItem().getType() != Material.AIR)
				.forEach(block -> protectionBlocks.put(block.getInformation().getId().toLowerCase(), block));
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

	public ProtectionBlock getProtectionBlockById(String id) {
		return protectionBlocks.get(id.toLowerCase());
	}

	public void registerProtectionBlock(ProtectionBlock protectionBlock) {
		protectionBlocks.putIfAbsent(protectionBlock.getInformation().getId().toLowerCase(), protectionBlock);
	}

	public void unregisterProtectionBlock(ProtectionBlock protectionBlock) {
		protectionBlocks.remove(protectionBlock.getInformation().getId());
	}

}
