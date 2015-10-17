
import java.util.Arrays;
import java.util.Scanner;

class Player {

  public static class Field {

    public int x, y;

    public Field(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }

  public int x, y, timeJumps;

  public static int moveToX = 0, moveToY = 0;

  public static int[][] map;

  public static int maxX = 35, maxY = 20, gameRound = 0;

  public static Player myPlayer, enemies[];

  public static void main(String args[]) {
    map = new int[maxX][maxY];
    myPlayer = new Player();

    Scanner in = new Scanner(System.in);
    int opponentCount = in.nextInt(); // Opponent count
    enemies = new Player[opponentCount];
    for (int i = 0; i < opponentCount; i++) {
      enemies[i] = new Player();
    }

    while (true) {
      gameRound = in.nextInt();
      myPlayer.x = in.nextInt(); // Your x position
      myPlayer.y = in.nextInt(); // Your y position
      myPlayer.timeJumps = in.nextInt(); // Remaining back in time

      for (int i = 0; i < opponentCount; i++) {
        enemies[i].x = in.nextInt();
        enemies[i].y = in.nextInt();
        enemies[i].timeJumps = in.nextInt();
      }

      for (int i = 0; i < 20; i++) {
        String line = in.next(); // One line of the map ('.' = free, '0' = you, otherwise the id of the opponent)
        int length = line.length();
        for (int c = 0; c < length; c++) {
          char ownedBy = line.charAt(c);
          if (ownedBy == '.') {
            map[c][i] = -1;
          } else {
            map[c][i] = ownedBy - '0';
          }
        }
      }

      createSquare();
//      System.err.println(map[0][0]);
//      System.err.println(map[enemies[0].x][enemies[0].y]);
      System.out.println(moveToX + " " + moveToY); // action: "x y" to move or "BACK rounds" to go back in time
    }
  }

  public static void createSquare() {
    if (map[moveToX][moveToY] == -1 && moveToX != myPlayer.x && moveToY != myPlayer.y) {
      return;
    }

    int hX = maxX / 2;
    int hY = maxY / 2;
    int ix, iy;
    if (myPlayer.x < hX && myPlayer.y < hY) { //Left Top
      ix = -1;
      iy = -1;
    } else if (myPlayer.x < hX && myPlayer.y >= hY) { // Left Bot
      ix = -1;
      iy = 1;
    } else if (myPlayer.x >= hX && myPlayer.y < hY) { // Right Top
      ix = 1;
      iy = -1;
    } else if (myPlayer.x >= hX && myPlayer.y >= hY) { // Right Bot
      ix = 1;
      iy = 1;
    }

    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < 4; y++) {

      }
    }
  }

//  public static void findNearestCorner() {
//    int hX = maxX / 2;
//    int hY = maxY / 2;
////    if
//  }

  public static int distanceToNearestEnemy(Player enemies[]) {
    return Arrays.stream(enemies).map((Player enemy) -> calcDistance(myPlayer, enemy)).min((Integer d1, Integer d2) -> d1.compareTo(d2)).get();
  }

  public static int distanceToEnemy(Player enemy) {
    return calcDistance(myPlayer, enemy);
  }

  public static void move() {
    if (map[moveToX][moveToY] == -1 && moveToX != myPlayer.x && moveToY != myPlayer.y) {
      return;
    }

    moveToX = myPlayer.x;
    moveToY = myPlayer.y;
    if (myPlayer.x + 1 < maxX && map[myPlayer.x + 1][myPlayer.y] < 0) {
      moveToX += 1;
    } else if (myPlayer.x - 1 >= 0 && map[myPlayer.x - 1][myPlayer.y] < 0) {
      moveToX -= 1;
    } else if (myPlayer.y + 1 < maxY && map[myPlayer.x][myPlayer.y + 1] < 0) {
      moveToY += 1;
    } else if (myPlayer.y - 1 >= 0 && map[myPlayer.x + 1][myPlayer.y - 1] < 0) {
      moveToY -= 1;
    } else {
      Field freeField = findFreeField();
      moveToX = freeField.x;
      moveToY = freeField.y;
    }
  }

  public static Field findFreeField() {
    for (int x = 0; x < maxX; x++) {
      for (int y = 0; y < maxY; y++) {
        if (map[x][y] == -1) {
          return new Field(x, y);
        }
      }
    }
    return new Field(0, 0);
  }

  public static int calcDistance(int x, int y, Field f2) {
    return calcDistance(x, y, f2.x, f2.y);
  }

  public static int calcDistance(Field f, Field f2) {
    return calcDistance(f.x, f.y, f2.x, f2.y);
  }

  public static int calcDistance(Player p, Player p2) {
    return calcDistance(p.x, p.y, p2);
  }

  public static int calcDistance(int x, int y, Player p) {
    return calcDistance(x, y, p.x, p.y);
  }

  public static int calcDistance(int x, int y, int x2, int y2) {
    int xd = Math.abs(x - x2);
    int yd = Math.abs(y - y2);
    return (int) Math.round(Math.sqrt((xd * xd) + (yd * yd)));
  }
//  public static void move() {
//    moveToX = myPlayer.x;
//    moveToY = myPlayer.y;
//    if (myPlayer.x + 1 < maxX && map[myPlayer.x + 1][myPlayer.y] < 0) {
//      moveToX += 1;
//    } else if (myPlayer.x - 1 >= 0 && map[myPlayer.x - 1][myPlayer.y] < 0) {
//      moveToX -= 1;
//    } else if (myPlayer.y + 1 < maxY && map[myPlayer.x][myPlayer.y + 1] < 0) {
//      moveToY += 1;
//    } else if (myPlayer.y - 1 >= 0 && map[myPlayer.x + 1][myPlayer.y - 1] < 0) {
//      moveToY -= 1;
//    } else {
//      if (myPlayer.x - 1 >= 0) {
//        moveToX -= 1;
//      } else {
//        moveToX += 1;
//      }
//      if (myPlayer.y - 1 >= 0) {
//        moveToY -= 1;
//      } else {
//        moveToY += 1;
//      }
//    }
//  }
}
