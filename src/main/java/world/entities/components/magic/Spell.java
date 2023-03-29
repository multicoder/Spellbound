package world.entities.components.magic;

import assets.Assets;
//import com.github.mathiewz.slick.Color;
//import com.github.mathiewz.slick.Image;
import network.MPServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import world.Region;
import world.entities.components.LocationComponent;
import world.events.event.SpellCastEvent;
import world.entities.components.magic.techniques.Technique;
import world.entities.components.magic.techniques.Techniques;

import java.util.ArrayList;
import java.util.HashMap;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

public class Spell {

    private String name;
    private ArrayList<String> techniques;
    private HashMap<String, Integer> levels;

    private int iconIndex;
    private Color color;

    public Spell() {
        this.name = "Untitled Spell";
        this.techniques = new ArrayList<>();
        this.levels = new HashMap<>();
        this.color = Color.white;
    }

    public Spell(Spell template) {
        this();
        this.techniques.addAll(template.techniques);
        this.levels.putAll(template.levels);
        this.color = template.color;
        this.iconIndex = template.iconIndex;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void addTechnique(String technique) { this.techniques.add(technique); }
    public void addTechnique(String technique, int level) {
        addTechnique(technique);
        setLevel(technique, level);
    }
    public void removeTechnique(String technique) {
        this.techniques.remove(technique);
        this.levels.remove(technique);
    }
    public boolean hasTechnique(String techniqueName) { return techniques.contains(techniqueName); }

    private ArrayList<Technique> loadTechniques() {
        ArrayList<Technique> loaded = new ArrayList<>();
        String[] allTechs = Techniques.getAll();
        //maintain proper order of techniques
        for (String techniqueName: allTechs) {
            if (hasTechnique(techniqueName)) {
                Technique instance = Technique.createFrom(techniqueName);
                if (instance != null) {
                    loaded.add(instance);
                    instance.setLevel(getLevel(techniqueName));
                }
            }
        }
        return loaded;
    }

    public void addLevel(String technique) {
        setLevel(technique, getLevel(technique) + 1);
    }
    public void setLevel(String technique, int l) { levels.put(technique, l); }

    public void resetLevel(String technique) {
        levels.put(technique, 1);
    }

    public int getLevel(String technique) {
        if (levels.get(technique) == null) return 1;
        return levels.get(technique);
    }

    public void setColor(Color c) { this.color = c; }
    public void setIconIndex(int index) { iconIndex = index; }

    public int getIconIndex() {
        return iconIndex;
    }

    public Image getIcon() {
        return Assets.getImage("gui/icons/spells/" +iconIndex+".png");
    }

    public Color getColor() {
        return color;
    }

    public boolean isEmpty() { return techniques.isEmpty(); }

    public ArrayList<String> getConflicts(String technique) {
        ArrayList<String> conflicts = new ArrayList<>();
        for (String t: techniques) {
            if (!t.equals(technique) && t.matches(Techniques.getConflictsWith(technique)))
                conflicts.add(t);
        }
        return conflicts;
    }

    public float getVolatility() {
        float conflicting = 0;
        for (String technique: techniques) conflicting += getConflicts(technique).isEmpty() ? 0 : 1;
        return conflicting / (float)techniques.size();
    }

    public int getCrystalCost() {
        int cost = 0;
        for (String technique: techniques) cost += Techniques.getCrystalCost(technique) * getLevel(technique);
        return cost;
    }

    public int getManaCost() {
        int cost = 0;
        for (String technique: techniques) cost += Techniques.getManaCost(technique) * getLevel(technique);
        return cost;
    }

    public int getDyeCost() {
        double cmax = Math.max(color.getRed(), Math.max(color.getGreen(), color.getBlue()));
        double cmin = Math.min(color.getRed(), Math.min(color.getGreen(), color.getBlue()));
        double delta = (int)(cmax - cmin);
        double lightness = (int)((cmax + cmin) / 2);
        double saturation = Math.abs(delta == 0 ? 0 : delta / (1 - Math.abs((lightness*2)-1)));
        return (int)Math.abs(10 * (saturation / 0.75));
    }

    public JSONObject serialize() {
        JSONObject serialized = new JSONObject();
        JSONArray jsonColor = new JSONArray();
        jsonColor.add(color.getRed());
        jsonColor.add(color.getGreen());
        jsonColor.add(color.getBlue());
        jsonColor.add(color.getAlpha());
        serialized.put("name", name);
        serialized.put("icon", iconIndex);
        serialized.put("color", jsonColor);
        for (String t: techniques) serialized.put(t, getLevel(t));
        return serialized;
    }

    public void deserialize(JSONObject json) {
        for (String technique: Techniques.getAll()) {
            if (json.get(technique) != null) {
                addTechnique(technique);
                setLevel(technique, (int)(long)json.get(technique));
            }
        }
        JSONArray jsonColor = (JSONArray)json.get("color");
        color = new Color(
                (int)(long)jsonColor.get(0),
                (int)(long)jsonColor.get(1),
                (int)(long)jsonColor.get(2),
                (int)(long)jsonColor.get(3));
        name = (String)json.get("name");
        iconIndex = (int)(long)json.get("icon");
    }

}
