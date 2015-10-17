
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Auto-generated code below aims at helping you parse the standard input according to the problem statement.
 *
 */
class MarsLander2 {

  public static final int landWidth = 1000;
  public static final int maxPower = 4;
  public static final float maxVs = -40.0f;

  public static class LandSurface {

    public Integer start, end, high;
  }

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int N = in.nextInt(); // the number of points used to draw the surface of Mars.
    System.err.println("N is " + N);
    Map<Integer, Integer> surface = new TreeMap<>();
    for (int i = 0; i < N; i++) {
      int LAND_X = in.nextInt(); // X coordinate of a surface point. (0 to 6999)
      int LAND_Y = in.nextInt(); // Y coordinate of a surface point. By linking all the points together in a sequential fashion, you form the surface of Mars.
      surface.put(LAND_X, LAND_Y);
      System.err.println("X " + LAND_X + " with Y " + LAND_Y);
    }
    LandSurface landSurface = getLandSurface(surface);

    // game loop
    while (true) {
      int X = in.nextInt();
      int Y = in.nextInt();
      int HS = in.nextInt(); // the horizontal speed (in m/s), can be negative.
      int VS = in.nextInt(); // the vertical speed (in m/s), can be negative.
      int F = in.nextInt(); // the quantity of remaining fuel in liters.
      int R = in.nextInt(); // the rotation angle in degrees (-90 to 90).
      int P = in.nextInt(); // the thrust power (0 to 4).

      int degrees = 0;
      int power = 0;
      if (landSurface.start + HS > X) {
        degrees = degrees > 0 ? 10 : degrees + 10;
      } else if (landSurface.end + HS < X) {
        degrees = degrees > 0 ? (-10) : degrees - 10;
      } else {
        if (VS <= maxVs) {
          power = maxPower;
        } else {
          power = power == 0 ? 0 : power--;
        }
      }
      System.out.println(degrees + " " + power);
//      System.out.println("-20 3"); // R P. R is the desired rotation angle. P is the desired thrust power.
    }
  }

  public static LandSurface getLandSurface(Map<Integer, Integer> surface) {
    Integer start = 0;
    Integer lastH = 0;
    for (Map.Entry<Integer, Integer> part : surface.entrySet()) {
      Integer x = part.getKey();
      Integer y = part.getValue();
      if ( x - start >= landWidth && lastH.equals(y) && x > 0) {
        LandSurface landSurface = new LandSurface();
        landSurface.start = start;
        landSurface.end = x;
        landSurface.high = y;
        return landSurface;
      }
      start = x;
      lastH = y;
    }
    return null;
  }
}
