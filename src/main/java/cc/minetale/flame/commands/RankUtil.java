package cc.minetale.flame.commands;

import cc.minetale.commonlib.modules.grant.Grant;
import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.mlib.util.ProfileUtil;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;

import java.util.function.Consumer;

public class RankUtil {

//   public static void hasMinimumRank(Player player, String rankName, Consumer<RankCallback> callback) {
//       ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
//           Rank rank = Rank.getRank(rankName);
//
//           if(rank == null)
//               throw new RuntimeException(rankName + " is not defined or not loaded.");
//
//           if(profile.getGrant().api().getRank().getWeight() > rank.getWeight()) {
//               callback.accept(new RankCallback(profile, rank, false, false));
//           } else {
//               callback.accept(new RankCallback(profile, rank, true, false));
//           }
//       });
//   }

    public static boolean canUseCommand(Profile profile, String rankName) {
        if(hasMinimumRank(profile, rankName)) {
            Player player = MinecraftServer.getConnectionManager().getPlayer(profile.getId());

            if(player != null)
                player.sendMessage(MC.Chat.notificationMessage("Permission", Component.text()
                    .append(Component.text("You need rank ", MC.CC.GRAY.getTextColor()))
                    .append(Component.text(rankName, MC.CC.GOLD.getTextColor()))
                    .append(Component.text(" to use this command.", MC.CC.GRAY.getTextColor()))
                    .build()));
            return true;
        } else {
            return false;
        }
   }

    public static boolean hasMinimumRank(Profile profile, String rankName) {
        Rank rank = Rank.getRank(rankName);

        if(rank == null)
            throw new RuntimeException(rankName + " is not defined or not loaded.");

        Rank playerRank = profile.getGrant().api().getRank();

        return playerRank.getWeight() > rank.getWeight();
    }

}
