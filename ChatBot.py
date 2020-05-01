from random import choice
from re import *

def introduce():
	return ("Heyaa I am Vasi, a really handsome looking guy created me. His name is Vasu. " +
			"If I am being nasty, he is the one to blame, contact him at vasum@uw.edu for any concerns.")

def agentName():
	return ("Vasi")

#def startChatting():
#    print(introduce())
 #   while True:
 #       chatInput = input('TYPE HERE:>>')
 #       print(respond(chatInput))

randomResponses = False
memoryResponseKeywords = False
numberOfResponses = 0


def respond(inputString):

    wordlist, mappedWordlist = preprocessString(inputString)
    global numberOfResponses
    numberOfResponses = numberOfResponses + 1
    updateMemory(mappedWordlist)

    # Rule 1
    if wordlist[0] == '':
        return "What are you trying say dude!.Make sense."

    # Rule 2
    if wordlist[0] in MEETING_WORDS:
        return choice(MEETING_WORDS).capitalize() + "! " + " to you too. "

    # Rule 3
    if wordlist[0:2] == ['i', 'have']:
        return "Do you really want to have " + stringify(mappedWordlist[3:]) + "."

    # Rule 4
    res = whoIsVasi(wordlist, mappedWordlist)
    if res != False:
        return res
            
    # Rule 5
    if wordlist[0:3] == ['i', ' feel', 'better']:
        return "I know right!"

    # Rule 6
    if 'formula' in wordlist:
        return "Choose a chapter first" + choice(CHAPTERS) + "."

    # Rule 7
    if 'hard' in wordlist:
        return " Nothing is easy at first. Keep working, you got this!"

    # Rule 8
    if 'love' in wordlist:
        return "And this love with Math is endless."


    # Rule 9
    if wpred(wordlist[0]):
        return "Can I know more about this"

    # Rule 10
    if 'because' in wordlist:
        return 'We have been studying for so long. Would you like to learn something interesting like ' + choice(INTERESTING) + "?"

    # Rule 12
    if 'study' in wordlist:
        return "What do you want to study now " + choice(CHAPTERS)

    # Rule 13
    res = whoAreYouResponses(wordlist, mappedWordlist)
    if res != False:
        return res

    # Rule 14 (Implementation based on keywords)
    res = retrieveResponseFromMemory(mappedWordlist)
    if res != False:
        randomResponses = False
        return res

    # Rule 15
    # Random Response Rule
    global randomResponse
    if randomResponse == False:
        randomResponse = True
        return randomResponse()

    return punt()
#_______________________________________________________________
countWhoIsVasiResponses = 0
def whoIsVasi(wordlist, mappedWordlist):
    global countWhoIsVasiResponses
    if len(wordlist) > 2 and wordlist[0:3] == ['who', 'are', 'you']:
        WHO_ARE_YOU_RESPONSES = ["I am a Mathematician, I ll make you go crazy talking Math",
                                 "If you want to know more about me, ask questions",
                                 "I can help you in many ways ;)",
                                 "Tell me something about yourself, I ll remember it",
                                 "I am a rare combinattion of handsome guy and a Mathematician, surprised?"]
        countWhoIsVasiResponses += 1
        return WHO_ARE_YOU_RESPONSES[countWhoIsVasiResponses % 5]
    return False

countWhoAreYouResponses = 0
def whoAreYouResponses(wordlist, mappedWordlist):
    global countYouAreResponses
    if wordlist[0:2] == ['you', 'are'] or wordlist[0:2] == ['are', 'you']:
            YOU_ARE_RESPONSE_TYPES = ["Tell me more about you Dude",
                                      "Let's talk about something else.",
                                      "I will search the web for that, nah just kidding!"]
            countWhoAreYouResponses += 1
            return YOU_ARE_RESPONSE_TYPES[countWhoAreYouResponses % 3]
    return False
#____________________________________________________________________
def randomResponse():
    RANDOM_RESPONSE = ["I hope that you learnt something while talking me.",
                       "Did you like the formulaes and chapters I showed you?",
                       "Do you want to become a great Mathematician someday",
                       "I solve Math puzzles when I am bored",
                       "Sorry but not Sorry"]
    return choice(RANDOM_RESPONSE)
