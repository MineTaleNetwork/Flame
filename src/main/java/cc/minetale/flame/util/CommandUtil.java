package cc.minetale.flame.util;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.flame.commands.RankUtil;
import cc.minetale.mlib.util.ProfileUtil;
import net.minestom.server.command.builder.condition.CommandCondition;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CommandUtil {

//    public static CommandCondition getRankCondition(String rank) {
//        return ((sender, s) -> {
//            if(sender.isConsole())
//                return true;
//
//            if(sender.isPlayer()) {
//                CompletableFuture<Profile> profileFuture = ProfileUtil.getAssociatedProfile(sender.asPlayer(), true, false);
//
//                return RankUtil.canUseCommand(profileFuture.getNow(null), rank);
//            }
//
//            return false;
//        });
//    }


}
