import java.util.*;

class APU_INIT_PHASE_1 {

    private static final char FULL = '0', EMPTY = '.';
    private static Integer width, height;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        width = in.nextInt(); // the number of cells on the X axis
        in.nextLine();
        height = in.nextInt(); // the number of cells on the Y axis
        in.nextLine();
        Integer[][] map  = new Integer[width][height];
        for (int i = 0; i < height; i++) {
            String line = in.nextLine(); // width characters, each either 0 or .
            int[] lineInts = line.chars().map((int c) -> c == FULL ? 1 : 0).toArray();
            for (int x=0;x<width;x++) {
                map[x][i] = lineInts[x];
                System.err.print(map[x][i]);
            }
            System.err.println("");
        }

//        System.out.println("0 0 1 0 0 1"); // Three coordinates: a node, its right neighbor, its bottom neighbor
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                if (map[x][y] == 1) {
                    System.out.print(x + " " + y + " ");
                    Integer nx = getNextX(map, x, y);
                    Integer ny = getNextY(map, x, y);
                    if (nx != -1) {
                        System.out.print(nx + " " + y + " ");
                    } else {
                        System.out.print("-1 -1 ");
                    }
                    if (ny != -1) {
                        System.out.print(x + " " + ny);
                    } else {
                        System.out.print("-1 -1");
                    }
                    System.out.println("");
                }
            }
        }
    }

    public static Integer getNextX(Integer[][] map, Integer x, Integer y) {
        for (int xi=x+1; xi <width; xi++) {
            if (map[xi][y] == 1) {
                return xi;
            }
        }
        return -1;
    }

    public static Integer getNextY(Integer[][] map, Integer x, Integer y) {
        for (int yi=y+1; yi <height; yi++) {
            if (map[x][yi] == 1) {
                return yi;
            }
        }
        return -1;
    }
}
