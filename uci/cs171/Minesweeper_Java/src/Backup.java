//package src;
//
//public class Backup {
//}

// 1.

// find a tile that has most bomb to place.
//        int chosenMax = 0;
//                int chosenX = -1;
//                int chosenY = -1;
//                // find a tile
//                for (int n = 8; n >= 5; n--) {
//                for (Pair<Integer, Integer> pos : this.choices.get(n)) {
//        int bombTileCount = getInfo(pos.getKey(), pos.getValue()).getValue().size();
//
//        if (n - bombTileCount > chosenMax) {
//        chosenMax = n - bombTileCount;
//        chosenX = pos.getKey();
//        chosenY = pos.getValue();
//        }
//        }
//        }

////         check around, start from left, left top, top, ...
//        if (checkBoundAndState(chosenX - 1, chosenY, STATE.COVERED)) {
//                changeState(chosenX - 1, chosenY, STATE.BOMB);
//                this.totalMines--;
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX - 1, chosenY));
//                } else if (checkBoundAndState(chosenX - 1, chosenY + 1, STATE.COVERED)) {
//                changeState(chosenX - 1, chosenY + 1, STATE.BOMB);
//                this.totalMines--;
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX - 1, chosenY + 1));
//                } else
//                if (checkBoundAndState(chosenX, chosenY + 1, STATE.COVERED)) {
//                changeState(chosenX, chosenY + 1, STATE.BOMB);
//                this.totalMines--;
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX, chosenY + 1));
//                } else if (checkBoundAndState(chosenX + 1, chosenY + 1, STATE.COVERED)) {
//                changeState(chosenX + 1, chosenY + 1, STATE.BOMB);
//                this.totalMines--;
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX + 1, chosenY + 1));
//                } else if (checkBoundAndState(chosenX + 1, chosenY, STATE.COVERED)) {
//                changeState(chosenX + 1, chosenY, STATE.BOMB);
//                this.totalMines--;
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX + 1, chosenY));
//                } else if (checkBoundAndState(chosenX + 1, chosenY - 1, STATE.COVERED)) {
//                changeState(chosenX + 1, chosenY - 1, STATE.BOMB);
//                this.totalMines--;
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX + 1, chosenY - 1));
//                } else if (checkBoundAndState(chosenX, chosenY - 1, STATE.COVERED)) {
//                changeState(chosenX, chosenY - 1, STATE.BOMB);
//                this.totalMines--;
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX, chosenY - 1));
//                } else if (checkBoundAndState(chosenX - 1, chosenY - 1, STATE.COVERED)) {
//                changeState(chosenX - 1, chosenY - 1, STATE.BOMB);
//                this.totalMines--;
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX - 1, chosenY - 1));
//                }
//                if (readyQueue.size() > 0) {
//                Action action = readyQueue.removeFirst();
//                this.nextX = action.x;
//                this.nextY = action.y;
//                return action;
//                }

// 2.
// 1-2-1 pattern
// left to right
//        for (int x = 1; x < this.rowDimension - 1; x++) {
//            for (int y = 1; y <= this.colDimension; y++) {
//                if (checkNumber(x, y, 1) && checkNumber(x + 1, y, 2)
//                        && checkNumber(x + 2, y, 1)) {
//                    Pair<LinkedList<Pair<Integer, Integer>>, LinkedList<Pair<Integer, Integer>>> firstInfo = getInfo(x, y);
//                    Pair<LinkedList<Pair<Integer, Integer>>, LinkedList<Pair<Integer, Integer>>> secondInfo = getInfo(x + 1, y);
//                    Pair<LinkedList<Pair<Integer, Integer>>, LinkedList<Pair<Integer, Integer>>> thirdInfo = getInfo(x + 2, y);
//
//                    if (firstInfo.getKey().size() == 3
//                            && firstInfo.getKey().size() == secondInfo.getKey().size()
//                            && secondInfo.getKey().size() == thirdInfo.getKey().size()) {
//                        // check above
//                        if (checkBoundAndState(x, y + 1, STATE.COVERED)
//                                && checkBoundAndState(x + 1, y + 1, STATE.COVERED)
//                                && checkBoundAndState(x + 2 , y + 1, STATE.COVERED)) {
//                            changeState(x, y + 1, STATE.BOMB);
//                            readyQueue.addLast(new Action(ACTION.FLAG, x, y + 1));
//
//                            changeState(x + 2, y + 1, STATE.BOMB);
//                            readyQueue.addLast(new Action(ACTION.FLAG, x + 2, y + 1));
//                        }
//
//                        // check below
//                        if (checkBoundAndState(x, y - 1, STATE.COVERED)
//                                && checkBoundAndState(x - 1, y + 1, STATE.COVERED)
//                                && checkBoundAndState(x - 2 , y + 1, STATE.COVERED)) {
//                            changeState(x, y - 1, STATE.BOMB);
//                            readyQueue.addLast(new Action(ACTION.FLAG, x, y - 1));
//
//                            changeState(x - 2, y - 1, STATE.BOMB);
//                            readyQueue.addLast(new Action(ACTION.FLAG, x - 2, y - 1));
//                        }
//                    }
//                }
//            }
//        }



