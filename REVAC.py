import numpy as np
import subprocess
import sys

from random import randint, uniform

"""
    Global variable for the REVAC Tuning Evolutionary Algorithm
"""

POPULATION_SIZE = 80
BEST_SIZE = 40
SMOOTHING_COEFFICIENT = 10 # It is not clear from the paper but it should be the h parameter in the mutation operator.
REPETITIONS = 20
MAX_NUMBER_OF_VECTOR_TESTED = 5000

"""
    Search space for our parameters
"""

N_PARAMETERS = 10

BOUNDS_POPULATION_SIZE = (100, 110)
BOUNDS_OFFSPRING_SIZE = (4,13)
BOUNDS_MUTATION_RATE = (0.1, 0.35)
BOUNDS_PARENTS_RATIO = (0.3, 0.5)
# BOUNDS_RS_S = (1.0001, 2.0)
BOUNDS_RS_SCALING_FACTOR = (7,12)
BOUNDS_TOURNAMENT_SIZE = (2,8)
BOUNDS_RRTOURNAMENT_SIZE = (6, 9)
BOUNDS_TAU = (0.6, 1.3)
BOUNDS_TAU_PRIME = (0.6, 1.2)
BOUNDS_MIN_STD = (0, 0.001)

def initialize():

    population = []
    for i in range(POPULATION_SIZE):
        ind = [
            randint(*BOUNDS_POPULATION_SIZE),
            randint(*BOUNDS_OFFSPRING_SIZE),
            uniform(*BOUNDS_MUTATION_RATE),
            uniform(*BOUNDS_PARENTS_RATIO),
            # uniform(*BOUNDS_RS_S),
            uniform(*BOUNDS_RS_SCALING_FACTOR),
            randint(*BOUNDS_TOURNAMENT_SIZE),
            randint(*BOUNDS_RRTOURNAMENT_SIZE),
            uniform(*BOUNDS_TAU),
            uniform(*BOUNDS_TAU_PRIME),
            uniform(*BOUNDS_MIN_STD)
        ]

        population.append(ind)

    return population

def evaluate_individual(individual):

    # the result of the evaluation, i.e. the average performance is saved as last column in each parameter vector
    total_score = 0
    scores = []
    for i in range(REPETITIONS):
        
        cmd = "\
            java \
            -DpopulationSize={} \
            -DoffspringSize={} \
            -DmutationRate={} \
            -DparentsRatio={} \
            -DRSscalingFactor={} \
            -Ds=1.5 \
            -DtournamentSize={} \
            -DRRtournamentSize={} \
            -Dtau={} \
            -DtauPrime={} \
            -DstdMin={} \
            -jar testrun.jar -submission=player32 -evaluation=SchaffersEvaluation -seed=1 2>&1 | tee islands_4.stderr".format(*individual)

        cmd = ' '.join(cmd.split())
        result = subprocess.run([cmd], 
                universal_newlines=True,stderr=subprocess.PIPE,shell=True,stdout=subprocess.PIPE)

        # sys.stderr.write(result.stdout)
        # sys.stderr.flush()

        idx_score = result.stdout.find("Score:")
        scores.append((float)((result.stdout[idx_score+7:]).split('\n')[0]))
        print("Score: ", scores[-1])
    
    avg_score = np.mean(scores)
    std_score = np.std(scores)
    individual.append(avg_score)

    # save results
    with open("islands_4.results", 'a') as f:
        f.write(','.join([str(i) for i in individual] + [str(std_score)]) + '\n')

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

    # subprocess.run(["bash compile.sh 3; rm stderr_1"], 
    #             universal_newlines=True,stderr=subprocess.PIPE,shell=True,stdout=subprocess.PIPE)
    
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