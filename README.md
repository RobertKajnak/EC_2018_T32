# EC_2018_T32
The great work of team 32

## WARNING 
 There has been a great update since last commit. Everything is changed in favour to make implementation and testing of new operators more straightforward.

## Index

 - [File location notice](#file-location-notice)
 - [Dependencies](#dependencies)
 - [Code Structure](#code-structure)
 - [How to contribute](#how-to-contribute)
 - [How to connect the new operators to the other modules](#how-to-connect-the-new-operators-to-the-other-modules)
 - [Supported recombination operators](#supported-recombination-operators)
 - [Supported mutation operators](#supported-mutation-operators)

# File location notice
**To keep it as cross platform as possible, (e.g. Giuseppe had the player32.java in the parent directory, while in Eclipse it goes under src/)** I put the src/ in the gitignore.  
~~To circumvent this create a symbolic link to the parent directory;~~  
~~In Windows: etc.~~  

Just pull the project in src/



### Dependencies
 - [Apache Commons Math 3.6.1](http://commons.apache.org/proper/commons-math/)
 - [Apache Commons Lang 3.8.1](https://commons.apache.org/proper/commons-lang/download_lang.cgi)

### Code Structure
 - **Config.java:** The configuration file allows to specify which operators the EA should use for recombination and mutation. Based on which combination of operators is choosen, the config files creates 4 objetcs:
   - `HashMap<String, Object> EAParams`: it contains the global optimizer's parameters;
   - `HashMap<String, Object> recombinationDescriptor`: it contains the all the stuff needed by the selected recombination operator to work;
   - `HashMap<String, Object> mutationDescriptor`: it contains the all the stuff needed by the selected mutation operator to work;
   - `ArrayList<String> individualDescriptor`: contains the all the stuff needed to define the genotype of each indiviudal based on the selected combination of operators;

   In particular, the recombination and mutation's descriptors contain a 'pointer' to the selected recombination or mutation function. This abstraction is provided by the following snippet:

       mutationDescriptor.put("call", new MutationFunctionInterface() {
           public HashMap<String, Object> execute(HashMap<String, Object> genotype, HashMap<String, Object> params) {
               return Mutator.gaussian(genotype, params);
           }
       };

	**Note**: if a wrong name is provided for any of the recombination/mutation operators, a `NotValidOperatorNameException` is thrown.

- **Recombinator.java** It contains the implementations of the recombination operator.
- **Mutator.java** It contains the implementations of the mutator operator.
- **Individual.java** Instead of defining set and get functions for each individual parameter, there's only one get which allows to retrieve the entire genotype of the individual.
- **EA.java** The main function is:

		public void evolve() throws NotEnoughEvaluationsException {
			this.parents    = this.selectParents();
			this.offspring  = this.recombine(this.parents);
			this.offspring  = this.mutate(this.offspring);
			this.population = this.selectSurvivors(this.parents, this.offspring);
		}

	where parents, offspring and population are instances of the class `ArrayList<Individual>`.

### How to contribute
New operators should be added to the files Recombinator.java and Mutator.java:

 - **Recombination operator**: each operator is a function with the following function declaration:

		public static Pair< HashMap<String, Object>, HashMap<String, Object> > onePointCrossover(Individual mom, Individual dad) {

			// some cool stuff

		}

	**Input**: the `Individual` instances of the parents.
	
	**Output**: the `Pair<A, B>` where A and B are the recombined genotypes (offspring's genotypes).

 - **Mutation operator**: each operator is a function with the following function declaration:

		public static HashMap<String, Object> OperatorName(HashMap<String, Object> genotype, HashMap<String, Object> params) { 

			// some very cool stuff

		}

	**Input**: genotype and operator's specific parameters (defined in Config.java)

	**Output**: mutated genotype

### How to connect the new operators to the other modules
Once you've created your new operator, do:

1. Go to Config.java;
2. Inside the private variables declaration, add all the paramters the new operator needs to work (not those specific for each individual);
3. Go into `getRecombinationDescriptor()` or into `getMutationDescriptor()` according to which operator type you've created;
4. Add a new case to handle the new operator. Follow the same structure of the other cases;
5. Go into `getIndividualDescriptor()`;
6. Add a new case in recombination or mutation switch specifying which specific parameters the genotype of each individual consists of;
7. Go into player32.java;
8. Go into `fillEmptyIndividualSlots()`;
9. Add new `if` cases to initialize your new Individual's specific parameters;
10. Enjoy :)

### Supported recombination operators

 - [x] One-point Crossover
 - [ ] n-points Crossover
 - [ ] Diagonal Crossover
 - [ ] Uniform Crossover
 - [ ] Blend Crossover
 - [ ] Simple Arithmetic Recombination
 - [ ] Single Arithmetic Recombination 
 - [ ] Whole Arithmetic Recombination

### Supported mutation operators

 - [x] Uniform
 - [x] Gaussian
 - [x] Uncorrelated one step size
 - [x] Uncorrelated N step sizes
 - [x] Correlated N step sizes

 ### Note
 The `correlated_N_stepSizes` mutation operator does not compile. It throws a `SecurityException` with the message "*Attempting to create a class loader!*" caused by the statement:

	MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(ArrayUtils.toPrimitive(coords), cov);

Unfortunately, that's the statement that creates a multivariate gaussian distribution, from which we should sample the coordinates' shift. Anyway, I think it's a bug. There's a discussion on canvas related to a similar issue. In that case a SecurityException was thrown with the message "*SecurityException: Attempting to access system properties!*" for a really silly reason. I guess the teacher doesn't want we use third party libraries.
