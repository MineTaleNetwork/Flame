package cc.minetale.flame.menu.friend;

import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.template.PaginatedMenu;
import cc.minetale.sodium.profile.RedisProfile;
import cc.minetale.sodium.util.Colors;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
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
                    var lore = new ArrayList<Component>();

                    var online = friend.getServer() != null;
                    var status = online ? "is currently in " + friend.getServer() : "is currently offline";
                    var color = online ? Colors.GREEN : Colors.RED;

                    lore.add(Component.text().append(
                            Component.text("● ", color),
                            friend.getProfile().getChatFormat(),
                            Component.text(" " + status, color)
                    ).build());

                    return Fragment.of(ItemStack.of(Material.PLAYER_HEAD)
                            .withDisplayName(friend.getProfile().getChatFormat())
                            .withLore(lore), event -> {
                        // TODO
                    });
                }).toArray(Fragment[]::new);
    }

}
