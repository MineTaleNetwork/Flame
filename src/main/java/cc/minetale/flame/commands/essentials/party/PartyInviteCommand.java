package cc.minetale.flame.commands.essentials.party;

import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.party.Party;
import cc.minetale.commonlib.pigeon.payloads.friend.FriendRequestCreatePayload;
import cc.minetale.commonlib.pigeon.payloads.party.PartyRequestCreatePayload;
import cc.minetale.commonlib.util.Cache;
import cc.minetale.commonlib.util.Message;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.commonlib.util.ProfileUtil;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.SubCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SubCommand
public class PartyInviteCommand extends Command {

    public PartyInviteCommand() {
        super("invite");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::onPartyInvite, ArgumentType.Word("player"));
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getUsage("party invite", "player"));
    }

    private void onPartyInvite(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            var playerUuid = player.getUuid();

            CompletableFuture.runAsync(() -> {
                try {
                    var cachedProfile = ProfileUtil.getCachedProfile(playerUuid).get();

                    if(cachedProfile == null) {
                        player.sendMessage(Message.parse(Language.Error.NETWORK_ERROR));
                        return;
                    }

                    var partyUuid = cachedProfile.getParty();

                    if(partyUuid == null) {
                        // TODO -> Not in a party
                        return;
                    }

                    var party = Party.getParty(partyUuid).get();

                    if(party == null) {
                        // TODO -> Not in a party
                        Cache.getProfileCache().updateParty(playerUuid, null);
                        return;
                    }

                    var target = ProfileUtil.getProfile((String) context.get("player")).get();

                    if(target == null) {
                        player.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER_ERROR));
                        return;
                    }

                    var profile = cachedProfile.getProfile();
                    var response = party.invitePlayer(cachedProfile.getProfile(), target).get();

                    switch (response) {
                        case SUCCESS -> {
                            var targetPlayer = MinecraftServer.getConnectionManager().getPlayer(target.getUuid());

                            if (targetPlayer != null) {
                                targetPlayer.sendMessage(Message.parse(Language.Party.Invite.SUCCESS_TARGET, profile.getChatFormat()));
                            } else {
                                // TODO -> Success
//                                PigeonUtil.broadcast(new PartyRequestCreatePayload(profile, target.getUuid()));
                            }

                            var message = Message.parse(Language.Friend.Add.SUCCESS_PLAYER, target.getChatFormat());

                            for(var member : party.getMembers()) {
                                var memberPlayer = MinecraftServer.getConnectionManager().getPlayer(member.player());

                                if(memberPlayer == null) { continue; }

                                memberPlayer.sendMessage(message);
                            }

                        }
                        case ERROR -> sender.sendMessage(Message.parse(Language.Command.COMMAND_EXCEPTION_ERROR));
                        // TODO -> Add more cases and messages
                    }
                } catch (InterruptedException | ExecutionException e) {
                    player.sendMessage(Message.parse(Language.Command.COMMAND_EXCEPTION_ERROR));
                    e.printStackTrace();
                }
            });
        }
    }

}
