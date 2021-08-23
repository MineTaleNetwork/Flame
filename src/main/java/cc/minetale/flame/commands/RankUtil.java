package cc.minetale.flame.commands;

import cc.minetale.mlib.util.ProfileUtil;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

import java.util.function.Consumer;

public class RankUtil {

   public static void canUseCommand(Player player, Rank rank, Consumer<RankCallback> callback) {
       hasMinimumRank(player, rank, rankCallback -> {
           if (!rankCallback.isMinimum()) {
               player.sendMessage(MC.Chat.notificationMessage("Permission", Component.text()
                       .append(Component.text("You need rank ", MC.CC.GRAY.getTextColor()))
                       .append(Component.text(rank.getName(), MC.CC.GOLD.getTextColor()))
                       .append(Component.text(" to use this command.", MC.CC.GRAY.getTextColor()))
                       .build()));
           }

           callback.accept(rankCallback);
       });
   }

   public static void hasMinimumRank(Player player, Rank rank, Consumer<RankCallback> callback) {
       ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
           if(profile.api().getActiveGrant().api().getRank().getWeight() > rank.getWeight()) {
               callback.accept(new RankCallback(profile, false));
           } else {
               callback.accept(new RankCallback(profile, true));
           }
       });
   }

}
