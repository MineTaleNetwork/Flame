package cc.minetale.flame.commands.essentials.conversation;

import cc.minetale.flame.util.CommandUtil;
import cc.minetale.postman.Postman;
import cc.minetale.sodium.cache.ProfileCache;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.payloads.ConversationPayload;
import cc.minetale.sodium.profile.ProfileUtil;
import cc.minetale.sodium.util.Message;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class ReplyCommand extends Command {

    public ReplyCommand() {
        super("reply", "r");

        setDefaultExecutor((sender, context) -> sender.sendMessage(CommandUtil.getUsage(getName(), "message")));

        addSyntax((sender, context) -> {
            if (sender instanceof Player player) {
                var message = String.join(" ", (String[]) context.get("message"));

                var playerCachedProfile = ProfileUtil.fromCache(player.getUuid());

                if (playerCachedProfile == null) {
                    sender.sendMessage(Message.parse(Language.Error.PLAYER_NETWORK));
                    return;
                }

                if (playerCachedProfile.getLastMessaged() == null) {
                    sender.sendMessage(Message.parse(Language.Conversation.UNKNOWN_CONVERSATION));
                    return;
                }

                var targetCachedProfile = ProfileUtil.fromCache(playerCachedProfile.getLastMessaged());

                if (targetCachedProfile == null || targetCachedProfile.getServer() == null) {
                    sender.sendMessage(Message.parse(Language.Error.PLAYER_OFFLINE));
                    return;
                }

                var playerProfile = playerCachedProfile.getProfile();
                var targetProfile = targetCachedProfile.getProfile();

                if (playerProfile.equals(targetProfile)) {
                    player.sendMessage(Message.parse(Language.Conversation.SELF_TARGET));
                    return;
                }

                if (playerProfile.isIgnoring(targetProfile)) {
                    player.sendMessage(Message.parse(Language.Conversation.TARGET_IGNORED));
                    return;
                }

                if (targetProfile.isIgnoring(playerProfile) || !targetProfile.getOptionsProfile().isReceivingConversations()) {
                    player.sendMessage(Message.parse(Language.Conversation.TARGET_TOGGLED));
                    return;
                }

                var target = MinecraftServer.getConnectionManager().getPlayer(targetProfile.getUuid());

                playerCachedProfile.setLastMessaged(targetProfile.getUuid());
                targetCachedProfile.setLastMessaged(playerProfile.getUuid());

                ProfileCache.pushCache(playerCachedProfile);
                ProfileCache.pushCache(targetCachedProfile);

                player.sendMessage(Message.parse(Language.Conversation.TO_MSG, targetProfile.getChatFormat(), message));

                if (target != null) {
                    target.sendMessage(Message.parse(Language.Conversation.FROM_MSG, playerProfile.getChatFormat(), message));
                } else {
                    Postman.getPostman().broadcast(new ConversationPayload(playerProfile.getUuid(), targetProfile.getUuid(), message));
                }
            }
        }, ArgumentType.StringArray("message"));
    }

}
