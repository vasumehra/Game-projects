'''PlayerSkeletonA.py
The beginnings of an agent that might someday play Baroque Chess.

'''

import winTester
import BC_state_etc as BC
import time
import random

# Colors
BLACK = 0
WHITE = 1

# Directions of a pieces
# Pincer can only use first 4
REVERSE_DIRECTIONS = [1, 0, 3, 2, 7, 6, 5, 4]
DIRECTIONS = [(-1,0), (1,0), (0,-1), (0,1), (-1,-1), (-1,1), (1,-1), (1,1)]

# global variables representing the various types of pieces on the board
EMPTY = 0
BLACK_PINCER = 2
WHITE_PINCER = 3
BLACK_COORDINATOR = 4
WHITE_COORDINATOR = 5
BLACK_LEAPER = 6
WHITE_LEAPER = 7
BLACK_IMITATOR = 8
WHITE_IMITATOR = 9
BLACK_WITHDRAWER = 10
WHITE_WITHDRAWER = 11
BLACK_KING = 12
WHITE_KING = 13
BLACK_FREEZER = 14
WHITE_FREEZER = 15

TIME_LIMIT = 0
START = 0
MAX_TIME = 0.25

N_STATES_EXPANDED = 0
N_STATIC_EVALS = 0
N_CUTOFFS = 0



def nickname():
    return "Magnus"


def introduce():
    return "I'm Magnus Carlson, and I'm the best chess player ever!"


def prepare(player2Nickname):
    ''' Here the game master will give your agent the nickname of
    the opponent agent, in case your agent can use it in some of
    the dialog responses.  Other than that, this function can be
    used for initializing data structures, if needed.'''
    global N_STATIC_EVALS, N_STATES_EXPANDED, N_CUTOFFS
    N_STATIC_EVALS = 0
    N_STATES_EXPANDED = 0
    N_CUTOFFS = 0


def basicStaticEval(state):
    '''Use the simple method for state evaluation described in the spec.
    This is typically used in parameterized_minimax calls to verify
    that minimax and alpha-beta pruning work correctly.'''
    board = state.board
    value = 0
    if winTester.winTester(state) == "No win":
        for row in range(8):
            for col in range(8):
                piece = board[row][col]
                if piece != EMPTY and BC.who(piece) == WHITE:
                    if piece == WHITE_KING:
                        value += 100
                    elif piece == WHITE_PINCER:
                        value += 1
                    else:
                        value += 2
                if piece != EMPTY and BC.who(piece) == BLACK:
                    if piece == BLACK_KING:
                        value -= 100
                    elif piece == BLACK_PINCER:
                        value -= 1
                    else:
                        value -= 2
    return value

# Finds if there is an available spot
# in the given direction
def can_move(spot, direction):

    # Find the row and column of the new spot
    row = DIRECTIONS[direction][0] + spot[0]
    col = DIRECTIONS[direction][1] + spot[1]

    # returns the row and column if it is on the board
    if row >= 0 and row < 8 and col >= 0 and col < 8:
        return row, col
    else:
        return None

# Finds if the board has a legal move for
# for the given piece at the beginning and end
def legal_move(board, current, move, direction):

    # Initializes current piece
    piece = board[current[0]][current[1]]
    next_piece = board[move[0]][move[1]]
    turn = BC.who(piece)
    next_turn = 1 - turn

    # Gets all the space in between the current and move
    empty_spots, spots = inbetween(board, current, move, direction)

    # If the piece isn't frozen, find the legal moves
    if not frozen(board, current):
        if spots != []:

            if piece not in [BLACK_IMITATOR, WHITE_IMITATOR, BLACK_KING,
                               WHITE_KING, BLACK_LEAPER, WHITE_LEAPER]:
                if direction > 3 and piece in [BLACK_PINCER and WHITE_PINCER]:
                    return False
                else:
                    return empty_spots
             # if the piece is a leaper, returns whether the leaper is moving
                # across only empty spaces, or can legally capture an enemy piece
            elif piece == BLACK_LEAPER or piece == WHITE_LEAPER:
                can_capture, captured = leaper(board, current, move, spots, empty_spots)
                if empty_spots:
                    return True
                else:
                    return can_capture


            # legal moves for the king
            elif piece == BLACK_KING or piece == WHITE_KING:
                # ensures the length of the move is one, and the space
                # being moved into is either an empty space and not a suicidal
                # move
                if len(spots) != 1:
                    return False
                if spots[0] == EMPTY:
                    return True
                if BC.who(next_piece) == next_turn:
                    return True
                else:
                    return False

            else:
                opponent = WHITE_KING if next_turn else BLACK_KING
                can_capture, captured = leaper(board, current, move, spots, empty_spots)
                return empty_spots or spots[0] == opponent and \
                       len(spots) == 1 or \
                       can_capture
    return False


