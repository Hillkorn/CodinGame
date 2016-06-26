
import java.util.*;

class Player {

    static final int HEIGHT = 12, WIDTH = 6;
    static final Integer[][] myGrid = new Integer[6][12];
    static final Random RANDOM = new Random(System.currentTimeMillis());

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        Field[] nextColors = new Field[8];
        Integer[] heights = new Integer[6];

        for (int i = 0; i < 8; i++) {
            nextColors[i] = new Field(0, 0, -1);
        }

        while (true) {
            readNextColors(nextColors, in);

            for (int i = 0; i < HEIGHT; i++) {
                char[] row = in.next().toCharArray();
                for (int j = 0; j < WIDTH; j++) {
                    if ('.' == row[j]) {
                        myGrid[j][i] = -1;
                        heights[j] = i;
                    } else {
                        String valueOf = String.valueOf(row[j]);
                        myGrid[j][i] = Integer.parseInt(valueOf);
                    }
                }
            }

            for (int i = 0; i < HEIGHT; i++) {
                String row = in.next(); // One line of the map ('.' = empty, '0' = skull block, '1' to '5' = colored block)
            }

            System.out.println(findBestColumn(nextColors, heights));
        }
    }

    private static int findBestColumn(Field[] nextColors, Integer[] heights) {
        int nextColor = nextColors[0].x;
        int highestAround = 0;
        Field highestField = null;
        for (int i = 0; i < WIDTH; i++) {
            if (heights[i] >= HEIGHT - 2) {
                return i;
            }
        }
        for (int w = 0; w < WIDTH; w++) {
            int h = heights[w];
            if (myGrid[w][h] != -1 || heights[w] > h + 1) {
                continue;
            }
            int colorsAround = 0;
            if (w > 0 && myGrid[w - 1][h] == nextColor) {
                colorsAround += 2;
            }
            if (w < WIDTH - 1 && myGrid[w + 1][h] == nextColor) {
                colorsAround += 2;
            }
            if (h < HEIGHT - 1 && myGrid[w][h + 1] == nextColor) {
                colorsAround++;
            }
            if (colorsAround > highestAround) {
                highestAround = colorsAround;
                highestField = new Field(w, h, nextColor);
            }
        }
        if (highestField != null) {
            return highestField.x;
        }
        return RANDOM.nextInt(WIDTH);
    }

    private static void readNextColors(Field[] nextColors, Scanner in) {
        for (int i = 0; i < 8; i++) {
            nextColors[i].x = in.nextInt(); // color of the first block
            nextColors[i].y = in.nextInt(); // color of the first block
        }
    }

    static class Field {

        int x, y, color;

        public Field(int x, int y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        @Override
        public String toString() {
            return "Field{" + "x=" + x + ", y=" + y + ", color=" + color + '}';
        }

    }
}
