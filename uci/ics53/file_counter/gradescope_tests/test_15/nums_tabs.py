# Sample code from http://python3.codes/already-seen-number/
# only the start of the lines have tabs. All comments have spaces in front of them.

#   Given a stream of integers ranging in value from 0 to M,
#   indicate when one of them has been seen before.
import random

M = 500000000           # max size of integer
N = 80000               # number of ints in the stream

random.seed('test')
seen=set()

def dejaVu(n):          # the required function
	if n in seen:
		return True
	else:
		seen.add(n)     # not seen before so add to set
		return False

def tryit():            # generate another number & test it
	n = random.randint(0,M+1)
	if dejaVu(n): print (n)
    
                        # let's try it N times...
for _ in range(0,N): tryit()

print("Done")
