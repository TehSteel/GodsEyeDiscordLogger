package me.tehsteel.godseyediscordlogger;

import godseye.GodsEyeAlertEvent;
import godseye.GodsEyePunishPlayerEvent;
import me.tehsteel.godseyediscordlogger.util.DiscordWebhook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author TehSteel
 * @link https://github.com/TehSteel/GodsEyeDiscordLogger
 */
public class GodsEyeDiscordLogger extends JavaPlugin implements Listener {


	@Override
	public void onEnable() {
		super.onEnable();

		getConfig().options().copyDefaults();
		saveDefaultConfig();

		Bukkit.getPluginManager().registerEvents(this, this);

		loadConstants();

		getLogger().info(ChatColor.GREEN + "Thanks for using GodsEyeDiscordLogger :)");
		
	}

	@Override
	public void onDisable() {
		super.onDisable();

		getLogger().info(ChatColor.GREEN + "Thanks for using GodsEyeDiscordLogger :)");
	}


	@EventHandler
	public void onPlayerAlertEvent(final GodsEyeAlertEvent event) {
		final String name = event.getPlayer().getName();
		final DiscordWebhook webhook = new DiscordWebhook(Constants.Alert.Webhook);
		webhook.addEmbed(new DiscordWebhook.EmbedObject()
				.setTitle(Constants.Alert.Title.replace("{player}", name)
						.replace("{checktype}", event.getCheckType().getName()))
				.setDescription(Constants.Alert.Description
						.replace("{player}", name)
						.replace("{checktype}", event.getCheckType().getName())
						.replace("{vl}", String.valueOf(event.getViolationCount())))
		);

		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				try {
					webhook.execute();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

	@EventHandler
	public void onPlayerPunishEvent(final GodsEyePunishPlayerEvent event) {
		final String name = event.getPlayer().getName();
		final String checkType = event.getCheckType().getName();
		final DiscordWebhook webhook = new DiscordWebhook(Constants.Punish.Webhook);
		webhook.addEmbed(new DiscordWebhook.EmbedObject()
				.setTitle(Constants.Punish.Title
						.replace("{player}", name)
						.replace("{checktype}", checkType))
				.setDescription(Constants.Punish.Description
						.replace("{player}", name)
						.replace("{checktype}", checkType))
		);

		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				try {
					webhook.execute();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		});
	}


	private void loadConstants() {
		final FileConfiguration con = getConfig();

		Arrays.asList(Constants.Alert.class.getFields()).forEach(field -> {
			try {
				field.set(String.class, con.getString("Alert." + field.getName()));
			} catch (final IllegalAccessException ignore) {
			}
		});

		Arrays.asList(Constants.Punish.class.getFields()).forEach(field -> {
			try {
				field.set(String.class, con.getString("Punish." + field.getName()));
			} catch (final IllegalAccessException ignore) {
			}
		});


	}
}
