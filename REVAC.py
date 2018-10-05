import numpy as np
import subprocess

from random import randint, uniform

"""
    Global variable for the REVAC Tuning Evolutionary Algorithm
"""

POPULATION_SIZE = 80
BEST_SIZE = 40
SMOOTHING_COEFFICIENT = 10 # It is not clear from the paper but it should be the h parameter in the mutation operator.
REPETITIONS = 15
MAX_NUMBER_OF_VECTOR_TESTED = 5000

"""
    Search space for our parameters
"""

N_PARAMETERS = 9

BOUNDS_POPULATION_SIZE = (20, 300)
BOUNDS_OFFSPRING_SIZE = (1, 1e4)
BOUNDS_MUTATION_RATE = (1e-3, 1.0)
BOUNDS_PARENTS_RATIO = (0.01, 1.0)
BOUNDS_RS_SCALING_FACTOR = (1, 30)

# THE BOUND FOR THE TOURNAMENT SELECTORS ARE DEPENDENT ON HOW MANY INDIVIDUAL THERE ARE (POP + OFFSPRING)

BOUNDS_TAU = (1, 10)
BOUNDS_TAU_PRIME = (1, 100)
BOUNDS_MIN_STD = (0, 1.0)

def initialize():

    population = []
    for i in range(POPULATION_SIZE):
        ind = [
            randint(*BOUNDS_POPULATION_SIZE),
            randint(*BOUNDS_OFFSPRING_SIZE),
            uniform(*BOUNDS_MUTATION_RATE),
            uniform(*BOUNDS_PARENTS_RATIO),
            uniform(*BOUNDS_RS_SCALING_FACTOR),
            # -1, # tournament selector
            -1, # RR tournament size
            uniform(*BOUNDS_TAU),
            uniform(*BOUNDS_TAU_PRIME),
            uniform(*BOUNDS_MIN_STD)
        ]

        # sample the tournament size
        ind[5] = randint(2, ind[0] + ind[1])
        # ind[6] = randint(2, ind[0] + ind[1])

        population.append(ind)

    return population

def evaluate_individual(individual):

    # the result of the evaluation, i.e. the average performance is saved as last column in each parameter vector
    total_score = 0
    for i in range(REPETITIONS):
        
        cmd = "\
            java \
            -DpopulationSize={} \
            -DoffspringSize={} \
            -DmutationRate={} \
            -DparentsRatio={} \
            -DRSScalingFactor={} \
            -DtournamentSize=0 \
            -DRRtournamentSize={} \
            -Dtau={} \
            -DtauPrime={} \
            -DstdMin={} \
            -jar testrun.jar -submission=player32 -evaluation=BentCigarFunction -seed=1".format(*individual)

        cmd = ' '.join(cmd.split())
        result = subprocess.run([cmd], 
                universal_newlines=True,stderr=subprocess.PIPE,shell=True,stdout=subprocess.PIPE)

        print(result.stderr)
        idx_score = result.stdout.find("Score:")
        score = (float)(result.stdout[idx_score+7:idx_score+23])
        total_score += score

    avg_score = total_score / REPETITIONS
    individual.append(avg_score)

    # save results
    with open("results.txt", 'a') as f:
        f.write(','.join([str(i) for i in individual]) + '\n')

    return individual

def select_parents(population):
    population.sort(key=lambda x: x[-1], reverse=True) # sort according to fitness
    parents = population[:BEST_SIZE]
    return parents

def recombine(parents):
    child = []
    for param in range(N_PARAMETERS):
        parent_id = randint(0, len(parents)-1)
        child.append(parents[parent_id][param])
    
    return child

def mutate(parents, child):
    for param in range(N_PARAMETERS):

        child_param = child[param]

        parent_params = [i[param] for i in parents]
        parent_params.sort()
        
        # find the id in parent_params in which child_param == parent_param
        idx = [i for i, value in enumerate(parent_params) if value == child_param][0]

        xA = parent_params[max(idx - SMOOTHING_COEFFICIENT, 0)]
        xB = parent_params[min(idx + SMOOTHING_COEFFICIENT, len(parent_params)-1)]

        child[param] = uniform(xA, xB)

        if param in [0, 1, 5, 6]:
            child[param] = int(round(child[param]))

    return child

def select_survivor(population, child):
    population.append(child)
    population.sort(key=lambda x: x[-1], reverse=True) # sort according to fitness
    parents = population[:POPULATION_SIZE]

def evolve(population):

    parents = select_parents(population)

    child = recombine(parents)
    child = mutate(parents, child)
    child = evaluate_individual(child)
    
    population = select_survivor(population, child)

def main():
    
    evaluation = POPULATION_SIZE
    population = initialize()

    for ind in population:
        ind = evaluate_individual(ind)

    while evaluation < MAX_NUMBER_OF_VECTOR_TESTED:
        evolve(population)
        evaluation += 1
        print("Number of vector tested: {}".format(evaluation))    

if __name__ == "__main__":
    main()