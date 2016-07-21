package com.android.route_helper.CheckpointManaging;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by obao_Lenovo on 7/8/2016.
 */
public class Checkpoints implements Iterable<Checkpoint> {
    /*
        Variables
     */
    static private ArrayList<Checkpoint> checkpointsList;
    static int currentCheckpointIndex;
    /*
        Public methods
     */
    public Checkpoints() {
        checkpointsList = new ArrayList<>();
        currentCheckpointIndex = 0;
    }

    public Iterator<Checkpoint> iterator() {return checkpointsList.iterator();}
    public static void clear() {checkpointsList = new ArrayList<>();}
    public static void add(Checkpoint cp) {checkpointsList.add(cp);}
    public static void remove(Checkpoint cp) {checkpointsList.remove(cp);}
    public static void moveToNext() {currentCheckpointIndex++;}
    public static Checkpoint currentCheckpoint() {return checkpointsList.get(currentCheckpointIndex);}
    public static boolean atEnd() {return currentCheckpointIndex >= checkpointsList.size();}

    /*
        Private methods
     */
}
