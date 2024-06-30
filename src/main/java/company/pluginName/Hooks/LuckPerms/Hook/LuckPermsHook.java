package company.pluginName.Hooks.LuckPerms.Hook;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

public class LuckPermsHook extends PandaAbstractHook {

	private LuckPerms api;

	public void load() throws Throwable {
		this.api = LuckPermsProvider.get();

		if (this.api != null) {
//			this.api.getEventBus().subscribe(LuckPermsEvent.class, (event) -> {
//				System.out.println(event.getClass().getName());
//			});

			this.hooked = true;
		}
	}

	public void unload() throws Throwable {
	}

}
