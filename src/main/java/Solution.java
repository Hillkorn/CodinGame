import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            int n = in.nextInt(); // total number of players (2 to 4).
            int p = in.nextInt(); // your player number (0 to 3).
            for (int i = 0; i < n; i++) {
                int x0 = in.nextInt(); // starting X coordinate of lightcycle (or -1)
                int y0 = in.nextInt(); // starting Y coordinate of lightcycle (or -1)
                int x1 = in.nextInt(); // starting X coordinate of lightcycle (can be the same as X0 if you play before this player)
                int y1 = in.nextInt(); // starting Y coordinate of lightcycle (can be the same as Y0 if you play before this player)
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println("LEFT"); // A single line with UP, DOWN, LEFT or RIGHT
        }
    }
}
