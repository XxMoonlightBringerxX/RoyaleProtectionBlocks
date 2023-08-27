package company.pluginName.Modules.ProtectionsPckg.Objects;

import org.bukkit.entity.Player;

import company.pluginName.MainPluginClass;
import company.pluginName.Permissions;
import company.pluginName.Exceptions.ProtectionBlocks.Delete.ProtectionBlocksDeleteDeniedException;
import company.pluginName.Exceptions.ProtectionBlocks.Delete.ProtectionBlocksDeleteException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveDeniedException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveIdInUseException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveIdNullException;
import company.pluginName.Exceptions.ProtectionBlocks.Save.ProtectionBlocksSaveItemNullException;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionBlocks.ProtectionBlockAllowedWorlds;
import company.pluginName.Modules.ProtectionsPckg.Objects.Components.ProtectionBlocks.ProtectionBlockInformation;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Setter(lombok.AccessLevel.NONE)
public class ProtectionBlock {

	public static final String PROTECTION_BLOCK_ID_KEY = "ProtectionBlockId";

	private @NonNull ProtectionBlockInformation information;
	private @NonNull ProtectionBlockAllowedWorlds allowedWorlds;

	public ProtectionBlock() {
		this(new ProtectionBlockInformation());
	}

	public ProtectionBlock(ProtectionBlockInformation information) {
		this(information, new ProtectionBlockAllowedWorlds());
	}

	public ProtectionBlock(ProtectionBlockInformation information, ProtectionBlockAllowedWorlds allowedWorlds) {
		this.information = information;
		this.allowedWorlds = allowedWorlds;
	}

	public ProtectionBlock(ProtectionBlock protectionBlock) {
		this.information = new ProtectionBlockInformation();
		this.allowedWorlds = new ProtectionBlockAllowedWorlds();

		this.information.setId(protectionBlock.getInformation().getId());
		this.copy(protectionBlock);
	}

	public void save() throws ProtectionBlocksSaveException {
		this.save(null);
	}

	public void save(Player player) throws ProtectionBlocksSaveException {
		if (player != null) {
			if (!player.hasPermission(Permissions.PROTECTION_BLOCKS_CREATE)) {
				throw new ProtectionBlocksSaveDeniedException();
			}
		}

		if (this.getInformation().getId() == null) {
			throw new ProtectionBlocksSaveIdNullException();
		}

		ProtectionBlock registeredProtectionBlock = MainPluginClass.getPlugin().getProtectionsModule()
				.getProtectionBlockById(this.getInformation().getId());

		if (registeredProtectionBlock != null && registeredProtectionBlock != this) {
			throw new ProtectionBlocksSaveIdInUseException();
		}

		if (this.getInformation().getItem() == null) {
			throw new ProtectionBlocksSaveItemNullException();
		}

		MainPluginClass.getPlugin().getSqlModule().saveProtectionBlock(this);
		MainPluginClass.getPlugin().getProtectionsModule().registerProtectionBlock(this);
	}

	public void delete() throws ProtectionBlocksDeleteException {
		this.delete(null);
	}

	public void delete(Player pl) throws ProtectionBlocksDeleteException {
		if (pl != null) {
			if (!pl.hasPermission(Permissions.PROTECTION_BLOCKS_DELETE)) {
				throw new ProtectionBlocksDeleteDeniedException();
			}
		}

		MainPluginClass.getPlugin().getSqlModule().deleteProtectionBlock(this);
		MainPluginClass.getPlugin().getProtectionsModule().unregisterProtectionBlock(this);
	}

	public void copy(ProtectionBlock protectionBlock) {
		this.information.copy(protectionBlock.getInformation());
		this.allowedWorlds.copy(protectionBlock.getAllowedWorlds());
	}

}
