package cc.minetale.flame.procedure;

import cc.minetale.sodium.profile.Profile;
import cc.minetale.sodium.profile.punishment.PunishmentType;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;

import java.util.UUID;

@Getter @Setter
public class PunishmentProcedure extends Procedure {

    private Profile profile;
    private String punishment;
    private PunishmentType punishmentType;

    public PunishmentProcedure(UUID issuer, Profile profile, Type type, Stage stage) {
        super(issuer, profile.getUuid(), type, stage);

        this.profile = profile;
    }

    @Override
    public void finish() {
        Procedure.removeProcedure(getIssuer());
    }

    @Override
    public void cancel() {
        var player = MinecraftServer.getConnectionManager().getPlayer(getIssuer());

        if(player != null) {
            player.sendMessage(Component.text("You have cancelled the punishment " + (this.getType() == Type.REMOVE ? "removal " : "") + "procedure.", NamedTextColor.RED));
        }

        finish();
    }

}
