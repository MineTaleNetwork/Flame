package cc.minetale.flame.util;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.api.LibProvider;
import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.punishment.Punishment;
import cc.minetale.commonlib.util.Message;
import cc.minetale.commonlib.util.TimeUtil;
import cc.minetale.mlib.nametag.NameplateHandler;
import cc.minetale.mlib.nametag.NameplateProvider;
import cc.minetale.mlib.nametag.ProviderType;
import cc.minetale.mlib.util.TeamUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

public class FlameProvider {

    public static void init() {
        CommonLib.getProviders().add(new LibProvider() {
            @Override
            public void addGrant(Grant grant) {
                var player = MinecraftServer.getConnectionManager().getPlayer(grant.getPlayerId());

                if (player != null) {
                    FlameProvider.addGrant(player, grant);
                }
            }

            @Override
            public void removeGrant(Grant grant) {
                var player = MinecraftServer.getConnectionManager().getPlayer(grant.getPlayerId());

                if (player != null) {
                    FlameProvider.removeGrant(player, grant);
                }
            }

            @Override
            public void expireGrant(Grant grant) {
                var player = MinecraftServer.getConnectionManager().getPlayer(grant.getPlayerId());

                if (player != null) {
                    FlameProvider.expireGrant(player, grant);
                }
            }

            @Override
            public void addPunishment(Punishment punishment) {

            }

            @Override
            public void removePunishment(Punishment punishment) {

            }

            @Override
            public void expirePunishment(Punishment punishment) {

            }
        });
    }

    public static void addGrant(Player player, Grant grant) {
        var profile = FlamePlayer.fromPlayer(player).getProfile();
        var rank = grant.getRank();

        profile.activateNextGrant();
        NameplateHandler.addProvider(player, new NameplateProvider(TeamUtil.RANK_MAP.get(profile.getGrant().getRank()), ProviderType.RANK));
        player.refreshCommands();

        player.sendMessage(Message.chatSeparator());
        player.sendMessage(Message.notification("Grant",
                Component.text().append(
                        Component.text("A ", NamedTextColor.GRAY),
                        Component.text(rank.getName(), rank.getColor()),
                        Component.text(" grant has been applied to you " +
                                (grant.getDuration() == Integer.MAX_VALUE ? "permanently" :
                                        "for " + TimeUtil.millisToRoundedTime(grant.getDuration())) + ".", NamedTextColor.GRAY)
                ).build()));
        player.sendMessage(Message.chatSeparator());
    }

    public static void removeGrant(Player player, Grant grant) {
        var profile = FlamePlayer.fromPlayer(player).getProfile();
        var rank = grant.getRank();

        profile.activateNextGrant();
        NameplateHandler.addProvider(player, new NameplateProvider(TeamUtil.RANK_MAP.get(profile.getGrant().getRank()), ProviderType.RANK));
        player.refreshCommands();

        player.sendMessage(Message.chatSeparator());
        player.sendMessage(Message.notification("Grant",
                Component.text().append(
                        Component.text("Your ", NamedTextColor.GRAY),
                        Component.text(rank.getName(), rank.getColor()),
                        Component.text(" grant has been removed.", NamedTextColor.GRAY)
                ).build()));
        player.sendMessage(Message.chatSeparator());
    }

    public static void expireGrant(Player player, Grant grant) {
        var profile = FlamePlayer.fromPlayer(player).getProfile();
        var rank = grant.getRank();

        profile.activateNextGrant();
        NameplateHandler.addProvider(player, new NameplateProvider(TeamUtil.RANK_MAP.get(profile.getGrant().getRank()), ProviderType.RANK));
        player.refreshCommands();

        player.sendMessage(Message.chatSeparator());
        player.sendMessage(Message.notification("Grant",
                Component.text().append(
                        Component.text("Your ", NamedTextColor.GRAY),
                        Component.text(rank.getName(), rank.getColor()),
                        Component.text(" grant has expired.", NamedTextColor.GRAY)
                ).build()));
        player.sendMessage(Message.chatSeparator());
    }

}
