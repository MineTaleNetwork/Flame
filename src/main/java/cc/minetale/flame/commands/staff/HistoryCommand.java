package cc.minetale.flame.commands.staff;

import cc.minetale.flame.menu.grant.GrantsMenu;
import cc.minetale.flame.menu.punishment.PunishmentsMenu;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.profile.ProfileUtil;
import cc.minetale.sodium.profile.grant.Rank;
import cc.minetale.sodium.profile.punishment.PunishmentType;
import cc.minetale.sodium.util.Message;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HistoryCommand extends Command {

    public HistoryCommand() {
        super("history");

        setCondition(CommandUtil.getRankCondition(Rank.OWNER));

        setDefaultExecutor(this::defaultExecutor);

        var profile = ArgumentType.Word("player");
        var type = ArgumentType.Enum("type", PunishmentType.class).setFormat(ArgumentEnum.Format.LOWER_CASED);

        addSyntax(this::commandExecutor, profile, type);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getUsage("history", "player", "type"));
    }

    private void commandExecutor(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            var profile = FlamePlayer.getProfile((String) context.get("player"));
            var type = (PunishmentType) context.get("type");

            if(profile == null) {
                player.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER));
                return;
            }

            var uuids = new HashSet<UUID>();

            profile.getPunishments(type)
                    .forEach(punishment -> {
                        if(punishment.getAddedById() != null)
                            uuids.add(punishment.getAddedById());

                        if(punishment.getRemovedById() != null)
                            uuids.add(punishment.getRemovedById());
            });

            ProfileUtil.getProfiles(uuids.stream().toList())
                    .thenAccept(profiles -> {
                        var profileMap = profiles.stream().collect(Collectors.toMap(redisProfile -> redisProfile.getProfile().getUuid(), Function.identity()));

                        Menu.openMenu(new PunishmentsMenu(
                                player,
                                profile,
                                type,
                                profileMap
                        ));
                    })
                    .orTimeout(5, TimeUnit.SECONDS);
        }
    }

}
