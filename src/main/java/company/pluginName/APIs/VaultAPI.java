package company.pluginName.APIs;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import company.pluginName.TemporaryModules.FilePckg.Messages.MessageString;
import darkpanda73.PandaUtils.PandaColors.NMS.MessageBuilder;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;

public class VaultAPI {

	private @Getter boolean hooked = false;
	private @Getter Economy econ = null;

	public VaultAPI() {
		MessageBuilder.createMessage(MessageString.applyPrefix("<Vault> Hook is enabled. Finding Vault."))
				.sendMessage(Bukkit.getConsoleSender());
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			MessageBuilder
					.createMessage(MessageString.applyPrefix("<Vault> Vault found. Trying to get the Economy API."))
					.sendMessage(Bukkit.getConsoleSender());
			RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager()
					.getRegistration(Economy.class);
			if (rsp != null) {
				econ = rsp.getProvider();
				MessageBuilder.createMessage(MessageString.applyPrefix("<Vault> Done!"))
						.sendMessage(Bukkit.getConsoleSender());
				hooked = true;
			} else {
				MessageBuilder
						.createMessage(MessageString
								.applyPrefix("<Vault> Economy API not initialized. Ignoring its implementation."))
						.sendMessage(Bukkit.getConsoleSender());
			}
		} else {
			MessageBuilder
					.createMessage(MessageString.applyPrefix("<Vault> Vault not found. Ignoring its implementation."))
					.sendMessage(Bukkit.getConsoleSender());
		}
	}

}
