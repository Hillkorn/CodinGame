
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Save humans, destroy zombies!
 *
 */
class Player {

    public int x, y, tox = 0, toy = 0;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        Player player;
        HashMap<Integer, Human> humans = new HashMap<>();
        HashMap<Integer, Zombie> zombies = new HashMap<>();
        int x = in.nextInt();
        int y = in.nextInt();
        player = new Player(x, y);
        int humanCount = in.nextInt();
        for (int i = 0; i < humanCount; i++) {
            int humanId = in.nextInt();
            int humanX = in.nextInt();
            int humanY = in.nextInt();
            humans.put(humanId, new Human(humanId, humanX, humanY));
        }
        int zombieCount = in.nextInt();
        for (int i = 0; i < zombieCount; i++) {
            int zombieId = in.nextInt();
            int zombieX = in.nextInt();
            int zombieY = in.nextInt();
            int zombieXNext = in.nextInt();
            int zombieYNext = in.nextInt();
            zombies.put(zombieId, new Zombie(zombieXNext, zombieYNext, zombieId, zombieX, zombieY));
        }

        decide(player, humans, zombies);
        // game loop
        while (true) {
            player.x = in.nextInt();
            player.y = in.nextInt();
            humanCount = in.nextInt();
            final Set<Integer> humanKeys = new HashSet<>();
            for (int i = 0; i < humanCount; i++) {
                int humanId = in.nextInt();
                humanKeys.add(humanId);
                Human human = humans.get(humanId);
                human.x = in.nextInt();
                human.y = in.nextInt();
            }
            Iterator<Integer> humanIterator = humans.keySet().stream().filter((Integer t) -> !humanKeys.contains(t)).collect(Collectors.toList()).iterator();
            for (Iterator<Integer> iterator = humanIterator; iterator.hasNext();) {
                humans.remove(iterator.next());
            }
            zombieCount = in.nextInt();
            final Set<Integer> zombieKeys = new HashSet<>();
            for (int i = 0; i < zombieCount; i++) {
                int zombieId = in.nextInt();
                zombieKeys.add(zombieId);
                Zombie zombie = zombies.get(zombieId);
                zombie.x = in.nextInt();
                zombie.y = in.nextInt();
                zombie.nx = in.nextInt();
                zombie.ny = in.nextInt();
            }
            Iterator<Integer> zombieIterator = zombies.keySet().stream().filter((Integer t) -> !zombieKeys.contains(t)).collect(Collectors.toList()).iterator();
            for (Iterator<Integer> iterator = zombieIterator; iterator.hasNext();) {
                zombies.remove(iterator.next());
            }

            decide(player, humans, zombies);
        }
    }

    private static void decide(Player player, HashMap<Integer, Human> humans, HashMap<Integer, Zombie> zombies) {
        List<Human> savable = getSavable(player, humans, zombies);
        Iterator<Human> iterator = savable.iterator();
        if (iterator.hasNext()) {
            Human next = iterator.next();
//            System.err.println("Human " + next.id);
            Zombie zombie = getNearestZombie(next, zombies);
            System.err.println("Zombie " + zombie.id + " " + getDistance(next, zombie));
//            System.err.println("Zombie " + zombies.get(21).id + " " + getDistance(next, zombies.get(21)));
            List<Zombie> zombiesInRange = getZombiesInRange(player, zombie, zombies);
            if (zombiesInRange.size() > 2) {
                int x = 0, y = 0;
                for (Zombie zombieToCalc : zombiesInRange) {
                    x += zombieToCalc.x;
                    y += zombieToCalc.y;
                }
                x /= zombiesInRange.size();
                y /= zombiesInRange.size();
                move(x, y, player);
            } else {
                move(zombie.x, zombie.y, player);
            }
        } else {
            Human human = getNearestHuman(player, humans);
            move(human.x, human.y, player);
        }
    }

    private static List<Zombie> getZombiesInRange(Player player, Zombie zombie, HashMap<Integer, Zombie> zombies) {
        int dx = zombie.x - player.x;
        int dy = zombie.y - player.y;
        return zombies.values().stream().filter(new Predicate<Zombie>() {
            @Override
            public boolean test(Zombie t) {
                int tx = t.x - player.x;
                int ty = t.y - player.y;
                if (((tx <= 0 && dx <= 0) || (tx > 0 && dx > 0)) && ((ty <= 0 && dy <= 0) || (ty > 0 && dy > 0))) {
                    if (getDistance(player, t) <= 3000) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }).collect(Collectors.toList());
    }

    private static List<Human> getSavable(Player player, HashMap<Integer, Human> humans, HashMap<Integer, Zombie> zombies) {
        return humans.values().stream().filter((Human t) -> {
            Zombie nearestZombie = getNearestZombie(t, zombies);
            int playerHuman = getDistance(player, t);
            int humanZombie = getDistance(t, nearestZombie);
            return ((playerHuman - 1600) / 1000) < (humanZombie) / 400;
        }).sorted((Human o1, Human o2) -> {
            Zombie nearestZombie1 = getNearestZombie(o1, zombies);
            Zombie nearestZombie2 = getNearestZombie(o2, zombies);
            return getDistance(nearestZombie1, o1) - getDistance(nearestZombie2, o2);
        })
            .collect(Collectors.toList());
    }

    private static Human getNearestHuman(final Player player, HashMap<Integer, Human> humans) {
        Optional<Human> min = humans.values().stream().min((Human z1, Human z2) -> {
            int d1 = getDistance(player, z1);
            int d2 = getDistance(player, z2);
            return d1 - d2;
        });
        return min.get();
    }

    private static Zombie getNearestZombie(final Player player, HashMap<Integer, Zombie> zombies) {
        Optional<Zombie> min = zombies.values().stream().min((Zombie z1, Zombie z2) -> {
            int d1 = getDistance(player, z1);
            int d2 = getDistance(player, z2);
            return d1 - d2;
        });
        return min.get();
    }

    private static Zombie getNearestZombie(final Human human, HashMap<Integer, Zombie> zombies) {
        Optional<Zombie> min = zombies.values().stream().min((Zombie z1, Zombie z2) -> {
            int d1 = getDistance(human, z1);
            int d2 = getDistance(human, z2);
            return d1 - d2;
        });
        return min.get();
    }

    private static int getDistance(Human player, Human to) {
        int x = player.x - to.x;
        int y = player.y - to.y;
        return (int) Math.sqrt((x * x) + (y * y));
    }

    private static int getDistance(Player player, Human to) {
        int x = player.x - to.x;
        int y = player.y - to.y;
        return (int) Math.sqrt((x * x) + (y * y));
    }

    private static void move(int x, int y, Player player) {
        player.tox = x;
        player.toy = y;
        System.out.println(x + " " + y); // Your destination coordinates
    }

    static class Human {

        public int id, x, y;

        public Human(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    public static class Pos {

        public int x, y;

        public Pos(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Zombie extends Human {

        public int nx, ny;

        public Zombie(int nx, int ny, int id, int x, int y) {
            super(id, x, y);
            this.nx = nx;
            this.ny = ny;
        }
    }
}
