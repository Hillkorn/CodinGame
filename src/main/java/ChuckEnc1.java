
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse the standard input according to the problem statement.
 *
 */
class ChuckEnc1 {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    String MESSAGE = in.nextLine();

    byte[] msgBytes = MESSAGE.getBytes();
    boolean first = true;
    boolean last = !((msgBytes[0] << 1 & 0b10000000) == 0b10000000);
    for (byte msgByte : msgBytes) {
      for (int i = 1; i < 8; i++) {
        if ((0b10000000 & (msgByte << i)) == 0b10000000) {
//          System.err.println("Found 1");
          if (last) {
            System.out.print("0");
          } else {
            if (!first) {
              System.out.print(" ");
            }
            System.out.print("0 0");
          }
          last = true;
        } else {
//          System.err.println("Found 0");
          if (!last) {
            System.out.print("0");
          } else {
            if (!first) {
              System.out.print(" ");
            }
            System.out.print("00 0");
          }
          last = false;
          first = false;
        }
      }
    }
    System.out.println();
  }
}
