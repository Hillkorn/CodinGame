
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class CodeOfTheRings {

  private static final int LENGTH = 30;
  private static final int CHAR_RANGE = 27;
  private static final String UP = "+";
  private static final String DOWN = "-";

  private static int pos = 0;
  private static int[] map;

  private static final Character A = 'A';
  private static final Character Z = 'Z';

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    String magicPhrase = in.nextLine();
    init();
    IntStream chars = magicPhrase.chars();
    IntStream charsMapped = chars.map((int c) -> {
      return c == 32 ? 0 : c + 1 - A;
    });

    int[] transformed = charsMapped.toArray();
    Stream<Integer> charsStream = IntStream.of(transformed).boxed();

    long count = charsStream.distinct().count();
    System.err.println("CharTypes => " + count);

    if (count == 1) {
      solveOneLetter(transformed[0], transformed.length);
    } else if (count == 2 && transformed.length > 5) {

    } else {
      for (int i = 0; i < transformed.length; i++) {
        int item = transformed[i];
        findShortest(item);
      }
    }

    end();
  }

  private static void findShortest(int newLetter) {
    int localDelta = Math.abs(map[pos] - newLetter);
    int leftD = 100, leftP = 1;
    int rightD = 100, rightP = 1;
    for (int i = 1; i < localDelta; i++) {
      int rightPToCheck = (pos + rightP + 1) % LENGTH;
      if (rightD + rightP > ((rightP + 1) + (Math.abs(map[rightPToCheck] - newLetter)))) {
        rightP += 1;
        rightD = Math.abs(map[rightPToCheck] - newLetter);
      }
      int leftPToCheck = pos - leftP - 1 >= 0 ? pos - leftP - 1 : LENGTH - pos - leftP - 1;
      if (rightD + rightP > ((rightP + 1) + (Math.abs(map[leftPToCheck] - newLetter)))) {
        leftP += 1;
        leftD = Math.abs(map[leftPToCheck] - newLetter);
      }
    }
    if (localDelta > leftD + leftP || localDelta > rightD + rightP) {
      if (rightD + rightP <= leftD + leftP) {
        moveRight(rightP);
      } else {
        moveLeft(leftP);
      }
    }
    setLetter(newLetter);
    activateStone();
  }

  private static void setLetter(int newLetter) {
//    System.err.println("New Letter => " + newLetter);
//    System.err.println("Old Letter => " + map[pos]);

    int delta = Math.abs(map[pos] - newLetter);
    System.err.println("D => " + delta);

    String cmd;

    if (delta < CHAR_RANGE - delta) {
      cmd = map[pos] < newLetter ? UP : DOWN;
      IntStream.range(0, delta).forEach(x -> System.out.print(cmd));
    } else {
      cmd = map[pos] > newLetter ? UP : DOWN;
      IntStream.range(0, CHAR_RANGE - delta).forEach(x -> System.out.print(cmd));
    }
    map[pos] = newLetter;
  }

  private static void activateStone() {
    System.out.print(".");
  }

  private static void moveLeft(final int x) {
    pos = pos - x >= 0 ? pos - x : LENGTH - pos - x;
    IntStream.range(0, Math.abs(x)).forEach(q -> System.out.print("<"));
  }

  private static void moveRight(final int x) {
    pos = (pos + x) % LENGTH;
    IntStream.range(0, Math.abs(x)).forEach(q -> System.out.print(">"));
  }

  private static void init() {
    map = IntStream.range(0, LENGTH).map(q -> 0).toArray();
  }

  private static void end() {
    System.out.println("");
  }

  private static void solveOneLetter(int letter, int count) {
    for (int i = 1; i < count; i++) {

    }

    //loop setup letter
//    if (letter < 5 || letter > 22) {
      setLetter(letter);
//    } else {

//    }
    //loop triggers
    System.out.print(">");

    System.out.print(">-[<.>--]<..");
  }
}
