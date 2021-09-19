package cc.minetale.flame.arguments;

import cc.minetale.commonlib.modules.profile.Profile;
import cc.minetale.mlib.util.ProfileUtil;
import net.minestom.server.command.builder.NodeMaker;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket.Node;
import net.minestom.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ArgumentProfile extends Argument<CompletableFuture<Profile>> {

    public ArgumentProfile(@NotNull String id) {
        super(id);
    }

    @NotNull
    @Override
    public CompletableFuture<Profile> parse(@NotNull String input) throws ArgumentSyntaxException {
        return ProfileUtil.getProfileByName(input);
    }

    @Override
    public void processNodes(@NotNull NodeMaker nodeMaker, boolean executable) {
        DeclareCommandsPacket.Node argumentNode = simpleArgumentNode(this, executable, false, false);

        argumentNode.parser = "brigadier:string";
        argumentNode.properties = BinaryWriter.makeArray((packetWriter) -> {
            packetWriter.writeVarInt(0);
        });

        nodeMaker.addNodes(new Node[]{argumentNode});
    }

}
