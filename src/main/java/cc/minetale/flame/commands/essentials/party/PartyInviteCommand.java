package cc.minetale.flame.commands.essentials.party;

import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.party.Party;
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
                        player.sendMessage(Message.parse(Language.Party.NO_PARTY));
                        return;
                    }

                    var party = Party.getParty(partyUuid).get();

                    if(party == null) {
                        player.sendMessage(Message.parse(Language.Party.NO_PARTY));
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
                            }

                            var message = Message.parse(Language.Friend.Add.SUCCESS_PLAYER, target.getChatFormat());

                            for(var member : party.getMembers()) {
                                var memberPlayer = MinecraftServer.getConnectionManager().getPlayer(member.player());

                                if(memberPlayer == null) { continue; }

                                memberPlayer.sendMessage(message);
                            }

                            PigeonUtil.broadcast(new PartyRequestCreatePayload(party, profile, target));

                        }
                        case ERROR -> player.sendMessage(Message.parse(Language.Command.COMMAND_EXCEPTION_ERROR));
                        case REQUEST_EXIST -> player.sendMessage(Message.parse(Language.Party.Invite.REQUEST_EXIST, target.getChatFormat()));
                        case ALREADY_IN_PARTY -> player.sendMessage(Message.parse(Language.Party.Invite.IN_PARTY, target.getChatFormat()));
                        case TARGET_IS_PLAYER -> player.sendMessage(Message.parse(Language.Party.Invite.TARGET_IS_PLAYER));
                        case REQUESTS_TOGGLED, PLAYER_IGNORED -> player.sendMessage(Message.parse(Language.Party.Invite.TARGET_TOGGLED));
                        case MAXIMUM_REQUESTS -> player.sendMessage(Message.parse(Language.Party.Invite.PARTY_MAXIMUM_REQUESTS));
                        case TARGET_IGNORED -> player.sendMessage(Message.parse(Language.Party.Invite.TARGET_IGNORED));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    player.sendMessage(Message.parse(Language.Command.COMMAND_EXCEPTION_ERROR));
                    e.printStackTrace();
                }
            });
        }
    }

}
