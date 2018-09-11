# EC_2018_T32
The great work of team 32

# File location notice
**To keep it as cross platform as possible, (e.g. Guiseppe had the player32.java in the parent directory, while in eclipse it goes under src/) I put the src/ in the gitignore.**   
To circumvent this create a symbolic link to the parent directory;
In Windows:
1. cd to the src/
2. mklink /H player32.java ..\player32.java  
*If you get an invalid switch error, you didn't use the correct slash, read the previous line again*

# Code workflow

The code is structured in the following way:

 - EA.java : class for the Evolutionary Algorithm. It has the following methods:
   - `initialize()`: initialize the population creating *populationSize* Individuals of random coordinates.
   - `selectParents()`: order the population by fitness and select the first N individual, where N is given by the *parentsRatio* parameter.
   - `applyCrossover()`: given a couple of parents, it applies a one-point crossover generating two new individuals.
   - `applyMutation`: the offspring is mutated. Mutation consists of random variation of the original coordinates in an hypersphere of radius *mutationSwing*. Mutation occurs with a rate of *mutationRate*.
   - `applyReplacement`: to keep the population size constant, the population is sorted and the first N are kept for the next generation. N is, as before, given by the *populationSize* parameter.
   - `evaluateFitness`: sets the private fitness variable of each individual based on its coords' vector.
   - `getBestIndividual`: returns the individual with the highest score. 
 - Individual.java : class definition of an individual. Each individual has its coords and its fitness.
 - Pair.java : helper class.

 ### Note
 The population is encoded as an `ArrayList` of `Individual`s.
