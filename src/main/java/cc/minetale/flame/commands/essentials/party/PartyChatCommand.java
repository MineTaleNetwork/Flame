//package cc.minetale.flame.commands.essentials.party;
//
//import cc.minetale.commonlib.lang.Language;
//import cc.minetale.commonlib.party.Party;
//import cc.minetale.commonlib.pigeon.payloads.party.PartyChatPayload;
//import cc.minetale.commonlib.util.Colors;
//import cc.minetale.commonlib.util.Message;
//import cc.minetale.commonlib.util.PigeonUtil;
//import cc.minetale.commonlib.util.ProfileUtil;
//import cc.minetale.flame.util.CommandUtil;
//import net.kyori.adventure.text.Component;
//import net.minestom.server.MinecraftServer;
//import net.minestom.server.command.CommandSender;
//import net.minestom.server.command.builder.Command;
//import net.minestom.server.command.builder.CommandContext;
//import net.minestom.server.command.builder.arguments.ArgumentType;
//import net.minestom.server.entity.Player;
//
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//
//public class PartyChatCommand extends Command {
//
//    public PartyChatCommand() {
//        super("partychat", "pchat", "pc");
//
//        setDefaultExecutor(this::defaultExecutor);
//
//        var message = ArgumentType.StringArray("message");
//
//        addSyntax(this::partyChatSyntax, message);
//    }
//
//    private void defaultExecutor(CommandSender sender, CommandContext context) {
//        sender.sendMessage(CommandUtil.getUsage("grants", "player"));
//    }
//
//    private void partyChatSyntax(CommandSender sender, CommandContext context) {
//        if (sender instanceof Player player) {
//
//            var message = String.join(" ", (String[]) context.get("message"));
//
//            CompletableFuture.runAsync(() -> {
//                try {
//                    var cachedProfile = ProfileUtil.getCachedProfile(player.getUuid()).get();
//
//                    if(cachedProfile == null) {
//                        sender.sendMessage(Message.parse(Language.Error.PLAYER_NETWORK));
//                        return;
//                    }
//
//                    var profile = cachedProfile.getProfile();
//                    var partyUuid = cachedProfile.getParty();
//
//                    if(partyUuid == null) {
//                        player.sendMessage(Message.parse(Language.Party.NO_PARTY));
//                        return;
//                    }
//
//                    var party = Party.getParty(partyUuid).get();
//
//                    if(party.getSettings().isPartyMuted()) {
//                        player.sendMessage(Message.parse(Language.Party.PARTY_MUTED));
//                        return;
//                    }
//
//                    var partyMessage = Message.parse(
//                            Language.Party.PARTY_CHAT_FORMAT,
//                            profile.getColoredPrefix(),
//                            profile.getColoredName(),
//                            "",
//                            Component.text(message, Colors.bleach(profile.getGrant().getRank().getColor(), 0.80))
//                    );
//
//                    for(var member : party.getMembers()) {
//                        var memberPlayer = MinecraftServer.getConnectionManager().getPlayer(member.player());
//
//                        if(memberPlayer != null) {
//                            memberPlayer.sendMessage(partyMessage);
//                        }
//                    }
//
//                    PigeonUtil.broadcast(new PartyChatPayload(party, cachedProfile.getProfile(), message));
//                } catch (InterruptedException | ExecutionException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
//    }
//
//}
