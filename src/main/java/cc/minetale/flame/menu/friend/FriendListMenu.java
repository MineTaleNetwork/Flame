package cc.minetale.flame.menu.friend;

import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.template.PaginatedMenu;
import cc.minetale.sodium.profile.RedisProfile;
import cc.minetale.sodium.util.Colors;
import cc.minetale.sodium.util.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FriendListMenu extends PaginatedMenu {

    private final List<RedisProfile> friends;

    public FriendListMenu(Player player, List<RedisProfile> friends) {
        super(player, Component.text("Your Friends"), CanvasType.FIVE_ROW);

        this.friends = friends;
    }

    @Override
    public Fragment[] getPaginatedFragments(Player player) {
        var sorter = (Comparator<RedisProfile>) (profile1, profile2) -> {
            var onlineCompare = Boolean.compare(profile2.getServer() != null, profile1.getServer() != null);

            if (onlineCompare != 0) { return onlineCompare; }

            var rank1 = profile1.getProfile().getGrant().getRank();
            var rank2 = profile2.getProfile().getGrant().getRank();

            return Integer.compare(rank1.ordinal(), rank2.ordinal());
        };

        friends.sort(sorter);

        return friends.stream()
                .map(friend -> {
                    Component status;

                    if(friend.getServer() != null) {
                        status = Component.text(" is currently playing " + friend.getServer(), Message.style(Colors.GREEN));
                    } else {
                        status = Component.text(" is currently offline", Colors.RED);
                    }

                    return Fragment.of(ItemStack.of(Material.PLAYER_HEAD)
                            .withDisplayName(friend.getProfile().getChatFormat())
                            .withLore(Arrays.asList(
                                    Message.menuSeparator(),
                                    Component.text().append(
                                            Component.text("● ", Message.style(Colors.MIDNIGHT)),
                                            friend.getProfile().getChatFormat(),
                                            status
                                    ).build(),
                                    Component.empty(),
                                    Component.text().append(
                                            Component.text("SHIFT + LMB", Message.style(NamedTextColor.YELLOW, TextDecoration.BOLD)),
                                            Component.text(" to remove this friend", Message.style(NamedTextColor.YELLOW))
                                    ).build(),
                                    Message.menuSeparator()
                            )), event -> {
                        if(event.getClickType() == ClickType.SHIFT_CLICK) {
                            // TODO
                        }
                    });
                }).toArray(Fragment[]::new);
    }

}
