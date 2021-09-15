package cc.minetale.flame.listeners;

import cc.minetale.commonlib.modules.api.APIListener;
import cc.minetale.commonlib.modules.grant.Grant;
import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.commonlib.modules.punishment.Punishment;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.TimeUtil;
import cc.minetale.flame.team.TeamUtils;
import cc.minetale.flame.util.FlameUtil;
import cc.minetale.mlib.util.ProfileUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

public class CoreListener implements APIListener {

    @Override
    public void grantAdd(Grant grant) {
        Player player = MinecraftServer.getConnectionManager().getPlayer(grant.getPlayerUUID());

        if(player == null) { return; }

        Rank rank = grant.api().getRank();

        ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
            if(profile == null) { return; }

            if(rank != null && !rank.api().isDefaultRank()) {
                player.sendMessage(MC.Style.SEPARATOR_80);
                player.sendMessage(MC.Chat.notificationMessage("Grant",
                        Component.text("A '" + rank.getName() + "' grant has been applied to you " +
                                (grant.getDuration() == Integer.MAX_VALUE ? "permanently" : "for " + TimeUtil.millisToRoundedTime(grant.getDuration())) + "."))
                        .color(NamedTextColor.GRAY));
                player.sendMessage(MC.Style.SEPARATOR_80);

                TeamUtils.updateTeam(rank, player);
            }

            profile.reloadGrant();
        });
    }

    @Override
    public void grantExpire(Grant grant) {
        Player player = MinecraftServer.getConnectionManager().getPlayer(grant.getPlayerUUID());

        if(player == null) { return; }

        Rank rank = grant.api().getRank();

        ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
            if(rank != null) {
                player.sendMessage(MC.Style.SEPARATOR_80);
                player.sendMessage(MC.Chat.notificationMessage("Grant",
                        Component.text("Your '" + rank.getName() + "' grant has expired.")
                                .color(NamedTextColor.GRAY)));
                player.sendMessage(MC.Style.SEPARATOR_80);

                TeamUtils.updateTeam(rank, player);
            }

            profile.reloadGrant();
        });
    }

    @Override
    public void grantRemove(Grant grant) {
        Player player = MinecraftServer.getConnectionManager().getPlayer(grant.getPlayerUUID());

        if(player == null) { return; }

        Rank rank = grant.api().getRank();

        ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
            if(rank != null) {
                player.sendMessage(MC.Style.SEPARATOR_80);
                player.sendMessage(MC.Chat.notificationMessage("Grant",
                        Component.text("Your '" + rank.getName() + "' grant has been removed.")
                                .color(NamedTextColor.GRAY)));
                player.sendMessage(MC.Style.SEPARATOR_80);

                TeamUtils.updateTeam(rank, player);
            }

            profile.reloadGrant();
        });
    }

    public void announcePunishment(Profile receiverProfile, Punishment punishment) {
        Profile.getProfile(punishment.getAddedByUUID()).thenAccept(profile -> {
            FlameUtil.broadcast("Helper",
                    MC.Style.SEPARATOR_80,
                    MC.Chat.notificationMessage("Punishment",
                            MC.component(
                                    receiverProfile.api().getColoredName(),
                                    MC.component(" has been " + punishment.api().getContext() + " by ", MC.CC.GRAY),
                                    (punishment.getAddedByUUID() != null ? profile.api().getColoredName() : MC.Style.CONSOLE)
                            )
                    ),
                    MC.Style.SEPARATOR_80
            );
        });
    }

    @Override
    public void punishmentAdd(Punishment punishment) {
        Player player = MinecraftServer.getConnectionManager().getPlayer(punishment.getPlayerUUID());

        if(player == null) { return; }

        ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
            announcePunishment(profile, punishment);

            if(!punishment.isRemoved()) {
                if (punishment.getType() == Punishment.Type.BAN || punishment.getType() == Punishment.Type.BLACKLIST) {
                    player.kick(Component.join(Component.newline(), FlameUtil.getPunishmentMessage(punishment)));
                } else if (punishment.getType() == Punishment.Type.MUTE) {
                    FlameUtil.getPunishmentMessage(punishment).forEach(player::sendMessage);
                    FlameUtil.playErrorSound(player);
                }
            }
        });
    }

    @Override
    public void punishmentExpire(Punishment punishment) {
        ProfileUtil.getProfileById(punishment.getPlayerUUID()).thenAccept(profile -> {
            if(profile == null) { return; }
            announcePunishment(profile, punishment);
        });
    }

    @Override
    public void punishmentRemove(Punishment punishment) {
        ProfileUtil.getProfileById(punishment.getPlayerUUID()).thenAccept(profile -> {
            if (profile == null) { return; }
            announcePunishment(profile, punishment);
        });
    }

}
