import java.util.Random;


public class NeuralLayer {

	float weights[][];
	float output[];
	float threshold = 0.5f;
	int inputNeurons;
	int neurons = 10;
	
	public NeuralLayer(int neurons, int inputNeurons)
	{
		this.neurons = neurons;
		this.inputNeurons = inputNeurons;
		output = new float[neurons];
		weights = new float[inputNeurons][neurons];
	}
	
	public float[] calculate(float[] input)
	{
		for(int n = 0; n < neurons; n++)
		{
			float sum = 0;
			for(int i = 0; i < input.length; i++)
				sum += weights[i][n] * input[i];
			
			/*if (sum > threshold)
				output[n]  = 1;
			else
				output[n] = 0;*/
			output[n] = (float) (1/(1 + Math.pow(Math.E, -sum)));
		}
		return output;
	}
	
	public void initializeWeights()
	{
		Random r = new Random();
		for(int n = 0; n < inputNeurons; n++)
			for(int i = 0; i < neurons; i++)
				weights[n][i] = r.nextFloat() - 0.5f;
	}
}
