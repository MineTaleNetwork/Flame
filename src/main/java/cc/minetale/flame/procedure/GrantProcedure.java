package cc.minetale.flame.procedure;

import cc.minetale.commonlib.api.Rank;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;

import java.util.UUID;

@Getter @Setter
public class GrantProcedure extends Procedure {

    private String grant;
    private Rank rank;
    private long duration;
    private String reason;

    public GrantProcedure(Player issuer, UUID recipient, Type type, Stage stage) {
        super(issuer, recipient, type, stage);
    }

    @Override
    public void finish() {
        Procedure.removeProcedure(this.getIssuer());
    }

    @Override
    public void cancel() {
        this.getIssuer().sendMessage(Component.text("You have cancelled the grant " + (this.getType() == Type.REMOVE ? "removal " : "") + "procedure.", NamedTextColor.RED));
        this.finish();
    }

}