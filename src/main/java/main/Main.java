package main;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	

	@Override
	public void onEnable() {
		getLogger().info("onEnable has been invoked!");
		System.out.println("enabled");
		getServer().getPluginManager().registerEvents(new ClickListener(), this); // registers the click listener
		this.getCommand("fillOn").setExecutor(new ToggleFillCommand());
	}

	@Override
	public void onDisable() {
		getLogger().info("onDisable has been invoked!");
	}

}
