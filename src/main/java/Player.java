import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    static class Factory {
        int id, production, owner = 0, nCybors;
        Map<Integer, Factory> linkedFactories = new HashMap<>();

        public Factory(int id, int production) {
            this.id = id;
            this.production = production;
        }

    }
    static class Troop {
        int owner, fromFactory, toFactory, nCyborgs, restDistance;

        public Troop(int owner, int fromFactory, int toFactory, int nCyborgs, int restDistance) {
            this.owner = owner;
            this.fromFactory = fromFactory;
            this.toFactory = toFactory;
            this.nCyborgs = nCyborgs;
            this.restDistance = restDistance;
        }

    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        List<Factory> factories = new ArrayList<>();
        int factoryCount = in.nextInt(); // the number of factories
        for (int i = 0; i < factoryCount; i++) {
            factories.add(new Factory(i, 0));
        }
        int linkCount = in.nextInt(); // the number of links between factories
        for (int i = 0; i < linkCount; i++) {
            int factory1 = in.nextInt();
            int factory2 = in.nextInt();
            int distance = in.nextInt();
            factories.get(factory1).linkedFactories.put(distance, factories.get(factory2));
            factories.get(factory2).linkedFactories.put(distance, factories.get(factory1));
        }

        List<Troop> troops;

        // game loop
        while (true) {
            troops = new ArrayList<>();
            int entityCount = in.nextInt(); // the number of entities (e.g. factories and troops)
            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();
                int arg1 = in.nextInt();
                int arg2 = in.nextInt();
                int arg3 = in.nextInt();
                int arg4 = in.nextInt();
                int arg5 = in.nextInt();
                if ("FACTORY".equals(entityType)) {
                    Factory currentFactory = factories.get(entityId);
                    currentFactory.owner = arg1;
                    currentFactory.nCybors = arg2;
                    currentFactory.production = arg3;
                } else { //Type is TROOP
                    troops.add(new Troop(arg1, arg2, arg3, arg4, arg5));
                }
            }

            List<Pair<Factory, Factory>> movements = new ArrayList<>();
            for (Factory factory : factories) {
                if (factory.owner == 1) {
                    int distance = Integer.MAX_VALUE;
                    Pair<Factory, Factory> move = null;
                    for (Map.Entry<Integer, Factory> entry : factory.linkedFactories.entrySet()) {
                        if (entry.getValue().owner != 1 && distance > entry.getKey()) {
                            distance = entry.getKey();
                            move = new Pair<>(factory, entry.getValue());
                        }
                    }
                    if (move != null) {
                        movements.add(move);
                    }
                }
            }

            if (!movements.isEmpty()) {
                int i = 0;
                for (Pair<Factory, Factory> movement : movements) {
                    System.out.print("MOVE " + movement.a.id + " " + movement.b.id + " " + movement.a.nCybors);
                    if (++i < movements.size()) {
                        System.out.print(";");
                    } else {
                        System.out.println("");
                    }
                }
            } else {
                System.out.println("WAIT");
            }
        }
    }

    static class Pair<A,B> {
        A a;
        B b;

        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }
}
