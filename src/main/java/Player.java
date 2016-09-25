
import java.util.*;
import java.util.stream.Collectors;

/**
 * Send your busters out into the fog to trap ghosts and bring them home!
 *
 */
class Player {

    static int ghostCount, myTeamId, bustersPerPlayer;
    static HashMap<Integer, Ghost> ghosts = new HashMap<>();
    static HashMap<Integer, Buster> busters = new HashMap<>();
    static HashMap<Integer, Buster> enemyBusters = new HashMap<>();
    static boolean discoveredAll = false;

    public static void main(String args[]) {
        toDiscover = new ArrayList<>();
        filloutDiscoveryList();
        discoveredAll = false;

        Scanner in = new Scanner(System.in);
        bustersPerPlayer = in.nextInt(); // the amount of busters you control
        ghostCount = in.nextInt(); // the amount of ghosts on the map
        myTeamId = in.nextInt(); // if this is 0, your base is on the top left of the map, if it is one, on the bottom right
        Coord myBaseCoords = getBaseCoords();

        Integer lastBusted = -1;

        // game loop
        while (true) {
            for (Buster buster : busters.values()) {
                buster.onGhost = null;
            }
            for (Buster enemy : enemyBusters.values()) {
                enemy.vis = false;
            }
            for (Ghost ghost : ghosts.values()) {
                ghost.vis = false;
            }
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
            Set<Integer> busterKeys = busters.keySet();
            List<Integer> busterKeysSorted = busterKeys.stream().sorted().collect(Collectors.toList());
            for (Integer busterKey : busterKeysSorted) {
                String action = getBusterAction(busterKey, myBaseCoords);
                System.out.println(action);
            }
        }
    }

    private static String getBusterAction(Integer busterKey, Coord myBaseCoords) {
        Buster buster = busters.get(busterKey);
        if (buster.stunCD > 0) {
            buster.stunCD--;
        }
        checkDiscovery(buster.x, buster.y);
        if (buster.state == 2) {
            return moveTo(new Coord(0, 0));
        }
        if (buster.state == 1) {
            Integer distBase = getDist(buster, myBaseCoords);
            System.err.println("DistBase " + distBase);
            if (distBase <= BASE_COLLECT_RANGE) {
                return "RELEASE";
            } else {
                System.err.println("Move to base " + myBaseCoords);
                return "MOVE " + myBaseCoords.x + " " + myBaseCoords.y + " " + busterKey;
            }
        }
        for (Buster enemyBuster : enemyBusters.values()) {
            if (enemyBuster.vis && getDist(buster, enemyBuster) <= STUN_RANGE && enemyBuster.hasGhost()) {
                buster.stunCD = 20;
                return "STUN " + enemyBuster.id;
            }
        }
        if (ghosts.isEmpty()) {
            return discoverMove(buster);
        } else {
            Ghost ghost = getNextGhost(buster, ghosts, true, discoveredAll ? 20 : 4);
            if (ghost == null) {
                return discoverMove(buster);
            }
            Integer dist = getDist(buster, ghost);
            if (ghost.vis && dist <= BUST_RANGE_MAX && dist >= BUST_RANGE_MIN) {
                System.err.println("Nearest is " + ghost.id + " " + dist);
                buster.onGhost = ghost.id;
                return "BUST " + ghost.id;
            } else {
                if (dist < BUST_RANGE_MIN) {
                    System.err.println("To close to ghost!");
                }
                return moveTo(ghost);
            }
        }
    }

    private static void filloutDiscoveryList() {
        discoveredAll = true;
        for (int x = 0; x < (SIZE_X / FIELD_SIZE + 1); x++) {
            for (int y = 0; y < (SIZE_Y / FIELD_SIZE + 1); y++) {
                int xC = x * FIELD_SIZE, yC = y * FIELD_SIZE;
                if (!(x == 0 && y == 0 | x == (SIZE_X / FIELD_SIZE) && y == (SIZE_Y / FIELD_SIZE))
                        && xC < (SIZE_X - 800) && xC > (800) && yC > 400 && yC < (SIZE_Y - 400)) {
                    toDiscover.add(new Coord(x, y));
                }
            }
        }
        Collections.shuffle(toDiscover);
    }

    private static String moveTo(Coord myBaseCoords) {
        return "MOVE " + myBaseCoords.x + " " + myBaseCoords.y;
    }

