package company.pluginName.APIs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import company.pluginName.MainPluginClass;
import company.pluginName.Modules.ProtectionsPckg.Objects.ProtectionBlock;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import dev.espi.protectionstones.ProtectionStones;
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

				MessageBuilder.createMessage(getPrefix().concat("<ProtectionStones> Done!."))
						.sendMessage(Bukkit.getConsoleSender());
			} else {
				MessageBuilder
						.createMessage(getPrefix().concat("<ProtectionStones> ProtectionStones is not enabled. ")
								.concat(isOptional() ? "Ignoring its implementation." : ""))
						.sendMessage(Bukkit.getConsoleSender());
			}
		} catch (Exception e) {
			MessageBuilder
					.createMessage(getPrefix().concat("<ProtectionStones> ProtectionStones could not be loaded. ")
							.concat(isOptional() ? "Ignoring its implementation." : ""))
					.sendMessage(Bukkit.getConsoleSender());
			if (!isOptional()) {
				e.printStackTrace();
			}
		}
	}

	public List<ProtectionBlock> getProtectionBlocks() {
		List<ProtectionBlock> blocks = new ArrayList<>();
		if (isHooked()) {
			ProtectionStones.getInstance().getConfiguredBlocks().forEach(pb -> {
				blocks.add(new ProtectionBlock(pb.alias, pb.createItem(), pb.xRadius, pb.yRadius, pb.zRadius,
						pb.permission));
			});
		}
		return blocks;
	}

	@Override
	public String getPrefix() {
		return MainPluginClass.getPlugin().getPrefix();
	}

	@Override
	public boolean isOptional() {
		return true;
	}

}
