package cc.minetale.flame.listeners;

import cc.minetale.commonlib.api.APIListener;
import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.punishment.Punishment;
import cc.minetale.commonlib.rank.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.TimeUtil;
import cc.minetale.flame.Lang;
import cc.minetale.flame.team.TeamUtils;
import cc.minetale.flame.util.FlameUtil;
import cc.minetale.mlib.util.ProfileUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Override
    public void punishmentAdd(Punishment punishment) {
        Player player = MinecraftServer.getConnectionManager().getPlayer(punishment.getPlayerUUID());

        announcePunishment(punishment);

        if(player == null) { return; }

        ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
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
        announcePunishment(punishment);
    }

    @Override
    public void punishmentRemove(Punishment punishment) {
        announcePunishment(punishment);
    }


    public void announcePunishment(Punishment punishment) {
        List<UUID> profileIds = new ArrayList<>();

        UUID target = punishment.getPlayerUUID();

        profileIds.add(target);

        UUID initiator = punishment.getAddedByUUID();

        if(initiator != null)
            profileIds.add(initiator);

        ProfileUtil.getProfilesByIds(profileIds).thenAccept(profiles -> {
            Profile targetProfile = null;
            Profile initiatorProfile = null;

            for(Profile profile : profiles) {
                if(profile.getId().equals(target)) {
                    targetProfile = profile;
                } else if(profile.getId().equals(initiator)) {
                    initiatorProfile = profile;
                }
            }

            if(targetProfile != null)
                FlameUtil.broadcast("Helper",
                        MC.Style.SEPARATOR_80,
                        Lang.ANNOUNCE_PUNISHMENT_CONTEXT(targetProfile, initiatorProfile, punishment),
                        MC.Style.SEPARATOR_80
                );
        });
    }

}
