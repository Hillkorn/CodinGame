
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CodeBusters {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getDist() {
        Player.Coord src = new Player.Coord(8, 8);
        Player.Coord dest = new Player.Coord(4, 4);

        assertEquals(new Integer(8), src.x);
        assertEquals(new Integer(8), src.y);

        assertEquals(new Integer(5), Player.getDist(dest, src));
    }
}
