package cc.minetale.flame.commands.essentials.friend;

import cc.minetale.flame.menu.friend.FriendListMenu;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.flame.util.SubCommand;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.profile.ProfileUtil;
import cc.minetale.sodium.util.Message;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;

@SubCommand
public class FriendListCommand extends Command {

    public FriendListCommand() {
        super("list");

        setDefaultExecutor(this::defaultExecutor);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            var profile = FlamePlayer.fromPlayer(player).getProfile();
            var friendUuids = profile.getFriends();

            if (friendUuids.size() == 0) {
                player.sendMessage(Message.parse(Language.Friend.NO_FRIENDS));
                return;
            }

            ProfileUtil.getProfiles(friendUuids)
                    .thenAccept(friends -> {
                        Menu.openMenu(new FriendListMenu(player, friends));
                    });
        }
    }

}