// 3.
// * 3
// *
// * 3

//        // horizontal - top
//        for (int x = 1; x <= this.rowDimension - 2; x++) {
//            for (int y = 1; y <= this.colDimension; y++) {
//                if (checkState(x, y, STATE.BOMB)
//                        && checkState(x + 1, y, STATE.BOMB)
//                        && checkState(x + 2, y, STATE.BOMB)) {
//                    if (checkBoundAndState(x, y + 1, STATE.UNCOVERED)
//                            && checkBoundAndState(x + 1, y + 1, STATE.COVERED)
//                            && checkBoundAndState(x + 2, y + 1, STATE.UNCOVERED)
//                            && checkBoundAndState(x, y - 1, STATE.UNCOVERED)
//                            && checkBoundAndState(x + 1, y - 1, STATE.UNCOVERED)
//                            && checkBoundAndState(x + 2, y - 1, STATE.UNCOVERED)) {
//                        changeState(x + 1, y + 1, STATE.UNCOVERED);
//                        this.nextX = x + 1;
//                        this.nextY = y + 1;
//                        return new Action(ACTION.UNCOVER, x + 1, y + 1);
//                    }
//                }
//            }
//        }
//        // horizontal - bottom
//        for (int x = 1; x <= this.rowDimension - 2; x++) {
//            for (int y = 1; y <= this.colDimension; y++) {
//                if (checkState(x, y, STATE.BOMB)
//                        && checkState(x + 1, y, STATE.BOMB)
//                        && checkState(x + 2, y, STATE.BOMB)) {
//                    // check bottom
//                    if (checkBoundAndState(x, y - 1, STATE.UNCOVERED)
//                            && checkBoundAndState(x + 1, y - 1, STATE.COVERED)
//                            && checkBoundAndState(x + 2, y - 1, STATE.UNCOVERED)
//                            && checkBoundAndState(x, y + 1, STATE.UNCOVERED)
//                            && checkBoundAndState(x + 1, y + 1, STATE.UNCOVERED)
//                            && checkBoundAndState(x + 2, y + 1, STATE.UNCOVERED)) {
//                        changeState(x + 1, y - 1, STATE.UNCOVERED);
//                        this.nextX = x + 1;
//                        this.nextY = y - 1;
//                        return new Action(ACTION.UNCOVER, x + 1, y - 1);
//                    }
//                }
//            }
//        }
//        // vertical - left
//        for (int x = 1; x <= this.rowDimension; x++) {
//            for (int y = 1; y <= this.colDimension - 2; y++) {
//                if (checkState(x, y, STATE.BOMB)
//                        && checkState(x, y + 1, STATE.BOMB)
//                        && checkState(x, y + 2, STATE.BOMB)) {
//                    if (checkBoundAndState(x - 1, y + 1, STATE.COVERED)
//                            && checkBoundAndState(x + 1, y, STATE.UNCOVERED)
//                            && checkBoundAndState(x + 1, y + 1, STATE.UNCOVERED)
//                            && checkBoundAndState(x + 1, y + 2, STATE.UNCOVERED)) {
//                        changeState(x - 1, y + 1, STATE.UNCOVERED);
//                        this.nextX = x - 1;
//                        this.nextY = y + 1;
//                        return new Action(ACTION.UNCOVER, x - 1, y + 1);
//                    }
//                }
//            }
//        }
//        // vertical - right
//        for (int x = 1; x <= this.rowDimension; x++) {
//            for (int y = 1; y <= this.colDimension - 2; y++) {
//                if (checkState(x, y, STATE.BOMB)
//                        && checkState(x, y + 1, STATE.BOMB)
//                        && checkState(x, y + 2, STATE.BOMB)) {
//                    if (checkBoundAndState(x + 1, y + 1, STATE.COVERED)
//                            && checkBoundAndState(x - 1, y, STATE.UNCOVERED)
//                            && checkBoundAndState(x - 1, y + 1, STATE.UNCOVERED)
//                            && checkBoundAndState(x - 1, y + 2, STATE.UNCOVERED)) {
//                        changeState(x + 1, y + 1, STATE.UNCOVERED);
//                        this.nextX = x + 1;
//                        this.nextY = y + 1;
//                        return new Action(ACTION.UNCOVER, x + 1, y + 1);
//                    }
//                }
//            }
//        }