# checks if the piece at the given location is frozen
def frozen(board, location):
    turn = BC.who(board[location[0]][location[1]])
    next_turn = 1 - turn
    # determine the opponents freezer
    if next_turn:
        opponent = WHITE_FREEZER
    else:
        opponent = BLACK_FREEZER
    # check all spaces around the given location for the opponents freezer
    for direction in range(8):
        next_space = can_move(location, direction)
        if next_space != None and board[next_space[0]][next_space[1]] == opponent:
            return True
    return False

# Finds all the spaces in between a current space and the move
# Then it also returns if there is an piece between them
def inbetween(board, current, move, direction):
    spaces = []
    empty_spaces = True

    # Traverses through the spaces in between the spaces
    # and finds whether there is a piece in between them.
    while current != move:
        next_space = can_move(current, direction)
        if next_space != None:
            next_row = next_space[0]
            next_col = next_space[1]
            spaces.append((next_row, next_col))
            piece = board[next_row][next_col]
            if piece != EMPTY:
                empty_spaces = False
            current = next_row, next_col

    return empty_spaces, spaces

# Finds all the possible moves that each piece
# on the board can do.
def successors(state, turn):
    board = state.board
    next_turn = 1 - turn
    moves = []

    # Search through every piece by row-major order
    if winTester.winTester(state) == 'No win':
        for row in range(0, 8):
            for column in range(0, 8):
                piece = board[row][column]
                square = row, column

                # If the piece is friendly
                if piece != EMPTY and BC.who(piece) == turn:

                    # If the piece is a pincer it can only move up
                    # and down, else it can go in all 8 directions
                    if piece == BLACK_PINCER or piece == WHITE_PINCER:
                        direction = 4
                    else:
                        direction = 8
                    for dir in range(direction):

                        # Finds whether this movement is actually on the board
                        possible_square = can_move(square, dir)
                        # Keeps looking to find the legal moves of given piece
                        # in all the directions it can move
                        while possible_square != None:
                            next_row = possible_square[0]
                            next_col  = possible_square[1]
                            next_piece = board[next_row][next_col]

                            # A piece can't go on top of a friendly piece
                            if next_piece != EMPTY and BC.who(next_piece) == state.whose_move:
                                break

                            # A piece cannot go past another piece or land on another piece if
                            # they are not an imitator, leaper, or king.
                            if piece not in [BLACK_IMITATOR, WHITE_IMITATOR, \
                                             BLACK_LEAPER, WHITE_LEAPER, BLACK_KING, WHITE_KING] and \
                                    next_piece != EMPTY and BC.who(next_piece) == next_turn:
                                break
                            # If the move is considered legal given the piece, then you can
                            # add this move to the given successors list
                            if legal_move(board, square, possible_square, dir):
                                move = [square[0], square[1], possible_square[0], possible_square[1], dir]
                                moves.append(move)
                            possible_square = can_move(possible_square, dir)
    return moves