    private static Ghost getNextGhost(Buster buster, HashMap<Integer, Ghost> ghosts, boolean visOnly, int maxHp) {
        Integer currId = -1;
        Integer dist = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Ghost> ghostEntry : ghosts.entrySet()) {
            Integer ghostId = ghostEntry.getKey();
            Ghost ghost = ghostEntry.getValue();
            if ((!ghost.vis && visOnly) || bustersOnGhost(ghost.id) > 2 || maxHp <= ghost.hp) {
                continue;
            }
            Integer newDist = getDist(buster, ghost);
            if (newDist < dist && newDist > BUST_RANGE_MIN) {
                currId = ghostId;
                dist = newDist;
            }
        }
        return ghosts.get(currId);
    }

    private static int bustersOnGhost(Integer ghostId) {
        int bustersOnGhost = 0;
        for (Buster buster : busters.values()) {
            if (ghostId.equals(buster.onGhost)) {
                bustersOnGhost++;
            }
        }
        return bustersOnGhost;
    }

    private static String discoverMove(Buster buster) {
        if (toDiscover.isEmpty()) {
            filloutDiscoveryList();
            return moveTo(new Coord(SIZE_X / 2, SIZE_Y / 2));
        } else if (buster.discoverMove != null) {
            return moveTo(new Coord(buster.discoverMove.x * FIELD_SIZE, buster.discoverMove.y * FIELD_SIZE));
        } else {
            Coord next = toDiscover.get(0);
            toDiscover.remove(0);
            buster.discoverMove = next;
            return moveTo(new Coord(next.x * FIELD_SIZE, next.y * FIELD_SIZE));
        }
    }

    private static void checkDiscovery(int x, int y) {
        for (Buster buster : busters.values()) {
            if (buster.discoverMove != null && buster.discoverMove.x.equals(new Integer(x / FIELD_SIZE)) && buster.discoverMove.y.equals(new Integer(y / FIELD_SIZE))) {
                buster.discoverMove = null;
            }
        }
        toDiscover.removeIf((Coord t) -> {
            if (t.x.equals(new Integer(x / FIELD_SIZE)) && t.y.equals(new Integer(y / FIELD_SIZE))) {
                return true;
            }
            return false;
        });
    }

    private static void setEntity(int entityId, int x, int y, int entityType, int state, int value) {
        if (entityType == -1) {
            setGhost(entityId, x, y, state, value);
        } else if (entityType == myTeamId) {
            setBuster(busters, entityId, x, y, state, value);
        } else {
            setBuster(enemyBusters, entityId, x, y, state, value);
        }
    }

    private static void setGhost(int entityId, int x, int y, int state, int value) {
        if (ghosts.containsKey(entityId)) {
            Ghost ghost = ghosts.get(entityId);
            ghost.x = x;
            ghost.y = y;
            ghost.value = value;
            ghost.hp = state;
            ghost.vis = true;
        } else {
            Ghost ghost = new Ghost(entityId, value, state, x, y);
            ghost.vis = true;
            ghosts.put(entityId, ghost);
        }
    }

    private static void setBuster(HashMap<Integer, Buster> busters, int entityId, int x, int y, int state, int value) {
        if (busters.containsKey(entityId)) {
            Buster buster = busters.get(entityId);
            buster.x = x;
            buster.y = y;
            buster.state = state;
            buster.value = value;
            buster.vis = true;
        } else {
            Buster buster = new Buster(entityId, state, value, x, y);
            buster.vis = true;
            busters.put(entityId, buster);
        }
    }

    public static class Ghost extends Coord {

        int id, value, hp;
        boolean vis = false;

        public Ghost(int id, int value, int state, Integer x, Integer y) {
            super(x, y);
            this.id = id;
            this.value = value;
            this.hp = state;
        }

        @Override
        public String toString() {
            return "Ghost{" + "id=" + id + ", value=" + value + '}';
        }

    }

    public static class Buster extends Coord {

        Integer id, state, value, onGhost = null;
        int stunCD = 0;
        Coord discoverMove = null;
        boolean vis = false;

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

        public boolean isStunned() {
            return state == 2;
        }

        public boolean hasGhost() {
            return state == 1;
        }

        public boolean isBustingGhost() {
            return state == 3;
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
    static final Integer STUN_RANGE = 1760, MOVE_RANGE = 800;

    static final Coord BASE_ONE = new Coord(0, 0), BASE_TWO = new Coord(16000, 9000);

    static List<Coord> toDiscover;
}