// 4. pick a reduced tile with most adjacent tiles.
//
//        int maxAvailableBombs = 0;
//        int chosenX = 0;
//        int chosenY = 0;
//        for (int n = 7; n >= 4; n--) {
//            for (Pair<Integer, Integer> pos : this.choices.get(n)) {
//                int x = pos.getKey();
//                int y = pos.getValue();
//                Pair<LinkedList<Pair<Integer, Integer>>, LinkedList<Pair<Integer, Integer>>> info = getInfo(x, y);
//
//                int availableBombs = n - info.getValue().size();
//                if (availableBombs >= 3 && availableBombs > maxAvailableBombs) {
//                    maxAvailableBombs = availableBombs;
//                    chosenX = x;
//                    chosenY = y;
//                }
//            }
//        }

//        if (isInBound(chosenX, chosenY)) {
//            if (checkBoundAndState(chosenX - 1, chosenY - 1, STATE.COVERED)) {
//                changeState(chosenX - 1, chosenY - 1, STATE.BOMB);
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX - 1, chosenY - 1));
//            } else if (checkBoundAndState(chosenX, chosenY - 1, STATE.COVERED)) {
//                changeState(chosenX, chosenY - 1, STATE.BOMB);
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX, chosenY - 1));
//            } else if (checkBoundAndState(chosenX + 1, chosenY - 1, STATE.COVERED)) {
//                changeState(chosenX + 1, chosenY - 1, STATE.BOMB);
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX + 1, chosenY - 1));
//            } else if (checkBoundAndState(chosenX + 1, chosenY, STATE.COVERED)) {
//                changeState(chosenX + 1, chosenY, STATE.BOMB);
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX + 1, chosenY));
//            } else if (checkBoundAndState(chosenX + 1, chosenY + 1, STATE.COVERED)) {
//                changeState(chosenX + 1, chosenY + 1, STATE.BOMB);
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX + 1, chosenY + 1));
//            } else if (checkBoundAndState(chosenX, chosenY + 1, STATE.COVERED)) {
//                changeState(chosenX, chosenY + 1, STATE.BOMB);
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX, chosenY + 1));
//            } else if (checkBoundAndState(chosenX - 1, chosenY + 1, STATE.COVERED)) {
//                changeState(chosenX - 1, chosenY + 1, STATE.BOMB);
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX - 1, chosenY + 1));
//            } else if (checkBoundAndState(chosenX - 1, chosenY, STATE.COVERED)) {
//                changeState(chosenX - 1, chosenY, STATE.BOMB);
//                readyQueue.addLast(new Action(ACTION.FLAG, chosenX - 1, chosenY));
//            }
//        }
//
//        if (readyQueue.size() > 0) {
//            Action action = readyQueue.removeFirst();
//            this.nextX = action.x;
//            this.nextY = action.y;
//            return action;
//        }


