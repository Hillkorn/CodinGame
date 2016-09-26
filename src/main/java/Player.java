
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Hypersonic contest
 */
class Player {

    static final int ENTITY_TYPE_PLAYER = 0, ENTITY_TYPE_BOMB = 1, ENTITY_TYPE_ITEM = 2;

    static class PlayerEntity extends Coord {

        int id, bombsLeft, explosionRange, territory;

        public PlayerEntity(Integer x, Integer y) {
            super(x, y);
        }
    }

    static class BombEntity extends Coord {

        int owner, roundsLeft, explosionRange;

        public BombEntity(Integer x, Integer y) {
            super(x, y);
        }
    }

    static class Field extends Coord {

        int boxesInRange = 0, player = -1, bombExplosionIn = -1, entity = -1, itemType = -1, territory = -1;

        Field(int x, int y, int entity) {
            super(x, y);
            this.entity = entity;
        }

        boolean isMoveable() {
            return entity == FIELD_TYPE_EMPTY || entity == FIELD_TYPE_ITEM;
        }

        boolean isDestroyable() {
            return entity == FIELD_TYPE_BOX || entity == FIELD_TYPE_ITEM;
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

    static final int FIELD_TYPE_EMPTY = 0, FIELD_TYPE_BOX = 1, FIELD_TYPE_BOMB = 2, FIELD_TYPE_ITEM = 4, FIELD_TYPE_WALL = 5, FIELD_TYPE_SOON_DESTROYED = 6;
    static int myId, width, height, territoryCounter;
    static Field[][] map;
    static PlayerEntity myPlayer = new PlayerEntity(0, 0);
    static PlayerEntity enemyPlayer = new PlayerEntity(0, 0);

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        width = in.nextInt();
        height = in.nextInt();
        myId = in.nextInt();
        map = new Field[height][width];
        in.nextLine();

        // game loop
        while (true) {
            territoryCounter = 0;
            for (int y = 0; y < height; y++) {
                String row = in.nextLine();
                for (int x = 0; x < width; x++) {
                    if (row.charAt(x) == '.') {
                        map[y][x] = new Field(x, y, FIELD_TYPE_EMPTY);
                    } else if (row.charAt(x) == 'X') {
                        map[y][x] = new Field(x, y, FIELD_TYPE_WALL);
                    } else {
                        int boxItem = Integer.valueOf(row.charAt(x));
                        map[y][x] = new Field(x, y, FIELD_TYPE_BOX);
                        map[y][x].itemType = boxItem;
                    }
                }
            }
            int entities = in.nextInt();
            boolean hasBomb = false;
            boolean hasItem = false;
            List<Field> items = new ArrayList<>();
            for (int i = 0; i < entities; i++) {
                int entityType = in.nextInt();
                int owner = in.nextInt();
                int x = in.nextInt();
                int y = in.nextInt();
                int bombsOrRoundsLeft = in.nextInt();
                int explosionRange = in.nextInt();

                if (entityType == ENTITY_TYPE_ITEM) {
                    items.add(new Field(x, y, bombsOrRoundsLeft));
                    map[y][x].entity = FIELD_TYPE_ITEM;
                    hasItem = true;
                } else if (entityType == ENTITY_TYPE_PLAYER) {
                    if (owner == myId) {
                        myPlayer.x = x;
                        myPlayer.y = y;
                        myPlayer.bombsLeft = bombsOrRoundsLeft;
                        myPlayer.explosionRange = explosionRange;
                    } else {
                        enemyPlayer.x = x;
                        enemyPlayer.y = y;
                        enemyPlayer.bombsLeft = bombsOrRoundsLeft;
                        enemyPlayer.explosionRange = explosionRange;
                    }
                }

                if (entityType == ENTITY_TYPE_BOMB && myId == owner) {
                    hasBomb = true;
                }

                if (entityType == ENTITY_TYPE_BOMB) {
                    map[y][x].entity = FIELD_TYPE_BOMB;
                    for (int exRange = 0; exRange < explosionRange; exRange++) {
                        if (x - exRange >= 0) {

                            if (map[y][x - exRange].entity == FIELD_TYPE_EMPTY && owner != myId) {
                                map[y][x - exRange].bombExplosionIn = bombsOrRoundsLeft;
                            } else if (map[y][x - exRange].entity == FIELD_TYPE_BOX) {
                                map[y][x - exRange].entity = FIELD_TYPE_SOON_DESTROYED;
                                map[y][x - exRange].bombExplosionIn = bombsOrRoundsLeft;
                            }
                        } else {
                            break;
                        }
                    }
                    for (int exRange = 0; exRange < explosionRange; exRange++) {
                        if (x + exRange < width) {
                            if (map[y][x + exRange].entity == FIELD_TYPE_EMPTY && owner != myId) {
                                map[y][x + exRange].bombExplosionIn = bombsOrRoundsLeft;
                            } else if (map[y][x + exRange].entity == FIELD_TYPE_BOX) {
                                map[y][x + exRange].entity = FIELD_TYPE_SOON_DESTROYED;
                                map[y][x + exRange].bombExplosionIn = bombsOrRoundsLeft;
                            }
                        } else {
                            break;
                        }
                    }
                    for (int exRange = 0; exRange < explosionRange; exRange++) {
                        if (y - exRange >= 0) {
                            if (map[y - exRange][x].entity == FIELD_TYPE_EMPTY && owner != myId) {
                                map[y - exRange][x].bombExplosionIn = bombsOrRoundsLeft;
                            } else if (map[y - exRange][x].entity == FIELD_TYPE_BOX) {
                                map[y - exRange][x].entity = FIELD_TYPE_SOON_DESTROYED;
                                map[y - exRange][x].bombExplosionIn = bombsOrRoundsLeft;
                            }
                        } else {
                            break;
                        }
                    }
                    for (int exRange = 0; exRange < explosionRange; exRange++) {
                        if (y + exRange < height) {
                            if (map[y + exRange][x].entity == FIELD_TYPE_EMPTY && owner != myId) {
                                map[y + exRange][x].bombExplosionIn = bombsOrRoundsLeft;
                            } else if (map[y + exRange][x].entity == FIELD_TYPE_BOX) {
                                map[y + exRange][x].entity = FIELD_TYPE_SOON_DESTROYED;
                                map[y + exRange][x].bombExplosionIn = bombsOrRoundsLeft;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
            setTerritories(map);
            in.nextLine();
            myPlayer.territory = map[myPlayer.y][myPlayer.x].territory;

            calcBestChests(map);

            List<Field> bestFields = Stream.of(map)
                    .flatMap(Stream::of)
                    .filter(item -> item.boxesInRange > 0 && item.territory == myPlayer.territory && item.bombExplosionIn == -1)
                    .sorted((Field o1, Field o2) -> (getDist(myPlayer, o1) - (o1.boxesInRange / 2)) - (getDist(myPlayer, o2) - (o2.boxesInRange / 2)))
                    .collect(Collectors.toList());
            Field bestField = null;
            for (Field bestFieldOption : bestFields) {
                if (bestFieldOption.entity == FIELD_TYPE_EMPTY) {
                    bestField = bestFieldOption;
                    break;
                }
            }
            Field bestItem = null;
            for (Field item : items) {
                if (item.territory == myPlayer.territory && (bestItem == null || getDist(myPlayer, item) < getDist(myPlayer, bestItem))) {
                    bestItem = item;
                }
            }
            if (bestItem != null && getDist(myPlayer, bestItem) < 5) {
                System.out.println("MOVE " + bestItem.x + " " + bestItem.y + " It will be mine HAHA");
            } else if (bestField != null && bestField.boxesInRange > 0) {
                if (myPlayer.bombsLeft > 0 && getDist(myPlayer, bestField) == 0) {
                    System.out.println("BOMB " + myPlayer.x + " " + myPlayer.y + " Hyper Hyp! " + bestField.boxesInRange);
                } else {
                    System.out.println("MOVE " + bestField.x + " " + bestField.y + " GoGoGo " + bestField.boxesInRange);
                }
            } else {
                int gx = -1, gy = -1;
                int dist = 1000;
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        Integer newDist = getDist(myPlayer, new Coord(x, y));
                        if (map[y][x].entity == FIELD_TYPE_BOX && newDist < dist) {
                            gx = x;
                            gy = y;
                            dist = newDist;
                        }
                    }
                    if (gx != -1) {
                        break;
                    }
                }
                if (hasItem && myPlayer.bombsLeft == 0) {
                    Field item = findItem();
                    if (item != null && getDist(item, myPlayer) < 4) {
                        System.out.println("MOVE " + item.x + " " + item.y);
                    } else {
                        System.out.println("MOVE " + gx + " " + gy);
                    }
                } else if (gx != -1) {
                    if (hasBomb) {
                        System.out.println("MOVE " + gx + " " + gy);
                    } else {
                        System.out.println("BOMB " + gx + " " + gy + " Hyper Hyper!");
                    }
                } else {
                    System.out.println("BOMB 6 5");
                }
            }
        }
    }

    private static Field findItem() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x].entity == FIELD_TYPE_ITEM) {
                    return map[y][x];
                }
            }
        }
        return null;
    }

    public static Integer getDist(Coord c1, Coord c2) {
        int x = Math.abs(c1.x - c2.x);
        int y = Math.abs(c1.y - c2.y);
//        return new Double(Math.sqrt((x * x) + (y * y))).intValue();
        return x + y;
    }

    private static void calcBestChests(Field[][] map) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x].entity == FIELD_TYPE_BOX) {
                    for (int expRange = 1; expRange < myPlayer.explosionRange; expRange++) {
                        if (x + expRange < width && map[y][x + expRange].entity == FIELD_TYPE_EMPTY) {
                            map[y][x + expRange].boxesInRange++;
                        } else {
                            break;
                        }
                    }
                    for (int expRange = 1; expRange < myPlayer.explosionRange; expRange++) {
                        if (x - expRange >= 0 && map[y][x - expRange].entity == FIELD_TYPE_EMPTY) {
                            map[y][x - expRange].boxesInRange++;
                        } else {
                            break;
                        }
                    }
                    for (int expRange = 1; expRange < myPlayer.explosionRange; expRange++) {
                        if (y + expRange < height && map[y + expRange][x].entity == FIELD_TYPE_EMPTY) {
                            map[y + expRange][x].boxesInRange++;
                        } else {
                            break;
                        }
                    }
                    for (int expRange = 1; expRange < myPlayer.explosionRange; expRange++) {
                        if (y - expRange > 0 && map[y - expRange][x].entity == FIELD_TYPE_EMPTY) {
                            map[y - expRange][x].boxesInRange++;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void setTerritories(Field[][] map) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Field current = map[y][x];
                if (current.territory == -1 && (current.entity == FIELD_TYPE_EMPTY || current.entity == FIELD_TYPE_ITEM)) {
                    int territory = territoryCounter++;
                    calcTerritory(map, current, territory);
                }
            }
        }
    }

    private static void calcTerritory(Field[][] map, Field current, int territory) {
        int x = current.x;
        int y = current.y;
        current.territory = territory;
        if (x + 1 < width && map[y][x + 1].territory != territory && map[y][x + 1].isMoveable()) {
            calcTerritory(map, map[y][x + 1], territory);
        }
        if (x - 1 >= 0 && map[y][x - 1].territory != territory && map[y][x - 1].isMoveable()) {
            calcTerritory(map, map[y][x - 1], territory);
        }
        if (y + 1 < height && map[y + 1][x].territory != territory && map[y + 1][x].isMoveable()) {
            calcTerritory(map, map[y + 1][x], territory);
        }
        if (y - 1 >= 0 && map[y - 1][x].territory != territory && map[y - 1][x].isMoveable()) {
            calcTerritory(map, map[y - 1][x], territory);
        }
    }
}
