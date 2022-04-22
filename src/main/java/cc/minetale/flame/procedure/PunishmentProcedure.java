package cc.minetale.flame.procedure;

import cc.minetale.sodium.profile.punishment.PunishmentType;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;

import java.util.UUID;

@Getter @Setter
public class PunishmentProcedure extends Procedure {

    private String punishment;
    private PunishmentType punishmentType;

    public PunishmentProcedure(Player issuer, UUID recipient, Type type, Stage stage) {
        super(issuer, recipient, type, stage);
    }

    @Override
    public void finish() {
        Procedure.removeProcedure(this.getIssuer());
    }

    @Override
    public void cancel() {
        getIssuer().sendMessage(Component.text("You have cancelled the punishment " + (this.getType() == Type.REMOVE ? "removal " : "") + "procedure.", NamedTextColor.RED));
        finish();
    }

}