/**

 // region rule 4.1: special pattern

 // horizontal - top
 for (int x = 1; x <= this.rowDimension - 2; x++) {
 for (int y = 1; y <= this.colDimension; y++) {
 if (checkState(x, y, STATE.BOMB)
 && checkState(x + 1, y, STATE.BOMB)
 && checkState(x + 2, y, STATE.BOMB)) {
 if ((checkBoundAndState(x, y + 1, STATE.UNCOVERED) && getNumber(x, y + 1) >= 4)
 && checkBoundAndState(x + 1, y + 1, STATE.COVERED)
 && (checkBoundAndState(x + 2, y + 1, STATE.UNCOVERED) && getNumber(x + 2, y + 1) >= 4)
 && checkBoundAndState(x, y - 1, STATE.UNCOVERED)
 && checkBoundAndState(x + 1, y - 1, STATE.UNCOVERED)
 && checkBoundAndState(x + 2, y - 1, STATE.UNCOVERED)) {
 changeState(x + 1, y + 1, STATE.UNCOVERED);
 this.nextX = x + 1;
 this.nextY = y + 1;
 totalTiles--;
 return new Action(ACTION.UNCOVER, x + 1, y + 1);
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
 if ((checkBoundAndState(x, y - 1, STATE.UNCOVERED) && getNumber(x, y - 1) >= 4)
 && checkBoundAndState(x + 1, y - 1, STATE.COVERED)
 && (checkBoundAndState(x + 2, y - 1, STATE.UNCOVERED) && getNumber(x + 2, y - 1) >= 4)
 && checkBoundAndState(x, y + 1, STATE.UNCOVERED)
 && checkBoundAndState(x + 1, y + 1, STATE.UNCOVERED)
 && checkBoundAndState(x + 2, y + 1, STATE.UNCOVERED)) {
 changeState(x + 1, y - 1, STATE.UNCOVERED);
 this.nextX = x + 1;
 this.nextY = y - 1;
 totalTiles--;
 return new Action(ACTION.UNCOVER, x + 1, y - 1);
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
 if ((checkBoundAndState(x - 1, y, STATE.UNCOVERED) && getNumber(x - 1, y) >= 4)
 && checkBoundAndState(x - 1, y + 1, STATE.COVERED)
 && (checkBoundAndState(x - 1, y + 2, STATE.UNCOVERED) && getNumber(x - 1, y + 2) >= 4)
 && checkBoundAndState(x + 1, y, STATE.UNCOVERED)
 && checkBoundAndState(x + 1, y + 1, STATE.UNCOVERED)
 && checkBoundAndState(x + 1, y + 2, STATE.UNCOVERED)) {
 changeState(x - 1, y + 1, STATE.UNCOVERED);
 this.nextX = x - 1;
 this.nextY = y + 1;
 totalTiles--;
 return new Action(ACTION.UNCOVER, x - 1, y + 1);
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
 if ((checkBoundAndState(x + 1, y, STATE.UNCOVERED) && getNumber(x + 1, y) >= 4)
 && checkBoundAndState(x + 1, y + 1, STATE.COVERED)
 && (checkBoundAndState(x + 1, y + 2, STATE.UNCOVERED) && getNumber(x + 1, y + 2) >= 4)
 && checkBoundAndState(x - 1, y, STATE.UNCOVERED)
 && checkBoundAndState(x - 1, y + 1, STATE.UNCOVERED)
 && checkBoundAndState(x - 1, y + 2, STATE.UNCOVERED)) {
 changeState(x + 1, y + 1, STATE.UNCOVERED);
 this.nextX = x + 1;
 this.nextY = y + 1;
 totalTiles--;
 return new Action(ACTION.UNCOVER, x + 1, y + 1);
 }
 }
 }
 }

 // endregion

 // region rule 4.2: special pattern (3 bombs)

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

 // region rule 4.3: special pattern (2 4 4)
 for (int x = 1; x < this.rowDimension - 2; x++) {
 for (int y = 1; y < this.colDimension; y++) {
 if (readyQueue.size() > 0) {
 break;
 }
 if ((checkState(x, y, STATE.UNCOVERED) && checkNumber(x, y, 2))
 && (checkState(x + 1, y, STATE.UNCOVERED) && checkNumber(x + 1, y, 4))
 && (checkState(x + 2, y, STATE.UNCOVERED) && checkNumber(x + 2, y, 4))) {
 // mark bomb
 if (checkBoundAndState(x + 2, y - 1, STATE.COVERED)) {
 changeState(x + 2, y - 1, STATE.BOMB);
 readyQueue.add(new Action(ACTION.FLAG, x + 2, y - 1));
 }
 if (checkBoundAndState(x + 2, y + 1, STATE.COVERED)) {
 changeState(x + 2, y + 1, STATE.BOMB);
 readyQueue.add(new Action(ACTION.FLAG, x + 2, y + 1));
 }
 // mark uncovered
 if (checkBoundAndState(x - 1, y - 1, STATE.COVERED)) {
 changeState(x - 1, y - 1, STATE.UNCOVERED);
 readyQueue.add(new Action(ACTION.UNCOVER, x - 1, y - 1));
 }
 if (checkBoundAndState(x - 1, y, STATE.COVERED)) {
 changeState(x - 1, y, STATE.UNCOVERED);
 readyQueue.add(new Action(ACTION.UNCOVER, x - 1, y));
 }
 if (checkBoundAndState(x - 1, y + 1, STATE.COVERED)) {
 changeState(x - 1, y + 1, STATE.UNCOVERED);
 readyQueue.add(new Action(ACTION.UNCOVER, x - 1, y + 1));
 }
 }
 }
 }

 //endregion

 // region rule 4.4 special pattern (4 \ 1 2 3) (never used)
 for (int x = 1; x < this.rowDimension - 2; x++) {
 for (int y = 1; y < this.colDimension; y++) {
 if (readyQueue.size() > 0) {
 break;
 }
 if ((checkState(x, y, STATE.UNCOVERED) && checkNumber(x, y, 1))
 && (checkState(x + 1, y, STATE.UNCOVERED) && checkNumber(x + 1, y, 2))
 && (checkState(x + 2, y, STATE.UNCOVERED) && checkNumber(x + 2, y, 3))) {

 if (checkBoundAndState(x, y + 1, STATE.UNCOVERED) && checkNumber(x, y + 1, 4)) {
 // mark bomb
 if (checkBoundAndState(x - 1, y + 2, STATE.COVERED)) {
 changeState(x - 1, y + 2, STATE.BOMB);
 readyQueue.add(new Action(ACTION.FLAG, x - 1, y + 2));
 }
 if (checkBoundAndState(x, y + 2, STATE.COVERED)) {
 changeState(x, y + 2, STATE.BOMB);
 readyQueue.add(new Action(ACTION.FLAG, x, y + 2));
 }
 if (checkBoundAndState(x + 1, y + 2, STATE.COVERED)) {
 changeState(x + 1, y + 2, STATE.BOMB);
 readyQueue.add(new Action(ACTION.FLAG, x + 1, y + 2));
 }
 // mark uncovered
 if (checkBoundAndState(x - 1, y - 1, STATE.COVERED)) {
 changeState(x - 1, y - 1, STATE.UNCOVERED);
 readyQueue.add(new Action(ACTION.UNCOVER, x - 1, y - 1));
 }
 if (checkBoundAndState(x, y - 1, STATE.COVERED)) {
 changeState(x, y - 1, STATE.UNCOVERED);
 readyQueue.add(new Action(ACTION.UNCOVER, x, y - 1));
 }
 if (checkBoundAndState(x + 1, y - 1, STATE.COVERED)) {
 changeState(x + 1, y - 1, STATE.UNCOVERED);
 readyQueue.add(new Action(ACTION.UNCOVER, x + 1, y - 1));
 }
 } else if (checkBoundAndState(x, y - 1, STATE.UNCOVERED) && checkNumber(x, y - 1, 4)) {
 // mark bomb
 if (checkBoundAndState(x - 1, y - 2, STATE.COVERED)) {
 changeState(x - 1, y - 2, STATE.BOMB);
 readyQueue.add(new Action(ACTION.FLAG, x - 1, y - 2));
 }
 if (checkBoundAndState(x, y - 2, STATE.COVERED)) {
 changeState(x, y - 2, STATE.BOMB);
 readyQueue.add(new Action(ACTION.FLAG, x, y - 2));
 }
 if (checkBoundAndState(x + 1, y - 2, STATE.COVERED)) {
 changeState(x + 1, y - 2, STATE.BOMB);
 readyQueue.add(new Action(ACTION.FLAG, x + 1, y - 2));
 }
 // mark uncovered
 if (checkBoundAndState(x - 1, y + 1, STATE.COVERED)) {
 changeState(x - 1, y + 1, STATE.UNCOVERED);
 readyQueue.add(new Action(ACTION.UNCOVER, x - 1, y + 1));
 }
 if (checkBoundAndState(x, y + 1, STATE.COVERED)) {
 changeState(x, y + 1, STATE.UNCOVERED);
 readyQueue.add(new Action(ACTION.UNCOVER, x, y + 1));
 }
 if (checkBoundAndState(x + 1, y + 1, STATE.COVERED)) {
 changeState(x + 1, y + 1, STATE.UNCOVERED);
 readyQueue.add(new Action(ACTION.UNCOVER, x + 1, y + 1));
 }
 }
 }
 }
 }
 // endregion


 */


