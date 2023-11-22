/*
 * By Arya Srivastava, Project02.java is a simulation of an elevator and adjusts the simulation according to the property file
 * given through args. If no property file is provided, it will initialize needed values with a default property file
 * which is full of default values. 
 */
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Project02 {
    /*
     * Sets the property variables given by the file
     */
    static String structures;
    static int floors;
    static double passengers;
    static int elevators;
    static int elevatorCap;
    static int duration;
    static List<Elevator> ele; // I chose to use Lists because depending on what the property file says, I could make it either an array or linked list
    static List<Floor> fl;

    /*
     * this is where my program reads from the given/default property file,
     * initializes the list of elevator(s) and floors to be either an arraylist/linked list,
     * runs the simulation, and calculates the ave, longest, and shortest trips of all passengers
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        FileReader r = new FileReader("default.properties");
        Properties p = new Properties();
        //String [] args2 = {""}; // I used this to test my outputs
        if (!args[0].equals("")) { // gets input from String [] args
            r = new FileReader(args[0]); // use a filereader to read the prop file, I used https://www.geeksforgeeks.org/java-util-properties-class-java/
            p.load(r); // load the filereader to property object and get the vals for every variable
            structures = p.getProperty("structures");
            floors = Integer.parseInt(p.getProperty("floors"));
            passengers = Double.parseDouble(p.getProperty("passengers"));
            elevators = Integer.parseInt(p.getProperty("elevators"));
            elevatorCap = Integer.parseInt(p.getProperty("elevatorCapacity"));
            duration = Integer.parseInt(p.getProperty("duration"));
        }
        else {
            p.load(r); // if there is no given property file, read from the default one and fill out all the variables
            structures = p.getProperty("structures");
            floors = Integer.parseInt(p.getProperty("floors"));
            passengers = Double.parseDouble(p.getProperty("passengers"));
            elevators = Integer.parseInt(p.getProperty("elevators"));
            elevatorCap = Integer.parseInt(p.getProperty("elevatorCapacity"));
            duration = Integer.parseInt(p.getProperty("duration"));
        }
        if(structures.equals("linked")) { // if the structures is required to be linked, create linked lists
            ele = new LinkedList<>(); // elevator (if there is one or more than one)
            fl = new LinkedList<>(); // floors (there should be more than 2)
        }
        else if (structures.equals("array")) { // else, create array lists
            ele = new ArrayList<>();
            fl = new ArrayList<>();
        }
        for(int i = 0; i < elevators; i++) { // initialize the elevator(s)
            ele.add(i, new Elevator(passengers, floors, duration, structures, elevatorCap));
        }
        for(int j = 0; j < floors; j++) { // initialize the floors
            fl.add(j, new Floor(structures, j + 1));
        }
        // placeholders for the outputs for the times, specifically in the case of multiple elevators
        int ave = 0;
        int min = 100000;
        int max = -1;
        int nums = 0;
        if(ele.size() == 1) { // if there is one elevator just call the functions and feed in info from the passengers that took the elevator
            simulate(ele.get(0), fl); // simulates the elevator
            System.out.println("Average length of time: " + ave(ele.get(0)));
            System.out.println("Longest length of time: " + max(ele.get(0)));
            System.out.println("Shortest length of time: " + min(ele.get(0)));
        }
        else {
            for(int i = 0; i < ele.size(); i++) { // else if there are multiple
                simulate(ele.get(i), fl); // simulate every elevator
                ave += ave(ele.get(i)); // add on the ave from every simulation
                nums += ele.get(i).passengerList.size(); // count up the total num of passengers
                if (min > min(ele.get(i))) { // then calculate the min and max times
                    min = min(ele.get(i));
                }
                if (max < max(ele.get(i))) {
                    max = max(ele.get(i));
                }
            }
            ave /= nums; // get average for mult elevators and print out
            System.out.println("Average length of time: " + ave);
            System.out.println("Longest length of time: " + max);
            System.out.println("Shortest length of time: " + min);
        }


    }
    static int ave(Elevator e) { // average function
        int a = 0;
        for(int i = 0; i < e.allPassengers.size(); i++) {
            a += e.allPassengers.get(i).ticks;
        }
        return a/e.allPassengers.size();
    }

    static int min(Elevator e) { // shortest time function
        int a = e.allPassengers.get(0).ticks;
        for(int i = 1; i < e.allPassengers.size(); i++) {
            if (e.allPassengers.get(i).ticks < a) {
                a = e.allPassengers.get(i).ticks;
            }
        }
        return a;
    }

    static int max(Elevator e) { // longest time function
        int a = e.allPassengers.get(0).ticks;
        for(int i = 1; i < e.allPassengers.size(); i++) {
            if (e.allPassengers.get(i).ticks > a) {
                a = e.allPassengers.get(i).ticks;
            }
        }
        return a;
    }

    /*
     * The simulate function is a void function that takes in an elevator and a list of floors
     * They will both hold lists of passengers, the elevator will hold the list of passengers in the elevator and
     * each floor will hold a list of passengers waiting to get on to the elevator. It will just go about going up to the
     * final floor and then down to the first floor, taking in and replacing passengers from every floor if there is someone
     * present. If the elevator capacity is full or going in the wrong direction, the passengers will wait in a list in a floor
     * object.
     */
    static void simulate(Elevator e, List<Floor> f) {
        Random rand = new Random(); // random object to calc if a passenger is spawned or not
        int addition = -1; // helps inc and dec floors
        int currTicks = 0; // holds current ticks
        int currFloor = 0; // starts from first floor
        boolean goUp = true; // start by going up
        int countDistance = 0; // meant to count every 5 floors to count a tick
        while(currTicks <= duration) { // while loop that goes until the duration is reached
            double rDub = rand.nextDouble(1); // find the prob of a passenger
            if (rDub < passengers * f.size() && currFloor < f.size()) { // if a passenger has spawned and we aren't on the last floor
                Passenger p = new Passenger(currFloor, f.size()); // go ahead and create a new passenger
                f.get(currFloor).addToList(p); // and add it to the floor's list
                // System.out.println("Passenger added: " + p.floorFrom + " " + p.floorOff); // <= was using this to check my work
                if(p.goingUp == goUp && e.currCap < e.cap) { // if the passenger is going the same way as the elevator and the elevator isn't full, take them on
                    e.addPassenger(p);
                    p.start = currTicks;
                    f.get(currFloor).removeFromList(p); // remove from floor list and increment ticks
                    currTicks++;
                }
            }
            if(e.passengerList.size() > 0) { // in case there are no passengers
                for (int i = 0; i < e.passengerList.size(); i++) { // go through and remove the passengers who have arrived at their stop
                    if (e.passengerList.get(i).floorOff == currFloor && e.passengerList.get(i).goingUp == goUp) {
                        // System.out.println("Passenger removed: " + e.passengerList.get(i).floorFrom + " " + e.passengerList.get(i).floorOff); // <= using this to check work
                        e.passengerList.get(i).end = currTicks; // set the currticks as the end of the duration for the passenger
                        e.passengerList.get(i).setTicks(); // find the time it took for them to reach their dest
                        e.allPassengers.add(e.passengerList.get(i)); // add it to the elevator's master list of passengers
                        e.takeOutPassenger(e.passengerList.get(i)); // vanish the passenger
                        currTicks++; // inc ticks
                    }
                }
                for (int i = 0; i < f.size(); i++) {
                    for (int j = 0; j < f.get(i).passengerList.size(); j++) {
                        if (f.get(i).passengerList.get(j).floorFrom == currFloor) { // if we're on a floor with ppl waiting to get on
                            if (e.currCap < e.cap) { // and we have space
                                f.get(i).passengerList.get(j).start = currTicks; // keep track of when they got on and add to the elevator
                                e.addPassenger(f.get(i).passengerList.get(j));
                                f.get(i).passengerList.remove(f.get(i).passengerList.get(j)); // remove from floor list
                                currTicks++; // inc ticks
                            }
                        }
                    }
                }
                if (currFloor == f.size()) { // if we reach the top reverse the movement of the elevator
                    goUp = false;
                    addition = -1;
                } else if (currFloor == 0) { // else if we reach the bottom reverse the movement of the elevator
                    goUp = true;
                    addition = 1;
                }
                currFloor += addition; // either inc or dec the floor number by 1 depending on the direction we're going in
                // System.out.println("current floor " + currFloor); // <= using this to check work
                countDistance++; // increment the dist between the floors
                if (countDistance == 5) { // if it has reached 5
                    currTicks++; // inc ticks by 1 and resent count dist
                    countDistance = 0;
                }
            }
        }
    }
}

/*
 * the elevator class is meant to hold the passengers, ticks, capacity, structure type, etc in it
 * It also can take out or add passengers while keeping track of the current capacity
 * It also initializes some class vars in the constructor
 *
 * It also keeps track of every passenger to enter the elevator, which will help in calculating the ave, longest, and shortest
 * times it took for passengers to reach their dest
 */
class Elevator {
    double passengers;
    int floors;
    int ticks;
    int cap;
    int currCap;
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
}

/*
 * class Floor helps keep track of what passengers are waiting in the hall to go in the direction that the elevator is going in
 * It also keeps track of the level of floor we're on. We can remove and add people to the passenger list for floor
 */
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

/*
 * the passenger class keeps track of important info per passenger summoned
 * For example, which floor they came from, where they're going, how many ticks it took for them to reach the desired
 * floor, whether they're going up or down, etc
 *
 * It chooses which floor to get off at using a random object, and also sets the number of ticks it took to reach their dest
 * by keeping track of when they got on and off
 */
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