def minimizer(currentState, ply, a, b):
    global N_STATES_EXPANDED, N_CUTOFFS, N_STATIC_EVALS
    best = None

    # if we have reached the max depth, or game is over, return the basic static eval
    if ply == 0 or winTester.winTester(currentState):
        N_STATIC_EVALS += 1
        return staticEval(currentState), best

    # Find all possible moves
    success = successors(currentState, BLACK)
    success = sorted(success, key=lambda x: (x[3], -x[2], x[1], -x[0]))

    # Find the best of the successors
    for move in success:

        # if alpha is greater than beta, then we cutoff
        if a >= b:
            N_CUTOFFS += 1
            break

        new_state = find_new_state(currentState, (move[0], move[1]), (move[2], move[3]), move[4])
        N_STATES_EXPANDED += 1

        score, n_move = maximizer(new_state, ply - 1, a, b)

        # Update beta
        if score < b:
            b = score
            best = move

        # If it goes past the time limit, return the current best alpha and move
        if MAX_TIME > TIME_LIMIT - (time.time() - START):
            return b, best
    return b, best


def maximizer(currentState, ply, a, b):
    global N_STATES_EXPANDED, N_CUTOFFS, N_STATIC_EVALS

    best = None

    # if the max depth is reached or game is over, return the basic static eval
    if ply == 0 or winTester.winTester(currentState) != 'No win':
        N_STATIC_EVALS += 1
        return staticEval(currentState), best

    # Find all of the possible successors with the given state
    success = successors(currentState, WHITE)
    success = sorted(success, key=lambda x: (x[3], -x[2], x[1], -x[0]))

    #Iterate through all of these moves and find the best one
    for move in success:

        # If alpha is greater than beta, then we cutoff
        if a >= b:
            N_CUTOFFS += 1
            break

        new_state = find_new_state(currentState, (move[0], move[1]), (move[2], move[3]), move[4])
        N_STATES_EXPANDED += 1

        score, next_move = minimizer(new_state, ply - 1, a, b)

        #update alpha
        if score > a:
            a = score
            best = move

        # Stop if time is up, and pick the current best move
        if MAX_TIME > TIME_LIMIT - (time.time() - START):
            return a, best
    return a, best

def pincer(board, location, turn):
    next_turn = 1 - turn
    capture = []

    # Checks in the 4 directions a pincer can move
    for direction in range(4):
        next_space = can_move(location, direction)
        if next_space != None:
            next_row = next_space[0]
            next_column = next_space[1]
            next_piece = board[next_row][next_column]
            # if the next location is not empty and is an enemy piece
            if next_piece != EMPTY and BC.who(next_piece) == next_turn:
                if next_piece not in [BLACK_PINCER, WHITE_PINCER]:
                    next_next_space = can_move(next_space, direction)
                    # expands move in the given direction
                    if next_next_space != None:
                        next_next_row = next_next_space[0]
                        next_next_col = next_next_space[1]
                        next_next_piece = board[next_next_row][next_next_col]
                        # If the piece behind the enemy is on our team, then this
                        # piece will be captured
                        if next_next_piece != EMPTY and BC.who(next_next_piece) == turn:
                            capture.append(next_space)
    if capture != []:
        return capture
    else:
        return None



def withdrawer(board, location, direction, whose_turn):
    next_turn = 1 - whose_turn
    next_space = can_move(location, REVERSE_DIRECTIONS[direction])
    if next_space != None:
        next_row = next_space[0]
        next_col = next_space[1]
        next_piece = board[next_row][next_col]
        # if piece in next space is not empty and is an opponent piece
        if next_piece != EMPTY and BC.who(next_piece) == next_turn:
            return next_space
    return None


def coordinator(board, move, turn):
    next_turn = 1- turn
    capture = []
    if BC.who(WHITE_KING) == next_turn:
        king = WHITE_KING
    else:
        king = BLACK_KING
    row_king = None
    col_king = None
    coor_row = move[0]
    coor_col = move[1]
    for row in range(8):
        for col in range(8):
            if board[row][col] == king:
                row_king = row
                col_king = col

    if row_king != None and col_king != None:
        first_corner = board[row_king][coor_col]
        second_corner = board[coor_row][col_king]
        if first_corner != EMPTY and BC.who(first_corner) != next_turn:
            capture.append([row_king, coor_col])
        if second_corner != EMPTY and BC.who(second_corner) != next_turn:
            capture.append([coor_row, col_king])

    if capture != []:
        return capture
    else:
        return  None

