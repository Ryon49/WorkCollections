/** FCFSSchedulingAlgorithm.java
 * 
 * A first-come first-served scheduling algorithm.
 * The current implementation will work without memory management features
 *
 */
package com.jimweller.cpuscheduler;

import java.util.*;

public class FCFSSchedulingAlgorithm extends BaseSchedulingAlgorithm {
	enum MemAlloc {NONE, FIRST, BEST}

	private ArrayList<Process> jobs;

	private MemAlloc memAlloc;

	private boolean[] memory;
	private ArrayList<MemoryHole> holes;

	class FCFSComparator implements Comparator<Process> {
		public int compare(Process p1, Process p2) {
			if (p1.getArrivalTime() != p2.getArrivalTime()) {
				return Long.signum(p1.getArrivalTime() - p2.getArrivalTime());
			}
			return Long.signum(p1.getPID() - p2.getPID());
		}
	}

	private FCFSComparator comparator = new FCFSComparator();

	FCFSSchedulingAlgorithm() {
		activeJob = null;
		jobs = new ArrayList<Process>();

		// Initialize memory
		/*------------------------------------------------------------*/
		memory = new boolean[380];
		for (int i = 0; i < 380; i++) {
			memory[i] = false;
		}
		holes = new ArrayList<>();
		memAlloc = MemAlloc.NONE;
		/*------------------------------------------------------------*/

	}


	/** Add the new job to the correct queue. */
	public void addJob(Process p) {
	// Check if any memory is available
	/*------------------------------------------------------------*/
	/*------------------------------------------------------------*/

    // If enough memory is not available then don't add it to queue
        if (memAlloc != MemAlloc.NONE) {
            int fit = findFit(p.getMemSize());
            if (fit >= 0) {
                for (int i = 0; i < p.getMemSize(); i++) {
                    memory[fit + i] = true;
                }
                holes.add(new MemoryHole(p, fit));
            } else {
                p.setIgnore(true);
                return;
            }
        }

		jobs.add(p);
		Collections.sort(jobs, comparator);
	}

	/** Returns true if the job was present and was removed. */
	public boolean removeJob(Process p) {
		if (p == activeJob)
			activeJob = null;

		// In case memory was allocated, free it
		/*------------------------------------------------------------*/
        for (MemoryHole hole : holes) {
            if (hole.process == p) {
                for (int i = 0; i < p.getMemSize(); i++) {
                    memory[hole.start + i] = false;
                }
				holes.remove(hole);
                break;
            }
        }
		/*------------------------------------------------------------*/

		return jobs.remove(p);
	}

	/**
	 * Transfer all the jobs in the queue of a SchedulingAlgorithm to another, such
	 * as when switching to another algorithm in the GUI
	 */
	public void transferJobsTo(SchedulingAlgorithm otherAlg) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the next process that should be run by the CPU, null if none
	 * available.
	 */
	public Process getNextJob(long currentTime) {
		Process earliest = null;

		if (!isJobFinished())
			return activeJob;
		if (jobs.size() > 0)
			earliest = jobs.get(0);
		activeJob = earliest;
		return activeJob;
	}

	public String getName() {
		return "First-Come First-Served";
	}

	public void setMemoryManagement(String v) {
		// Modify class to support memory management
		if (v.compareTo("FIRST") == 0) {
			memAlloc = MemAlloc.FIRST;
		} else if (v.compareTo("BEST") == 0) {
			memAlloc = MemAlloc.BEST;
		}
	}


    public int findFit(long size) {
        int bestAt = -1;
        int bestLen = 0;
        int at = -1;
        int availableLen = 0;

        boolean start = false;

        for (int i = 0; i < 380; i++) {
            if (!memory[i]) {
                if (start) {
                    availableLen++;
                } else {
                    start = true;
                    at = i;
                    availableLen = 1;
                }
            } else {
                start = false;
                if (memAlloc == MemAlloc.FIRST) {
                    if (availableLen >= size) {
                        bestAt = at;
                        break;
                    }
                } else if (memAlloc == MemAlloc.BEST) {
                    if (availableLen >= size) {
						if (bestLen == 0 || availableLen < bestLen) {
                            bestAt = at;
                            bestLen = availableLen;
                        }
                    }
                }
            }
        }

        if (availableLen >= size) {
			if (memAlloc == MemAlloc.FIRST) {
				bestAt = at;
			} else if (memAlloc == MemAlloc.BEST) {
				if (bestLen == 0 || availableLen < bestLen) {
					bestAt = at;
				}
			}
		}

        return bestAt;
    }


	private class MemoryHole {
		Process process;
		int start;

        public MemoryHole(Process process, int start) {
            this.process = process;
            this.start = start;
        }
    }
}