//                    if (checkBoundAndState(x - 1, y, STATE.UNCOVERED)
//                            && checkBoundAndState(x - 1, y + 1, STATE.UNCOVERED)
//                            && checkBoundAndState(x - 1, y + 2, STATE.UNCOVERED)) {
//                        if ((checkBoundAndState(x + 1, y, STATE.UNCOVERED) && getNumber(x + 1, y) >= 4)
//                                && checkBoundAndState(x + 1, y + 1, STATE.COVERED)
//                                && (checkBoundAndState(x + 1, y + 2, STATE.UNCOVERED) && getNumber(x + 1, y + 2) >= 4)) {
//                            changeState(x + 1, y + 1, STATE.UNCOVERED);
//                            this.nextX = x + 1;
//                            this.nextY = y + 1;
//                            totalTiles--;
//                            return new Action(ACTION.UNCOVER, x + 1, y + 1);
//                        }
//                        if ((checkBoundAndState(x + 1, y, STATE.UNCOVERED) && getNumber(x + 1, y) == 3)
//                                && checkBoundAndState(x + 1, y + 1, STATE.COVERED)
//                                && (checkBoundAndState(x + 1, y + 2, STATE.UNCOVERED) && getNumber(x + 1, y + 2) == 3)) {
//                            changeState(x + 1, y + 1, STATE.BOMB);
//                            this.nextX = x + 1;
//                            this.nextY = y + 1;
//                            totalTiles--;
//                            totalMines--;
//                            return new Action(ACTION.FLAG, x + 1, y + 1);
//                        }
//                    }


