
import java.util.*;
import java.util.function.Predicate;

/**
 * Send your busters out into the fog to trap ghosts and bring them home!
 *
 */
class Player {

    static int ghostCount, myTeamId, bustersPerPlayer;
    static HashMap<Integer, Ghost> ghosts = new HashMap<>();
    static HashMap<Integer, Buster> busters = new HashMap<>();
    static HashMap<Integer, Buster> enemyBusters = new HashMap<>();

    public static void main(String args[]) {
        toDiscover = new ArrayList<>();
        filloutDiscoveryList();

        Scanner in = new Scanner(System.in);
        bustersPerPlayer = in.nextInt(); // the amount of busters you control
        ghostCount = in.nextInt(); // the amount of ghosts on the map
        myTeamId = in.nextInt(); // if this is 0, your base is on the top left of the map, if it is one, on the bottom right
        Coord myBaseCoords = getBaseCoords();

        Integer lastBusted = -1;

        // game loop
        while (true) {
            int entities = in.nextInt(); // the number of busters and ghosts visible to you
            for (int i = 0; i < entities; i++) {
                int entityId = in.nextInt(); // buster id or ghost id
                int x = in.nextInt();
                int y = in.nextInt(); // position of this buster / ghost
                int entityType = in.nextInt(); // the team id if it is a buster, -1 if it is a ghost.
                int state = in.nextInt(); // For busters: 0=idle, 1=carrying a ghost.
                int value = in.nextInt(); // For busters: Ghost id being carried. For ghosts: number of busters attempting to trap this ghost.
                setEntity(entityId, x, y, entityType, state, value);
            }
            if (lastBusted >= 0) {
//                System.err.println(ghosts.get(lastBusted));
            }
            int i = 0;
            for (Buster buster : busters.values()) {
                checkDiscovery(buster.x, buster.y);
                i++;
                if (buster.state == 1) {
                    Integer distBase = getDist(buster, myBaseCoords);
                    System.err.println("DistBase " + distBase);
                    if (distBase <= BASE_COLLECT_RANGE) {
                        System.out.println("RELEASE");
                    } else {
                        System.err.println("Move to base " + myBaseCoords);
                        System.out.println("MOVE " + myBaseCoords.x + " " + myBaseCoords.y);
//                        moveTo(myBaseCoords);
                    }
                } else if (ghosts.isEmpty()) {
//                    System.err.println("All emtpy");
//                    System.out.println("MOVE 8000 4500"); // MOVE x y | BUST id | RELEASE
                    discoverMove(buster);
                } else {
                    Ghost ghost = getNextGhost(buster, ghosts);
                    if (ghost == null) {
                        System.err.println("NGhost is null");
//                        System.err.println(buster);
//                        System.out.println("MOVE 8000 4500"); // MOVE x y | BUST id | RELEASE
                        discoverMove(buster);
                    } else if (getDist(buster, ghost) <= BUST_RANGE_MAX) {
                        System.err.println("Nearest is " + ghost.id + " " + getDist(buster, ghost));
                        System.out.println("BUST " + ghost.id);
//                        System.out.println("BUST " + ghost.id);
                        System.err.println("BUST WRITTEN!!!");
                        lastBusted = ghost.id;
//                        ghost.value += 1;
                        ghosts.remove(ghost.id);
                    } else {
                        System.err.println("Last move to" + ghost.x + " " + ghost.y);
                        moveTo(ghost);
//                        ghost.value += 1;
                    }
                }
                // Write an action using System.out.println()
                // To debug: System.err.println("Debug messages...");
            }
            System.err.println("Moved for " + i);
        }
    }

    private static void filloutDiscoveryList() {
        for (int x = 0; x < (SIZE_X / FIELD_SIZE + 1); x++) {
            for (int y = 0; y < (SIZE_Y / FIELD_SIZE + 1); y++) {
                int xC = x * FIELD_SIZE, yC = y * FIELD_SIZE;
                if (!(x == 0 && y == 0 | x == (SIZE_X / FIELD_SIZE) && y == (SIZE_Y / FIELD_SIZE))
                        && xC < (SIZE_X - 1000) && xC > (1000) && yC > 1000 && yC < (SIZE_Y - 1000)) {
                    toDiscover.add(new Coord(x, y));
                }
            }
        }
        Collections.shuffle(toDiscover);
    }

