package cc.minetale.flame.menu.grant;

import cc.minetale.commonlib.api.Grant;
import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.TimeUtil;
import cc.minetale.flame.FlameAPI;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.flame.util.ProfileUtil;
import cc.minetale.mlib.canvas.*;
import cc.minetale.mlib.util.ColorUtil;
import cc.minetale.mlib.util.MenuUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.ExecutionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GrantsMenu extends Menu {

    public GrantsMenu(Player player, Profile profile) {
        super(player, Component.text("Grants"), CanvasType.FOUR_ROW);

        var playerProfile = FlamePlayer.fromPlayer(player).getProfile();

        setFiller(FillingType.BORDER);

        setFragment(30, MenuUtil.PREVIOUS_PAGE(this));
        setFragment(32, MenuUtil.NEXT_PAGE(this));

        var grants = new ArrayList<>(profile.getCachedGrants());
        grants.removeIf(grant -> grant.getRank() == Rank.MEMBER);

        var pagination = new Pagination(10, 14);
        var fragments = new Fragment[grants.size()];

        List<Grant> sortedGrants = new ArrayList<>();
        List<Grant> activeGrants = new ArrayList<>();
        List<Grant> removedGrants = new ArrayList<>();

        for (var grant : grants) {
            if (grant.isRemoved()) {
                removedGrants.add(grant);
            } else {
                activeGrants.add(grant);
            }
        }

        activeGrants.sort(Grant.COMPARATOR);
        removedGrants.sort(Grant.COMPARATOR);

        sortedGrants.addAll(activeGrants);
        sortedGrants.addAll(removedGrants);

        MinecraftServer.getSchedulerManager().buildTask(() -> {
            int i = 0;

            for (var grant : grants) {
                try {
                    var rank = grant.getRank();
                    var color = rank.getColor();

                    var addedBy = "Console";
                    var addedById = grant.getAddedById();

                    if (addedById != null) {
                        addedBy = "Could not fetch...";

                        var addedByProfile = ProfileUtil.getProfile(addedById).get(5, TimeUnit.SECONDS);

                        if (addedByProfile != null)
                            addedBy = addedByProfile.getName();
                    }

                    var removedBy = "Console";
                    var removedById = grant.getRemovedById();

                    if(removedById != null) {
                        removedBy = "Could not fetch...";

                        Profile removedByProfile = ProfileUtil.getProfile(removedById).get(5, TimeUnit.SECONDS);
                        if(removedByProfile != null)
                            removedBy = removedByProfile.getName();
                    }

                    List<Component> lore = new ArrayList<>(Arrays.asList(
                            MC.SEPARATOR_50,
                            Component.text().append(
                                    Component.text("Added On: ", NamedTextColor.GRAY),
                                    Component.text(TimeUtil.dateToString(new Date(grant.getAddedAt()), true), color)
                            ).build().decoration(TextDecoration.ITALIC, false),
                            Component.text().append(
                                    Component.text("Added By: ", NamedTextColor.GRAY),
                                    Component.text(addedBy, color)
                            ).build().decoration(TextDecoration.ITALIC, false),
                            Component.text().append(
                                    Component.text("Added Reason: ", NamedTextColor.GRAY),
                                    Component.text(grant.getAddedReason(), color)
                            ).build().decoration(TextDecoration.ITALIC, false),
                            Component.text().append(
                                    Component.text("Duration: ", NamedTextColor.GRAY),
                                    Component.text(grant.getDurationText(), color)
                            ).build().decoration(TextDecoration.ITALIC, false),
                            MC.SEPARATOR_50
                    ));

                    boolean temporary = !grant.isPermanent() && grant.isActive();

                    if(temporary) {
                            lore.add(3, Component.text().append(
                                    Component.text("Remaining: ", NamedTextColor.GRAY),
                                    Component.text(grant.getTimeRemaining(), color)
                            ).build().decoration(TextDecoration.ITALIC, false));
                    }

                    if(grant.isRemoved()) {
                        List<Component> removedLore = Arrays.asList(
                                MC.SEPARATOR_50,
                                Component.text().append(
                                        Component.text("Removed On: ", NamedTextColor.GRAY),
                                        Component.text(TimeUtil.dateToString(new Date(grant.getRemovedAt()), true), color)
                                ).build().decoration(TextDecoration.ITALIC, false),
                                Component.text().append(
                                        Component.text("Removed By: ", NamedTextColor.GRAY),
                                        Component.text(removedBy, color)
                                ).build().decoration(TextDecoration.ITALIC, false),
                                Component.text().append(
                                        Component.text("Removed Reason: ", NamedTextColor.GRAY),
                                        Component.text(grant.getRemovedReason(), color)
                                ).build().decoration(TextDecoration.ITALIC, false)
                        );

                        lore.addAll((temporary ? 6 : 5), removedLore);
                    } else {
                        if(grant.isActive() && FlameAPI.canStartProcedure(player) && Rank.hasMinimumRank(playerProfile, Rank.ADMIN)) {
                            List<Component> removeGrantLore = Arrays.asList(
                                    Component.text("Click to remove this Grant", Style.style(color, TextDecoration.ITALIC.as(false))),
                                    MC.SEPARATOR_50
                            );

                            lore.addAll(lore.size(), removeGrantLore);
                        }
                    }

                    fragments[i] = Fragment.of(ItemStack.of(ColorUtil.toConcrete(color))
                            .withDisplayName(Component.text().append(
                                    Component.text(rank.getName(), color),
                                    Component.text(" (", NamedTextColor.DARK_GRAY),
                                    Component.text(grant.getId(), color),
                                    Component.text(")", NamedTextColor.DARK_GRAY)
                            ).build().decoration(TextDecoration.ITALIC, false))
                            .withLore(lore)
                            .withMeta(meta -> {
                                if(grant.isActive()) {
                                    meta.enchantment(Enchantment.UNBREAKING, (short) 1)
                                            .hideFlag(ItemHideFlag.HIDE_ENCHANTS);
                                }

                                return meta;
                            }), event -> {
                        if(grant.isActive() && FlameAPI.canStartProcedure(player) && Rank.hasMinimumRank(playerProfile, Rank.ADMIN)) {
//                            GrantProcedure procedure = new GrantProcedure(player, profile, GrantProcedure.Type.REMOVE, GrantProcedure.Stage.PROVIDE_REASON);
//                            procedure.setGrantId(grant.getId());
//
//                            player.sendMessage(Component.text("Type a reason for removing this grant in chat...", NamedTextColor.GREEN));
//                            player.closeInventory();
                        }
                    });
                } catch (ExecutionException | InterruptedException | TimeoutException e) {
                    e.printStackTrace();
                }

                i++;
            }

            pagination.setFragments(fragments);
            setPagination(pagination);

            openMenu();
        }).executionType(ExecutionType.ASYNC).schedule();
    }

    @Override
    public void close() {

    }

}
