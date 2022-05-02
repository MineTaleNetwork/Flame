package cc.minetale.flame.menu.grant;

import cc.minetale.flame.FlameAPI;
import cc.minetale.flame.procedure.GrantProcedure;
import cc.minetale.flame.procedure.Procedure;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.template.PaginatedMenu;
import cc.minetale.mlib.util.ColorUtil;
import cc.minetale.sodium.profile.Profile;
import cc.minetale.sodium.profile.RedisProfile;
import cc.minetale.sodium.profile.grant.Rank;
import cc.minetale.sodium.util.Message;
import cc.minetale.sodium.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;

import java.util.*;

public class GrantsMenu extends PaginatedMenu {

    private final Profile profile;
    private final Map<UUID, RedisProfile> profiles;

    public GrantsMenu(Player player, Profile profile, Map<UUID, RedisProfile> profiles) {
        super(player, Component.text("Grants"), CanvasType.FOUR_ROW);

        this.profile = profile;
        this.profiles = profiles;
    }

    @Override
    public Fragment[] getPaginatedFragments(Player player) {
        var playerProfile = FlamePlayer.fromPlayer(getPlayer()).getProfile();

        var grants = new ArrayList<>(profile.getSortedGrants());
        grants.removeIf(grant -> grant.getRank() == Rank.MEMBER);

        return grants.stream().map(grant -> {
            var rank = grant.getRank();
            var color = rank.getColor();

            var addedById = grant.getAddedById();
            var addedBy = (addedById == null ?
                    "Console" :
                    profiles.get(addedById) == null ?
                            "Could not fetch..." :
                            profiles.get(addedById).getProfile().getUsername());

            var removedById = grant.getRemovedById();
            var removedBy = (removedById == null ?
                    "Console" :
                    profiles.get(removedById) == null ?
                            "Could not fetch..." :
                            profiles.get(removedById).getProfile().getUsername());

            var lore = new ArrayList<>(List.of(
                    Message.menuSeparator(),
                    Component.text().append(
                            Component.text("Added On: ", Message.style(NamedTextColor.GRAY)),
                            Component.text(TimeUtil.dateToString(new Date(grant.getAddedAt()), true), Message.style(color))
                    ).build(),
                    Component.text().append(
                            Component.text("Added By: ", Message.style(NamedTextColor.GRAY)),
                            Component.text(addedBy, Message.style(color))
                    ).build(),
                    Component.text().append(
                            Component.text("Added Reason: ", Message.style(NamedTextColor.GRAY)),
                            Component.text(grant.getAddedReason(), Message.style(color))
                    ).build(),
                    Component.text().append(
                            Component.text("Duration: ", Message.style(NamedTextColor.GRAY)),
                            Component.text(grant.getDurationText(), Message.style(color))
                    ).build()

            ));

            if (!grant.isPermanent() && grant.isActive()) {
                lore.add(Component.text().append(
                        Component.text("Remaining: ", Message.style(NamedTextColor.GRAY)),
                        Component.text(grant.getTimeRemaining(), Message.style(color))
                ).build());
            }

            if (grant.isRemoved()) {
                var removedLore = List.of(
                        Message.menuSeparator(),
                        Component.text().append(
                                Component.text("Removed On: ", Message.style(NamedTextColor.GRAY)),
                                Component.text(TimeUtil.dateToString(new Date(grant.getRemovedAt()), true), Message.style(color))
                        ).build(),
                        Component.text().append(
                                Component.text("Removed By: ", Message.style(NamedTextColor.GRAY)),
                                Component.text(removedBy, Message.style(color))
                        ).build(),
                        Component.text().append(
                                Component.text("Removed Reason: ", Message.style(NamedTextColor.GRAY)),
                                Component.text(grant.getRemovedReason(), Message.style(color))
                        ).build()
                );

                lore.addAll(removedLore);
            } else {
                if (grant.isActive() && Procedure.canStartProcedure(player.getUuid()) && Rank.hasMinimumRank(playerProfile, Rank.ADMIN)) {
                    var removeGrantLore = List.of(
                            Message.menuSeparator(),
                            Component.text("Click to remove this Grant", Message.style(color))
                    );

                    lore.addAll(removeGrantLore);
                }
            }

            lore.add(Message.menuSeparator());

            return Fragment.of(ItemStack.of(ColorUtil.toConcrete(color))
                    .withDisplayName(Component.text().append(
                            Component.text(rank.getName(), Message.style(color)),
                            Component.text(" (", Message.style(NamedTextColor.DARK_GRAY)),
                            Component.text(grant.getUuid(), Message.style(color)),
                            Component.text(")", Message.style(NamedTextColor.DARK_GRAY))
                    ).build())
                    .withLore(lore)
                    .withMeta(meta -> {
                        if (grant.isActive())
                            meta.enchantment(Enchantment.UNBREAKING, (short) 1)
                                    .hideFlag(ItemHideFlag.HIDE_ENCHANTS);
                    }), event -> {
                if (grant.isActive() && Procedure.canStartProcedure(player.getUuid()) && Rank.hasMinimumRank(playerProfile, Rank.ADMIN)) {
                    var procedure = new GrantProcedure(player.getUuid(), profile, GrantProcedure.Type.REMOVE, GrantProcedure.Stage.PROVIDE_REASON);
                    procedure.setGrant(grant.getUuid());

                    new GrantReasonMenu(player, procedure);
                }
            });
        }).toArray(Fragment[]::new);
    }

    @Override
    public void close() {}

}
