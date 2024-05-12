package com.viaversion.pscompat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PSCompatPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolSupport") != null && ProtocolSupportCompat.isMultiplatformPS()) {
            ProtocolSupportCompat.registerPSConnectListener(this);
        } else {
            getLogger().severe("ProtocolSupport not found or not multiplatform, not registering ProtocolSupport compat connection listener");
        }
    }
}
