package cc.minetale.flame.menu.punishment;

import cc.minetale.flame.procedure.Procedure;
import cc.minetale.flame.procedure.PunishmentProcedure;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.mlib.canvas.template.PaginatedMenu;
import cc.minetale.sodium.profile.Profile;
import cc.minetale.sodium.profile.RedisProfile;
import cc.minetale.sodium.profile.grant.Rank;
import cc.minetale.sodium.profile.punishment.PunishmentType;
import cc.minetale.sodium.util.Message;
import cc.minetale.sodium.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.*;

public class PunishmentsMenu extends PaginatedMenu {

    private final Profile profile;
    private final PunishmentType type;
    private final Map<UUID, RedisProfile> profiles;

    public PunishmentsMenu(Player player, Profile profile, PunishmentType type, Map<UUID, RedisProfile> profiles) {
        super(player, Component.text(player.getName() + "'s Punishments"), CanvasType.FIVE_ROW);

        this.profile = profile;
        this.type = type;
        this.profiles = profiles;
    }

    @Override
    public Fragment[] getPaginatedFragments(Player player) {
        var playerProfile = FlamePlayer.fromPlayer(getPlayer()).getProfile();

        var punishments = profile.getPunishments(type);

        return punishments.stream().map(punishment -> {
            var addedById = punishment.getAddedById();
            var addedBy = (addedById == null ?
                    "Console" :
                    profiles.get(addedById) == null ?
                            "Could not fetch..." :
                            profiles.get(addedById).getProfile().getUsername());

            var removedById = punishment.getRemovedById();
            var removedBy = (removedById == null ?
                    "Console" :
                    profiles.get(removedById) == null ?
                            "Could not fetch..." :
                            profiles.get(removedById).getProfile().getUsername());

            var lore = new ArrayList<>(List.of(
                    Message.menuSeparator(),
                    Component.text().append(
                            Component.text("Added On: ", Message.style(NamedTextColor.GRAY)),
                            Component.text(TimeUtil.dateToString(new Date(punishment.getAddedAt()), true), Message.style(NamedTextColor.RED))
                    ).build(),
                    Component.text().append(
                            Component.text("Added By: ", Message.style(NamedTextColor.GRAY)),
                            Component.text(addedBy, Message.style(NamedTextColor.RED))
                    ).build(),
                    Component.text().append(
                            Component.text("Added Reason: ", Message.style(NamedTextColor.GRAY)),
                            Component.text(punishment.getAddedReason(), Message.style(NamedTextColor.RED))
                    ).build(),
                    Component.text().append(
                            Component.text("Duration: ", Message.style(NamedTextColor.GRAY)),
                            Component.text(punishment.getDurationText(), Message.style(NamedTextColor.RED))
                    ).build()

            ));

            if (!punishment.isPermanent() && punishment.isActive()) {
                lore.add(Component.text().append(
                        Component.text("Remaining: ", Message.style(NamedTextColor.GRAY)),
                        Component.text(punishment.getTimeRemaining(), Message.style(NamedTextColor.RED))
                ).build());
            }

            if (punishment.isRemoved()) {
                var removedLore = List.of(
                        Message.menuSeparator(),
                        Component.text().append(
                                Component.text("Removed On: ", Message.style(NamedTextColor.GRAY)),
                                Component.text(TimeUtil.dateToString(new Date(punishment.getRemovedAt()), true), Message.style(NamedTextColor.RED))
                        ).build(),
                        Component.text().append(
                                Component.text("Removed By: ", Message.style(NamedTextColor.GRAY)),
                                Component.text(removedBy, Message.style(NamedTextColor.RED))
                        ).build(),
                        Component.text().append(
                                Component.text("Removed Reason: ", Message.style(NamedTextColor.GRAY)),
                                Component.text(punishment.getRemovedReason(), Message.style(NamedTextColor.RED))
                        ).build()
                );

                lore.addAll(removedLore);
            } else {
                if (punishment.isActive() && Procedure.canStartProcedure(player.getUuid()) && Rank.hasMinimumRank(playerProfile, Rank.ADMIN)) {
                    var removePunishmentLore = List.of(
                            Message.menuSeparator(),
                            Component.text("Click to remove this Punishment", Message.style(NamedTextColor.RED))
                    );

                    lore.addAll(removePunishmentLore);
                }
            }

            lore.add(Message.menuSeparator());

            return Fragment.of(ItemStack.of(Material.BOOK)
                    .withDisplayName(Component.text().append(
                            Component.text(type.getReadable(), Message.style(NamedTextColor.RED)),
                            Component.text(" (", Message.style(NamedTextColor.DARK_GRAY)),
                            Component.text(punishment.getUuid(), Message.style(NamedTextColor.RED)),
                            Component.text(")", Message.style(NamedTextColor.DARK_GRAY))
                    ).build())
                    .withLore(lore)
                    .withMeta(meta -> {
                        if (punishment.isActive())
                            meta.enchantment(Enchantment.UNBREAKING, (short) 1)
                                    .hideFlag(ItemHideFlag.HIDE_ENCHANTS);
                    }), event -> {
                if (punishment.isActive() && Procedure.canStartProcedure(player.getUuid()) && Rank.hasMinimumRank(playerProfile, Rank.ADMIN)) {
                    var procedure = new PunishmentProcedure(player.getUuid(), profile, Procedure.Type.REMOVE, Procedure.Stage.PROVIDE_REASON);
                    procedure.setPunishment(punishment.getUuid());

                    Menu.openMenu(new PunishmentReasonMenu(player, procedure));
                }
            });
        }).toArray(Fragment[]::new);
    }

}
