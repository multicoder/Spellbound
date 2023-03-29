package network.handlers.server.packet;

import com.esotericsoftware.kryonet.Connection;
//import com.github.mathiewz.slick.Input;
import misc.MiscMath;
import network.MPClient;
import network.MPServer;
import network.Packet;
import network.PacketHandler;
import network.packets.input.KeyPressedPacket;
import org.newdawn.slick.Input;
import world.entities.components.InputComponent;
import world.entities.systems.MovementSystem;

public class ServerKeyPressedHandler implements PacketHandler {

    @Override
    public boolean handle(Packet p, Connection from) {
        KeyPressedPacket kpp = (KeyPressedPacket)p;
        int entityID = MPServer.getEntityID(from);

        InputComponent input = (InputComponent)MPServer.getWorld().getEntities().getComponent(InputComponent.class, entityID);
        input.setKey(kpp.key, true);
        if (kpp.key == Input.KEY_E) {
            //MPServer.getEventManager().invoke(new PlayerInteractEvent());
        }
        return true;
    }

}
