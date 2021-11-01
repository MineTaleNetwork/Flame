package cc.minetale.flame.arguments;

import cc.minetale.commonlib.rank.Rank;
import net.minestom.server.command.builder.NodeMaker;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket;
import net.minestom.server.utils.binary.BinaryWriter;
import org.jetbrains.annotations.NotNull;

public class ArgumentRank extends Argument<Rank> {

    public static final int UNKNOWN_RANK_ERROR = 1;
    private final Rank[] values;

    public ArgumentRank(@NotNull String id) {
        super(id);

        this.values = Rank.getRanks().values().toArray(Rank[]::new);
    }

    @NotNull @Override
    public Rank parse(@NotNull String input) throws ArgumentSyntaxException {
        Rank rank = Rank.getRank(input);

        if(rank != null)
            return rank;

        throw new ArgumentSyntaxException("Invalid or unknown rank", input, UNKNOWN_RANK_ERROR);
    }

    @Override
    public void processNodes(@NotNull NodeMaker nodeMaker, boolean executable) {
        DeclareCommandsPacket.Node[] nodes = new DeclareCommandsPacket.Node[this.values.length];

        for (int i = 0; i < nodes.length; i++) {
            DeclareCommandsPacket.Node argumentNode = new DeclareCommandsPacket.Node();

            argumentNode.flags = DeclareCommandsPacket.getFlag(DeclareCommandsPacket.NodeType.LITERAL,
                    executable, false, false);
            argumentNode.name = this.values[i].getName();

            argumentNode.parser = "brigadier:string";
            argumentNode.properties = BinaryWriter.makeArray((packetWriter) -> packetWriter.writeVarInt(0));

            nodes[i] = argumentNode;
        }

        nodeMaker.addNodes(nodes);
    }

}
