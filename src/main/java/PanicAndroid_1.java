
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class PanicAndroid_1 {
public static final String LEFT = "LEFT", RIGHT = "RIGHT";
private static final String WAIT = "WAIT";
private static final String BLOCK = "BLOCK";

public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int nbFloors = in.nextInt(), width = in.nextInt();
    int nbRounds = in.nextInt(), exitFloor = in.nextInt();
    int exitPos = in.nextInt(), nbTotalClones = in.nextInt();
    int nbAdditionalElevators = in.nextInt(), nbElevators = in.nextInt();
    Map<Integer, Integer> elevators = new HashMap<>();
    for (int i = 0; i < nbElevators; i++) {
        elevators.put(in.nextInt(), in.nextInt());
    }

    while (true) {
        int cloneFloor = in.nextInt(), clonePos = in.nextInt();
        String direction = in.next();
        Integer elPos = elevators.get(cloneFloor);
        if (cloneFloor == exitFloor) {
            if (exitPos < clonePos && direction.equals(RIGHT)) {
                System.out.println(BLOCK);
            } else if (exitPos > clonePos && direction.equals(LEFT)) {
                System.out.println(BLOCK);
            } else {
                System.out.println(WAIT); // action: WAIT or BLOCK
            }
        } else if (elPos != null) {
            if (elPos < clonePos && direction.equals(RIGHT)) {
                System.out.println(BLOCK);
            } else if (elPos > clonePos && direction.equals(LEFT)) {
                System.out.println(BLOCK);
            } else {
                System.out.println(WAIT); // action: WAIT or BLOCK
            }
        } else if (clonePos == 1 || clonePos == width - 1) {
            System.out.println(BLOCK);
        } else {
            System.out.println(WAIT); // action: WAIT or BLOCK
        }
    }
}
}
