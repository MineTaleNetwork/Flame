package cc.minetale.flame.util;

import cc.minetale.commonlib.modules.punishment.Punishment;
import cc.minetale.commonlib.modules.rank.Rank;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.TimeUtil;
import cc.minetale.flame.commands.RankUtil;
import cc.minetale.mlib.util.ProfileUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.color.DyeColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FlameUtil {

    public static void playClickSound(Player player) {
        player.playSound(Sound.sound(Key.key("minecraft:ui.button.click"), Sound.Source.MASTER, 1F, 2.0F));
    }

    public static void playErrorSound(Player player) {
        player.playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.MASTER, 1F, 0.5F));
    }

    private static final ImmutableMap<MC.CC, DyeColor> CHAT_DYE_COLOR_MAP = Maps.immutableEnumMap((Map) ImmutableMap.builder()
            .put(MC.CC.AQUA,         DyeColor.LIGHT_BLUE)
            .put(MC.CC.BLACK,        DyeColor.BLACK)
            .put(MC.CC.BLUE,         DyeColor.LIGHT_BLUE)
            .put(MC.CC.DARK_AQUA,    DyeColor.CYAN)
            .put(MC.CC.DARK_BLUE,    DyeColor.BLUE)
            .put(MC.CC.DARK_GRAY,    DyeColor.GRAY)
            .put(MC.CC.DARK_GREEN,   DyeColor.GREEN)
            .put(MC.CC.DARK_PURPLE,  DyeColor.PURPLE)
            .put(MC.CC.DARK_RED,     DyeColor.RED)
            .put(MC.CC.GOLD,         DyeColor.ORANGE)
            .put(MC.CC.GRAY,         DyeColor.LIGHT_GRAY)
            .put(MC.CC.GREEN,        DyeColor.LIME)
            .put(MC.CC.LIGHT_PURPLE, DyeColor.MAGENTA)
            .put(MC.CC.RED,          DyeColor.RED)
            .put(MC.CC.WHITE,        DyeColor.WHITE)
            .put(MC.CC.YELLOW,       DyeColor.YELLOW)
            .build()
    );

    private static final ImmutableMap<MC.CC, Material> CHAT_CONCRETE_COLOR_MAP = Maps.immutableEnumMap((Map) ImmutableMap.builder()
            .put(MC.CC.AQUA,         Material.LIGHT_BLUE_CONCRETE)
            .put(MC.CC.BLACK,        Material.BLACK_CONCRETE)
            .put(MC.CC.BLUE,         Material.LIGHT_BLUE_CONCRETE)
            .put(MC.CC.DARK_AQUA,    Material.CYAN_CONCRETE)
            .put(MC.CC.DARK_BLUE,    Material.BLUE_CONCRETE)
            .put(MC.CC.DARK_GRAY,    Material.GRAY_CONCRETE)
            .put(MC.CC.DARK_GREEN,   Material.GREEN_CONCRETE)
            .put(MC.CC.DARK_PURPLE,  Material.PURPLE_CONCRETE)
            .put(MC.CC.DARK_RED,     Material.RED_CONCRETE)
            .put(MC.CC.GOLD,         Material.ORANGE_CONCRETE)
            .put(MC.CC.GRAY,         Material.LIGHT_GRAY_CONCRETE)
            .put(MC.CC.GREEN,        Material.LIME_CONCRETE)
            .put(MC.CC.LIGHT_PURPLE, Material.MAGENTA_CONCRETE)
            .put(MC.CC.RED,          Material.RED_CONCRETE)
            .put(MC.CC.WHITE,        Material.WHITE_CONCRETE)
            .put(MC.CC.YELLOW,       Material.YELLOW_CONCRETE)
            .build()
    );

    public static DyeColor toDyeColor(MC.CC color) {
        return CHAT_DYE_COLOR_MAP.get(color);
    }


    public static Material toConcrete(MC.CC color) {
        return CHAT_CONCRETE_COLOR_MAP.get(color);
    }

    public static void broadcast(String rank, Component... messages) {
        for(Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            ProfileUtil.getAssociatedProfile(player).thenAccept(profile -> {
                boolean isEligible = RankUtil.hasMinimumRank(profile, rank);

                if(isEligible) {
                    for (Component component : messages) {
                        player.sendMessage(component);
                    }
                }
            });
        }
    }

    public static TextColor getPunishmentColor(Punishment punishment) {
        switch (punishment.getType()) {
            case BLACKLIST: {
                return MC.CC.RED.getTextColor();
            }
            case BAN: {
                return MC.CC.GOLD.getTextColor();
            }
            case MUTE: {
                return MC.CC.GREEN.getTextColor();
            }
            case WARN: {
                return MC.CC.BLUE.getTextColor();
            }
            default:
                return MC.CC.WHITE.getTextColor();
        }
    }

    public static List<Component> getPunishmentMessage(Punishment punishment) {
        Date date = new Date(punishment.getAddedAt());

        return Arrays.asList(
                MC.Style.SEPARATOR_80,
                Component.text("You are " + punishment.api().getContext() + (!punishment.api().isPermanent() ? " for " + punishment.api().getTimeRemaining() : "") + ".")
                        .color(NamedTextColor.RED),
                Component.empty(),
                Component.text("Reason: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text(punishment.getAddedReason())
                                        .color(NamedTextColor.WHITE)
                        ),
                Component.text("Added On: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text(TimeUtil.dateToString(date, true))
                                        .color(NamedTextColor.WHITE)
                        ),
                Component.text("Punishment ID: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text(punishment.getId())
                                        .color(NamedTextColor.WHITE)
                        ),
                Component.empty(),
                Component.text("Appeal At: ")
                        .color(NamedTextColor.GRAY)
                        .append(
                                Component.text("https://minetale.cc/discord")
                                        .color(NamedTextColor.AQUA)
                                        .decoration(TextDecoration.UNDERLINED, true)
                        ),
                MC.Style.SEPARATOR_80
        );
    }

}
