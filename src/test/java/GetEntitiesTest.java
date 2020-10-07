import assets.Assets;
import misc.MiscMath;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import world.Chunk;
import world.Region;
import world.World;
import world.entities.components.LocationComponent;
import world.entities.systems.MovementSystem;
import world.generation.region.OverworldGenerator;

import java.util.ArrayList;
import java.util.List;

public class GetEntitiesTest {

    private static JSONObject testEntityData;
    private static World world;

    private static LocationComponent testEntityLocation;

    @BeforeAll
    static void beforeAll() {
        testEntityData = Assets.json("definitions/entities/test/exampleNonPlayer.json", true);
        world = new World();
        world.addRegion(new Region("world", new OverworldGenerator(0)));
        world.spawnEntity(0, testEntityData, null);
        testEntityLocation = (LocationComponent)world.getEntities().getComponent(LocationComponent.class, 0);
        testEntityLocation.getLocation().setCoordinates(MiscMath.random(0, Chunk.CHUNK_SIZE), MiscMath.random(0, Chunk.CHUNK_SIZE));
    }

    @BeforeEach
    void setUp() {

    }

    @Test
    void testExistence() {
        Assertions.assertTrue(world.getEntities().exists(0));
    }

    @Test
    void testCachesAllAdjacentChunks() {
        Chunk c = world.getRegion(testEntityLocation.getLocation()).getChunk(testEntityLocation.getLocation());
        MovementSystem.cacheEntity(0, c, 1);
        ArrayList<Chunk> adj = world.getRegion(testEntityLocation.getLocation()).getChunks(c.getCoordinates()[0], c.getCoordinates()[1], 1);
        Assertions.assertEquals(9, adj.size());
        for (Chunk a: adj)
            Assertions.assertEquals(1, a.getCachedEntities().size());
    }

    @Test
    void testChunkCacheExistence() {
        Chunk c = world.getRegion(testEntityLocation.getLocation()).getChunk(testEntityLocation.getLocation());
        Assertions.assertNotNull(c);
        Assertions.assertEquals(1, c.getCachedEntities().size());
    }

    @Test
    void testGetAllInRectangle() {
        List<Integer> found = world.getRegion("world").getEntities(0, 0, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE);
        Assertions.assertEquals(1, found.size());
    }

    @Test
    void testGetChunks() {
        List<Chunk> gotChunks = world.getRegion(testEntityLocation.getLocation()).getChunks(testEntityLocation.getLocation().getChunkCoordinates()[0], testEntityLocation.getLocation().getChunkCoordinates()[1], 1);
        Assertions.assertNotNull(gotChunks);
        Assertions.assertEquals(9, gotChunks.size());
    }

}