def leaper(board, curr, move, spot, empty):
    piece = board[curr[0]][curr[1]]
    turn = BC.who(piece)
    next_turn = 1 - turn

    can_capture = False
    capture = None
    if len(spot) >= 2 and not empty:
        for i, space in enumerate(spot):
            new_piece = board[space[0]][space[1]]
            if new_piece != EMPTY and BC.who(new_piece) == next_turn:
                if i < len(spot) - 1:
                    next_next_piece = spot[i + 1]
                    if next_next_piece == move:
                        can_capture = True
                        capture = space

    return can_capture, capture


def find_new_state(currentState, curr, move, direction):
    first = curr
    second = move
    board = currentState.board
    spot = board[first[0]][first[1]]
    turn = currentState.whose_move
    next_turn = 1 - turn
    new_state = BC.BC_state(board, next_turn)
    new_board = new_state.board

    captured_pieces = None
    capture = None
    # Checks given the piece if it will be a capturing move

    # update pincer capture
    if spot == BLACK_PINCER or spot == WHITE_PINCER:
        capture = pincer(new_board, second, turn)
        if capture != None:
            new_board[first[0]][first[1]] = EMPTY

    if spot == BLACK_COORDINATOR or spot == WHITE_COORDINATOR:
        capture = coordinator(new_board, second, turn)
        if capture != None:
            for c in capture:
                new_board[c[0]][c[1]] = EMPTY

    if spot == BLACK_LEAPER or spot == WHITE_LEAPER:
        empty_spaces, spaces = inbetween(new_board, first, second, direction)
        can_capture, capture = leaper(new_board, first, second, spaces, empty_spaces)
        if can_capture:
            captured_pieces = capture
            capture = None

    if spot == BLACK_WITHDRAWER or spot == WHITE_WITHDRAWER:
        captured_pieces = withdrawer(new_board, first, direction, turn)

    if captured_pieces != None:
        new_board[captured_pieces[0]][captured_pieces[1]] = EMPTY

    if capture != None:
        new_board[capture[0][0]][capture[0][1]] = EMPTY

    # Move piece from first to second
    new_board[first[0]][first[1]] = EMPTY
    new_board[second[0]][second[1]] = spot
    new_state.board = new_board
    return new_state


def parameterized_minimax(currentState, alphaBeta=False, ply=3, \
                          useBasicStaticEval=False, useZobristHashing=False):
    '''Implement this testing function for your agent's basic
    capabilities here.'''
    global N_STATES_EXPANDED
    b_move = None
    b_score = None

    # a list of successors for every piece in the current state
    moves = successors(currentState, currentState.whose_move)

    # orders the moves by column-wise, then row-wise
    moves = sorted(moves, key=lambda x: (x[3], -x[2], x[1], -x[0]))


    # Looks every depth of the tree to assign different alpha beta values
    # for each possible state spt.
    for depth in range(ply):

        a = float('-inf')
        b = float('-inf')

        # go through each of the successors, to explore all the possible states
        for move in moves:

            new_state = find_new_state(currentState, (move[0], move[1]), (move[2], move[3]), move[4])
            N_STATES_EXPANDED += 1

            # Does alpha-beta min or max depending on the turn.
            if new_state.whose_move == WHITE:
                n_score, n_move = maximizer(new_state, depth, a, b)
            else:
                n_score, n_move = minimizer(new_state, depth, a, b)

            # Adjust the alpha beta scores
            if currentState.whose_move == WHITE:
                if n_score > a:
                    b_move = move
                    a = n_score
                    b_score = a
            else: # Adjust if it is black's turn

                if n_score < b:
                    b_move = move
                    b = n_score
                    b_score = b

            # Stop the alpha-beta minimax if it goes over the time
            if MAX_TIME > TIME_LIMIT - (time.time() - START) :
                return b_move, b_score

    return b_move, b_score


