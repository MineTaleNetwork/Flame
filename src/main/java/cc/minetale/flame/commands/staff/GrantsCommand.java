package cc.minetale.flame.commands.staff;

import cc.minetale.flame.menu.grant.GrantsMenu;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.mlib.canvas.template.Menu;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.profile.Profile;
import cc.minetale.sodium.profile.ProfileUtil;
import cc.minetale.sodium.profile.RedisProfile;
import cc.minetale.sodium.profile.grant.Rank;
import cc.minetale.sodium.util.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.time.Tick;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GrantsCommand extends Command {

    public GrantsCommand() {
        super("grants");

        setCondition(CommandUtil.getRankCondition(Rank.OWNER));

        setDefaultExecutor(this::defaultExecutor);

        var profile = ArgumentType.Word("player");

        addSyntax(this::grantsExecutor, profile);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(CommandUtil.getUsage("grants", "player"));
    }

    private void grantsExecutor(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            var profile = FlamePlayer.getProfile((String) context.get("player"));

            if(profile == null) {
                player.sendMessage(Message.parse(Language.Error.UNKNOWN_PLAYER));
                return;
            }

            var uuids = new HashSet<UUID>();

            profile.getGrants().forEach(grant -> {
                if(grant.getAddedById() != null)
                    uuids.add(grant.getAddedById());

                if(grant.getRemovedById() != null)
                    uuids.add(grant.getRemovedById());
            });

            ProfileUtil.getProfiles(uuids.stream().toList())
                    .thenAccept(profiles -> {
                        var profileMap = profiles.stream().collect(Collectors.toMap(redisProfile -> redisProfile.getProfile().getUuid(), Function.identity()));

                        Menu.openMenu(new GrantsMenu(
                                player,
                                profile,
                                profileMap
                        ));
                    })
                    .orTimeout(5, TimeUnit.SECONDS);


        }
    }

}
