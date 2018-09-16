import org.vu.contest.ContestEvaluation;

public class CompetitionCustomPack {
	private ContestEvaluation evaluation;
	private int currentEvaluationCount;
	private int maxEvaluationCount;
	
	public CompetitionCustomPack(ContestEvaluation evaluation) {
		this.evaluation = evaluation;
		currentEvaluationCount = 0;
		maxEvaluationCount = Integer.parseInt(evaluation.getProperties().getProperty("Evaluations"));
	}

	public int getCurrentEvaluationCount() {
		return currentEvaluationCount;
	}
	
	public int getCurretEvaluationCount() {
		return currentEvaluationCount;
	}
	
	public int evaluationsRemaining() {
		return maxEvaluationCount-currentEvaluationCount;
	}
	
	
	public Object evaluate(double [] X) {
		if (maxEvaluationCount==currentEvaluationCount) {
			return null;
		}
		currentEvaluationCount++;
		return (double)evaluation.evaluate(X);
		
	}

}
