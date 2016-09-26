
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class Thor1 {

  static public class Position {

    public int x;
    public int y;

    public Position(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public String toString() {
      return "Position " + x + " " + y;
    }
  }

  public static int TX, TY;

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    TX = in.nextInt();
    TY = in.nextInt();

    // game loop
    while (true) {
      System.err.println("Thor " + TX + " " + TY);
      int H = in.nextInt(); // the remaining number of hammer strikes.
      int N = in.nextInt(); // the number of giants which are still present on the map.
//      for (int i = 0; i < N; i++) {
//        int X = in.nextInt();
//        int Y = in.nextInt();
//      }
      Stream<Thor1.Position> enemiesStream = IntStream.range(0, N).mapToObj((int i) -> {
        int X = in.nextInt();
        int Y = in.nextInt();
          return new Thor1.Position(X, Y);
      });

      if (N > H) {
        List<Position> sourrouningEnemies = (List<Position>) enemiesStream.filter(p -> calcDistance(p) <= 3).collect(Collectors.toList());
        sourrouningEnemies.forEach((Position p) -> System.err.println(p));
        long count = sourrouningEnemies.size();
        if (count >= N / H || N == count) {
          strike();
        } else if (count > 0) {
          int u = 0, d = 0, l = 0, r = 0;
          List<Position> sourEnemiesDist = (List<Position>) sourrouningEnemies.stream().map((Position p) -> calcDistancePosition(p)).collect(Collectors.toList());
          for (Position sourEnemy : sourEnemiesDist) {
            System.err.println("Enenmy " + sourEnemy);
            if (sourEnemy.x > 0) {
              r = 1;
            } else if (sourEnemy.x < 0) {
              l = 1;
            }
            if (sourEnemy.y > 0) {
              u = 1;
            } else if (sourEnemy.y < 0) {
              d = 1;
            }
          }
          System.err.println("r" + r + " l" + l + " d" + d + " u" + u);
          if (d + u + l + r == 4) {
            strike();
          } else {
            move(r - l, u - d);
          }
        } else {
          stay();
        }
      } else {
          Optional<Position> min = enemiesStream.min((Thor1.Position o1, Thor1.Position o2) -> {
          return calcDistance(o1) - calcDistance(o2);
        });
        if (calcDistance(min.get()) <= 1) {
          strike();
        } else {
          stay();
        }
      }

    }
  }

  public static void strike() {
    System.out.println("STRIKE");
  }

  public static void stay() {
    System.out.println("WAIT");
  }

  public static void move(int dx, int dy) {
    String dir = "";
    if (dy < 0) {
      TY -= 1;
      dir += "N";
    } else if (dy > 0) {
      TY += 1;
      dir += "S";
    }
    if (dx < 0) {
      TX -= 1;
      dir += "W";
    } else if (dx > 0) {
      TX += 1;
      dir += "E";
    }
    System.out.println(dir); // The movement or action to be carried out: WAIT STRIKE N NE E SE S SW W or N
  }

  public static int calcDistance(Position p) {
    int dx1 = Math.abs(TX - p.x);
    int dy1 = Math.abs(TY - p.y);
    return (int) Math.sqrt((dx1 * dx1) + (dy1 * dy1));
  }

  public static Position calcDistancePosition(Position p) {
    return new Position(TX - p.x, TY - p.y);
  }
}
