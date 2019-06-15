/** PrioritySchedulingAlgorithm.java
 * 
 * A single-queue priority scheduling algorithm.
 *
 * @author: Charles Zhu
 * Spring 2016
 *
 */
package com.jimweller.cpuscheduler;

import java.util.*;

import com.jimweller.cpuscheduler.Process;

public class PrioritySchedulingAlgorithm extends BaseSchedulingAlgorithm implements OptionallyPreemptiveSchedulingAlgorithm {

    private ArrayList<Process> jobs;

    private boolean preemptive;

    private PriorityComparator comparator = new PriorityComparator();

    class PriorityComparator implements Comparator<Process> {
        public int compare(Process p1, Process p2) {
            if (p1.getPriorityWeight() != p2.getPriorityWeight()) {
                return Long.signum(p1.getPriorityWeight() - p2.getPriorityWeight());
            }
            return Long.signum(p1.getPID() - p2.getPID());
        }
    }

    PrioritySchedulingAlgorithm(){
        activeJob = null;
        jobs = new ArrayList<Process>();

        preemptive = false;
    }

    /** Add the new job to the correct queue.*/
    public void addJob(Process p){
        jobs.add(p);
        Collections.sort(jobs, comparator);
    }
    
    /** Returns true if the job was present and was removed. */
    public boolean removeJob(Process p){
        if (p == activeJob)
            activeJob = null;

        return jobs.remove(p);
    }

    /** Transfer all the jobs in the queue of a SchedulingAlgorithm to another, such as
    when switching to another algorithm in the GUI */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
        throw new UnsupportedOperationException();
    }


    /** Returns the next process that should be run by the CPU, null if none available.*/
    public Process getNextJob(long currentTime){
        Process highest = null;

        if (!isJobFinished() && !preemptive)
            return activeJob;
        if (jobs.size() > 0)
            highest = jobs.get(0);
        activeJob = highest;
        return activeJob;
    }

    public String getName(){
        return "Single-Queue Priority";
    }

    /**
     * @return Value of preemptive.
     */
    public boolean isPreemptive(){
        return preemptive;
    }
    
    /**
     * @param v Value to assign to preemptive.
     */
    public void setPreemptive(boolean v){
        preemptive = v;
    }
    
}