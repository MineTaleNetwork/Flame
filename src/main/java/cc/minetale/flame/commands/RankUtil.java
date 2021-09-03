package cc.minetale.flame.commands;

import cc.minetale.mlib.util.ProfileUtil;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;

import java.util.function.Consumer;

public class RankUtil {

   public static void canUseCommand(CommandSender sender, Rank rank, Consumer<RankCallback> callback) {
       if(sender.isConsole()) {
           callback.accept(new RankCallback(null, true, true));
       } else {
           Player player = sender.asPlayer();

           hasMinimumRank(player, rank, rankCallback -> {
               if (!rankCallback.isMinimum()) {
                   if(rank == null) {
                       player.sendMessage(Component.text("Could not execute command! The required rank does not exist.", MC.CC.RED.getTextColor()));
                   } else {
                       player.sendMessage(MC.Chat.notificationMessage("Permission", Component.text()
                               .append(Component.text("You need rank ", MC.CC.GRAY.getTextColor()))
                               .append(Component.text(rank.getName(), MC.CC.GOLD.getTextColor()))
                               .append(Component.text(" to use this command.", MC.CC.GRAY.getTextColor()))
                               .build()));
                   }
               }

               callback.accept(rankCallback);
           });
       }
   }

   public static void hasMinimumRank(Player player, Rank rank, Consumer<RankCallback> callback) {
       ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
           if(rank == null) {
               callback.accept(new RankCallback(profile, false, false));
               return;
           }

           if(profile.api().getActiveGrant().api().getRank().getWeight() > rank.getWeight()) {
               callback.accept(new RankCallback(profile, false, false));
           } else {
               callback.accept(new RankCallback(profile, true, false));
           }
       });
   }

}
