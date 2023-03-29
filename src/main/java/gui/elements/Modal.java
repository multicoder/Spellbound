package gui.elements;

import assets.Assets;
//import com.github.mathiewz.slick.Graphics;
//import com.github.mathiewz.slick.Image;
//import com.github.mathiewz.slick.Input;
import gui.GUIElement;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

public class Modal extends GUIElement {

    private Image image;

    public Modal(Image image) {
        this.image = image;
    }

    public Modal(String image) {
        this(Assets.getImage(image));
    }

    @Override
    public int[] getDimensions() {
        return new int[]{image.getWidth(), image.getHeight()};
    }

    @Override
    public boolean onMouseMoved(int ogx, int ogy) {
        return false;
    }

    @Override
    public boolean onMouseRelease(int ogx, int ogy, int button) {
        return true;
    }

    @Override
    public boolean onMousePressed(int ogx, int ogy, int button) {
        return true;
    }

    @Override
    public boolean onMouseScroll(int direction) {
        return false;
    }

    @Override
    public void drawUnder(Graphics g) {

    }

    @Override
    public void drawOver(Graphics g) {

    }

    @Override
    public boolean onKeyUp(int key, char c) {
        if (key == Input.KEY_ESCAPE) {
            getGUI().popModal();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int key, char c) {
        return true;
    }

    @Override
    protected void drawBuffered(Graphics b, boolean mouseHovering, boolean mouseDown) {
        b.drawImage(image, 0, 0);
    }

}
