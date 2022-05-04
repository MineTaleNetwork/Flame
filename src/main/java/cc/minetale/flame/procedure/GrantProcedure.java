package cc.minetale.flame.procedure;

import cc.minetale.sodium.profile.Profile;
import cc.minetale.sodium.profile.grant.Rank;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;

import java.util.UUID;

@Getter @Setter
public class GrantProcedure extends Procedure {

    private Profile profile;
    private String grant;
    private Rank rank;

    public GrantProcedure(UUID issuer, Profile profile, Type type, Stage stage) {
        super(issuer, profile.getUuid(), type, stage);

        this.profile = profile;
    }

    @Override
    public void cancel() {
        var player = MinecraftServer.getConnectionManager().getPlayer(getIssuer());

        if(player != null) {
            player.sendMessage(Component.text("You have cancelled the grant " + (this.getType() == Type.REMOVE ? "removal " : "") + "procedure.", NamedTextColor.RED));
        }

        finish();
    }

}