def makeMove(currentState, currentRemark, timelimit=10):
    # Compute the new state for a move.
    # You should implement an anytime algorithm based on IDDFS.
    global START, TIME_LIMIT
    TIME_LIMIT = timelimit
    START = time.time()
    currentRemark = randomize_remark()

    # Construct a representation of the move that goes from the
    # currentState to the newState.
    # Here is a placeholder in the right format but with made-up
    # numbers:

    move, score = parameterized_minimax(currentState)
    final_state = find_new_state(currentState, (move[0], move[1]), (move[2], move[3]), move[4])
    final_state.whose_move = 1 - currentState.whose_move

    move = (move[0], move[1]), (move[2], move[3])
    return [[move, final_state], currentRemark]


def randomize_remark():

    remarks = ['Nice try, but not good enough!',
                'You better watch out.',
                'Gothca!',
                'What a silly mistake you have made.',
                'You will never beat me!',
                'Have you never played chess?']

    random_remark = random.choice(remarks)

    return random.choice(random_remark)


#
def staticEval(state):
    '''Compute a more thorough static evaluation of the given state.
    This is intended for normal competitive play.  How you design this
    function could have a significant impact on your player's ability
    to win games.'''
    board = state.board
    val = 0
    turn = state.whose_move
    next_turn = 1 - turn

    # Has higher amounts for the states with the
    # most amounts of moves
    moves = successors(state, turn)
    val += len(moves)

    moves = successors(state, next_turn)
    val -= len(moves)

    # Assigns points to each specific piece
    # King is the most important, and the pincers
    # are the least.
    if winTester.winTester(state) == "No win":
        for row in range(8):
            for col in range(8):
                piece = board[row][col]
                if piece == BLACK_PINCER:
                    if turn == BLACK:
                        val += 10
                    else:
                        val -= 10
                elif piece == WHITE_PINCER:
                    if turn == BLACK:
                        val -= 10
                    else:
                        val += 10
                elif piece == BLACK_COORDINATOR:
                    if turn == BLACK:
                        val += 50
                    else:
                        val -= 50
                elif piece == WHITE_COORDINATOR:
                    if turn == BLACK:
                        val -= 50
                    else:
                        val += 50
                elif piece == BLACK_LEAPER:
                    if turn == BLACK:
                        val += 75
                    else:
                        val -= 75
                elif piece == WHITE_LEAPER:
                    if turn == BLACK:
                        val -= 75
                    else:
                        val += 75
                elif piece == BLACK_IMITATOR:
                    if turn == BLACK:
                        val += 150
                    else:
                        val -= 150
                elif piece == WHITE_IMITATOR:
                    if turn == BLACK:
                        val -= 150
                    else:
                        val += 150
                if piece == BLACK_KING:
                    if turn == BLACK:
                        val += 1000
                    else:
                        val -= 1000
                elif piece == WHITE_KING:
                    if turn == BLACK:
                        val -= 10000
                    else:
                        val += 1000
                else:
                    if piece % 2 == 0:
                        if turn == BLACK:
                            val += 15
                        else:
                            val -= 15
                    else:
                        if turn == BLACK:
                            val -= 15
                        else:
                            val += 15

        for row in range(8):
            for col in range(8):
                if board[row][col] == BLACK_KING and turn == BLACK:
                    for dir in range(4, 8):
                        next = can_move((row, col), dir)
                        if next != None:
                            next_spot = board[next[0]][next[1]]
                            if next_spot != None and next_spot != EMPTY and BC.who(next_spot) == WHITE:
                                val += 30
                            if next_spot != None and next_spot != EMPTY and BC.who(next_spot) == BLACK:
                                val -= 30

                if board[row][col] == WHITE_KING and turn == WHITE:
                    for dir in range(4, 8):
                        next = can_move((row, col), dir)
                        if next != None:
                            next_spot = board[next[0]][next[1]]
                            if next_spot != None and next_spot != EMPTY and BC.who(next_spot) == WHITE:
                                val -= 30
                            if next_spot != None and next_spot != EMPTY and BC.who(next_spot) == BLACK:
                                val += 30


    return val

