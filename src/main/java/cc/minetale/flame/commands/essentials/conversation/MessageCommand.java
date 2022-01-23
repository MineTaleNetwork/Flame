package cc.minetale.flame.commands.essentials.conversation;

import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.pigeon.payloads.conversation.ConversationMessagePayload;
import cc.minetale.commonlib.util.Message;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.commonlib.util.ProfileUtil;
import cc.minetale.flame.util.CommandUtil;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MessageCommand extends Command {

    public MessageCommand() {
        super("message", "msg", "whisper", "tell", "t");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::onGrantsCommand, ArgumentType.Word("player"), ArgumentType.StringArray("message"));
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getUsage("message", "player", "message"));
    }

    private void onGrantsCommand(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            CompletableFuture.runAsync(() -> {
                var message = String.join(" ", (String[]) context.get("message"));

                try {
                    var playerCachedProfile = ProfileUtil.fromCache(player.getUuid()).get();
                    var targetCachedProfile = ProfileUtil.fromCache((String) context.get("player")).get();

                    if(playerCachedProfile == null) {
                        sender.sendMessage(Message.parse(Language.Error.NETWORK_ERROR));
                        return;
                    }

                    if(targetCachedProfile == null || targetCachedProfile.getServer() == null) {
                        sender.sendMessage(Message.parse(Language.Error.PLAYER_OFFLINE_ERROR));
                        return;
                    }

                    var playerProfile = playerCachedProfile.getProfile();
                    var targetProfile = targetCachedProfile.getProfile();

                    if (playerProfile.equals(targetProfile)) {
                        player.sendMessage(Message.parse(Language.Conversation.TARGET_IS_PLAYER));
                        return;
                    }

                    if(playerProfile.isIgnoring(targetProfile)) {
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

                    ProfileCache.updateCache(playerCachedProfile);
                    ProfileCache.updateCache(targetCachedProfile);

                    player.sendMessage(Message.parse(Language.Conversation.TO_MSG, targetProfile.getChatFormat(), message));

                    if(target != null) {
                        target.sendMessage(Message.parse(Language.Conversation.FROM_MSG, playerProfile.getChatFormat(), message));
                    } else {
                        PigeonUtil.broadcast(new ConversationMessagePayload(playerProfile.getUuid(), targetProfile.getUuid(), message));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
