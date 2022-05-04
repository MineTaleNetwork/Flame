package cc.minetale.flame.menu.friend;

import cc.minetale.flame.commands.essentials.friend.FriendRequestsCommand;
import cc.minetale.mlib.canvas.CanvasType;
import cc.minetale.mlib.canvas.Fragment;
import cc.minetale.mlib.canvas.template.PaginatedMenu;
import cc.minetale.sodium.profile.RedisProfile;
import cc.minetale.sodium.util.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

public class FriendRequestsMenu extends PaginatedMenu {

    private final List<RedisProfile> friends;

    public FriendRequestsMenu(Player player, FriendRequestsCommand.RequestType type, List<RedisProfile> friends) {
        super(player, Component.text(type.getReadable() + " Friend Requests"), CanvasType.FIVE_ROW);

        this.friends = friends;
    }

    @Override
    public Fragment[] getPaginatedFragments(Player player) {

        //            var sorter = (Comparator<Profile>) (profile1, profile2) -> {
//                var rank1 = profile1.getGrant().getRank();
//                var rank2 = profile2.getGrant().getRank();
//
//                return Integer.compare(rank1.ordinal(), rank2.ordinal());
//            };

        return friends.stream()
                .map(friend -> Fragment.of(ItemStack.of(Material.PLAYER_HEAD)
                        .withDisplayName(friend.getProfile().getChatFormat())
                        .withLore(List.of(
                                Component.text().append(
                                        Component.text("Click to Cancel", Message.style(NamedTextColor.YELLOW, TextDecoration.BOLD))
                                ).build()
                        )), event -> {
                    // TODO
                })).toArray(Fragment[]::new);
    }

}
