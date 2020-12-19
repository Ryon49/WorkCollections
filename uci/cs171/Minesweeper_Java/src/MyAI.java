

/*

AUTHOR:      John Lu

DESCRIPTION: This file contains your agent class, which you will
             implement.

NOTES:       - If you are having trouble understanding how the shell
               works, look at the other parts of the code, as well as
               the documentation.

             - You are only allowed to make changes to this portion of
               the code. Any changes to other portions of the code will
               be lost when the tournament runs your code.
*/

package src;

import src.Action.ACTION;

import javax.swing.plaf.nimbus.State;
import java.lang.reflect.Array;
import java.util.*;

public class MyAI extends AI {
    enum STATE {
        BOMB, UNCOVERED, COVERED
    }
    // ########################## INSTRUCTIONS ##########################
    // 1) The Minesweeper Shell will pass in the board size, number of mines
    // 	  and first move coordinates to your agent. Create any instance variables
    //    necessary to store these variables.
    //
    // 2) You MUST implement the getAction() method which has a single parameter,
    // 	  number. If your most recent move is an Action.UNCOVER action, this value will
    //	  be the number of the tile just uncovered. If your most recent move is
    //    not Action.UNCOVER, then the value will be -1.
    //
    // 3) Feel free to implement any helper functions.
    //
    // ###################### END OF INSTURCTIONS #######################

    // This line is to remove compiler warnings related to using Java generics
    // if you decide to do so in your implementation.
    @SuppressWarnings("unchecked")

    // immutable values
    private int rowDimension;
    private int colDimension;

    private int totalTiles;
    private int totalMines;

    private int nextX;
    private int nextY;

    private Tile[][] board;

    private LinkedList<Action> readyQueue;

    private HashMap<Integer, HashSet<Location>> choices;

    // Use ArrayList to simulate a Action queue

    public MyAI(int rowDimension, int colDimension, int totalMines, int startX, int startY) {
        // ################### Implement Constructor (required) ####################
        this.colDimension = rowDimension;
        this.rowDimension = colDimension;

        this.totalTiles = this.rowDimension * this.colDimension - 1;
        this.totalMines = totalMines;

        this.nextX = startX;
        this.nextY = startY;

        this.board = new Tile[this.rowDimension + 1][this.colDimension + 1];
        for (int x = 1; x <= this.rowDimension; x++) {
            for (int y = 1; y <= this.colDimension; y++) {
                board[x][y] = new Tile();
            }

        }
        // 0 was given
        changeState(startX, startY, STATE.UNCOVERED);

        choices = new HashMap<>();
        choices.put(0, new HashSet<Location>());
        choices.put(1, new HashSet<Location>());
        choices.put(2, new HashSet<Location>());
        choices.put(3, new HashSet<Location>());
        choices.put(4, new HashSet<Location>());
        choices.put(5, new HashSet<Location>());
        choices.put(6, new HashSet<Location>());
        choices.put(7, new HashSet<Location>());
        choices.put(8, new HashSet<Location>());

        choices.get(0).add(new Location(startX, startY));
        readyQueue = new LinkedList<>();
    }

