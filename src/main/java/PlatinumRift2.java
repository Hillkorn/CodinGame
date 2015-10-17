
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class PlatinumRift2 {

  public enum ZoneState {

    FREE, FREE_FIGHTING, ENEMY_UNPROTECTED, ENEMY_WITH_PODS, ENEMY_FIGHTING, MY_FIGHTING, MY_UNPROTECTED, MY_WITH_PODS, SAFE;
  }

  public static Comparator<Integer> intComparator = new Comparator<Integer>() {

    @Override
    public int compare(Integer o1, Integer o2
    ) {
      return o1 - o2;
    }
  };

  public static class Zone {

    public Integer id;
    public Integer owner = -1;
    public Integer platinum;
    public List<Zone> links;
    public int pods[];
    public boolean safe = false;
    public int safeVal = 0;
    public int unitsNextRound = 0;
    public int neededUnits = 0;
    public int platinumPriority = 0;
    public Map<Integer, List<Zone>> platinumDistances;

    public Zone(Integer id, Integer platinum) {
      platinumDistances = new TreeMap<>();
      this.id = id;
      this.platinum = platinum;
      links = new ArrayList<>();
    }

    @Override
    public String toString() {
      return "[Zone: " + id + "]";
    }
  }

  public static class Continent {

    public TreeSet<Zone> zones;
    public Integer size;
    public Integer myUnits;
    public Integer enemies;
    public Integer platinum;
    public Integer myZonesCount;
    public List<Zone> myZones;
    public Integer freeZonesCount;
    public List<Integer> freeZones;
    HashMap<Integer, List<Zone>> platinumRichZones;

    public Continent() {
      zones = new TreeSet<>(zoneComparator);
      size = 0;
      myUnits = 0;
      platinum = 0;
      platinumRichZones = new HashMap<>();
    }

    @Override
    public String toString() {
      return "[Size: " + size + ", Platinum: " + platinum + "]";
    }
  }

  public static boolean noGoodNext = false;
  public static boolean moved = false;
  public static int maxPlat = 0;
  public static int roundNumber = 0;
  public static int playerCount = 0;
  public static Integer payablePods = 0;
  public static Integer podCost = 20; // One POD cost 20 platinum
  public static Comparator<Zone> zoneComparator = new Comparator<Zone>() {

    @Override
    public int compare(Zone o1, Zone o2) {
      return o1.id - o2.id;
    }
  };

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    playerCount = in.nextInt(); // the amount of players (2 to 4)
    int myId = in.nextInt(); // my player ID (0, 1, 2 or 3)
    int zoneCount = in.nextInt(); // the amount of zones on the map
    int linkCount = in.nextInt(); // the amount of links between all zones
    in.nextLine();

    HashMap<Integer, Zone> zones = new HashMap<>();
    List<Continent> continents = new ArrayList<>();
    for (int i = 0; i < zoneCount; i++) {
      int zoneId = in.nextInt(); // this zone's ID (between 0 and zoneCount-1)
      int platinumSource = in.nextInt(); // the amount of Platinum this zone can provide per game turn
      Zone newZone = new Zone(zoneId, platinumSource);
      newZone.platinum = platinumSource;
      zones.put(zoneId, newZone);
      in.nextLine();
    }

    for (int i = 0; i < linkCount; i++) {
      int zone1 = in.nextInt();
      int zone2 = in.nextInt();
      Zone z1 = zones.get(zone1);
      Zone z2 = zones.get(zone2);
      z1.links.add(z2);
      z2.links.add(z1);
      in.nextLine();
    }

    for (Zone zone : zones.values()) {
      boolean added = false;
      for (Continent continent : continents) {
        if (continent.zones.contains(zone)) {
          added = true;
          continent.platinum += zone.platinum;
          continent.size++;
          if (zone.platinum > 0) {
            maxPlat = maxPlat < zone.platinum ? zone.platinum : maxPlat;
            if (!continent.platinumRichZones.containsKey(zone.platinum)) {
              continent.platinumRichZones.put(zone.platinum, new ArrayList<Zone>());
            }
            continent.platinumRichZones.get(zone.platinum).add(zone);
          }
          break;
        }
      }
      if (!added) {
        Continent continent = new Continent();
        continent.platinum += zone.platinum;
        continent.zones.add(zone);
        continents.add(continent);
        continent.size++;
        if (zone.platinum > 0) {
          maxPlat = maxPlat < zone.platinum ? zone.platinum : maxPlat;
          if (!continent.platinumRichZones.containsKey(zone.platinum)) {
            continent.platinumRichZones.put(zone.platinum, new ArrayList<Zone>());
          }
          continent.platinumRichZones.get(zone.platinum).add(zone);
        }
        List<Zone> toAdd = zone.links;
        List<Zone> nextAdd = new ArrayList<>();
        do {
          for (Zone add : toAdd) {
            for (Zone link : add.links) {
              if (!continent.zones.contains(link)) {
                continent.zones.add(link);
                nextAdd.add(link);
              }
            }
          }
          toAdd = nextAdd;
          nextAdd = new ArrayList<>();
        } while (!toAdd.isEmpty());
      }
    }
    setPlatinumLinkInformation(continents);
    int platinum = in.nextInt(); // my available Platinum
    payablePods = platinum / podCost;
    in.nextLine();
    for (int i = 0; i < zoneCount; i++) {
      int zId = in.nextInt(); // this zone's ID
      int ownerId = in.nextInt(); // the player who owns this zone (-1 otherwise)
      int pods[] = {in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt()};
    }
    if (playerCount > 2) {
      firstRound(myId, continents, platinum);
    } else {
      firstRound1v1(myId, continents, platinum);
    }
    roundNumber++;

    System.err.println("P Dist of 16: " + zones.get(16).platinumDistances);
    System.err.println("P Dist of 22: " + zones.get(22).platinumDistances);
    gameLoop(in, zoneCount, zones, myId, continents);
  }

  public static void gameLoop(Scanner in, int zoneCount, HashMap<Integer, Zone> zones, int myId, List<Continent> continents) {
    while (true) {
      List<Integer> myUnitZones = new ArrayList<>();
      for (Continent continent : continents) {
        continent.myZones = new ArrayList<>();
        continent.freeZones = new ArrayList<>();
        continent.freeZonesCount = 0;
        continent.myZonesCount = 0;
        continent.myUnits = 0;
        continent.enemies = 0;
      }
      int platinum = in.nextInt(); // my available Platinum
      payablePods = platinum / podCost;
      in.nextLine();
      for (int i = 0; i < zoneCount; i++) {
        int zId = in.nextInt(); // this zone's ID
        Zone zone = zones.get(zId);
        Continent continent = null;
        for (Continent c : continents) {
          if (c.zones.contains(zone)) {
            continent = c;
            break;
          }
        }
        int ownerId = in.nextInt(); // the player who owns this zone (-1 otherwise)
        int pods[] = {in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt()};
        pods = calulateEnemies(pods);
        zone.pods = pods;
        zone.owner = ownerId;
        zone.safeVal = 1;
        zone.unitsNextRound = 0;

        if (pods[myId] > 0) { //Im inside
          myUnitZones.add(zId);
        }

        if (ownerId == (-1)) {
          continent.freeZones.add(zId);
          continent.freeZonesCount++;
        } else if (ownerId == myId) {
          continent.myZonesCount++;
          continent.myUnits += pods[myId];
          zone.safe = true;
          continent.myZones.add(zone);
          if (containsEnemy(myId, pods) && pods[myId] > 0) {
            continent.enemies = getHighestEnemyCount(myId, pods);
          }
        } else {
          zone.safe = false;
          continent.myUnits += pods[myId];
          if (containsEnemy(myId, pods)) {
            continent.enemies = getHighestEnemyCount(myId, pods);
          }
        }
        in.nextLine();
      }
      setSafeStates(myId, continents);

      movePods(myUnitZones, zones, myId);

      buyPods(platinum, continents, myUnitZones, myId);
      roundNumber++;
    }
  }

  public static void buyPods(int platinum, List<Continent> continents, List<Integer> myUnitZones, int myId) {
    if (payablePods == 0) {
      waitCmd();
      return;
    }
    Integer broughtPods = 0;
    //Defend my zones!
    List<Zone> zonesToBuy = new ArrayList<>();
    int foundZones = 0;
    for (Continent continent : continents) {
      for (int c = maxPlat - 1; c >= 0; c--) {
        List<Zone> goodZones = continent.platinumRichZones.get(c);
        if (goodZones == null) {
          continue;
        }
        for (Zone goodZone : goodZones) {
          if (goodZone.owner == myId && goodZone.safeVal == 0 && goodZone.pods[myId] < 3) {
            zonesToBuy.add(goodZone);
            foundZones++;
          }
        }
      }
    }
    if (zonesToBuy.size() > 0) {
      for (int i = 0; zonesToBuy.size() > 0 && payablePods > broughtPods;) {
        Zone highest = null;
        for (Zone zone : zonesToBuy) {
          if (highest == null) {
            highest = zone;
          } else if (highest.platinum < zone.platinum) {
            highest = zone;
          }
        }
        if (payablePods >= broughtPods + highest.neededUnits) {
          buyPod(highest, broughtPods + highest.neededUnits);
          i += broughtPods + highest.neededUnits;
        } else {
          buyPod(highest, payablePods - broughtPods);
          broughtPods = payablePods;
        }
        zonesToBuy.remove(highest);
      }
    }
    TreeMap<Integer, List<Zone>> buyPods = new TreeMap<>();

    //Find next good zone
    boolean found = true;
    if (!noGoodNext && foundZones < payablePods) {
      while (broughtPods < payablePods && found) {
        found = false;
        for (Continent continent : continents) {
          Zone nextGoodZone = getNextGoodZone(myId, continent);
          if (nextGoodZone != null) {
            myUnitZones.add(nextGoodZone.id);
            nextGoodZone.owner = myId;
            int amount = nextGoodZone.platinum > 2 && payablePods - broughtPods > 1 ? 2 : 1;
            buyPod(nextGoodZone, amount);
            broughtPods += amount;
            found = true;
            if (broughtPods == payablePods) {
              break;
            }
          }
        }
        if (!found) {
          System.err.println("No good found :/");
          noGoodNext = true;
        }
      }
    }

    if (foundZones < payablePods) {
      found = true;
      while (foundZones < payablePods && found) {
        found = false;
        for (Continent continent : continents) {
          for (int i = maxPlat - 1; i >= 0; i--) {
            if (!continent.platinumRichZones.containsKey(i)) {
              continue;
            }
            List<Zone> zones = continent.platinumRichZones.get(i);
            for (Zone zone : zones) {
              if (zone.owner == -1) {
                for (Zone link : zone.links) {
                  if (link.owner == myId) {
                    addToMap(buyPods, 2, link);
                    found = true;
                    foundZones++;
                    break;
                  }
                }
              }
            }
          }
        }
      }
    }
    if (foundZones < payablePods) {
      for (Continent continent : continents) {
        if (continent.enemies == 0 && continent.myUnits > 0) {
          continue;
        }
        if (continent.freeZonesCount > 0) {
          for (Integer freeZone : continent.freeZones) {
            addToMap(buyPods, 4, getZoneFor(continent.zones, freeZone));
            foundZones++;
          }
        }
      }
    }
    if (foundZones < payablePods) {
      for (Continent continent : continents) {
        if (continent.enemies >= continent.myUnits - (continent.myUnits / 6)) {
          for (Zone zone : continent.myZones) {
            if (!zone.safe) {
              addToMap(buyPods, 6, zone);
              foundZones++;
            }
          }
        }
      }
    }

//    System.err.println("Map is => " + buyPods);
    for (int i = 0; i < 7; i++) {
      if (buyPods.containsKey(i)) {
        List<Zone> buyZones = buyPods.get(i);
        List<Zone> nextBuyZones = buyPods.get(i);
        Integer buyPerZone = payablePods - broughtPods - nextBuyZones.size() < foundZones ? 1 : (payablePods - broughtPods) / foundZones;
        for (Zone buyZone : buyZones) {
          if (payablePods - broughtPods <= 0) {
            break;
          }
          int amount = 0;
          if (payablePods >= 15) {
            amount = 15 - buyZone.unitsNextRound;
          } else {
            amount = 4 - buyZone.unitsNextRound;
          }
          if (amount > 0) {
            int buyingPods = amount < buyPerZone ? amount : buyPerZone;
            buyPod(buyZone, buyingPods);
            broughtPods += buyingPods;
          }
        }
      }
    }

    endMove();
  }

  public static void movePods(List<Integer> myUnitZones, HashMap<Integer, Zone> zones, int myId) {
    moved = false;
    for (Integer zoneId : myUnitZones) {
      HashMap<Integer, List<Zone>> sendTo = new HashMap<>();
      Zone myZone = zones.get(zoneId);
      if (containsEnemy(myId, myZone.pods) && myZone.owner == myId) { //myzone contains enemy
        if (myZone.pods[myId] > 3) {
          for (Zone linkedZone : myZone.links) {
            if (linkedZone.owner != myId) {
              addToMap(sendTo, 4, linkedZone);
            }
          }
        } else {
          continue;
        }
      }
      if ((myZone.platinum == 0) || (myZone.safeVal == 1) || myZone.pods[myId] > 1) {
        Set<Map.Entry<Integer, List<Zone>>> entrySet = myZone.platinumDistances.entrySet();
        boolean foundToGo = false;
        for (Map.Entry<Integer, List<Zone>> entry : entrySet) {
          for (Zone entryZone : entry.getValue()) {
            if (entryZone.owner != myId && !containsEnemy(myId, entryZone.pods)) {
              Integer distance = entry.getKey();
              if (distance <= 1) {
                continue;
              }
              for (Zone link : myZone.links) {
                for (Map.Entry<Integer, List<Zone>> linkedES : link.platinumDistances.entrySet()) {
                  if (linkedES.getValue().contains(entryZone) && linkedES.getKey() < distance) {
                    addToMap(sendTo, 2, link);
                    foundToGo = true;
                    break;
                  }
                }
                if (foundToGo) {
                  break;
                }
              }
            }
            if (foundToGo) {
              break;
            }
          }
          if (foundToGo) {
            break;
          }
        }
      }
      for (Zone linkedZone : myZone.links) {
        if (linkedZone.owner == -1) { // Zone is free
          if (linkedZone.platinum > 0) { // Zone contains platinum!
            if ((myZone.safeVal > 0 || linkedZone.platinum > myZone.platinum) && linkedZone.pods[myId] <= 8) {
              addToMap(sendTo, 0, linkedZone);
            }
          } else if (linkedZone.platinumPriority >= myZone.platinumPriority) {
            if (linkedZone.pods[myId] <= 8 && myZone.safeVal == 1) {
              addToMap(sendTo, 2, linkedZone);
            }
          } else {
            if (myZone.safeVal == 0 && myZone.platinum > 0 && myZone.pods[myId] <= 3) { // Stay in zone
              myZone.unitsNextRound = myZone.pods[myId];
              continue;
            }
            boolean send = false;
            for (Zone link : linkedZone.links) {
              if (link != myZone && containsEnemy(myId, link.pods)) {
                addToMap(sendTo, 6, linkedZone);
              }
            }
            if (!send) {
              addToMap(sendTo, 4, linkedZone);
            }
          }
        } else if (linkedZone.owner != myId) { // Enemy zone
          if ((myZone.safeVal == 0 && myZone.platinum > linkedZone.platinum) && (myZone.pods[myId] <= 4)) {
            myZone.unitsNextRound = myZone.pods[myId];
            continue;
          }
          if (!containsEnemy(myId, linkedZone.pods)) { // No enemies in enemy zone
            if (linkedZone.platinum > 0) { // Zone contains platinum!
              addToMap(sendTo, 0, linkedZone);
//            } else if (linkedZone.platinumPriority >= myZone.platinumPriority) {
//              if ((myZone.safeVal > 0 || linkedZone.platinum > myZone.platinum) && linkedZone.pods[myId] <= 3) {
//                addToMap(sendTo, 1, linkedZone);
//              }
            } else {
              addToMap(sendTo, 3, linkedZone);
            }
          } else if (myZone.pods[myId] > getHighestEnemyCount(myId, linkedZone.pods) || myZone.pods[myId] >= 4) { // Enemy zone has fewer pods than mine
            if (linkedZone.platinum > 0) { // Zone contains platinum!
              addToMap(sendTo, 1, linkedZone);
            } else if (linkedZone.platinumPriority >= myZone.platinumPriority) {
              if ((myZone.safeVal > 0 || linkedZone.platinum > myZone.platinum) && linkedZone.pods[myId] <= 3) {
                addToMap(sendTo, 3, linkedZone);
              }
            } else {
              addToMap(sendTo, 4, linkedZone);
            }
          } else {
            addToMap(sendTo, 6, linkedZone);
          }
        } else { // My Zone
          if (linkedZone.safe == false && (myZone.platinum < linkedZone.platinum)) {
            if (containsEnemy(myId, linkedZone.pods)) {
              addToMap(sendTo, 2, linkedZone);
            } else if (linkedZone.platinum > 0 && linkedZone.safeVal == 0 && linkedZone.neededUnits > linkedZone.pods[myId]) {
              addToMap(sendTo, linkedZone.platinum > 2 ? 2 : 3, linkedZone);
            } else {
              addToMap(sendTo, 7, linkedZone);
            }
          } else if (linkedZone.safeVal < myZone.safeVal) {
            addToMap(sendTo, 9, linkedZone);
          }
        }
      }
      if (sendTo.isEmpty()) {
        myZone.unitsNextRound = myZone.pods[myId];
        continue;
      }
      moved = true;
      Integer myPods = myZone.pods[myId];
      Integer amount = myPods < sendTo.size() ? myPods : myPods / sendTo.size();
      for (int i = 0; i < 10; i++) {
        if (i > 4 && myZone.platinum > 0 && myZone.safeVal == 0) {
          break;
        }
        if (sendTo.containsKey(i)) {
          List<Zone> toIds = sendTo.get(i);
          for (Zone to : toIds) {
            if (myPods <= 0) {
              break;
            }
            movePod(myZone, to, amount);
            myPods -= amount;
            myZone.unitsNextRound = myPods;
          }
        }
        if (myPods <= 0) {
          break;
        }
      }
    }
    if (moved) {
      endMove();
    } else {
      waitCmd();
    }
  }

  private static void firstRound1v1(int myId, List<Continent> continents, Integer platinum) {
    waitCmd();
    int i = 0;
    int max = platinum / podCost;
    TreeSet<Zone> alreadySet = new TreeSet<>(zoneComparator);
    do {
      for (Continent continent : continents) {
        if (continent.size < 12 && continent.myUnits > 0 && continent.platinum < 12) {
          continue;
        }
        Zone bestZone = null;
        int sumPrio = 0;
        for (List<Zone> zones : continent.platinumRichZones.values()) {
          for (Zone zone : zones) {
            if (alreadySet.contains(zone)) {
              continue;
            }
            if (bestZone == null) {
              bestZone = zone;
              sumPrio = bestZone.platinum;
              for (Zone link : zone.links) {
                sumPrio += link.platinum;
              }
            } else {
              int currentPrio = zone.platinum;
              for (Zone link : zone.links) {
                currentPrio += link.platinum;
              }
              if (currentPrio >= sumPrio || (currentPrio == sumPrio && zone.links.size() <= bestZone.links.size())) {
                bestZone = zone;
                sumPrio = currentPrio;
              }
            }
          }
        }
        if (bestZone != null) {
          buyPod(bestZone, 1);
          alreadySet.add(bestZone);
          i++;
          continent.myUnits++;
        }
      }
    } while (i < max);
    endMove();
  }

  private static void firstRound(int myId, List<Continent> continents, Integer platinum) {
    waitCmd();
    int i = 0;
    int max = platinum / podCost;
    TreeSet<Zone> alreadySet = new TreeSet<>(zoneComparator);
    do {
      for (Continent continent : continents) {
        if (continent.size < 12 && continent.myUnits > 0 && continent.platinum < 12) {
          continue;
        }
        Zone bestZone = null;
        int sumPrio = 0;
        for (List<Zone> zones : continent.platinumRichZones.values()) {
          for (Zone zone : zones) {
            if (alreadySet.contains(zone)) {
              continue;
            }
            if (bestZone == null) {
              bestZone = zone;
              sumPrio = bestZone.platinumPriority;
              for (Zone link : zone.links) {
                sumPrio += link.platinumPriority;
              }
            } else {
              int currentPrio = zone.platinumPriority;
              for (Zone link : zone.links) {
                currentPrio += link.platinumPriority;
              }
              if (currentPrio > sumPrio || (currentPrio == sumPrio && zone.links.size() < bestZone.links.size())) {
                bestZone = zone;
                sumPrio = currentPrio;
              }
            }
          }
        }
        if (bestZone != null) {
          int amount = bestZone.platinum / 2 < 1 ? 1 : bestZone.platinum / 2;
          buyPod(bestZone, amount);
          alreadySet.add(bestZone);
          i += amount;
          continent.myUnits += amount;
        }
      }
    } while (i < max);
    endMove();
  }

  private static void addToMap(Map<Integer, List<Zone>> map, int priority, Zone id) {
    if (map.containsKey(priority)) {
      map.get(priority).add(id);
    } else {
      ArrayList<Zone> arrayList = new ArrayList<>();
      arrayList.add(id);
      map.put(priority, arrayList);
    }
  }

  private static Zone getZoneFor(Set<Zone> zones, Integer id) {
    Iterator<Zone> iterator = zones.iterator();
    while (iterator.hasNext()) {
      Zone next = iterator.next();
      if (next.id.equals(id)) {
        return next;
      }
    }
    return null;
  }

  private static void setSafeStates(int myId, List<Continent> continents) {
    Set<Zone> unsafeZones = new TreeSet<>(zoneComparator);
    Set<Zone> writtenZones = new TreeSet<>(zoneComparator);
    for (Continent continent : continents) {
      for (Zone zone : continent.zones) {
        zone.neededUnits = getHighestEnemyCount(myId, zone.pods);
        for (Zone linkedZone : zone.links) {
          if (linkedZone.owner != myId) {
            zone.safe = false;
            zone.safeVal = zone.safeVal != 0 && !containsEnemy(myId, linkedZone.pods) ? 1 : 0;
            if (containsEnemy(myId, linkedZone.pods)) {
              zone.neededUnits += getHighestEnemyCount(myId, linkedZone.pods);
            }
            unsafeZones.add(zone);
            writtenZones.add(zone);
            continue;
          }
        }
      }
    }

    Set<Zone> toWrite;

    do {
      toWrite = new TreeSet<>(zoneComparator);
      for (Zone unsafe : unsafeZones) {
        for (Zone linkedZone : unsafe.links) {
          if (linkedZone.owner == myId && !writtenZones.contains(linkedZone)) {
            linkedZone.safeVal = unsafe.safeVal + 1;
            linkedZone.safe = true;
            writtenZones.add(linkedZone);
            toWrite.add(linkedZone);
          }
        }
      }
      unsafeZones = toWrite;
    } while (!toWrite.isEmpty());
  }

  public static Zone getNextGoodZone(Integer myId, Continent continent) {
    Zone zoneToReturn = null;
    if (continent.freeZonesCount > 0 && continent.platinumRichZones.size() > continent.myUnits) { // && (continent.myZonesCount == 0 || continent.myUnits == 0)) {
      for (int i = maxPlat - 1; i > -1; i--) {
        if (!continent.platinumRichZones.containsKey(i)) {
          continue;
        }
        List<Zone> zones = continent.platinumRichZones.get(i);
        for (Zone zone : zones) {
          if (zone.owner == -1 && zone.pods[myId] < 1 && zone.platinum > 0) {
            if (zoneToReturn != null) {
              if (zone.platinumPriority <= zoneToReturn.platinumPriority && zone.platinum < zoneToReturn.platinum && zone.platinum != 0) {
                continue;
              }
            }
            zoneToReturn = zone;
          }
        }
      }
    }
    if (zoneToReturn != null) {
      zoneToReturn.owner = myId;
      zoneToReturn.pods[myId]++;
      continent.myZonesCount += 1;
      continent.myUnits += 1;
    }
    return zoneToReturn;
  }

  public static Integer getHighestEnemyCount(int myId, int pods[]) {
    Integer highestEnemyCount = 0;
    for (int i = 0; i < 4; i++) {
      if (i == myId) {
        continue;
      }
      if (highestEnemyCount < pods[i]) {
        highestEnemyCount = pods[i] - highestEnemyCount;
      }
    }
    return highestEnemyCount;
  }

  public static boolean containsEnemy(int myId, int pods[]) {
    for (int i = 0; i < 4; i++) {
      if (i == myId) {
        continue;
      }
      if (pods[i] > 0) {
        return true;
      }
    }
    return false;
  }

  private static void setPlatinumLinkInformation(List<Continent> continents) {
    long currentTimeMillis;
    System.err.println("Start setting platinum link infos");
    for (Continent continent : continents) {
      for (List<Zone> value : continent.platinumRichZones.values()) {
        for (Zone zone : value) {
          currentTimeMillis = System.currentTimeMillis();
          System.err.println("Set dist for zone " + zone);
          Set<Zone> writtenZones = new TreeSet<>(zoneComparator);
          Set<Zone> platinumNearZones = new TreeSet<>(zoneComparator);
          Set<Zone> toWrite;
          Integer distance = 1;
          zone.platinumPriority = zone.platinum + 1;
          if (zone.links.size() < 4) {
            zone.platinumPriority += (4 - zone.links.size());
          }
          for (Zone link : zone.links) {
            zone.platinumPriority += link.platinum;
            if (!link.platinumDistances.containsKey(1)) {
              ArrayList<Zone> arrayList = new ArrayList<Zone>();
              arrayList.add(zone);
              link.platinumDistances.put(distance, arrayList);
            } else {
              link.platinumDistances.get(distance).add(zone);
            }
          }
          platinumNearZones.addAll(zone.links);
          writtenZones.add(zone);
          writtenZones.addAll(zone.links);

          System.err.println("Start set paths now => t delta " + (System.currentTimeMillis() - currentTimeMillis));
          currentTimeMillis = System.currentTimeMillis();
          do {
            distance++;
            toWrite = new TreeSet<>(zoneComparator);
            for (Zone platNearZone : platinumNearZones) {
              for (Zone linkedZone : platNearZone.links) {
                if (writtenZones.contains(linkedZone)) {
                  continue;
                }
                if (!linkedZone.platinumDistances.containsKey(distance)) {
                  List<Zone> newZonesList = new ArrayList<>();
                  newZonesList.add(zone);
                  linkedZone.platinumDistances.put(distance, newZonesList);
                } else {
                  linkedZone.platinumDistances.get(distance).add(zone);
                }
                writtenZones.add(linkedZone);
                toWrite.add(linkedZone);
              }
              platinumNearZones = toWrite;
            }
          } while (!toWrite.isEmpty());
          System.err.println("Done set paths now => t delta " + (System.currentTimeMillis() - currentTimeMillis));
        }
      }
    }
  }

  private static int[] calulateEnemies(int[] pods) {
    int high = 0, sec = 0;
    for (int i = 0; i < 4; i++) {
      if (pods[i] > high) {
        sec = high;
        high = pods[i];
      } else if (sec < pods[i]) {
        sec = pods[i];
      }
    }
    if (sec == 0) {
      return pods;
    }
    for (int i = 0; i < 4; i++) {
      pods[i] -= sec;
    }
    return pods;
  }

  private static void buyPod(Zone zone, int amount) {
    zone.unitsNextRound += amount;
    System.out.print(amount + " " + zone.id + " ");
  }

  public static void endMove() {
    System.out.println();
  }

  public static void movePod(Zone fromZ, Zone toZ, Integer count) {
    fromZ.unitsNextRound -= count;
    toZ.unitsNextRound += count;
    System.out.print(count + " " + fromZ.id + " " + toZ.id + " ");
  }

  public static void waitCmd() {
    System.out.println("WAIT");
  }
}
