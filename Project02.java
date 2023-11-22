import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Project02 {
    static String structures;
    static int floors;
    static double passengers;
    static int elevators;
    static int elevatorCap;
    static int duration;
    static List<Elevator> ele;
    static List<Floor> fl;
    public static void main(String[] args) throws FileNotFoundException, IOException {
        FileReader r = new FileReader("default.properties");
        Properties p = new Properties();
        String [] args2 = {""};
        if (!args2[0].equals("")) {
            r = new FileReader(args2[0]);
            p.load(r);
            structures = p.getProperty("structures");
            floors = Integer.parseInt(p.getProperty("floors"));
            passengers = Double.parseDouble(p.getProperty("passengers"));
            elevators = Integer.parseInt(p.getProperty("elevators"));
            elevatorCap = Integer.parseInt(p.getProperty("elevatorCapacity"));
            duration = Integer.parseInt(p.getProperty("duration"));
        }
        else {
            p.load(r);
            structures = p.getProperty("structures");
            floors = Integer.parseInt(p.getProperty("floors"));
            passengers = Double.parseDouble(p.getProperty("passengers"));
            elevators = Integer.parseInt(p.getProperty("elevators"));
            elevatorCap = Integer.parseInt(p.getProperty("elevatorCapacity"));
            duration = Integer.parseInt(p.getProperty("duration"));
        }
        if(structures.equals("linked")) {
            ele = new LinkedList<>();
            fl = new LinkedList<>();
        }
        else if (structures.equals("array")) {
            ele = new ArrayList<>();
            fl = new ArrayList<>();
        }
        for(int i = 0; i < elevators; i++) {
            ele.add(i, new Elevator(passengers, floors, duration, structures, elevatorCap));
        }
        for(int j = 0; j < floors; j++) {
            fl.add(j, new Floor(structures, j + 1));
        }
        int ave = 0;
        int min = 100000;
        int max = -1;
        int nums = 0;
        if(ele.size() == 1) {
            simulate(ele.get(0), fl);
            System.out.println("Ave: " + ave(ele.get(0)));
            System.out.println("Min: " + min(ele.get(0)));
            System.out.println("Max: " + max(ele.get(0)));
        }
        else {
            for(int i = 0; i < ele.size(); i++) {
                simulate(ele.get(i), fl);
                ave += ave(ele.get(i));
                nums += ele.get(i).passengerList.size();
                if (min > min(ele.get(i))) {
                    min = min(ele.get(i));
                }
                if (max < max(ele.get(i))) {
                    max = max(ele.get(i));
                }
            }
            ave /= nums;
            System.out.println("Ave: " + ave);
            System.out.println("Min: " + min);
            System.out.println("Max: " + max);
        }


    }
    static int ave(Elevator e) {
        int a = 0;
        for(int i = 0; i < e.allPassengers.size(); i++) {
            a += e.allPassengers.get(i).ticks;
        }
        return a/e.allPassengers.size();
    }

    static int min(Elevator e) {
        int a = e.allPassengers.get(0).ticks;
        for(int i = 1; i < e.allPassengers.size(); i++) {
            if (e.allPassengers.get(i).ticks < a) {
                a = e.allPassengers.get(i).ticks;
            }
        }
        return a;
    }

    static int max(Elevator e) {
        int a = e.allPassengers.get(0).ticks;
        for(int i = 1; i < e.allPassengers.size(); i++) {
            if (e.allPassengers.get(i).ticks > a) {
                a = e.allPassengers.get(i).ticks;
            }
        }
        return a;
    }

    static void simulate(Elevator e, List<Floor> f) {
        Random rand = new Random();
        int addition = -1;
        int currTicks = 0;
        int currFloor = 0;
        boolean goUp = true;
        int countDistance = 0;
        while(currTicks <= duration) {
            double rDub = rand.nextDouble(1);
            if (rDub < passengers * f.size() && currFloor < f.size()) {
                Passenger p = new Passenger(currFloor, f.size());
                f.get(currFloor).addToList(p);
                System.out.println("Passenger added: " + p.floorFrom + " " + p.floorOff);
                if(p.goingUp == goUp && e.currCap < e.cap) {
                    e.addPassenger(p);
                    p.start = currTicks;
                    f.get(currFloor).removeFromList(p);
                    currTicks++;
                }
            }
            if(e.passengerList.size() > 0) {
                for (int i = 0; i < e.passengerList.size(); i++) {
                    if (e.passengerList.get(i).floorOff == currFloor) {
                        System.out.println("Passenger removed: " + e.passengerList.get(i).floorFrom + " " + e.passengerList.get(i).floorOff);
                        e.passengerList.get(i).end = currTicks;
                        e.passengerList.get(i).setTicks();
                        e.allPassengers.add(e.passengerList.get(i));
                        e.takeOutPassenger(e.passengerList.get(i));
                        currTicks++;
                    }
                }
                for (int i = 0; i < f.size(); i++) {
                    for (int j = 0; j < f.get(i).passengerList.size(); j++) {
                        if (f.get(i).passengerList.get(j).floorFrom == currFloor) {
                            if (e.currCap < e.cap) {
                                f.get(i).passengerList.get(j).start = currTicks;
                                e.addPassenger(f.get(i).passengerList.get(j));
                                f.get(i).passengerList.remove(f.get(i).passengerList.get(j));
                                currTicks++;
                            }
                        }
                    }
                }
                if (currFloor == f.size()) {
                    goUp = false;
                    addition = -1;
                } else if (currFloor == 0) {
                    goUp = true;
                    addition = 1;
                }
                currFloor += addition;
                System.out.println("current floor " + currFloor);
                countDistance++;
                if (countDistance == 5) {
                    currTicks++;
                    countDistance = 0;
                }
            }
        }
    }
}