    // ################## Implement getAction(), (required) #####################
    public Action getAction(int number) {
        changeNumber(this.nextX, this.nextY, number);
        if (number >= 0) {
            this.choices.get(number).add(new Location(this.nextX, this.nextY));
        }

        if (readyQueue.size() > 0) {
            Action action = readyQueue.removeFirst();
            if (action.action == ACTION.FLAG) {
                totalMines--;
            }
            totalTiles--;
            this.nextX = action.x;
            this.nextY = action.y;
            return action;
        }

        // a temporary place to store the pos that has no more action.
        HashSet<Location> recycleQueue = new HashSet<>();

        // region rule 1: baseline

        // handle every 0's
        if (choices.get(0).size() > 0) {
            for (Location pos : choices.get(0)) {
                handle0(pos.x, pos.y, 0);
                recycleQueue.add(pos);
            }
            choices.get(0).removeAll(recycleQueue);
            recycleQueue.clear();
        }

        // handle every 8's
        if (choices.get(8).size() > 0) {
            for (Location pos : choices.get(8)) {
                handle0(pos.x, pos.y, 8);
                recycleQueue.add(pos);
            }
            choices.get(8).removeAll(recycleQueue);
            recycleQueue.clear();
        }

        if (readyQueue.size() > 0) {
            Action action = readyQueue.removeFirst();
            if (action.action == ACTION.FLAG) {
                totalMines--;
            }
            totalTiles--;
            this.nextX = action.x;
            this.nextY = action.y;
            return action;
        }

        // handle every 1's
        if (choices.get(1).size() > 0) {
            for (Location pos : choices.get(1)) {
                boolean recycle = handle1(pos.x, pos.y, 1);
                if (recycle) {
                    recycleQueue.add(pos);
                }
            }
            choices.get(1).removeAll(recycleQueue);
            recycleQueue.clear();
        }

        // handle every 2's
        if (choices.get(2).size() > 0) {
            for (Location pos : choices.get(2)) {
                boolean recycle = handle1(pos.x, pos.y, 2);
                if (recycle) {
                    recycleQueue.add(pos);
                }
            }
            choices.get(2).removeAll(recycleQueue);
            recycleQueue.clear();
        }

        if (readyQueue.size() > 0) {
            Action action = readyQueue.removeFirst();
            if (action.action == ACTION.FLAG) {
                totalMines--;
            }
            totalTiles--;
            this.nextX = action.x;
            this.nextY = action.y;
            return action;
        }

        // handle every 3's
        if (choices.get(3).size() > 0) {
            for (Location pos : choices.get(3)) {
                boolean recycle = handle1(pos.x, pos.y, 3);
                if (recycle) {
                    recycleQueue.add(pos);
                }
            }
            choices.get(3).removeAll(recycleQueue);
            recycleQueue.clear();
        }

        // handle every 4's
        if (choices.get(4).size() > 0) {
            for (Location pos : choices.get(4)) {
                boolean recycle = handle1(pos.x, pos.y, 4);
                if (recycle) {
                    recycleQueue.add(pos);
                }
            }
            choices.get(4).removeAll(recycleQueue);
            recycleQueue.clear();
        }

        if (readyQueue.size() > 0) {
            Action action = readyQueue.removeFirst();
            if (action.action == ACTION.FLAG) {
                totalMines--;
            }
            totalTiles--;
            this.nextX = action.x;
            this.nextY = action.y;
            return action;
        }

        // handle every 5's
        if (choices.get(5).size() > 0) {
            for (Location pos : choices.get(5)) {
                boolean recycle = handle1(pos.x, pos.y, 5);
                if (recycle) {
                    recycleQueue.add(pos);
                }
            }
            choices.get(5).removeAll(recycleQueue);
            recycleQueue.clear();
        }

        // handle every 6's
        if (choices.get(6).size() > 0) {
            for (Location pos : choices.get(6)) {
                boolean recycle = handle1(pos.x, pos.y, 6);
                if (recycle) {
                    recycleQueue.add(pos);
                }
            }
            choices.get(6).removeAll(recycleQueue);
            recycleQueue.clear();
        }

        // handle every 7's
        if (choices.get(7).size() > 0) {
            for (Location pos : choices.get(7)) {
                boolean recycle = handle1(pos.x, pos.y, 7);
                if (recycle) {
                    recycleQueue.add(pos);
                }
            }
            choices.get(7).removeAll(recycleQueue);
            recycleQueue.clear();
        }

        if (readyQueue.size() > 0) {
            Action action = readyQueue.removeFirst();
            if (action.action == ACTION.FLAG) {
                totalMines--;
            }
            totalTiles--;
            this.nextX = action.x;
            this.nextY = action.y;
            return action;
        }

        // endregion: : :

        // region rule 2.1: constraint check (pair of 2)

        for (int n = 8; n >= 1; n--) {
            if (readyQueue.size() > 0) {
                break;
            }
            for (Location pos : choices.get(n)) {
                if (readyQueue.size() > 0) {
                    break;
                }
                for (Location op_pos : getUncoveredTiles(pos.x, pos.y, 2)) {
                    if (readyQueue.size() > 0) {
                        break;
                    }
                    int op_bombLeft = getNumber(op_pos.x, op_pos.y) - getBombTiles(op_pos.x, op_pos.y).size();
                    if (op_bombLeft == 1) {
                        LinkedList<Location> coveredTiles = getCoveredTiles(pos.x, pos.y);

                        LinkedList<Location> op_coveredTiles = getCoveredTiles(op_pos.x, op_pos.y);

                        if (isSubset(coveredTiles, op_coveredTiles)) {
                            op_coveredTiles.removeAll(coveredTiles);
                            for (Location safe : op_coveredTiles) {
                                changeState(safe.x, safe.y, STATE.UNCOVERED);
                                readyQueue.add(new Action(ACTION.UNCOVER, safe.x, safe.y));
                            }
                        }
//                        else if (isSubset(op_coveredTiles, coveredTiles)) {
//                            coveredTiles.removeAll(op_coveredTiles);
//                            if ((n - getBombTiles(pos.x, pos.y).size()) - op_bombLeft == coveredTiles.size()) {
//                                for (Location bomb : coveredTiles) {
//                                    changeState(bomb.x, bomb.y, STATE.BOMB);
//                                    readyQueue.add(new Action(ACTION.FLAG, bomb.x, bomb.y));
//                                }
//                            }
//                        }
                    }
                }
            }
        }

        if (readyQueue.size() > 0) {
            Action action = readyQueue.removeFirst();
            if (action.action == ACTION.FLAG) {
                totalMines--;
            }
            totalTiles--;
            this.nextX = action.x;
            this.nextY = action.y;
            return action;
        }

        for (int n = 8; n >= 1; n--) {
            if (readyQueue.size() > 0) {
                break;
            }
            for (Location pos : choices.get(n)) {
                if (readyQueue.size() > 0) {
                    break;
                }
                for (Location op_pos : getUncoveredTiles(pos.x, pos.y)) {
                    if (readyQueue.size() > 0) {
                        break;
                    }
                    int bombLeft = getNumber(op_pos.x, op_pos.y) - getBombTiles(op_pos.x, op_pos.y).size();
                    LinkedList<Location> op_coveredTiles = getCoveredTiles(op_pos.x, op_pos.y);
                    if (isSubset(op_coveredTiles, getCoveredTiles(pos.x, pos.y))) {
                        LinkedList<Location> coveredTiles = getCoveredTiles(pos.x, pos.y);
                        coveredTiles.removeAll(op_coveredTiles);

                        if (coveredTiles.size() == n - getBombTiles(pos.x, pos.y).size() - bombLeft) {
                            for (Location bomb : coveredTiles) {
                                changeState(bomb.x, bomb.y, STATE.BOMB);
                                readyQueue.add(new Action(ACTION.FLAG, bomb.x, bomb.y));
                            }
                        }
                    }
                }
            }
        }

        if (readyQueue.size() > 0) {
            Action action = readyQueue.removeFirst();
            if (action.action == ACTION.FLAG) {
                totalMines--;
            }
            totalTiles--;
            this.nextX = action.x;
            this.nextY = action.y;
            return action;
        }

        // endregion

        // region rule 2.2: constraint check (pair of 3)
        for (int n = 8; n >= 1; n--) {
            for (Location pos : choices.get(n)) {
                LinkedList<Location> coveredTiles = getCoveredTiles(pos.x, pos.y);
                LinkedList<Location> surrounding = getUncoveredTiles(pos.x, pos.y, 2);

                for (Location surround1 : surrounding) {
                    if (getNumber(surround1.x, surround1.y) - getBombTiles(surround1.x, surround1.y).size() == 1) {
                        for (Location surround2 : surrounding) {
                            if (getNumber(surround2.x, surround2.y) - getBombTiles(surround2.x, surround2.y).size() == 1) {
                                LinkedList<Location> surround1_coveredTiles = getCoveredTiles(surround1.x, surround1.y);
                                LinkedList<Location> surround2_coveredTiles = getCoveredTiles(surround2.x, surround2.y);
                                if (isMutualSet(surround1_coveredTiles, surround2_coveredTiles)) {
                                    coveredTiles.removeAll(surround1_coveredTiles);
                                    coveredTiles.removeAll(surround2_coveredTiles);
                                    if (n - getBombTiles(pos.x, pos.y).size() - 2 == coveredTiles.size()) {
                                        for (Location bomb : coveredTiles) {
                                            changeState(bomb.x, bomb.y, STATE.BOMB);
                                            readyQueue.add(new Action(ACTION.FLAG, bomb.x, bomb.y));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (readyQueue.size() > 0) {
            Action action = readyQueue.removeFirst();
            if (action.action == ACTION.FLAG) {
                totalMines--;
            }
            totalTiles--;
            this.nextX = action.x;
            this.nextY = action.y;
            return action;
        }
        // endregion

        // region rule 3: 1-1 pattern & 1-2 pattern
        // noted that 1-2 pattern doesn't need to check the wall

        // left to right
        for (int x = 1; x <= this.rowDimension - 1; x++) {
            for (int y = 1; y <= this.colDimension; y++) {

                if (checkState(x, y, STATE.UNCOVERED) && checkState(x + 1, y, STATE.UNCOVERED)) {

                    LinkedList<Location> firstBombTiles = getBombTiles(x, y);
                    LinkedList<Location> secondBombTiles = getBombTiles(x + 1, y);

                    if (getNumber(x, y) - firstBombTiles.size() == 1
                            && getNumber(x + 1, y) - secondBombTiles.size() == 1) {
                        LinkedList<Location> firstCoveredTiles = getCoveredTiles(x, y);
                        if (firstCoveredTiles.size() == 2) {
                            // check above
                            if (!isInBound(x - 1, y + 1) || checkState(x - 1, y + 1, STATE.UNCOVERED, STATE.BOMB)) {
                                if (checkBoundAndState(x, y + 1, STATE.COVERED)
                                        && checkBoundAndState(x + 1, y + 1, STATE.COVERED)
                                        && checkBoundAndState(x + 2, y + 1, STATE.COVERED)) {
                                    changeState(x + 2, y + 1, STATE.UNCOVERED);
                                    readyQueue.addLast(new Action(ACTION.UNCOVER, x + 2, y + 1));
                                }
                            }
                            // check below
                            if (!isInBound(x - 1, y - 1) || checkState(x - 1, y - 1, STATE.UNCOVERED, STATE.BOMB)) {
                                if (checkBoundAndState(x, y - 1, STATE.COVERED)
                                        && checkBoundAndState(x + 1, y - 1, STATE.COVERED)
                                        && checkBoundAndState(x + 2, y - 1, STATE.COVERED)) {
                                    changeState(x + 2, y - 1, STATE.UNCOVERED);
                                    readyQueue.addLast(new Action(ACTION.UNCOVER, x + 2, y - 1));
                                }
                            }
                        }
                    }

                    LinkedList<Location> secondCoveredTiles = getCoveredTiles(x + 1, y);
                    if (checkBoundAndState(x + 2, y, STATE.COVERED)
                            && getNumber(x, y) - firstBombTiles.size() == 1
                            && getNumber(x + 1, y) - secondBombTiles.size() == 2
                            && (secondCoveredTiles.size() <= 3 && secondCoveredTiles.size() >= 1)) {
                        // check above
                        if (checkBoundAndState(x, y + 1, STATE.COVERED)
                                && checkBoundAndState(x + 1, y + 1, STATE.COVERED)
                                && checkBoundAndState(x + 2, y + 1, STATE.COVERED)) {
                            changeState(x + 2, y + 1, STATE.BOMB);

                            readyQueue.addLast(new Action(ACTION.FLAG, x + 2, y + 1));
                        }
                        // check below
                        if (checkBoundAndState(x, y - 1, STATE.COVERED)
                                && checkBoundAndState(x + 1, y - 1, STATE.COVERED)
                                && checkBoundAndState(x + 2, y - 1, STATE.COVERED)) {
                            changeState(x + 2, y - 1, STATE.BOMB);

                            readyQueue.addLast(new Action(ACTION.FLAG, x + 2, y - 1));
                        }
                    }
                }
            }
        }

        // right to left
        for (int x = this.rowDimension; x > 1; x--) {
            for (int y = 1; y <= this.colDimension; y++) {
                if (checkState(x, y, STATE.UNCOVERED) && checkState(x - 1, y, STATE.UNCOVERED)) {
                    LinkedList<Location> firstBombTiles = getBombTiles(x, y);
                    LinkedList<Location> secondBombTiles = getBombTiles(x - 1, y);

                    if (getNumber(x, y) - firstBombTiles.size() == 1
                            && getNumber(x - 1, y) - secondBombTiles.size() == 1) {
                        LinkedList<Location> firstCoveredTiles = getCoveredTiles(x, y);
                        if (firstCoveredTiles.size() == 2) {
                            // check above
                            if (!isInBound(x + 1, y - 1) || checkState(x + 1, y - 1, STATE.UNCOVERED, STATE.BOMB)) {
                                if (checkBoundAndState(x, y - 1, STATE.COVERED)
                                        && checkBoundAndState(x - 1, y - 1, STATE.COVERED)
                                        && checkBoundAndState(x - 2, y - 1, STATE.COVERED)) {
                                    changeState(x - 2, y - 1, STATE.UNCOVERED);
                                    readyQueue.addLast(new Action(ACTION.UNCOVER, x - 2, y - 1));
                                }
                            }
                            // check below
                            if (!isInBound(x + 1, y + 1) || checkState(x + 1, y + 1, STATE.UNCOVERED, STATE.BOMB)) {
                                if (checkBoundAndState(x, y + 1, STATE.COVERED)
                                        && checkBoundAndState(x - 1, y + 1, STATE.COVERED)
                                        && checkBoundAndState(x - 2, y + 1, STATE.COVERED)) {
                                    changeState(x - 2, y + 1, STATE.UNCOVERED);
                                    readyQueue.addLast(new Action(ACTION.UNCOVER, x - 2, y + 1));
                                }
                            }
                        }
                    }

                    LinkedList<Location> secondCoveredTiles = getCoveredTiles(x - 1, y);
                    if (getNumber(x, y) - firstBombTiles.size() == 1
                            && getNumber(x - 1, y) - secondBombTiles.size() == 2
                            && (secondCoveredTiles.size() <= 3 && secondCoveredTiles.size() >= 1)) {
                        // check above
                        if (checkBoundAndState(x, y - 1, STATE.COVERED)
                                && checkBoundAndState(x - 1, y - 1, STATE.COVERED)
                                && checkBoundAndState(x - 2, y - 1, STATE.COVERED)) {
                            changeState(x - 2, y - 1, STATE.BOMB);

                            readyQueue.addLast(new Action(ACTION.FLAG, x - 2, y - 1));
                        }
                        // check below
                        if (checkBoundAndState(x, y + 1, STATE.COVERED)
                                && checkBoundAndState(x - 1, y + 1, STATE.COVERED)
                                && checkBoundAndState(x - 2, y + 1, STATE.COVERED)) {
                            changeState(x - 2, y + 1, STATE.BOMB);

                            readyQueue.addLast(new Action(ACTION.FLAG, x - 2, y + 1));
                        }
                    }
                }
            }
        }

        // top to bottom
        for (int x = 1; x <= this.rowDimension; x++) {
            for (int y = this.colDimension; y > 1; y--) {
                if (checkState(x, y, STATE.UNCOVERED) && checkState(x, y - 1, STATE.UNCOVERED)) {
                    LinkedList<Location> firstBombTiles = getBombTiles(x, y);
                    LinkedList<Location> secondBombTiles = getBombTiles(x, y - 1);

                    if (getNumber(x, y) - firstBombTiles.size() == 1
                            && getNumber(x, y - 1) - secondBombTiles.size() == 1) {
                        LinkedList<Location> firstCoveredTiles = getCoveredTiles(x, y);
                        if (firstCoveredTiles.size() == 2) {
                            // check left
                            if (!isInBound(x - 1, y + 1) || checkState(x - 1, y + 1, STATE.UNCOVERED, STATE.BOMB)) {
                                if (checkBoundAndState(x - 1, y, STATE.COVERED)
                                        && checkBoundAndState(x - 1, y - 1, STATE.COVERED)
                                        && checkBoundAndState(x - 1, y - 2, STATE.COVERED)) {
                                    changeState(x - 1, y - 2, STATE.UNCOVERED);
                                    readyQueue.addLast(new Action(ACTION.UNCOVER, x - 1, y - 2));
                                }
                            }
                            // check right
                            if (!isInBound(x + 1, y + 1) || checkState(x + 1, y + 1, STATE.UNCOVERED, STATE.BOMB)) {
                                if (checkBoundAndState(x + 1, y, STATE.COVERED)
                                        && checkBoundAndState(x + 1, y - 1, STATE.COVERED)
                                        && checkBoundAndState(x + 1, y - 2, STATE.COVERED)) {
                                    changeState(x + 1, y - 2, STATE.UNCOVERED);
                                    readyQueue.addLast(new Action(ACTION.UNCOVER, x + 1, y - 2));
                                }
                            }
                        }
                    }

                    LinkedList<Location> secondCoveredTiles = getCoveredTiles(x, y - 1);
                    if (getNumber(x, y) - firstBombTiles.size() == 1
                            && getNumber(x, y - 1) - secondBombTiles.size() == 2
                            && (secondCoveredTiles.size() <= 3 && secondCoveredTiles.size() >= 1)) {
                        // check left
                        if (checkBoundAndState(x - 1, y, STATE.COVERED)
                                && checkBoundAndState(x - 1, y - 1, STATE.COVERED)
                                && checkBoundAndState(x - 1, y - 2, STATE.COVERED)) {
                            changeState(x - 1, y - 2, STATE.BOMB);

                            readyQueue.addLast(new Action(ACTION.FLAG, x - 1, y - 2));
                        }
                        // check right
                        if (checkBoundAndState(x + 1, y, STATE.COVERED)
                                && checkBoundAndState(x + 1, y - 1, STATE.COVERED)
                                && checkBoundAndState(x + 1, y - 2, STATE.COVERED)) {
                            changeState(x + 1, y - 2, STATE.BOMB);

                            readyQueue.addLast(new Action(ACTION.FLAG, x + 1, y - 2));
                        }
                    }
                }
            }
        }

        // bottom to top
        for (int x = 1; x <= this.rowDimension; x++) {
            for (int y = 1; y < this.colDimension; y++) {
                if (checkState(x, y, STATE.UNCOVERED) && checkState(x, y + 1, STATE.UNCOVERED)) {
                    LinkedList<Location> firstBombTiles = getBombTiles(x, y);
                    LinkedList<Location> secondBombTiles = getBombTiles(x, y + 1);

                    if (getNumber(x, y) - firstBombTiles.size() == 1
                            && getNumber(x, y + 1) - secondBombTiles.size() == 1) {
                        LinkedList<Location> firstCoveredTiles = getCoveredTiles(x, y);
                        if (firstCoveredTiles.size() == 2) {
                            // check left
                            if (!isInBound(x - 1, y - 1) || checkState(x - 1, y - 1, STATE.UNCOVERED, STATE.BOMB)) {
                                if (checkBoundAndState(x - 1, y, STATE.COVERED)
                                        && checkBoundAndState(x - 1, y + 1, STATE.COVERED)
                                        && checkBoundAndState(x - 1, y + 2, STATE.COVERED)) {
                                    changeState(x - 1, y + 2, STATE.UNCOVERED);
                                    readyQueue.addLast(new Action(ACTION.UNCOVER, x - 1, y + 2));
                                }
                            }
                            // check right
                            if (!isInBound(x + 1, y - 1) || checkState(x + 1, y - 1, STATE.UNCOVERED, STATE.BOMB)) {
                                if (checkBoundAndState(x + 1, y, STATE.COVERED)
                                        && checkBoundAndState(x + 1, y + 1, STATE.COVERED)
                                        && checkBoundAndState(x + 1, y + 2, STATE.COVERED)) {
                                    changeState(x + 1, y + 2, STATE.UNCOVERED);
                                    readyQueue.addLast(new Action(ACTION.UNCOVER, x + 1, y + 2));
                                }
                            }
                        }
                    }
                    LinkedList<Location> secondCoveredTiles = getCoveredTiles(x, y + 1);
                    if (getNumber(x, y) - firstBombTiles.size() == 1
                            && getNumber(x, y + 1) - secondBombTiles.size() == 2
                            && (secondCoveredTiles.size() <= 3 && secondCoveredTiles.size() >= 1)) {
                        // check left
                        if (checkBoundAndState(x - 1, y, STATE.COVERED)
                                && checkBoundAndState(x - 1, y + 1, STATE.COVERED)
                                && checkBoundAndState(x - 1, y + 2, STATE.COVERED)) {
                            changeState(x - 1, y + 2, STATE.BOMB);

                            readyQueue.addLast(new Action(ACTION.FLAG, x - 1, y + 2));
                        }
                        // check right
                        if (checkBoundAndState(x + 1, y, STATE.COVERED)
                                && checkBoundAndState(x + 1, y + 1, STATE.COVERED)
                                && checkBoundAndState(x + 1, y + 2, STATE.COVERED)) {
                            changeState(x + 1, y + 2, STATE.BOMB);

                            readyQueue.addLast(new Action(ACTION.FLAG, x + 1, y + 2));
                        }
                    }
                }
            }
        }

        if (readyQueue.size() > 0) {
            Action action = readyQueue.removeFirst();
            if (action.action == ACTION.FLAG) {
                totalMines--;
            }
            totalTiles--;
            this.nextX = action.x;
            this.nextY = action.y;
            return action;
        }

        // endregion

        // region rule 4.1: special pattern (3 bombs)

        // horizontal - top
        for (int x = 1; x <= this.rowDimension - 2; x++) {
            for (int y = 1; y <= this.colDimension; y++) {
                if (checkState(x, y, STATE.BOMB)
                        && checkState(x + 1, y, STATE.BOMB)
                        && checkState(x + 2, y, STATE.BOMB)) {
                    if ((checkBoundAndState(x, y + 1, STATE.UNCOVERED) && getNumber(x, y + 1) >= 3)
                            && checkBoundAndState(x + 1, y + 1, STATE.COVERED)
                            && (checkBoundAndState(x + 2, y + 1, STATE.UNCOVERED) && getNumber(x + 2, y + 1) >= 3)
                            && checkBoundAndState(x, y - 1, STATE.UNCOVERED)
                            && checkBoundAndState(x + 1, y - 1, STATE.UNCOVERED)
                            && checkBoundAndState(x + 2, y - 1, STATE.UNCOVERED)) {
                        changeState(x + 1, y + 1, STATE.BOMB);
                        this.nextX = x + 1;
                        this.nextY = y + 1;
                        totalTiles--;
                        return new Action(ACTION.FLAG, x + 1, y + 1);
                    }
                }
            }
        }
        // horizontal - bottom
        for (int x = 1; x <= this.rowDimension - 2; x++) {
            for (int y = 1; y <= this.colDimension; y++) {
                if (checkState(x, y, STATE.BOMB)
                        && checkState(x + 1, y, STATE.BOMB)
                        && checkState(x + 2, y, STATE.BOMB)) {
                    // check bottom
                    if ((checkBoundAndState(x, y - 1, STATE.UNCOVERED) && getNumber(x, y - 1) >= 3)
                            && checkBoundAndState(x + 1, y - 1, STATE.COVERED)
                            && (checkBoundAndState(x + 2, y - 1, STATE.UNCOVERED) && getNumber(x + 2, y - 1) >= 3)
                            && checkBoundAndState(x, y + 1, STATE.UNCOVERED)
                            && checkBoundAndState(x + 1, y + 1, STATE.UNCOVERED)
                            && checkBoundAndState(x + 2, y + 1, STATE.UNCOVERED)) {
                        changeState(x + 1, y - 1, STATE.BOMB);
                        this.nextX = x + 1;
                        this.nextY = y - 1;
                        totalTiles--;
                        return new Action(ACTION.FLAG, x + 1, y - 1);
                    }
                }
            }
        }
        // vertical - left
        for (int x = 1; x <= this.rowDimension; x++) {
            for (int y = 1; y <= this.colDimension - 2; y++) {
                if (checkState(x, y, STATE.BOMB)
                        && checkState(x, y + 1, STATE.BOMB)
                        && checkState(x, y + 2, STATE.BOMB)) {
                    if ((checkBoundAndState(x - 1, y, STATE.UNCOVERED) && getNumber(x - 1, y) >= 3)
                            && checkBoundAndState(x - 1, y + 1, STATE.COVERED)
                            && (checkBoundAndState(x - 1, y + 2, STATE.UNCOVERED) && getNumber(x - 1, y + 2) >= 3)
                            && checkBoundAndState(x + 1, y, STATE.UNCOVERED)
                            && checkBoundAndState(x + 1, y + 1, STATE.UNCOVERED)
                            && checkBoundAndState(x + 1, y + 2, STATE.UNCOVERED)) {
                        changeState(x - 1, y + 1, STATE.BOMB);
                        this.nextX = x - 1;
                        this.nextY = y + 1;
                        totalTiles--;
                        totalMines--;
                        return new Action(ACTION.FLAG, x - 1, y + 1);
                    }
                }
            }
        }
        // vertical - right
        for (int x = 1; x <= this.rowDimension; x++) {
            for (int y = 1; y <= this.colDimension - 2; y++) {
                if (checkState(x, y, STATE.BOMB)
                        && checkState(x, y + 1, STATE.BOMB)
                        && checkState(x, y + 2, STATE.BOMB)) {
                    if ((checkBoundAndState(x + 1, y, STATE.UNCOVERED) && getNumber(x + 1, y) >= 3)
                            && checkBoundAndState(x + 1, y + 1, STATE.COVERED)
                            && (checkBoundAndState(x + 1, y + 2, STATE.UNCOVERED) && getNumber(x + 1, y + 2) >= 3)
                            && checkBoundAndState(x - 1, y, STATE.UNCOVERED)
                            && checkBoundAndState(x - 1, y + 1, STATE.UNCOVERED)
                            && checkBoundAndState(x - 1, y + 2, STATE.UNCOVERED)) {
                        changeState(x + 1, y + 1, STATE.BOMB);
                        this.nextX = x + 1;
                        this.nextY = y + 1;
                        totalTiles--;
                        totalMines--;
                        return new Action(ACTION.FLAG, x + 1, y + 1);
                    }
                }
            }
        }

        // endregion

        // region rule 4.2: special pattern

        // vertical
        for (int x = 1; x < this.rowDimension; x++) {
            for (int y = 1; y < this.colDimension - 2; y++) {
                if (readyQueue.size() > 0) {
                    break;
                }
                if (checkState(x, y, STATE.BOMB) && checkState(x, y + 1, STATE.BOMB) && checkState(x, y + 2, STATE.BOMB)) {
                    if ((checkBoundAndState(x - 1, y, STATE.UNCOVERED) && getNumber(x - 1, y) >= 3)
                            && (checkBoundAndState(x, y + 3, STATE.UNCOVERED) && getNumber(x, y + 3) >= 3)
                            && (checkState(x - 1, y + 1, STATE.COVERED))) {

                        changeState(x - 1, y + 1, STATE.UNCOVERED);
                        readyQueue.add(new Action(ACTION.UNCOVER, x - 1, y + 1));
                    } else if ((checkBoundAndState(x + 1, y, STATE.UNCOVERED) && getNumber(x + 1, y) >= 3)
                            && (checkBoundAndState(x, y + 3, STATE.UNCOVERED) && getNumber(x, y + 3) >= 3)
                            && (checkState(x + 1, y + 1, STATE.COVERED))) {

                        changeState(x + 1, y + 1, STATE.UNCOVERED);
                        readyQueue.add(new Action(ACTION.UNCOVER, x + 1, y + 1));
                    } else if ((checkBoundAndState(x - 1, y + 3, STATE.UNCOVERED) && getNumber(x - 1, y + 3) >= 3)
                            && checkBoundAndState(x, y - 1, STATE.UNCOVERED) && getNumber(x, y - 1) >= 3
                            && (checkState(x - 1, y + 1, STATE.COVERED))) {

                        changeState(x - 1, y + 1, STATE.UNCOVERED);
                        readyQueue.add(new Action(ACTION.UNCOVER, x - 1, y + 1));
                    } else if ((checkBoundAndState(x + 1, y + 3, STATE.UNCOVERED) && getNumber(x + 1, y + 3) >= 3)
                            && checkBoundAndState(x, y - 1, STATE.UNCOVERED) && getNumber(x, y - 1) >= 3
                            && (checkState(x + 1, y + 1, STATE.COVERED))) {

                        changeState(x + 1, y + 1, STATE.UNCOVERED);
                        readyQueue.add(new Action(ACTION.UNCOVER, x + 1, y + 1));
                    }
                }
            }
        }

        if (readyQueue.size() > 0) {
            Action action = readyQueue.removeFirst();
            if (action.action == ACTION.FLAG) {
                totalMines--;
            }
            totalTiles--;
            this.nextX = action.x;
            this.nextY = action.y;
            return action;
        }

        // horizontal
        // TODO you know
        //        for (int x = 1; x < this.rowDimension - 2; x++) {
        //            for (int y = 1; y < this.colDimension; y++) {
        //                if (checkState(x, y, STATE.BOMB) && checkState(x + 1, y, STATE.BOMB) && checkState(x + 2, y, STATE.BOMB)) {
        //                    if ((checkBoundAndState(x, y + 1, STATE.UNCOVERED) && getNumber(x, y + 1) >= 3)
        //                            && (checkBoundAndState(x + 3, y, STATE.UNCOVERED) && getNumber(x + 3, y) >= 3)
        //                            && (checkState(x + 1, y + 1, STATE.COVERED))) {
        //                        changeState(x + 1, y + 1, STATE.UNCOVERED);
        //                        readyQueue.add(new Action(ACTION.UNCOVER, x + 1, y + 1));
        //                        break;
        //                    }
        //                }
        //            }
        //        }

        // endregion

        // region rule 4.3: special pattern (edge 4-bombs)

        // top
        for (int x = 1; x < this.rowDimension - 2; x++) {
            if (readyQueue.size() > 0) {
                break;
            }
            for (int y = 1; y < this.colDimension - 1; y++) {
                if (readyQueue.size() > 0) {
                    break;
                }
                if (checkState(x, y, STATE.BOMB) && checkState(x + 1, y, STATE.BOMB)
                        && checkState(x + 2, y, STATE.BOMB) && checkState(x + 3, y, STATE.BOMB)) {
                    if (checkState(x + 3, y + 1, STATE.BOMB)) {
                        if (checkState(x + 2, y + 1, STATE.COVERED)) {
                            changeState(x + 2, y + 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x + 2, y + 1));
                        }
                        if (checkState(x + 1, y + 1, STATE.COVERED)) {
                            changeState(x + 1, y + 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x + 1, y + 1));
                        }
                        if (checkState(x, y + 1, STATE.COVERED)) {
                            changeState(x, y + 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x, y + 1));
                        }
                    } else if (checkState(x, y + 1, STATE.BOMB)) {
                        if (checkState(x + 3, y + 1, STATE.COVERED)) {
                            changeState(x + 3, y + 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x + 3, y + 1));
                        }
                        if (checkState(x + 2, y + 1, STATE.COVERED)) {
                            changeState(x + 2, y + 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x + 2, y + 1));
                        }
                        if (checkState(x + 1, y + 1, STATE.COVERED)) {
                            changeState(x + 1, y + 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x + 1, y + 1));
                        }
                    } else if (checkState(x, y + 1, STATE.COVERED) && checkState(x + 3, y + 1, STATE.COVERED)) {
                        if (checkState(x + 2, y + 1, STATE.COVERED)) {
                            changeState(x + 2, y + 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x + 2, y + 1));
                        }
                        if (checkState(x + 1, y + 1, STATE.COVERED)) {
                            changeState(x + 1, y + 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x + 1, y + 1));
                        }
                    }
                }
            }
        }

        if (readyQueue.size() > 0) {
            Action action = readyQueue.removeFirst();
            if (action.action == ACTION.FLAG) {
                totalMines--;
            }
            totalTiles--;
            this.nextX = action.x;
            this.nextY = action.y;
            return action;
        }

        // bottom
        for (int x = 1; x < this.rowDimension - 2; x++) {
            if (readyQueue.size() > 0) {
                break;
            }
            for (int y = 2; y < this.colDimension; y++) {
                if (readyQueue.size() > 0) {
                    break;
                }
                if (checkState(x, y, STATE.BOMB) && checkState(x + 1, y, STATE.BOMB)
                        && checkState(x + 2, y, STATE.BOMB) && checkState(x + 3, y, STATE.BOMB)) {
                    if (checkState(x + 3, y - 1, STATE.BOMB)) {
                        if (checkState(x + 2, y - 1, STATE.COVERED)) {
                            changeState(x + 2, y - 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x + 2, y - 1));
                        }
                        if (checkState(x + 1, y - 1, STATE.COVERED)) {
                            changeState(x + 1, y - 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x + 1, y - 1));
                        }
                        if (checkState(x, y - 1, STATE.COVERED)) {
                            changeState(x, y - 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x, y - 1));
                        }
                    } else if (checkState(x, y - 1, STATE.BOMB)) {
                        if (checkState(x + 3, y - 1, STATE.COVERED)) {
                            changeState(x + 3, y - 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x + 3, y - 1));
                        }
                        if (checkState(x + 2, y - 1, STATE.COVERED)) {
                            changeState(x + 2, y - 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x + 2, y - 1));
                        }
                        if (checkState(x + 1, y - 1, STATE.COVERED)) {
                            changeState(x + 1, y - 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x + 1, y - 1));
                        }
                    } else if (checkState(x, y - 1, STATE.COVERED) && checkState(x + 3, y - 1, STATE.COVERED)) {
                        if (checkState(x + 2, y - 1, STATE.COVERED)) {
                            changeState(x + 2, y - 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x + 2, y - 1));
                        }
                        if (checkState(x + 1, y - 1, STATE.COVERED)) {
                            changeState(x + 1, y - 1, STATE.UNCOVERED);
                            readyQueue.add(new Action(ACTION.UNCOVER, x + 1, y - 1));
                        }
                    }
                }
            }
        }

        if (readyQueue.size() > 0) {
            Action action = readyQueue.removeFirst();
            if (action.action == ACTION.FLAG) {
                totalMines--;
            }
            totalTiles--;
            this.nextX = action.x;
            this.nextY = action.y;
            return action;
        }

        // endregion

        // region rule 4.4: special pattern (U-shape)

        // up U-shape
        for (int x = 1; x < this.rowDimension - 2; x++) {
            if (readyQueue.size() > 0) {
                break;
            }
            for (int y = 1; y < this.colDimension - 2; y++) {
                if (readyQueue.size() > 0) {
                    break;
                }
                if (checkState(x + 1, y + 1, STATE.COVERED) && checkState(x + 1, y + 2, STATE.COVERED)
                        && checkState(x, y + 2, STATE.UNCOVERED) && getReducedNumber(x, y + 2) <= 2
                        && checkState(x, y + 1, STATE.UNCOVERED) && getReducedNumber(x, y + 1) <= 2
                        && checkState(x, y, STATE.UNCOVERED) && getReducedNumber(x, y) <= 2
                        && checkState(x + 1, y, STATE.UNCOVERED) && getReducedNumber(x + 1, y) <= 2
                        && checkState(x + 2, y, STATE.UNCOVERED) && getReducedNumber(x + 2, y) <= 1
                        && checkState(x + 2, y + 1, STATE.UNCOVERED) && getReducedNumber(x + 2, y + 1) <= 2
                        && checkState(x + 2, y + 2, STATE.UNCOVERED) && getReducedNumber(x + 2, y + 2) <= 2) {
                    changeState(x + 1, y + 1, STATE.UNCOVERED);
                    readyQueue.add(new Action(ACTION.UNCOVER, x + 1, y + 1));
                }
            }
        }

        // endregion

        // region rule 4.5: special pattern (square bombs)

        for (int x = 1; x < this.rowDimension - 1; x++) {
            if (readyQueue.size() > 0) {
                break;
            }
            for (int y = 1; y < this.colDimension - 1; y++) {
                if (readyQueue.size() > 0) {
                    break;
                }
                // top left is safe
                if (checkState(x, y, STATE.BOMB) && checkState(x + 1, y, STATE.BOMB)
                        && checkState(x, y + 1, STATE.COVERED) && checkState(x + 1, y + 1, STATE.BOMB)) {
                    changeState(x, y + 1, STATE.UNCOVERED);
                    readyQueue.add(new Action(ACTION.UNCOVER, x, y + 1));
                }
                // top right is safe
                if (checkState(x, y, STATE.BOMB) && checkState(x + 1, y, STATE.BOMB)
                        && checkState(x, y + 1, STATE.BOMB) && checkState(x + 1, y + 1, STATE.COVERED)) {
                    changeState(x + 1, y + 1, STATE.UNCOVERED);
                    readyQueue.add(new Action(ACTION.UNCOVER, x + 1, y + 1));
                }
                // bottom left is safe
                if (checkState(x, y, STATE.COVERED) && checkState(x + 1, y, STATE.BOMB)
                        && checkState(x, y + 1, STATE.BOMB) && checkState(x + 1, y + 1, STATE.BOMB)) {
                    changeState(x, y, STATE.UNCOVERED);
                    readyQueue.add(new Action(ACTION.UNCOVER, x, y));
                }
                // bottom right is safe
                if (checkState(x, y, STATE.BOMB) && checkState(x + 1, y, STATE.COVERED)
                        && checkState(x, y + 1, STATE.BOMB) && checkState(x + 1, y + 1, STATE.BOMB)) {
                    changeState(x + 1, y, STATE.UNCOVERED);
                    readyQueue.add(new Action(ACTION.UNCOVER, x + 1, y));
                }

            }
        }

        if (readyQueue.size() > 0) {
            Action action = readyQueue.removeFirst();
            if (action.action == ACTION.FLAG) {
                totalMines--;
            }
            totalTiles--;
            this.nextX = action.x;
            this.nextY = action.y;
            return action;
        }
        // endregion

        // region rule 5: guess corners

        // open corners
        ArrayList<Location> corners = new ArrayList<>();

        if (checkState(this.rowDimension, this.colDimension, STATE.COVERED)) {
            // top right
            corners.add(new Location(this.rowDimension, this.colDimension));
        }
        if (checkState(this.rowDimension, 1, STATE.COVERED)) {
            // bottom right
            corners.add(new Location(this.rowDimension, 1));
        }
        if (checkState(1, this.colDimension, STATE.COVERED)) {
            // top left
            corners.add(new Location(1, this.colDimension));
        }
        if (checkState(1, 1, STATE.COVERED)) {
            // bottom left
            corners.add(new Location(1, 1));
        }

        if (corners.size() > 0) {
            Location loc = corners.get(new Random().nextInt(corners.size()));

            if (this.totalTiles < 3 && this.totalMines == 1) {
                changeState(loc.x, loc.y, STATE.BOMB);
                readyQueue.add(new Action(ACTION.FLAG, loc.x, loc.y));
            } else {
                changeState(loc.x, loc.y, STATE.UNCOVERED);
                readyQueue.add(new Action(ACTION.UNCOVER, loc.x, loc.y));
            }
        }

        if (readyQueue.size() > 0) {
            Action action = readyQueue.removeFirst();
            if (action.action == ACTION.FLAG) {
                totalMines--;
            }
            totalTiles--;
            this.nextX = action.x;
            this.nextY = action.y;
            return action;
        }

        // endregion

        // region rule 6: probability guess

        int maxShareCount = 0;
        int guessX = 0;
        int guessY = 0;

        for (int x = 1; x < this.rowDimension; x++) {
            for (int y = 1; y < this.colDimension; y++) {
                if (checkState(x, y, STATE.COVERED)) {
                    HashSet<LinkedList<Location>> sharedLocations = new HashSet<>();

                    int shareCount = 0;
                    for (Location uncovered : getUncoveredTiles(x, y)) {
                        if (getNumber(uncovered.x, uncovered.y) - getBombTiles(uncovered.x, uncovered.y).size() == 1) {
                            LinkedList<Location> coveredTiles = getCoveredTiles(uncovered.x, uncovered.y);
                            if (!sharedLocations.contains(coveredTiles)) {
                                sharedLocations.add(coveredTiles);
                                shareCount++;
                            }
                        }
                    }
                    if (shareCount > maxShareCount) {
                        maxShareCount = shareCount;
                        guessX = x;
                        guessY = y;
                    }
                }
            }
        }
        if (maxShareCount >= 3) {
            changeState(guessX, guessY, STATE.UNCOVERED);
            readyQueue.add(new Action(ACTION.UNCOVER, guessX, guessY));
        }
        if (readyQueue.size() > 0) {
            Action action = readyQueue.removeFirst();
            if (action.action == ACTION.FLAG) {
                totalMines--;
            }
            totalTiles--;
            this.nextX = action.x;
            this.nextY = action.y;
            return action;
        }

        // endregion

        // region rule 7: guess by bomb count

        // random guess, the covered tile that surrounds with most bombs
        int maxBomb = Integer.MIN_VALUE;
        int maxX = -1;
        int maxY = -1;

        for (int x = 1; x <= this.rowDimension; x++) {
            for (int y = 1; y <= this.colDimension; y++) {
                if (checkState(x, y, STATE.COVERED)) {
                    int bombCount = getBombTiles(x, y).size();
                    if (bombCount > maxBomb) {
                        maxBomb = bombCount;
                        maxX = x;
                        maxY = y;
                    }
                }
            }
        }

        if (isInBound(maxX, maxY)) {
            this.nextX = maxX;
            this.nextY = maxY;

            // look around and see if there is a 3
            int greaterThan = 3;

            int withinRange = 3;

            if ((isInBound(maxX - 1, maxY) && getNumber(maxX - 1, maxY) >= greaterThan)
                    || (isInBound(maxX - 1, maxY + 1) && getNumber(maxX - 1, maxY + 1) >= greaterThan)
                    || (isInBound(maxX, maxY + 1) && getNumber(maxX, maxY + 1) >= greaterThan)
                    || (isInBound(maxX + 1, maxY + 1) && getNumber(maxX + 1, maxY + 1) >= greaterThan)
                    || (isInBound(maxX + 1, maxY) && getNumber(maxX + 1, maxY) >= greaterThan)
                    || (isInBound(maxX + 1, maxY - 1) && getNumber(maxX + 1, maxY - 1) >= greaterThan)
                    || (isInBound(maxX, maxY - 1) && getNumber(maxX, maxY - 1) >= greaterThan)
                    || (isInBound(maxX - 1, maxY - 1) && getNumber(maxX - 1, maxY - 1) >= greaterThan)) {
                if ((maxX <= withinRange || this.rowDimension - maxX <= withinRange) && (maxY <= withinRange || this.colDimension - maxY <= withinRange)) {
                    if (this.totalMines * 2 >= this.totalTiles) {
                        changeState(maxX, maxY, STATE.UNCOVERED);
                        totalTiles--;
                        return new Action(ACTION.UNCOVER, maxX, maxY);
                    } else {
                        changeState(maxX, maxY, STATE.BOMB);
                        totalMines--;
                        totalTiles--;
                        return new Action(ACTION.FLAG, maxX, maxY);
                    }
                }
            }
            if (maxBomb >= 5) {
                changeState(maxX, maxY, STATE.UNCOVERED);
                totalTiles--;

                return new Action(ACTION.UNCOVER, maxX, maxY);
            } else if (maxBomb <= 2) {
                changeState(maxX, maxY, STATE.BOMB);
                totalMines--;
                totalTiles--;

                return new Action(ACTION.FLAG, maxX, maxY);
            } else {
                changeState(maxX, maxY, STATE.BOMB);
                totalMines--;
                totalTiles--;
                return new Action(ACTION.FLAG, maxX, maxY);
            }
        }

        // endregion

        return new Action(ACTION.LEAVE);
    }

    private boolean handle1(int x, int y, int number) {
        LinkedList<Location> coveredTiles = getCoveredTiles(x, y);
        LinkedList<Location> bombTiles = getBombTiles(x, y);

//        if (bombTiles.size() > number) {
//            System.out.println("Why there is >" + number + " bombs at (" + x + ", " + y + ") how");
//        }

        if (coveredTiles.size() == number - bombTiles.size()) {
            for (Location pos : coveredTiles) {
                changeState(pos.x, pos.y, STATE.BOMB);
                readyQueue.addLast(new Action(ACTION.FLAG, pos.x, pos.y));
            }
            return true;
        } else if (bombTiles.size() == number) {
            for (Location pos : coveredTiles) {
                changeState(pos.x, pos.y, STATE.UNCOVERED);
                readyQueue.addLast(new Action(ACTION.UNCOVER, pos.x, pos.y));
            }
            return true;
        }
        return false;
    }

    private boolean handle0(int x, int y, int number) {

        STATE state = number == 0 ? STATE.UNCOVERED : STATE.BOMB;
        ACTION action = number == 0 ? ACTION.UNCOVER : ACTION.FLAG;

        // 1. left
        if (checkBoundAndState(x - 1, y, STATE.COVERED)) {
            changeState(x - 1, y, state);
            readyQueue.addLast(new Action(action, x - 1, y));
        }
        // 2. left, top
        if (checkBoundAndState(x - 1, y + 1, STATE.COVERED)) {
            changeState(x - 1, y + 1, state);
            readyQueue.addLast(new Action(action, x - 1, y + 1));
        }
        // 3. top
        if (checkBoundAndState(x, y + 1, STATE.COVERED)) {
            changeState(x, y + 1, state);
            readyQueue.addLast(new Action(action, x, y + 1));
        }
        // 4. right top
        if (checkBoundAndState(x + 1, y + 1, STATE.COVERED)) {
            changeState(x + 1, y + 1, state);
            readyQueue.addLast(new Action(action, x + 1, y + 1));
        }
        // 5. right
        if (checkBoundAndState(x + 1, y, STATE.COVERED)) {
            changeState(x + 1, y, state);
            readyQueue.addLast(new Action(action, x + 1, y));

        }
        // 6. right, bottom
        if (checkBoundAndState(x + 1, y - 1, STATE.COVERED)) {
            changeState(x + 1, y - 1, state);
            readyQueue.addLast(new Action(action, x + 1, y - 1));
        }
        // 7. bottom
        if (checkBoundAndState(x, y - 1, STATE.COVERED)) {
            changeState(x, y - 1, state);
            readyQueue.addLast(new Action(action, x, y - 1));
        }
        // 8. left, bottom
        if (checkBoundAndState(x - 1, y - 1, STATE.COVERED)) {
            changeState(x - 1, y - 1, state);
            readyQueue.addLast(new Action(action, x - 1, y - 1));
        }
        return true;
    }

    private LinkedList<Location> getCoveredTiles(int x, int y) {
        LinkedList<Location> coveredTiles = new LinkedList<>();

        // 1. left
        if (isInBound(x - 1, y)) {
            if (checkState(x - 1, y, STATE.COVERED)) {
                coveredTiles.add(new Location(x - 1, y));
            }
        }
        // 2. left, top
        if (isInBound(x - 1, y + 1)) {
            if (checkState(x - 1, y + 1, STATE.COVERED)) {
                coveredTiles.add(new Location(x - 1, y + 1));
            }
        }
        // 3. top
        if (isInBound(x, y + 1)) {
            if (checkState(x, y + 1, STATE.COVERED)) {
                coveredTiles.add(new Location(x, y + 1));
            }
        }
        // 4. right top
        if (isInBound(x + 1, y + 1)) {
            if (checkState(x + 1, y + 1, STATE.COVERED)) {
                coveredTiles.add(new Location(x + 1, y + 1));
            }
        }
        // 5. right
        if (isInBound(x + 1, y)) {
            if (checkState(x + 1, y, STATE.COVERED)) {
                coveredTiles.add(new Location(x + 1, y));
            }
        }
        // 6. right, bottom
        if (isInBound(x + 1, y - 1)) {
            if (checkState(x + 1, y - 1, STATE.COVERED)) {
                coveredTiles.add(new Location(x + 1, y - 1));
            }
        }
        // 7. bottom
        if (isInBound(x, y - 1)) {
            if (checkState(x, y - 1, STATE.COVERED)) {
                coveredTiles.add(new Location(x, y - 1));
            }
        }
        // 8. left, bottom
        if (isInBound(x - 1, y - 1)) {
            if (checkState(x - 1, y - 1, STATE.COVERED)) {
                coveredTiles.add(new Location(x - 1, y - 1));
            }
        }
        return coveredTiles;
    }

    private LinkedList<Location> getUncoveredTiles(int x, int y) {
        LinkedList<Location> uncoveredTiles = new LinkedList<>();

        // 1. left
        if (isInBound(x - 1, y)) {
            if (checkState(x - 1, y, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x - 1, y));
            }
        }
        // 2. left, top
        if (isInBound(x - 1, y + 1)) {
            if (checkState(x - 1, y + 1, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x - 1, y + 1));
            }
        }
        // 3. top
        if (isInBound(x, y + 1)) {
            if (checkState(x, y + 1, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x, y + 1));
            }
        }
        // 4. right top
        if (isInBound(x + 1, y + 1)) {
            if (checkState(x + 1, y + 1, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x + 1, y + 1));
            }
        }
        // 5. right
        if (isInBound(x + 1, y)) {
            if (checkState(x + 1, y, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x + 1, y));
            }
        }
        // 6. right, bottom
        if (isInBound(x + 1, y - 1)) {
            if (checkState(x + 1, y - 1, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x + 1, y - 1));
            }
        }
        // 7. bottom
        if (isInBound(x, y - 1)) {
            if (checkState(x, y - 1, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x, y - 1));
            }
        }
        // 8. left, bottom
        if (isInBound(x - 1, y - 1)) {
            if (checkState(x - 1, y - 1, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x - 1, y - 1));
            }
        }
        return uncoveredTiles;
    }

    private LinkedList<Location> getUncoveredTiles(int x, int y, int range) {
        LinkedList<Location> uncoveredTiles = getUncoveredTiles(x, y);

        if (range == 2) {
            if (checkBoundAndState(x - 2, y, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x - 2, y));
            }
            if (checkBoundAndState(x - 2, y + 1, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x - 2, y + 1));
            }
            if (checkBoundAndState(x - 2, y + 2, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x - 2, y + 2));
            }
            if (checkBoundAndState(x - 1, y + 2, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x - 1, y + 2));
            }
            if (checkBoundAndState(x, y + 2, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x, y + 2));
            }
            if (checkBoundAndState(x + 1, y + 2, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x + 1, y + 2));
            }
            if (checkBoundAndState(x + 2, y + 2, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x + 2, y + 2));
            }
            if (checkBoundAndState(x + 2, y + 1, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x + 2, y + 1));
            }
            if (checkBoundAndState(x + 2, y, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x + 2, y));
            }
            if (checkBoundAndState(x + 2, y - 1, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x + 2, y - 1));
            }
            if (checkBoundAndState(x + 2, y - 2, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x + 2, y - 2));
            }
            if (checkBoundAndState(x + 1, y - 2, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x + 1, y - 2));
            }
            if (checkBoundAndState(x, y - 2, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x, y - 2));
            }
            if (checkBoundAndState(x - 1, y - 2, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x - 1, y - 2));
            }
            if (checkBoundAndState(x - 2, y - 2, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x - 2, y - 2));
            }
            if (checkBoundAndState(x - 2, y - 1, STATE.UNCOVERED)) {
                uncoveredTiles.add(new Location(x - 2, y - 1));
            }
        }
        return uncoveredTiles;
    }

    private LinkedList<Location> getBombTiles(int x, int y) {
        LinkedList<Location> bombTiles = new LinkedList<>();

        // 1. left
        if (isInBound(x - 1, y)) {
            if (checkState(x - 1, y, STATE.BOMB)) {
                bombTiles.add(new Location(x - 1, y));
            }
        }
        // 2. left, top
        if (isInBound(x - 1, y + 1)) {
            if (checkState(x - 1, y + 1, STATE.BOMB)) {
                bombTiles.add(new Location(x - 1, y + 1));
            }
        }
        // 3. top
        if (isInBound(x, y + 1)) {
            if (checkState(x, y + 1, STATE.BOMB)) {
                bombTiles.add(new Location(x, y + 1));
            }
        }
        // 4. right top
        if (isInBound(x + 1, y + 1)) {
            if (checkState(x + 1, y + 1, STATE.BOMB)) {
                bombTiles.add(new Location(x + 1, y + 1));
            }
        }
        // 5. right
        if (isInBound(x + 1, y)) {
            if (checkState(x + 1, y, STATE.BOMB)) {
                bombTiles.add(new Location(x + 1, y));
            }
        }
        // 6. right, bottom
        if (isInBound(x + 1, y - 1)) {
            if (checkState(x + 1, y - 1, STATE.BOMB)) {
                bombTiles.add(new Location(x + 1, y - 1));
            }
        }
        // 7. bottom
        if (isInBound(x, y - 1)) {
            if (checkState(x, y - 1, STATE.BOMB)) {
                bombTiles.add(new Location(x, y - 1));
            }
        }
        // 8. left, bottom
        if (isInBound(x - 1, y - 1)) {
            if (checkState(x - 1, y - 1, STATE.BOMB)) {
                bombTiles.add(new Location(x - 1, y - 1));
            }
        }
        return bombTiles;
    }

    private void changeNumber(int x, int y, int number) {
        this.board[x][y].number = number;
    }

    private boolean checkNumber(int x, int y, int number) {
        return this.board[x][y].number == number;
    }

    private int getNumber(int x, int y) {
        return this.board[x][y].number;
    }

    private int getReducedNumber(int x, int y) {
        return this.board[x][y].number - getBombTiles(x, y).size();
    }

    private void changeState(int x, int y, STATE state) {
        this.board[x][y].state = state;
    }

    private boolean checkState(int x, int y, STATE state) {
        return this.board[x][y].state == state;
    }

    private boolean checkState(int x, int y, STATE... states) {
        for (STATE state : states) {
            if (this.board[x][y].state == state) {
                return true;
            }
        }
        return false;
    }

    private boolean checkBoundAndState(int x, int y, STATE state) {
        return isInBound(x, y) && this.board[x][y].state == state;
    }

    private boolean checkBoundAndNumber(int x, int y, int number) {
        return isInBound(x, y) && this.board[x][y].number == number;
    }

    // ################### Helper Functions Go Here (optional) ##################

    // sam as World.isInBound
    private boolean isInBound(int x, int y) {
        return x > 0 && x <= this.rowDimension &&
                y > 0 && y <= this.colDimension;
    }

    private boolean isSubset(LinkedList<Location> subset, LinkedList<Location> superset) {
        for (Location loc : subset) {
            if (!superset.contains(loc)) {
                return false;
            }
        }
        return true;
    }

    private boolean isMutualSet(LinkedList<Location> set1, LinkedList<Location> set2) {
        for (Location loc : set1) {
            if (set2.contains(loc)) {
                return false;
            }
        }
        return true;
    }

    private class Tile {
        public STATE state;
        public int number;  // lets use -2 as default value, since -1 is bomb

        public Tile() {
            state = STATE.COVERED;
            number = -2;
        }
    }

    private int max(Set<Integer> collection) {
        int maxN = 0;
        for (int n : collection) {
            if (n > maxN) {
                maxN = n;
            }
        }
        return maxN;
    }

    private class Location {
        public int x;
        public int y;

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean equals(Location another) {
            return x == another.x &&
                    y == another.y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Location location = (Location) o;
            return x == location.x &&
                    y == location.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
