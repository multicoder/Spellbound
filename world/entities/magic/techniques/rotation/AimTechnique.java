package world.entities.magic.techniques.rotation;

import gui.states.GameScreen;
import misc.MiscMath;
import org.newdawn.slick.Game;
import world.entities.magic.MagicSource;
import world.entities.magic.techniques.Technique;

public class AimTechnique extends Technique {

    @Override
    public void applyTo(MagicSource source) {

    }

    @Override
    public void update(MagicSource source) {
        double angle = MiscMath.angleBetween(GameScreen.getInput().getMouseX(), GameScreen.getInput().getMouseY(),
                source.getBody().getLocation().getCoordinates()[0], source.getBody().getLocation().getCoordinates()[1]);
        source.setTargetDirection(angle);
    }

}
