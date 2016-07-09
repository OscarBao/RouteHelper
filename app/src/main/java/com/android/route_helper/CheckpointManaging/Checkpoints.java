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
    public void clear() {checkpointsList = new ArrayList<>();}
    public void add(Checkpoint cp) {checkpointsList.add(cp);}
    public void remove(Checkpoint cp) {checkpointsList.remove(cp);}
    public void moveToNext() {currentCheckpointIndex++;}
    public Checkpoint currentCheckpoint() {return checkpointsList.get(currentCheckpointIndex);}


    /*
        Private methods
     */
}
