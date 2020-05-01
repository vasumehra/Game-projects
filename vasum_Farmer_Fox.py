'''vasum_Farmer_Fox.py
by Vasu Mehra

Assignment 2, in CSE 415, Spring 2019.
 
This file contains my problem formulation for the problem of
the Farmer, Fox, Chicken, and Grain.
'''
#<METADATA>
SOLUZION_VERSION = "2.0"
PROBLEM_NAME = "Farmer, Chicken, Fox, Grain"
PROBLEM_VERSION = "2.0"
PROBLEM_AUTHORS = ['S. Tanimoto']
PROBLEM_CREATION_DATE = "17-APRIL-2019"

# The following field is mainly for the human solver, via either the Text_SOLUZION_Client.
# or the SVG graphics client.
PROBLEM_DESC=\
 '''The <b>"Missionaries and Cannibals"</b> problem is a traditional puzzle
in which the player starts off with three missionaries and three cannibals
on the left bank of a river.  The object is to execute a sequence of legal
moves that transfers them all to the right bank of the river.  In this
version, there is a boat that can carry at most three people, and one of
them must be a missionary to steer the boat.  It is forbidden to ever
have one or two missionaries outnumbered by cannibals, either on the
left bank, right bank, or in the boat.  In the formulation presented
here, the computer will not let you make a move to such a forbidden situation, and it
will only show you moves that could be executed "safely."
'''
#</METADATA>

#<COMMON_DATA>
#</COMMON_DATA>
Farmer = 0
Fox = 1
Chicken = 2
Grain = 3
LEFT = 0
RIGHT = 1

class State():

	def __init__(self, d=None):
		if d==None: 
		  d = {'people':[[0,0],[0,0],[0,0],[0,0]],
		       'boat':LEFT}
		self.d = d

	def __eq__(self,s2):
	    for prop in ['people', 'boat']:
	      if self.d[prop] != s2.d[prop]: return False
	    return True

	def __str__(self):
	    p = self.d['people']
	    txt = "\n Farmer on left:"+str(p[Farmer][LEFT])+"\n"
	    txt += " Fox on left:"+str(p[Fox][LEFT])+"\n"
	    txt += "   Chicken on left:"+str(p[Chicken][LEFT])+"\n"
	    txt += "   Grain on left:"+str(p[Grain][LEFT])+"\n"
	    txt = " Farmer on right:"+str(p[Farmer][RIGHT])+"\n"
	    txt += " Fox on right:"+str(p[Fox][RIGHT])+"\n"
	    txt += "   Chicken on right:"+str(p[Chicken][RIGHT])+"\n"
	    txt += "   Grain on right:"+str(p[Grain][RIGHT])+"\n"
	    side='left'
	    if self.d['boat']==1: side='right'
	    txt += " boat is on the "+side+".\n"
	    return txt

	def __hash__(self):
		return (self.__str__()).__hash__()

	def copy(self):
	    # Performs an appropriately deep copy of a state,
	    # for use by operators in creating new states.
	    news = State({})
	    news.d['people']=[self.d['people'][F_F_C_G][:] for F_F_C_G in [Farmer, Fox, Chicken, Grain]]
	    news.d['boat'] = self.d['boat']
	    return news 

	def can_move (self, farmer, fox, chic, grain):
		side = self.d['boat'] # Where the boat is.
		p = self.d['people']
		if(farmer != 1):
			return False

		Farmer_present = p[Farmer][side]
		Fox_present = p[Fox][side]
		Chicken_present = p[Chicken][side]
		Grain_present = p[Grain][side]
		if Farmer_present < farmer:
			return False
		if Fox_present < fox:
			return False
		if Chicken_present < chic:
			return False
		if Grain_present < grain:
			return False
		
		Farmer_Left = Farmer_present - farmer
		Fox_Left = Fox_present - fox
		Chicken_Left = Chicken_present - chic
		Grain_Left = Grain_present - grain 

		if Farmer_Left != 0:
			return False
		if Chicken_Left == 1 and Grain_Left == 1 and Farmer_Left == 0:
			return False
		if Fox_Left == 1 and Chicken_Left == 1 and Farmer_Left == 0:
			return False

		Farmer_arrives = p[Farmer][1-side] + farmer
		Chicken_arrives = p[Chicken][1-side] + chic
		Fox_arrives = p[Fox][1-side]+ fox
		Grain_arrives = p[Grain][1-side] + grain
		
		if Farmer_arrives != 1 : return False

		return True

	def move(self, farmer, fox, chic, grain):
		newState = self.copy()
		side = self.d['boat']
		p = newState.d['people']
		p[Farmer][side] = p[Farmer][side]-farmer
		p[Fox][side] = p[Fox][side]-fox
		p[Chicken][side] = p[Chicken][side]-chic
		p[Grain][side] = p[Grain][side]-grain

		p[Farmer][1-side] = p[Farmer][1-side]+farmer
		p[Fox][1-side] = p[Fox][1-side]+fox
		p[Chicken][1-side] = p[Chicken][1-side]+chic
		p[Grain][1-side] = p[Grain][1-side]+grain
		newState.d['boat'] = 1-side
		return newState

def goal_test(s):
    p = s.d['people']
    return (p[Farmer][RIGHT] == 1 and p[Fox][RIGHT] == 1 and p[Chicken][RIGHT] == 1 and p[Grain][RIGHT] == 1)

def goal_message(s):
	return "Congratulations on successfully guiding the Farmer, Fox, Chicken and Grain across the river."

class Operator:
	def __init__(self, name, precond, state_transf):
		self.name = name
		self.precond = precond
		self.state_transf = state_transf

	def is_applicable(self, s):
		return self.precond(s)

	def apply(self, s):
		return self.state_transf(s)

#</COMMON_CODE>
#<INITIAL_STATE>
CREATE_INITIAL_STATE = lambda : State(d={'people':[[1, 0], [1, 0], [1,0], [1,0]], 'boat':LEFT })
#</INITIAL_STATE>

#<OPERATORS>
Ffcg_combinations = [(1,0,0,0), (1,1,0,0), (1,0,1,0), (1,0,0,1)]

OPERATORS = [Operator("Cross the river with Farmer " + "taking " + str(f) + " fox, " +  str(c) + " chicken, " + "and " + str(g) + " grain.",
	    lambda s, F1=Fam, f1=f, c1=c, g1=g: s.can_move(F1, f1, c1, g1),
	    lambda s, F1=Fam, f1=f, c1=c, g1=g: s.move(F1, f1, c1, g1))
     	for (Fam, f, c, g) in Ffcg_combinations]
 #</OPERATORS>
#<GOAL_TEST>
GOAL_TEST = lambda s: goal_test(s)
#</GOAL_TEST>
#<GOAL_MESSAGE_FUNCTION>
GOAL_MESSAGE_FUNCTION = lambda s: goal_message(s)
#</GOAL_MESSAGE_FUNCTION>     	


    	

