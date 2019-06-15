/** RoundRobinSchedulingAlgorithm.java
 *
 * A scheduling algorithm that randomly picks the next job to go.
 *
 * @author: Kyle Benson
 * Winter 2013
 *
 */
package com.jimweller.cpuscheduler;

import java.util.*;

public class RoundRobinSchedulingAlgorithm extends BaseSchedulingAlgorithm {

    private ArrayList<JobContainer> jobs;

    private int at;
    private int activeTime;

    /** the time slice each process gets */
    private int quantum;

    private PidComparator comparator = new PidComparator();

    class PidComparator implements Comparator<JobContainer> {
        public int compare(JobContainer p1, JobContainer p2) {
            return Long.signum(p1.getPID() - p2.getPID());
        }
    }

    RoundRobinSchedulingAlgorithm() {
        jobs = new ArrayList<JobContainer>();
        at = 0;
        activeTime = 0;

        quantum = 10;   // default time quantum.
    }

    /** Add the new job to the correct queue. */
    public void addJob(Process p) {
        jobs.add(new JobContainer(p));
        Collections.sort(jobs, comparator);
    }

    /** Returns true if the job was present and was removed. */
    public boolean removeJob(Process p) {
        for (JobContainer jc : jobs) {
            if (jc.process == p) {
                jc.process = null;
                return true;
            }
        }
        return false;
    }

    /** Transfer all the jobs in the queue of a SchedulingAlgorithm to another, such as
    when switching to another algorithm in the GUI */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the value of quantum.
     *
     * @return Value of quantum.
     */
    public int getQuantum() {
        return quantum;
    }

    /**
     * Set the value of quantum.
     *
     * @param v
     *            Value to assign to quantum.
     */
    public void setQuantum(int v) {
        this.quantum = v;
    }

    /**
     * Returns the next process that should be run by the CPU, null if none
     * available.
     */
    public Process getNextJob(long currentTime) {
        if (!hasNewJob()) {
            activeJob = null;
            return null;
        }

        if ((activeJob != null && isJobFinished()) || activeTime == quantum) {
            do {
                at = (at + 1) % jobs.size();
            } while (jobs.get(at).process == null);
            activeTime = 0;
        }
        activeTime++;
        activeJob = jobs.get(at).process;
        return activeJob;
    }

    private boolean hasNewJob() {
        for (JobContainer jc : jobs) {
            if (jc.process != null) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return "Round Robin";
    }

    class JobContainer {
        long PID;
        Process process;

        public JobContainer(Process process) {
            this.PID = process.getPID();
            this.process = process;
        }

        public long getPID() {
            return PID;
        }

        public Process getProcess() {
            return process;
        }
    }
}