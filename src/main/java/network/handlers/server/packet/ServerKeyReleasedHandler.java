package network.handlers.server.packet;

import com.esotericsoftware.kryonet.Connection;
import misc.MiscMath;
import network.MPClient;
import network.MPServer;
import network.Packet;
import network.PacketHandler;
import network.packets.input.KeyReleasedPacket;
import world.entities.components.InputComponent;
import world.entities.systems.MovementSystem;

public class ServerKeyReleasedHandler implements PacketHandler {

    @Override
    public boolean handle(Packet p, Connection from) {
        KeyReleasedPacket kpp = (KeyReleasedPacket) p;
        int entityID = MPServer.getEntityID(from);

        InputComponent input = (InputComponent)MPServer.getWorld().getEntities().getComponent(InputComponent.class, entityID);
        input.setKey(kpp.key, false);
        return true;
    }

}
