import static java.lang.Integer.MAX_VALUE;
import java.util.Iterator;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

class Horse1 {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int N = in.nextInt();
    SortedSet<Integer> strengths = new TreeSet<>();
    for (int i = 0; i < N; i++) {
      int Pi = in.nextInt();
      if (strengths.contains(Pi)) {
        System.out.println("0");
        return;
      }
      strengths.add(Pi);
    }
    System.err.println(strengths.size() + " " + N);

    if (strengths.size() < N) {
      System.out.println("0");
      return;
    }

    Integer diff = MAX_VALUE;
    Iterator<Integer> iterator = strengths.iterator();
    Integer last = iterator.next();
    while (iterator.hasNext()) {
      Integer next = iterator.next();
      Integer newDiff = next - last;
      if (newDiff < diff) {
        diff = newDiff;
      }
      last = next;
    }
    System.out.println(diff);
  }
}