// down U-shape
//        for (int x = 1; x < this.rowDimension - 2; x++) {
//            if (readyQueue.size() > 0) {
//                break;
//            }
//            for (int y = 1; y < this.colDimension - 2; y++) {
//                if (readyQueue.size() > 0) {
//                    break;
//                }
//                if (checkState(x + 1, y + 1, STATE.COVERED) && checkState(x + 1, y, STATE.COVERED)
//                        && checkState(x, y, STATE.UNCOVERED) && getReducedNumber(x, y) == 1
//                        && checkState(x, y + 1, STATE.UNCOVERED) && getReducedNumber(x, y + 1) == 1
//                        && checkState(x, y + 2, STATE.UNCOVERED) && getReducedNumber(x, y + 2) == 1
//                        && checkState(x + 1, y + 2, STATE.UNCOVERED) && getReducedNumber(x + 1, y + 2) == 1
//                        && checkState(x + 2, y + 2, STATE.UNCOVERED) && getReducedNumber(x + 2, y + 2) == 1
//                        && checkState(x + 2, y + 1, STATE.UNCOVERED) && getReducedNumber(x + 2, y + 1) == 1
//                        && checkState(x + 2, y, STATE.UNCOVERED) && getReducedNumber(x + 2, y) == 1) {
//                    changeState(x + 1, y + 1, STATE.UNCOVERED);
//                    readyQueue.add(new Action(ACTION.UNCOVER, x + 1, y + 1));
//                }
//            }
//        }

// region rule 4.4: special pattern (4 b 3 b 4)

//        for (int x = 1; x < this.rowDimension - 3; x++) {
//        if (readyQueue.size() > 0) {
//        break;
//        }
//        for (int y = 1; y < this.colDimension - 1; y++) {
//        if (readyQueue.size() > 0) {
//        break;
//        }
//
//        if (checkState(x, y, STATE.UNCOVERED) && checkNumber(x, y, 4)
//        && checkState(x + 1, y, STATE.BOMB)
//        && checkState(x + 2, y, STATE.UNCOVERED) && checkNumber(x + 2, y, 3)
//        && checkState(x + 3, y, STATE.BOMB)
//        && checkState(x + 4, y, STATE.UNCOVERED) && checkNumber(x + 4, y, 4)) {
//        if (checkState(x + 2, y + 1, STATE.COVERED)) {
//        changeState(x + 2, y + 1, STATE.UNCOVERED);
//        readyQueue.add(new Action(ACTION.UNCOVER, x + 2, y + 1));
//        } else if (isInBound(x, y - 1) && checkState(x + 2, y - 1, STATE.COVERED)) {
//        changeState(x + 2, y - 1, STATE.UNCOVERED);
//        readyQueue.add(new Action(ACTION.UNCOVER, x + 2, y - 1));
//        }
//        }
//        }
//        }
//
//        if (readyQueue.size() > 0) {
//        Action action = readyQueue.removeFirst();
//        if (action.action == ACTION.FLAG) {
//        totalMines--;
//        }
//        totalTiles--;
//        this.nextX = action.x;
//        this.nextY = action.y;
//        return action;
//        }

// endregion