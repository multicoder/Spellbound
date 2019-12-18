package world;

import org.newdawn.slick.Graphics;
import world.entities.Entity;
import world.entities.actions.action.SetAnimationAction;
import world.entities.magic.MagicSource;
import world.entities.types.Player;
import world.events.EventDispatcher;
import world.events.EventListener;
import world.events.event.EntityMoveEvent;
import world.generators.chunk.ChunkType;
import world.generators.world.DefaultWorldGenerator;
import world.generators.world.WorldGenerator;

import java.util.ArrayList;

public class World {

    private static Chunk[][] chunks;
    private static ChunkType[][] chunk_map;

    private static Player player;
    private static ArrayList<MagicSource> magic_sources;

    public static void init(int size) {
        generate(size, new DefaultWorldGenerator());
        player = new Player();
        magic_sources = new ArrayList<>();
        EventDispatcher.register(new EventListener().on(EntityMoveEvent.class.toString(), e -> {
            EntityMoveEvent event = (EntityMoveEvent) e;
            Entity entity = event.getEntity();
            double[] coords = entity.getCoordinates();
            int[] chcoords = entity.getChunkCoordinates();
            int cdx = 0, cdy = 0;
            if (coords[0] == Chunk.CHUNK_SIZE - 1 && chcoords[0] < size - 1) cdx = 1;
            if (coords[0] == 0 && chcoords[0] > 0) cdx = -1;
            if (coords[1] == Chunk.CHUNK_SIZE - 1 && chcoords[1] < size - 1) cdy = 1;
            if (coords[1] == 0 && chcoords[1] > 0) cdy = -1;
            entity.setCoordinates(
                    (coords[0] + Chunk.CHUNK_SIZE + cdx) % Chunk.CHUNK_SIZE,
                    (coords[1] + Chunk.CHUNK_SIZE + cdy) % Chunk.CHUNK_SIZE);
            entity.setChunkCoordinates(chcoords[0] + cdx, chcoords[1] + cdy);
            if (cdx != 0 || cdy != 0) {
                entity.queueAction(new SetAnimationAction("walking"));
                entity.move(cdx, cdy);
                entity.queueAction(new SetAnimationAction("idle"));
            }
        }));
    }

    public static Chunk getChunk(int x, int y) {
        if (chunks[x][y] == null) chunks[x][y] = new Chunk(chunk_map[x][y]);
        return chunks[x][y];
    }

    public static Player getPlayer() {
        return player;
    }

    public static void addMagicSource(MagicSource magicSource) {
        magic_sources.add(magicSource);
    }

    public static void update() {
        player.update();
        for (int i = magic_sources.size() - 1; i >= 0; i--) {
            MagicSource magicSource = magic_sources.get(i);
            magicSource.update();
            if (magicSource.getBody().isDepleted()) magic_sources.remove(i);
        }
    }

    public static void generate(int size, WorldGenerator generator) {
        chunks = new Chunk[size][size];
        chunk_map = generator.generateChunkMap(size);
    }

    public static void draw(float ox, float oy, float scale, Graphics g) {
        Chunk current = getChunk(player.getChunkCoordinates()[0], player.getChunkCoordinates()[1]);
        current.draw(ox, oy, scale);
        player.draw(ox, oy, scale);
        for (int i = 0; i < magic_sources.size(); i++) magic_sources.get(i).draw(ox, oy, scale, g);
    }

}
