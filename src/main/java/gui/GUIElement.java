package gui;

import assets.Assets;
//import com.github.mathiewz.slick.Color;
//import com.github.mathiewz.slick.Graphics;
//import com.github.mathiewz.slick.Image;
//import com.github.mathiewz.slick.SlickException;
import misc.MiscMath;
import misc.Window;

import java.util.ArrayList;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public abstract class GUIElement {

    private double[] offset;
    private GUI gui;
    private GUIAnchor anchor = GUIAnchor.TOP_LEFT;
    private GUIElement parent;
    private Image buffer;
    private boolean buffered = true;
    private ArrayList<GUIElement> children = new ArrayList<>();
    private boolean inactive;
    private String tooltipText;

    public final void setAnchor(GUIAnchor anchor) { this.anchor = anchor; }
    public final void setOffset(double gx, double gy) { offset = new double[]{gx, gy}; }
    public abstract int[] getDimensions();
    public final double[] getCoordinates() {
        int[] dims = getDimensions();
        double[] p_coords = parent != null ? parent.getCoordinates() : new double[]{0, 0};
        int[] p_dims = parent != null ? parent.getDimensions() : new int[]{
                (int)(Window.getWidth() / Window.getScale()),
                (int)(Window.getHeight() / Window.getScale())
        };

        switch(anchor) {
            case TOP_LEFT: return new double[]{ p_coords[0] + offset[0], p_coords[1] + offset[1] };
            case TOP_MIDDLE: return new double[]{ p_coords[0] + ((double)p_dims[0]/2f) - (dims[0]/2f) + offset[0], p_coords[1] + offset[1] };
            case TOP_RIGHT: return new double[]{ p_coords[0] + p_dims[0] - dims[0] + offset[0], p_coords[1] + offset[1]};
            case LEFT_MIDDLE: return new double[]{ p_coords[0] + offset[0], p_coords[1] + ((double)p_dims[1]/2) - ((double)dims[1]/2) + offset[1] };
            case CENTER: return new double[]{ p_coords[0] + ((double)p_dims[0]/2f) - ((double)dims[0] / 2f) + offset[0], p_coords[1] + ((double)p_dims[1]/2f) - ((double)dims[1]/2f) + offset[1] };
            case RIGHT_MIDDLE: return new double[]{ p_coords[0] + p_dims[0] - dims[0] + offset[0], p_coords[1] + ((double)p_dims[1]/2) - ((double)dims[1]/2) + offset[1] };
            case BOTTOM_LEFT: return new double[]{ p_coords[0] + offset[0], p_coords[1] + p_dims[1] - dims[1] + offset[1] };
            case BOTTOM_MIDDLE: return new double[]{ p_coords[0] + ((double)p_dims[0]/2f) - ((double)dims[0]/2) + offset[0], p_coords[1] + p_dims[1] + offset[1] - dims[1] };
            case BOTTOM_RIGHT: return new double[]{ p_coords[0] + p_dims[0] - dims[0] + offset[0], p_coords[1] + p_dims[1] + offset[1] - dims[1] };
            default: return new double[]{0, 0};
        }
    }

    public final float[] getOnscreenCoordinates() {
        return new float[]{(float)getCoordinates()[0] * Window.getScale(), (float)getCoordinates()[1] * Window.getScale()};
    }

    public final void setParent(GUIElement parent) { this.parent = parent; }
    public final GUIElement getParent() { return parent; }

    public final void setGUI(GUI parent) { this.gui = parent; }
    public final GUI getGUI() { return parent != null ? parent.getGUI() : gui; }

    public boolean isActive() { return !inactive; }
    public void show() {
        if (inactive) onShow();
        inactive = false;
    }
    public void hide() {
        if (!inactive) onHide();
        inactive = true;
    }

    public void onShow() {}

    public void onHide() {}

    public final String getTooltipText() {
        if (!isActive()) return null;
        for (int i = children.size() - 1; i > -1; i--) {
            String hovered = children.get(i).getTooltipText();
            if (hovered != null) return hovered;
        }
        return mouseIntersects() && isActive() && tooltipText != null && tooltipText.length() > 0 ? tooltipText : null;
    }

    public final void setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText;
    }

    public final boolean mouseIntersects() {
        return MiscMath.pointIntersectsRect(
                getGUI().getParent().getInput().getMouseX() / Window.getScale(), getGUI().getParent().getInput().getMouseY() / Window.getScale(),
                getCoordinates()[0],
                getCoordinates()[1],
                getDimensions()[0],
                getDimensions()[1]);
    }

    public final boolean handleMouseMoved(int ogx, int ogy) {
        for (int i = children.size() - 1; i >= 0; i--) {
            GUIElement e = children.get(i);
            if (e.handleMouseMoved(ogx, ogy)) return true;
        }
        if (isActive()) {
            return onMouseMoved(ogx - (int)getCoordinates()[0], ogy - (int)getCoordinates()[1]);
        } else { return false; }
    }

    public final boolean handleMousePressed(int ogx, int ogy, int button) {
        if (!isActive()) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            GUIElement e = children.get(i);
            if (e.handleMousePressed(ogx, ogy, button)) return true;
        }
        return onMousePressed(ogx - (int)getCoordinates()[0], ogy - (int)getCoordinates()[1], button);
    }

    public final boolean handleMouseRelease(int ogx, int ogy, int button) {
        if (!isActive()) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            GUIElement e = children.get(i);
            if (e.handleMouseRelease(ogx, ogy, button)) return true;
        }
        return onMouseRelease(ogx - (int)getCoordinates()[0], ogy - (int)getCoordinates()[1], button);
    }

    public final boolean handleMouseScroll(int direction) {
        if (!isActive()) return false;
        for (int i = children.size() - 1; i >= 0; i--) {
            GUIElement e = children.get(i);
            if (e.handleMouseScroll(direction)) return true;
        }
        return onMouseScroll(direction);
    }

    public final boolean handleKeyUp(int key, char c) {
        for (int i = children.size() - 1; i >= 0; i--) {
            GUIElement e = children.get(i);
            if (e.handleKeyUp(key, c)) return true;
        }
        return isActive() && onKeyUp(key, c);
    }

    public final boolean handleKeyDown(int key, char c) {
        for (int i = children.size() - 1; i >= 0; i--) {
            GUIElement e = children.get(i);
            if (e.handleKeyDown(key, c)) return true;
        }
        return isActive() && onKeyDown(key, c);
    }

    public abstract boolean onMouseMoved(int ogx, int ogy);
    public abstract boolean onMouseRelease(int ogx, int ogy, int button);
    public abstract boolean onMousePressed(int ogx, int ogy, int button);
    public abstract boolean onMouseScroll(int direction);
    public abstract boolean onKeyDown(int key, char c);
    public abstract boolean onKeyUp(int key, char c);

    public final GUIElement addChild(GUIElement element, int ogx, int ogy, GUIAnchor anchor) {
        if (!children.contains(element)) children.add(element);
        element.setParent(this);
        element.setOffset(ogx, ogy);
        element.setAnchor(anchor);
        return this;
    }

    public final void removeChild(GUIElement element) {
        children.remove(element);
    }

    public final void removeAllChildren() { children.clear(); }

    public final void draw(Graphics g) {
        if (!isActive()) return;
        drawUnder(g);
        try {
            int[] dimensions = getDimensions();
            float[] coordinates = getOnscreenCoordinates();
            if (buffered) {
                if (buffer == null) buffer = Assets.getCachedBuffer(dimensions[0], dimensions[1]);
                buffer.getGraphics().clear();
                drawBuffered(buffer.getGraphics(),
                        mouseIntersects(),
                        getGUI().getParent().getInput().isMouseButtonDown(0));
                g.drawImage(buffer.getScaledCopy(Window.getScale()), coordinates[0], coordinates[1]);
            }
        } catch (SlickException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < children.size(); i++) children.get(i).draw(g);
        drawOver(g);
    }

    public void setBuffered(boolean b) { buffered = b; }

    public void drawUnder(Graphics g) {}
    public void drawOver(Graphics g) {}

    protected abstract void drawBuffered(Graphics b, boolean mouseHovering, boolean mouseDown);

    protected void drawDebug(Graphics g) {
        g.setColor(Color.magenta);
        g.drawRect(
                getOnscreenCoordinates()[0],
                getOnscreenCoordinates()[1],
                getDimensions()[0] * Window.getScale(),
                getDimensions()[1] * Window.getScale());
        for (GUIElement child: children) child.drawDebug(g);
        g.setColor(Color.white);
    }


}
