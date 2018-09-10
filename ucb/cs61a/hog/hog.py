"""CS 61A Presents The Game of Hog."""

from dice import four_sided, six_sided, make_test_dice
from ucb import main, trace, log_current_line, interact

GOAL_SCORE = 100  # The goal of Hog is to score 100 points.

######################
# Phase 1: Simulator #
######################

def roll_dice(num_rolls, dice=six_sided):
    """Simulate rolling the DICE exactly NUM_ROLLS > 0 times. Return the sum of
    the outcomes unless any of the outcomes is 1. In that case, return 1.

    num_rolls:  The number of dice rolls that will be made.
    dice:       A function that simulates a single dice roll outcome.
    """
    # These assert statements ensure that num_rolls is a positive integer.
    assert type(num_rolls) == int, 'num_rolls must be an integer.'
    assert num_rolls > 0, 'Must roll at least once.'
    # BEGIN PROBLEM 1

    """ Caution: have to let roll exact num_rolls times,
        do not return 1 immediately when rolled score 1
    """
    round_score, ones_count = 0, 0

    # compute
    while num_rolls >= 1:
        score = dice()
        if score == 1:
            ones_count += 1
        round_score, num_rolls = round_score + score, num_rolls - 1

    if ones_count > 0:
        return 1
    return round_score
    # END PROBLEM 1


