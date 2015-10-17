
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

class Mime {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int N = in.nextInt(); // Number of elements which make up the association table.
    in.nextLine();
    int Q = in.nextInt(); // Number Q of file names to be analyzed.
    in.nextLine();
    HashMap<String, String> extMap = new HashMap<>();
    for (int i = 0; i < N; i++) {
      String EXT = in.next().toLowerCase(); // file extension
      String MT = in.next(); // MIME type.
//            System.err.println("Mime " + EXT + " " + MT);
      if (MT.length() > 0) {
        extMap.put(EXT, MT);
      }
      in.nextLine();
    }
    List<String> output = new ArrayList<>();
    for (int i = 0; i < Q; i++) {
      String FNAME = in.nextLine(); // One file name per line.
      //System.err.println("F " + FNAME);
      int index = FNAME.lastIndexOf(".") + 1;
      String ext = FNAME.substring(index).toLowerCase();
      //System.err.println("Ext " + ext);
      String mime = extMap.get(ext);
      if (mime != null && index >= 1) {
        output.add(mime);
      } else {
        output.add("UNKNOWN");
      }
    }

    output.stream().forEach((out) -> {
      System.out.println(out);
    });
  }
}
