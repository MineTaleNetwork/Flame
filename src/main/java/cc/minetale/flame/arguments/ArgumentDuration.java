package cc.minetale.flame.arguments;

import cc.minetale.commonlib.util.Duration;
import net.minestom.server.command.builder.NodeMaker;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket.Node;
import net.minestom.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.NotNull;

public class ArgumentDuration extends Argument<Duration> {

    public static final int UNKNOWN_DURATION_ERROR = 1;

    public ArgumentDuration(@NotNull String id) {
        super(id);
    }

    @NotNull
    @Override
    public Duration parse(@NotNull String input) throws ArgumentSyntaxException {
        Duration duration = Duration.fromString(input);

        if(duration.getValue() != -1)
            return duration;

        throw new ArgumentSyntaxException("Invalid duration", input, UNKNOWN_DURATION_ERROR);
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
