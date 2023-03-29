package world;

//import com.github.mathiewz.slick.Graphics;
import misc.Location;
import misc.annotations.ServerClientExecution;
import org.json.simple.JSONObject;
import world.entities.Entities;
import world.entities.components.LocationComponent;
import world.entities.systems.MovementSystem;
import world.generation.region.OverworldGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.newdawn.slick.Graphics;

public class World {

    private Entities entities;
    private HashMap<String, Region> regions;
    private ArrayList<Portal> portals;

    private int seed;

    public World() {
        regions = new HashMap<>();
        portals = new ArrayList<>();
        entities = new Entities();
    }

    public void generate(int seed) {
        this.seed = seed;
        addRegion(new Region("world", new OverworldGenerator(seed)));
    }

    public Entities getEntities() {
        return entities;
    }

    /**
     * Spawns an entity by the given ID. If the ID already exists, simply update the entity.
     * @param entityID
     * @param json
     * @param location
     * @return
     */
    @ServerClientExecution
    public int spawnEntity(int entityID, JSONObject json, Location location) {
        getEntities().putEntity(entityID, json);
        LocationComponent lc = ((LocationComponent)getEntities().getComponent(LocationComponent.class, entityID));
        if (lc != null) {
            if (location != null) lc.setLocation(location);
            MovementSystem.cacheEntity(entityID, getRegion(lc.getLocation()).getChunk(lc.getLocation()), 1);
        }
        return entityID;
    }

    public void destroyEntity(int entityID) {
        getEntities().removeEntity(entityID);
    }

    //TODO: DESTROY ENTITY
    //TODO: MOVE ENTITY TO REGION?

    public int getSeed() {
        return seed;
    }

    public Region addRegion(Region region) {
        regions.put(region.getName(), region);
        region.setWorld(this);
        return region;
    }

    public Collection<Region> getRegions() {
        return regions.values();
    }
    public Region getRegion(String name) { return regions.get(name); }
    public Region getRegion(Location location) { return getRegion(location.getRegionName()); }

    public void draw(float scale, Graphics g) {
        getRegion(Camera.getLocation()).draw(scale, g);
    }

    public void drawDebug(float scale, Graphics g) { getRegion(Camera.getLocation()).drawDebug(scale, g); }

}
