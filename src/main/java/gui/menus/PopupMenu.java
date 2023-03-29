package gui.menus;

import assets.Assets;
//import com.github.mathiewz.slick.Color;
import gui.GUIAnchor;
import gui.elements.Button;
import gui.elements.IconLabel;
import gui.elements.Modal;
import gui.elements.TextLabel;
import org.newdawn.slick.Color;

public class PopupMenu extends Modal {

    public PopupMenu(String title, String subtitle, String description, String icon, Color iconFilter) {
        super(Assets.getImage("gui/popup.png"));
        addChild(new TextLabel(title, 6, Color.black, false, false), 0, 8, GUIAnchor.TOP_MIDDLE);
        addChild(new TextLabel(subtitle, 4, Color.gray, Color.white, false, false), 0, 16, GUIAnchor.TOP_MIDDLE);
        IconLabel iconLabel = new IconLabel(icon);
        iconLabel.setFilter(iconFilter);
        addChild(iconLabel, 0, -24, GUIAnchor.CENTER);
        addChild(new TextLabel(description, 4, (int)(16*4.75), 9, Color.darkGray, Color.white, false, false), 0, 48, GUIAnchor.TOP_MIDDLE);
        addChild(new Button("Continue", 32, 8, null, true) {
            @Override
            public void onClick(int button) {
                getGUI().popModal();
                //TODO: add hook for custom button actions
            }
        }, 0, -12, GUIAnchor.BOTTOM_MIDDLE);
    }

    @Override
    public boolean onKeyDown(int key, char c) {
        return true;
    }

}