#______________________________________________________________
memory = []

def updateMemory(statementWordlist):
    remove = []
    global memory
    for i in range(len(statementWordlist)):
        if ((statementWordlist[i].lower() in PRONOUNS) or
           (statementWordlist[i].lower() in SENTENCE_BUILDING_WORDS) or (statementWordlist[i].lower() in MEETING_WORDS)):
           remove.append(statementWordlist[i])    
    memory += ([word for word in statementWordlist if ((word not in remove) and (word not in memory))])

def retrieveResponseFromMemory(mappedWordlist):
    if len(memory) < 1:
        return "We did not touch this topic yet my dude."
    else:
        for word in mappedWordlist:
            if word in memory[:-1]:
                previousResponse = choice(memory[:-1])
                recallResponseOptions = ["Here is what you said a while ago. You said something about " + previousResponse + '.',
                                 "I remember that you said something about " + previousResponse + ". Do you want more info?"]
                recallResponse = choice(recallResponseOptions)
                return recallResponse
        return False

#___________________________________________________________________________

VERBS = ['Mathematician','Maths','formulae','chapter','theorem','proof','graphs','Add','substract','divide','multiply',
			'calculate','area','perimeter','trignometery','calculus','infinity','probability','variable','formula','slope','fraction','analysis',
			'go', 'have', 'be', 'try', 'eat', 'take', 'help',
                  'make', 'get', 'jump', 'write', 'type', 'fill',
                  'put', 'turn', 'compute', 'think', 'drink',
                  'blink', 'crash', 'crunch', 'add','like', 'tolerate', 'dislike', 'hate', 'love']

PRONOUNS = ['I','he','she','it','you','we','who','whom','they','me','him','her','our','this','that','these','those','myself','himself',
			'ourselves','them','themselves']

MEETING_WORDS = ['hii', 'Heyaa', 'hello', 'good morning', 'good afternoon', 'good evening','good night']

GOODBYE_WORDS = ['bye-bye', 'see you']

SENTENCE_BUILDING_WORDS = ['nice','bad','good','not','worst','because', 'how', 'when', 'come','sure','really','empty', 'said','try','must'
							,'should','not really','anyways','and','or','life','sad']

CHAPTERS = ['probability','calculus','trignometery','Graphs','Induction','Vectors','Limits','Sequences','complex numbers','number theory']

INTERESTING = ['Mathematical Physics', 'logic','reasoning','Combinatorics']

#_______________________________________________________________________
def preprocessString(inputString):
    if match('bye', inputString):
            return 'Goodbye!'
    wordlist = split(' ',remove_punctuation(inputString))
    wordlist[0]=wordlist[0].lower()
    mapped_wordlist = you_me_map(wordlist)
    return (wordlist, mapped_wordlist)

def stringify(wordlist):
    'Create a string from wordlist, but with spaces between words.'
    return ' '.join(wordlist)

punctuation_pattern = compile(r"\,|\.|\?|\!|\;|\:")    

def remove_punctuation(text):
    'Returns a string without any punctuation.'
    return sub(punctuation_pattern,'', text)

def wpred(w):
    'Returns True if w is one of the question words.'
    return (w in ['when','why','where','how'])

def dpred(w):
    'Returns True if w is an auxiliary verb.'
    return (w in ['do','can','should','would'])

PUNTS = ['Please go on.',
         'Tell me more.',
         'I see.',
         'What does that indicate?',
         'But why be concerned about it?',
         'Just tell me how you feel.']  

punt_count = 0
def punt():
    'Returns one from a list of default responses.'
    global punt_count
    punt_count += 1
    return PUNTS[punt_count % 6]

CASE_MAP = {'i':'you', 'I':'you', 'me':'you','you':'me',
            'my':'your','your':'my',
            'yours':'mine','mine':'yours','am':'are'}

def you_me(w):
    'Changes a word from 1st to 2nd person or vice-versa.'
    try:
        result = CASE_MAP[w]
    except KeyError:
        result = w
    return result

def you_me_map(wordlist):
    'Applies YOU-ME to a whole sentence or phrase.'
    return [you_me(w) for w in wordlist]   
    
def verbp(w):
    'Returns True if w is one of these known verbs.'
    return (w in VERBS)         