class Elevator {
    double passengers;
    int floors;
    int ticks;
    int aveTicks;
    int longestTicks;
    int shortestTicks;
    String struct;
    int cap;
    int currCap;
    boolean up = true;
    List<Passenger> passengerList;
    List<Passenger> allPassengers;

    int startFloor = 1;
    int endFloor;

    public Elevator(double p, int f, int t, String s, int e) {
        passengers = p;
        floors = f;
        ticks = t;
        endFloor = f;
        cap = e;
        if(s.equals("linked")) {
            this.passengerList = new LinkedList<>();
            this.allPassengers = new LinkedList<>();
        }
        else if (s.equals("array")) {
            this.passengerList = new ArrayList<>();
            this.allPassengers = new LinkedList<>();
        }
    }

    void takeOutPassenger(Passenger p) {
        this.passengerList.remove(p);
        currCap--;
    }

    void addPassenger(Passenger p) {
        this.passengerList.add(p);
        currCap++;
    }

    void simulate() {
        int numTicks = 0;
        while(numTicks <= ticks) {

        }
    }

    boolean moveUp(int floor) {
        if(floor == floors) {
            return false;
        }
        return true;
    }
}

class Floor {
    List<Passenger> passengerList;
    int level;

    public Floor(String s, int l) {
        level = l;
        if(s.equals("linked")) {
            this.passengerList = new LinkedList<>();
        }
        else if (s.equals("array")) {
            this.passengerList = new ArrayList<>();
        }
    }

    void removeFromList(Passenger p) {
        this.passengerList.remove(p);
    }
    void addToList(Passenger p) {
        this.passengerList.add(p);
    }
}

class Passenger {
    int floorFrom;
    int floorOff;
    int ticks;
    boolean goingUp;
    int start;
    int end;

    public Passenger(int ff, int totFloors) {
        floorFrom = ff;
        Random rand = new Random();
        floorOff = rand.nextInt(totFloors) + 1;
        while(floorOff == floorFrom) {
            floorOff = rand.nextInt(totFloors) + 1;
        }
        if(floorOff < floorFrom) {
            goingUp = false;
        }
        else {
            goingUp = true;
        }
    }

    void setTicks() {
        ticks = end - start;
    }
}
