package gui.elements;

//import com.github.mathiewz.slick.Graphics;
//import com.github.mathiewz.slick.Image;
//import com.github.mathiewz.slick.Input;
//import com.github.mathiewz.slick.SlickException;
import gui.GUIElement;
import misc.MiscMath;
import network.MPClient;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import world.entities.components.SpellbookComponent;
import world.particles.ParticleSource;

public class Hotbar extends GUIElement {

    private Image image, selected;

    private SpellbookComponent spellbook;

    private ParticleSource[] previews;

    public Hotbar() {
        this.previews = new ParticleSource[3];
        try {
            this.image = new Image("gui/hotbar.png", false, Image.FILTER_NEAREST);
            this.selected = new Image("gui/hotbar_selected.png", false, Image.FILTER_NEAREST);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public void setTarget(Integer entityID) {
        this.spellbook = (SpellbookComponent) MPClient.getWorld().getEntities().getComponent(SpellbookComponent.class, entityID);
    }

    @Override
    public int[] getDimensions() {
        return new int[]{ image.getWidth(), image.getHeight() };
    }

    @Override
    public boolean onMouseMoved(int ogx, int ogy) {
        return false;
    }

    @Override
    public boolean onMouseRelease(int ogx, int ogy, int button) {
        return false;
    }

    @Override
    public boolean onMousePressed(int ogx, int ogy, int button) { return false; }

    @Override
    public boolean onMouseScroll(int direction) {
        int current = spellbook.getSelectedIndex();
        int new_ = (int)MiscMath.clamp(current + direction, 0, 2);
        spellbook.selectSpell(new_);
        return true;
    }

    @Override
    public boolean onKeyDown(int key, char c) {
        if (key == Input.KEY_1) { spellbook.selectSpell(0); return true; }
        if (key == Input.KEY_2) { spellbook.selectSpell(1); return true; }
        if (key == Input.KEY_3) { spellbook.selectSpell(2); return true; }
        return false;
    }

    @Override
    public boolean onKeyUp(int key, char c) {
        return false;
    }

    @Override
    protected void drawBuffered(Graphics b, boolean mouseHovering, boolean mouseDown) {
        if (spellbook == null) return;
        b.drawImage(image, 0, 0);
        b.drawImage(selected, 3, 3 + (17 * spellbook.getSelectedIndex()));
        for (int i = 0; i < spellbook.getSpells().size(); i++) {
            Image icon = spellbook.getSpell(i).getIcon();
            if (icon == null) continue;
            b.drawImage(icon, 2, 2 + (i * 17), spellbook.getSpell(i).getColor());
        }
    }

}
