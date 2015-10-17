
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Auto-generated code below aims at helping you parse the standard input according to the problem statement.
 *
 */
class Skynet {

  public static class FieldNode {

    public int id;
    public boolean exitGateway;
    public List<FieldNode> links;

    public FieldNode(int id) {
      this.id = id;
      links = new ArrayList<>();
      exitGateway = false;
    }

    @Override
    public String toString() {
      return String.valueOf(id);
    }
  }

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int N = in.nextInt(); // the total number of nodes in the level, including the gateways
    int L = in.nextInt(); // the number of links
    int E = in.nextInt(); // the number of exit gateways
    Map<Integer, FieldNode> nodes = new TreeMap<>();
    List<Integer> exits = new ArrayList<>();
    TreeMap<Integer, List<Integer>> blockedLinks = new TreeMap<>();
    for (int i = 0; i < N; i++) {
      nodes.put(i, new FieldNode(i));
      blockedLinks.put(i, new ArrayList<Integer>());
    }

    for (int i = 0; i < L; i++) {
      int N1 = in.nextInt(); // N1 and N2 defines a link between these nodes
      int N2 = in.nextInt();
      Player.FieldNode node1 = nodes.get(N1);
      Player.FieldNode node2 = nodes.get(N2);
      node1.links.add(node2);
      node2.links.add(node1);
    }

    for (int i = 0; i < E; i++) {
      int EI = in.nextInt(); // the index of a gateway node
      nodes.get(EI).exitGateway = true;
      exits.add(EI);
    }

    // game loop
    TreeSet<Integer> visited = new TreeSet<>();
    while (true) {
      int SI = in.nextInt(); // The index of the node on which the Skynet agent is positioned this turn
      visited.add(SI);
      Player.FieldNode node = nodes.get(SI);

      System.err.println("Check for linked gateways");
      boolean blocked = false;
      for (FieldNode link : node.links) {
        if (link.exitGateway && !blockedLinks.get(node.id).contains(link.id)) {
          addToMap(blockedLinks, node.id, link.id);
          addToMap(blockedLinks, link.id, node.id);
          System.out.println(node + " " + link);
          blocked = true;
          break;
        }
      }
      if (blocked) {
        continue;
      }

//      for (FieldNode link : node.links) {
//        if (!visited.contains(link.id)) {
//          System.out.println(node + " " + link);
//          blocked = true;
//          break;
//        }
//      }
//      if (blocked) {
//        continue;
//      }
      System.err.println("Check gateways");
      for (Integer exit : exits) {
        for (FieldNode link : nodes.get(exit).links) {
          if (!visited.contains(link.id) && !blockedLinks.get(exit).contains(link.id)) {
            addToMap(blockedLinks, exit, link.id);
            addToMap(blockedLinks, link.id, exit);
            System.out.println(exit + " " + link);
            blocked = true;
            break;
          }
        }
        if (blocked) {
          break;
        }
      }

      // Write an action using System.out.println()
      // To debug: System.err.println("Debug messages...");
//      System.out.println("1 2"); // Example: 0 1 are the indices of the nodes you wish to sever the link between
    }
  }

  private static void addToMap(Map<Integer, List<Integer>> map, int nodeField, int id) {
    if (map.containsKey(nodeField)) {
      map.get(nodeField).add(id);
    } else {
      ArrayList<Integer> arrayList = new ArrayList<>();
      arrayList.add(id);
      map.put(nodeField, arrayList);
    }
  }
}
