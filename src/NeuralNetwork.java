
public class NeuralNetwork {
	
	NeuralLayer[] layers;
	
	public NeuralNetwork(int inputs, int outputs, int hiddenLayers, int neuronsPerLayer)
	{
		this.layers = new NeuralLayer[hiddenLayers+1];
		this.layers[0] = new NeuralLayer(neuronsPerLayer, inputs);
		for(int i = 1; i < hiddenLayers; i++)
			this.layers[i] = new NeuralLayer(neuronsPerLayer, neuronsPerLayer);
		this.layers[hiddenLayers] = new NeuralLayer(outputs, neuronsPerLayer); // output layer
	}
	
	public float[] calculate(float[] input)
	{
		float[] currentLayerOutput = input;
		for(int i = 0; i < layers.length; i++)
		{
			currentLayerOutput = layers[i].calculate(currentLayerOutput);
		}
		return currentLayerOutput;
	}
	
	public void initializeNetwork()
	{
		for(int i = 0; i < layers.length; i++)
			layers[i].initializeWeights();
	}
}
