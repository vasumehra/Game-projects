import random
import BC_state_etc as BC

all_hashed = {}
table = []

def get_table():
    global table
    table = [[[random.randint(1,2**64 - 1) for i in range(14)]
    				for j in range(8)]
    				for k in range(8)]

def zobristHashing(state):
    global zobrist_values
    h = 0
    board = state.board
    for row in range(8):
        for col in range(8):
            if board[row][col] != 0:
                piece = board[row][col]
                h ^= table[row][col][piece - 2]
    return h

get_table()
print(zobristHashing(BC.BC_state()))