    private static void moveTo(Coord myBaseCoords) {
        System.out.println("MOVE " + myBaseCoords.x + " " + myBaseCoords.y);
    }

    private static Ghost getNextGhost(Buster buster, HashMap<Integer, Ghost> ghosts) {
        Integer currId = -1;
        Integer dist = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Ghost> ghostEntry : ghosts.entrySet()) {
            Integer ghostId = ghostEntry.getKey();
            Ghost ghost = ghostEntry.getValue();
            if (ghost.value > 0) {
                System.err.println(ghost);
                continue;
            }
            Integer newDist = getDist(buster, ghost);
            if (newDist < dist) {
                currId = ghostId;
                dist = newDist;
            }
        }
        return ghosts.get(currId);
    }

    private static void discoverMove(Buster buster) {
        if (toDiscover.isEmpty()) {
            filloutDiscoveryList();
            moveTo(new Coord(SIZE_X / 2, SIZE_Y / 2));
        } else {
            Coord next = toDiscover.get(0);
            moveTo(new Coord(next.x * FIELD_SIZE, next.y * FIELD_SIZE));
        }
    }

    private static void checkDiscovery(int x, int y) {
        System.err.println("Before" + toDiscover.size());
        toDiscover.removeIf((Coord t) -> {
            if (t.x == (x / SIZE_X) && t.y == (y / SIZE_Y)) {
                return true;
            }
            return false;
        });
        System.err.println("After" + toDiscover.size());
    }

    public static class Ghost extends Coord {

        int id, value;

        public Ghost(int id, int value, Integer x, Integer y) {
            super(x, y);
            this.id = id;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Ghost{" + "id=" + id + ", value=" + value + '}';
        }

    }

    private static void setEntity(int entityId, int x, int y, int entityType, int state, int value) {
        if (entityType == -1) {
            setGhost(entityId, x, y, value);
        } else if (entityType == myTeamId) {
            System.err.println("Read B " + state + " " + value);
            setBuster(busters, entityId, x, y, state, value);
        } else {
            setBuster(enemyBusters, entityId, x, y, state, value);
        }
    }

    private static void setGhost(int entityId, int x, int y, int value) {
//        if (ghosts.containsKey(entityId)) {
//            Ghost ghost = ghosts.get(entityId);
//            ghost.x = x;
//            ghost.y = y;
//            ghost.value = value;
//        } else {
        ghosts.put(entityId, new Ghost(entityId, value, x, y));
//        }
    }

    private static void setBuster(HashMap<Integer, Buster> busters, int entityId, int x, int y, int state, int value) {
//        if (busters.containsKey(entityId)) {
//            Buster buster = busters.get(entityId);
//            buster.x = x;
//            buster.y = y;
//            buster.state = state;
//            buster.value = value;
//        } else {
        busters.put(entityId, new Buster(entityId, state, value, x, y));
//        }
    }

    public static class Buster extends Coord {

        Integer id, state, value;

        public Buster(Integer id, Integer state, Integer value, Integer x, Integer y) {
            super(x, y);
            this.id = id;
            this.state = state;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Buster{" + "id=" + id + ", state=" + state + ", value=" + value + '}';
        }

    }

    public static class Coord {

        Integer x, y;

        public Coord(Integer x, Integer y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Coord{" + "x=" + x + ", y=" + y + '}';
        }
    }

    public static Integer getDist(Coord c1, Coord c2) {
        int x = Math.abs(c1.x - c2.x);
        int y = Math.abs(c1.y - c2.y);
        return new Double(Math.sqrt((x * x) + (y * y))).intValue();
    }

    public static Coord getBaseCoords() {
        if (myTeamId == 0) {
            return BASE_ONE;
        } else {
            return BASE_TWO;
        }
    }

    static final Integer FIELD_SIZE = 1600;

    static final Integer BASE_COLLECT_RANGE = 1600, BUST_RANGE_MIN = 900, BUST_RANGE_MAX = 1760, FOG_DIST = 2200, SIZE_Y = 9000, SIZE_X = 16000;

    static final Coord BASE_ONE = new Coord(0, 0), BASE_TWO = new Coord(16000, 9000);

    static List<Coord> toDiscover;
}
