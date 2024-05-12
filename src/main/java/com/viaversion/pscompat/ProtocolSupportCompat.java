/*
 * This file is part of ViaVersion - https://github.com/ViaVersion/ViaVersion
 * Copyright (C) 2016-2024 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.viaversion.pscompat;

import com.viaversion.viaversion.bukkit.util.NMSUtil;
import java.util.logging.Level;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public final class ProtocolSupportCompat {

    public static void registerPSConnectListener(final Plugin plugin) {
        plugin.getLogger().info("Registering ProtocolSupport compat connection listener");
        try {
            //noinspection unchecked
            final Class<? extends Event> connectionOpenEvent = (Class<? extends Event>) Class.forName("protocolsupport.api.events.ConnectionOpenEvent");
            plugin.getServer().getPluginManager().registerEvent(connectionOpenEvent, new Listener() {
            }, EventPriority.HIGH, (listener, event) -> {
                try {
                    final Object connection = event.getClass().getMethod("getConnection").invoke(event);
                    final ProtocolSupportConnectionListener connectListener = new ProtocolSupportConnectionListener(connection);
                    ProtocolSupportConnectionListener.ADD_PACKET_LISTENER_METHOD.invoke(connection, connectListener);
                } catch (final ReflectiveOperationException e) {
                    plugin.getLogger().log(Level.WARNING, "Error when handling ProtocolSupport event", e);
                }
            }, plugin);
        } catch (final ClassNotFoundException e) {
            plugin.getLogger().log(Level.WARNING, "Unable to register ProtocolSupport listener", e);
        }
    }

    public static boolean isMultiplatformPS() {
        try {
            Class.forName("protocolsupport.zplatform.impl.spigot.network.pipeline.SpigotPacketEncoder");
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    static HandshakeProtocolType handshakeVersionMethod() {
        Class<?> clazz = null;
        // Check for the mapped method
        try {
            clazz = NMSUtil.nms(
                    "PacketHandshakingInSetProtocol",
                    "net.minecraft.network.protocol.handshake.PacketHandshakingInSetProtocol"
            );
            clazz.getMethod("getProtocolVersion");
            return HandshakeProtocolType.MAPPED;
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (final NoSuchMethodException ignored) {
        }

        // Check for obfuscated b/c methods
        try {
            if (clazz.getMethod("b").getReturnType() == int.class) {
                return HandshakeProtocolType.OBFUSCATED_B;
            } else if (clazz.getMethod("c").getReturnType() == int.class) {
                return HandshakeProtocolType.OBFUSCATED_C;
            }
            throw new UnsupportedOperationException("Protocol version method not found in " + clazz.getSimpleName());
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    enum HandshakeProtocolType {

        MAPPED("getProtocolVersion"),
        OBFUSCATED_B("b"),
        OBFUSCATED_C("c");

        private final String methodName;

        HandshakeProtocolType(final String methodName) {
            this.methodName = methodName;
        }

        public String methodName() {
            return methodName;
        }
    }
}