def free_bacon(opponent_score):
    """Return the points scored from rolling 0 dice (Free Bacon)."""
    # BEGIN PROBLEM 2
    return 1 + max(opponent_score % 10, opponent_score // 10)
    # END PROBLEM 2


# Write your prime functions here!
# https://en.wikipedia.org/wiki/Primality_test -- simple primality test
def is_prime(n):
    if n <= 1:
        return False
    elif n <= 3:
        return True
    elif (n % 2 == 0) or (n % 3 == 0):
        return False

    i = 5
    while i * i <= n:
        if (n % i == 0) or (n % (i + 2) == 0):
            return False
        i += 6
    return True

def find_next_prime_number(n):
    n += 1
    while not is_prime(n):
        n += 1
    return n

def take_turn(num_rolls, opponent_score, dice=six_sided):
    """Simulate a turn rolling NUM_ROLLS dice, which may be 0 (Free Bacon).
    Return the points scored for the turn by the current player. Also
    implements the Hogtimus Prime rule.

    num_rolls:       The number of dice rolls that will be made.
    opponent_score:  The total score of the opponent.
    dice:            A function that simulates a single dice roll outcome.
    """
    # Leave these assert statements here; they help check for errors.
    assert type(num_rolls) == int, 'num_rolls must be an integer.'
    assert num_rolls >= 0, 'Cannot roll a negative number of dice in take_turn.'
    assert num_rolls <= 10, 'Cannot roll more than 10 dice.'
    assert opponent_score < 100, 'The game should be over.'
    # BEGIN PROBLEM 2

    round_score = 0

    if num_rolls is 0:
        # Free Bacon
        round_score = free_bacon(opponent_score)
    else:
        # regular turn
        round_score = roll_dice(num_rolls, dice)

    # Hogtimus Prime
    if is_prime(round_score):
        round_score = find_next_prime_number(round_score)

    return round_score
    # END PROBLEM 2


def select_dice(dice_swapped):
    """Return a six-sided dice unless four-sided dice have been swapped in due
    to Perfect Piggy. DICE_SWAPPED is True if and only if four-sided dice are in
    play.
    """
    # BEGIN PROBLEM 3
    if dice_swapped:
        return four_sided
    return six_sided
    # END PROBLEM 3


# Write additional helper functions here!

def is_perfect_piggy(turn_score):
    """Returns whether the Perfect Piggy dice-swapping rule should occur."""
    # BEGIN PROBLEM 4

    if turn_score == 1:
        return False
    square, cube = pow(turn_score, 1./2.), pow(turn_score, 1./3.)
    if int(square) == square or int(cube) == cube:
        return True
    else:
        return False
    # END PROBLEM 4


def is_swap(score0, score1):
    """Returns whether one of the scores is double the other."""
    # BEGIN PROBLEM 5
    return score0 * 2 == score1 or score1 * 2 == score0
    # END PROBLEM 5

# Swine Swap
def swine_swap(score0, score1):
    if (score0 == 0) or (score1 == 0):
        return score0, score1
    elif is_swap(score0, score1):
        return score1, score0
    return score0, score1


def other(player):
    """Return the other player, for a player PLAYER numbered 0 or 1.

    >>> other(0)
    1
    >>> other(1)
    0
    """
    return 1 - player


def play(strategy0, strategy1, score0=0, score1=0, goal=GOAL_SCORE):
    """Simulate a game and return the final scores of both players, with Player
    0's score first, and Player 1's score second.

    A strategy is a function that takes two total scores as arguments (the
    current player's score, and the opponent's score), and returns a number of
    dice that the current player will roll this turn.

    strategy0:  The strategy function for Player 0, who plays first
    strategy1:  The strategy function for Player 1, who plays second
    score0:     The starting score for Player 0
    score1:     The starting score for Player 1
    """
    player = 0  # Which player is about to take a turn, 0 (first) or 1 (second)
    dice_swapped = False # Whether 4-sided dice have been swapped for 6-sided
    # BEGIN PROBLEM 6
    # print("score0:" + score0.__str__(), "score1:" + score1.__str__())
    while score0 < goal and score1 < goal:
        # initialize new roll
        round_score, num_rolls = 0, 0
        dice = select_dice(dice_swapped)

        if player == 0:
            opponent_score, num_rolls = score1, strategy0(score0, score1)

            # take turn
            round_score = take_turn(num_rolls, opponent_score, dice)
            score0 += round_score

            # Perfect Piggy
            if is_perfect_piggy(round_score):
                dice_swapped = not dice_swapped

        if player == 1:
            opponent_score, num_rolls = score0, strategy1(score1, score0)

            # take turn
            round_score = take_turn(num_rolls, opponent_score, dice)
            score1 += round_score

            # Perfect Piggy
            if is_perfect_piggy(round_score):
                dice_swapped = not dice_swapped

        # Swine Swap
        score0, score1 = swine_swap(score0, score1)

        # switch player
        player = other(player)
        # print("score0:" + score0.__str__(), "score1:" + score1.__str__())
    # END PROBLEM 6

    return score0, score1


#######################
# Phase 2: Strategies #
#######################

def always_roll(n):
    """Return a strategy that always rolls N dice.

    A strategy is a function that takes two total scores as arguments (the
    current player's score, and the opponent's score), and returns a number of
    dice that the current player will roll this turn.

    >>> strategy = always_roll(5)
    >>> strategy(0, 0)
    5
    >>> strategy(99, 99)
    5
    """
    def strategy(score, opponent_score):
        return n
    return strategy


def check_strategy_roll(score, opponent_score, num_rolls):
    """Raises an error with a helpful message if NUM_ROLLS is an invalid
    strategy output. All strategy outputs must be integers from -1 to 10.

    >>> check_strategy_roll(10, 20, num_rolls=100)
    Traceback (most recent call last):
     ...
    AssertionError: strategy(10, 20) returned 100 (invalid number of rolls)

    >>> check_strategy_roll(20, 10, num_rolls=0.1)
    Traceback (most recent call last):
     ...
    AssertionError: strategy(20, 10) returned 0.1 (not an integer)

    >>> check_strategy_roll(0, 0, num_rolls=None)
    Traceback (most recent call last):
     ...
    AssertionError: strategy(0, 0) returned None (not an integer)
    """
    msg = 'strategy({}, {}) returned {}'.format(
        score, opponent_score, num_rolls)
    assert type(num_rolls) == int, msg + ' (not an integer)'
    assert 0 <= num_rolls <= 10, msg + ' (invalid number of rolls)'


def check_strategy(strategy, goal=GOAL_SCORE):
    """Checks the strategy with all valid inputs and verifies that the strategy
    returns a valid input. Use `check_strategy_roll` to raise an error with a
    helpful message if the strategy returns an invalid output.

    >>> def fail_15_20(score, opponent_score):
    ...     if score != 15 or opponent_score != 20:
    ...         return 5
    ...
    >>> check_strategy(fail_15_20)
    Traceback (most recent call last):
     ...
    AssertionError: strategy(15, 20) returned None (not an integer)
    >>> def fail_102_115(score, opponent_score):
    ...     if score == 102 and opponent_score == 115:
    ...         return 100
    ...     return 5
    ...
    >>> check_strategy(fail_102_115)
    >>> fail_102_115 == check_strategy(fail_102_115, 120)
    Traceback (most recent call last):
     ...
    AssertionError: strategy(102, 115) returned 100 (invalid number of rolls)
    """
    # BEGIN PROBLEM 7

    # It calls the strategy with all valid inputs
    for score in range(0, goal + 1):
        for opponent_score in range(0, goal + 1):
            num_rolls = strategy(score, opponent_score)
            check_strategy_roll(score, opponent_score, num_rolls)
            
    # END PROBLEM 7


# Experiments

def make_averaged(fn, num_samples=10000):
    """Return a function that returns the average_value of FN when called.

    To implement this function, you will have to use *args syntax, a new Python
    feature introduced in this project.  See the project description.

    >>> dice = make_test_dice(4, 2, 5, 1)
    >>> averaged_dice = make_averaged(dice, 1000)
    >>> averaged_dice()
    3.0
    """
    # BEGIN PROBLEM 8
    def average(*args):
        total, iterator = 0, 0
        while iterator < num_samples:
            total, iterator = total + fn(*args), iterator + 1
        return total / num_samples
    return average
    # END PROBLEM 8


def max_scoring_num_rolls(dice=six_sided, num_samples=1000):
    """Return the number of dice (1 to 10) that gives the highest average turn
    score by calling roll_dice with the provided DICE over NUM_SAMPLES times.
    Assume that the dice always return positive outcomes.

    >>> dice = make_test_dice(1, 6)
    >>> max_scoring_num_rolls(dice)
    1
    """
    # BEGIN PROBLEM 9
    highest_score, highest_num_rolls, num_rolls = 0, 0, 1

    average = make_averaged(roll_dice, num_samples=1000)
    while num_rolls <= 10:

        score = average(num_rolls, dice)
        if score > highest_score:
            highest_score, highest_num_rolls = score, num_rolls
        num_rolls += 1
        # print('score:', score, 'highest_num_rolls:', highest_num_rolls, 'highest_num_rolls', highest_num_rolls)
    return highest_num_rolls
    # END PROBLEM 9


def winner(strategy0, strategy1):
    """Return 0 if strategy0 wins against strategy1, and 1 otherwise."""
    score0, score1 = play(strategy0, strategy1)
    if score0 > score1:
        return 0
    else:
        return 1


def average_win_rate(strategy, baseline=always_roll(4)):
    """Return the average win rate of STRATEGY against BASELINE. Averages the
    winrate when starting the game as player 0 and as player 1.
    """
    win_rate_as_player_0 = 1 - make_averaged(winner)(strategy, baseline)
    win_rate_as_player_1 = make_averaged(winner)(baseline, strategy)

    return (win_rate_as_player_0 + win_rate_as_player_1) / 2


def run_experiments():
    """Run a series of strategy experiments and report results."""
    if False:  # Change to False when done finding max_scoring_num_rolls
        six_sided_max = max_scoring_num_rolls(six_sided)
        print('Max scoring num rolls for six-sided dice:', six_sided_max)
        four_sided_max = max_scoring_num_rolls(four_sided)
        print('Max scoring num rolls for four-sided dice:', four_sided_max)

    if False:  # Change to True to test always_roll(8)
        print('always_roll(8) win rate:', average_win_rate(always_roll(8)))

    if False:  # Change to True to test bacon_strategy
        print('bacon_strategy win rate:', average_win_rate(bacon_strategy))

    if False:  # Change to True to test swap_strategy
        print('swap_strategy win rate:', average_win_rate(swap_strategy))

    if False:  # Change to True to test trap_strategy
        print('trap_strategy win rate:', average_win_rate(piggy_strategy))

    if True:  # Change to True to test final_strategy
        # for num_rolls in range(0, 11):
        #     print('final_strategy win rate:', average_win_rate(final_strategy, always_roll(num_rolls)), "with num rolls", num_rolls)
        for _ in range(5):
            print('final_strategy win rate:', average_win_rate(final_strategy))


# Strategies

def bacon_strategy(score, opponent_score, margin=8, num_rolls=4):
    """This strategy rolls 0 dice if that gives at least MARGIN points, and
    rolls NUM_ROLLS otherwise.
    """
    # BEGIN PROBLEM 10
    # round_score with Free Baron rule
    round_score = free_bacon_with_prime(opponent_score)

    if round_score >= margin:
        return 0
    return num_rolls

def free_bacon_with_prime(opponent_score):
    # round_score with Free Baron rule
    round_score = free_bacon(opponent_score)

    # check Hogtimus Prime
    if is_prime(round_score):
        round_score = find_next_prime_number(round_score)
    return round_score

    # END PROBLEM 10
check_strategy(bacon_strategy)


def swap_strategy(score, opponent_score, margin=8, num_rolls=4):
    """This strategy rolls 0 dice when it triggers a beneficial swap. It also
    rolls 0 dice if it gives at least MARGIN points. Otherwise, it rolls
    NUM_ROLLS.
    """
    # BEGIN PROBLEM 11

    # round_score with Free Baron rule
    round_score = free_bacon_with_prime(opponent_score)

    # beneficial swap
    if 2 * (score + round_score) == opponent_score:
        return 0
    # prevent swap
    elif (score + round_score) == 2 * opponent_score:
        return num_rolls
    # if both unsatisfy, let free bacon decide
    else:
        return bacon_strategy(score, opponent_score, margin, num_rolls)

    # END PROBLEM 11
check_strategy(swap_strategy)

def piggy_strategy(score, opponent_score, margin=8, num_rolls=4):
    """ This strategy rolls 0 dice when it triggers Perfect Piggy. It must satisfy
    score > opponent_scores
    """
    if score > opponent_score * 2:
        # round_score with Free Baron rule
        round_score = free_bacon_with_prime(opponent_score)

        if is_perfect_piggy(round_score):
            return 0
    return num_rolls

check_strategy(piggy_strategy)

def final_strategy(score, opponent_score):
    """Write a brief description of your final strategy.

    *** 1. since rolling 10 dice will give the largest probability of getting 1
            or the largest score if not getting 1 in 10 rolls, it will be reasonable
            to roll 10 times in order to perform beneficial swap or catch up the opponent
        2. If performing free bacon allow us to exceed the GOAL_SCORE, roll 0 times then will
            prevent us from getting score of 1
        3. when the average of score and opponent_score gets higher, following conditions will happen:
            i. score is close to opponent_score
                - free bacon score will always create larger benefit than rolling nurmally because
                    free barcon will return larger and larger score and it will prevent us from getting
                    score of 1 by rolling normally
            ii. score * 2 is close to opponent_score
                - consider swap_strategy
    """
    # BEGIN PROBLEM 12
    
    ##########################
    if is_swap(score + 1, opponent_score) and opponent_score > score:
        return 10

    ##########################
    round_score = free_bacon_with_prime(opponent_score)
    if score + round_score > GOAL_SCORE:
        return 0

    ##########################
    # force opponent to roll four_sided

    #########################
    margin = 8

    average_score = (score + opponent_score) // 2

    if average_score >= 90:
        margin = 1
    elif average_score >= 90:
        margin = 3
    elif average_score >= 60:
        margin = 5
    elif average_score >= 40:
        margin = 7
    ##########################


    return swap_strategy(score, opponent_score, margin, num_rolls=4)
    # END PROBLEM 12
check_strategy(final_strategy)



##########################
# Command Line Interface #
##########################

# NOTE: Functions in this section do not need to be changed. They use features
# of Python not yet covered in the course.

@main
def run(*args):
    """Read in the command-line argument and calls corresponding functions.

    This function uses Python syntax/techniques not yet covered in this course.
    """
    import argparse
    parser = argparse.ArgumentParser(description="Play Hog")
    parser.add_argument('--run_experiments', '-r', action='store_true',
                        help='Runs strategy experiments')

    args = parser.parse_args()

    if args.run_experiments:
        run_experiments()