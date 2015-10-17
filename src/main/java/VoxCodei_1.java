
import java.util.Scanner;

class VoxCode_1 {

    public static final Character sPASSIVE = '#', sSURV = '@', sEMTPY = '.';
    public static final Integer PASSIVE = 2, SURV = 1, EMPTY = 0, BOMB = 10, DESTROYED = 11;

    public static Integer survCount = 0;
    public static int height;
    public static int width;
    public static int prioHighest = 0;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        width = in.nextInt(); // width of the firewall grid
        height = in.nextInt(); // height of the firewall grid
        Integer[][] map = new Integer[width][height];
        for (int i = 0; i < height; i++) {
            String mapRow = in.next(); // one line of the firewall grid
            int[] row = mapRow.chars().map((int c) -> {
                if (c == sSURV) {
                    survCount++;
                }
                return c == sPASSIVE ? PASSIVE : c == sSURV ? SURV : EMPTY;
            }).toArray();
            for (int x = 0; x < width; x++) {
                map[x][i] = row[x];
            }
        }

        // game loop
        while (true) {
            int rounds = in.nextInt(); // number of rounds left before the end of the game
            int bombs = in.nextInt(); // number of bombs left
            System.err.println(rounds + " : " + bombs);

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            Integer[][] prioMap = calcPlaces(map);
            printMap(prioMap);
            if (bombs == 0) {
                System.out.println("WAIT");
            } else if (prioHighest > 0) {
                boolean placed = false;
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        if (prioMap[x][y] == prioHighest) {
                            System.out.println(x +" " + y);
                            map[x][y] = BOMB;
                            placed = true;
                            bombExploded(x, y, map);
                            break;
                        }
                    }
                    if (placed) {
                        break;
                    }
                }
                if (!placed) {
                    System.out.println("WAIT");
                }
            } else {
                System.out.println("WAIT");
            }
        }
    }

    public static Integer[][] calcPlaces(Integer[][] map) {
        prioHighest = 0;
        Integer[][] prioMap = new Integer[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                prioMap[x][y] = 0;
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (map[x][y] == SURV) {
                    calcPrios(x, y, map, prioMap);
                }
            }
        }
        return prioMap;
    }

    public static void calcPrios(Integer x, Integer y, Integer[][] map, Integer[][] prioMap) {
        boolean xl = true, xr = true, yu = true, yd = true;
        for (int i=1; i<=3; i++) {
            if (xl && x-i >= 0 && map[x-i][y] != PASSIVE) {
                if (map[x-i][y] == EMPTY) {
                    prioMap[x-i][y] += 1;
                    if (prioHighest < prioMap[x-i][y]) {
                        prioHighest = prioMap[x-i][y];
                    }
                }
            } else {
                xl = false;
            }
            if (xr && x+i < width && map[x+i][y] != PASSIVE) {
                if (map[x+i][y] == EMPTY) {
                    prioMap[x+i][y] += 1;
                    if (prioHighest < prioMap[x+i][y]) {
                        prioHighest = prioMap[x+i][y];
                    }
                }
            } else {
                xr = false;
            }
            if (yd && y-i >= 0 && map[x][y-i] != PASSIVE) {
                if (map[x][y-i] == EMPTY) {
                    prioMap[x][y-i] += 1;
                    if (prioHighest < prioMap[x][y-i]) {
                        prioHighest = prioMap[x][y-i];
                    }
                }
            } else {
                yd = false;
            }
            if (yu && y+i < height && map[x][y+i] != PASSIVE) {
                if (map[x][y+i] == EMPTY) {
                    prioMap[x][y+i] += 1;
                    if (prioHighest < prioMap[x][y+i]) {
                        prioHighest = prioMap[x][y+i];
                    }
                }
            } else {
                yu = false;
            }
        }
    }

    public static void bombExploded(Integer x, Integer y, Integer[][] map) {
        boolean xl = true, xr = true, yu = true, yd = true;
        for (int i=1; i<=3; i++) {
            if (xl && x-i >= 0 && map[x-i][y] != PASSIVE) {
                if (map[x-i][y] == SURV)
                    map[x-i][y] = BOMB;
            } else {
                xl = false;
            }
            if (xr && x+i < width && map[x+i][y] != PASSIVE) {
                if (map[x+i][y] == SURV)
                    map[x+i][y] = BOMB;
            } else {
                xr = false;
            }
            if (yd && y-i >= 0 && map[x][y-i] != PASSIVE) {
                if (map[x][y-i] == SURV)
                    map[x][y-i] = BOMB;
            } else {
                yd = false;
            }
            if (yu && y+i < height && map[x][y+i] != PASSIVE) {
                if (map[x][y+i] == SURV)
                    map[x][y+i] = BOMB;
            } else {
                yu = false;
            }
        }
    }

    public static void printMap(Integer[][] map) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.err.print(map[x][y] + " ");
            }
            System.err.println("");
        }
        System.err.println("END");
    }
}
