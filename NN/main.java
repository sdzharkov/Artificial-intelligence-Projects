import java.io.*;
import java.util.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;


class Network{
    public ArrayList<InputNode> inputNodes;
    public ArrayList<HiddenNode> hiddenNodes;
    public OutputNode outputNode;
    public double learningRate = 0.1;
    public double breakError = 0;
    public double alpha = 0.1;

    public Network() {
        inputNodes = new ArrayList<>();
        hiddenNodes = new ArrayList<>();
        outputNode = new OutputNode(); // or null for now?

        for (int i = 0; i < 15360; i++) {
            InputNode iNode = new InputNode(i);
            inputNodes.add(iNode);
        }

        for (int i = 0; i < 5; i++) {
            HiddenNode hNode = new HiddenNode(outputNode);
            for (int j = 0; j < 15360; j++) {
                new Edge(inputNodes.get(j), hNode);
            }
            hiddenNodes.add(hNode);
        }
    }

    public class Node {
        public double ai;
        public double aj;
        public double inputSum;
        public double error;
        public ArrayList<Edge> outgoingEdges;
        public ArrayList<Edge> incomingEdges;

        public Node() {
            this.ai = Double.NaN;
            this.aj = Double.NaN;
            this.inputSum = Double.NaN;
            this.error = Double.NaN;
            this.outgoingEdges = new ArrayList<>();
            this.incomingEdges = new ArrayList<>();
            this.addBias();
        }

        public void addBias(){
            incomingEdges.add(new Edge(new BiasNode(), this));
        }
    }

    public class Edge {
        public double weight;
        public Node src;
        public Node dest;
        public double deltaWeight;

        public Edge(Node source, Node destination) {
            this.weight = ThreadLocalRandom.current().nextDouble(-0.05, 0.05);
            this.deltaWeight = 0;
            this.src = source;
            this.dest = destination;
            this.src.outgoingEdges.add(this);
            this.dest.incomingEdges.add(this);
        }
    }

    public class BiasNode extends InputNode {
        public BiasNode(){
            super();
            this.ai = 1.0;
            this.aj = 1.0;
            this.inputSum = -1;
        }
    }

    public class InputNode extends Node {
        public int index;

        public InputNode(){
            super();
        }

        public InputNode(int i) {
            super();
            index = i;
//            Edge edge = new Edge(this, node);
        }

        public void addBias(){
            return;
        }
    }

    public class HiddenNode extends Node {
        public int index;

        public HiddenNode(OutputNode node) {
            super();
            Edge edge = new Edge(this, node);
        }
    }

    public class OutputNode extends Node {
        public int index;

        public OutputNode() {
            super();
        }

        public void addBias(){
            return;
        }
    }

    public double sumWeights(HiddenNode N){
        double sum = 0; // Hidden unit activation, confirm if correct.
        for (Edge I: N.incomingEdges) {
            //System.out.println(I.weight + " " + I.src.ai);
            sum += (I.weight * I.src.ai);
        }
        return sum;
    }

    public double sumHiddenWeights(){
        double sum = 0; //Hidden unit activation
        for (Edge I : outputNode.incomingEdges){
            sum += I.weight * I.src.aj; // weight of all incoming edges * the output.
        }
        return sum;
    }

    public double sumOutputWeightsError(){
        double sum = 0.0;
        for (Edge I : outputNode.incomingEdges){
            sum += I.weight * I.dest.error; // weight of all incoming edges * the output.
        }
        return sum;
    }

    public void propogateForward(ArrayList<Double> readValues){
        for (int i = 0; i < inputNodes.size(); i++) { // Set the input value from the picture to the input nodes
            double x = readValues.get(i)/ 255;
            inputNodes.get(i).ai = x;
        }
        for (HiddenNode H: hiddenNodes) { // For each hidden nodes, find its sum of imputs, and run the activation function
            H.inputSum = sumWeights(H);
            H.aj = sigmoid(H.inputSum);
        }
        outputNode.inputSum = sumHiddenWeights(); // output node summation of inputs
        outputNode.aj = sigmoid(outputNode.inputSum); // output node output
    }

    public void propogateBackwards(double gender){
        //outputNode.error = sigmoid(outputNode.inputSum) * (gender - outputNode.aj); // Textbook way
        outputNode.error = outputNode.aj * (1 - outputNode.aj) * (gender - outputNode.aj); // Textbook #2 way
//        breakError += 0.5 * (gender - outputNode.aj) * (1 - outputNode.aj); // sum squared error
//        if (breakError < 0.1){
//            System.out.println("Breaking because of too low breakErrro");
//            break;
//        }

        double outputWeightErrorSum = sumOutputWeightsError();
        for (HiddenNode H : hiddenNodes){
            //H.error = sigmoid(H.inputSum) * outputWeightErrorSum * outputNode.error;
            H.error = H.aj * (1 - H.aj) * outputWeightErrorSum; // Book 2 way
        }

        for (Edge I : outputNode.incomingEdges){ // update weight for hidden -> output edges
            I.weight += learningRate * I.src.aj * outputNode.error;// + alpha * I.deltaWeight;
            I.deltaWeight = I.weight;
        }

        for (HiddenNode H: hiddenNodes) { // update weights for incoming -> hidden edges
            for (Edge I : H.incomingEdges) {
                I.weight += learningRate * I.src.ai * H.error;// + alpha* I.deltaWeight;
                I.deltaWeight = I.weight;
            }
        }
    }

    public void train(ArrayList<Double> readValues, double gender){
        propogateForward(readValues);
        propogateBackwards(gender);
        System.out.println(outputNode.inputSum + " " + outputNode.error + " " + outputNode.aj);

    }

    public void test(ArrayList<Double> readValues){
        propogateForward(readValues);
        System.out.println(outputNode.inputSum + " " + outputNode.error + " " + outputNode.aj);
    }

    public double sigmoid(double input) {
        return 1/ (1 + Math.exp(-input));
    }

}


public class main {

    public static ArrayList<Double> readFile(File inputFile){
        try {
            //System.out.print(inputFile);
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            ArrayList<Double> list = new ArrayList<Double>();
            while((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                for (String token : tokens) {
                    list.add(Double.valueOf(token));
                }
            }
            return list;
        }
        catch (Exception e) {
            System.out.print("File: " + inputFile + "\n");
            System.out.print(e);
            System.out.print("Can't read file"+"\n");
            return null;
        }
    }
//    public static double mean(double[] data) {
//        double sum = 0;
//        for (double a: data) {
//            sum += m[i];
//        }
//        return sum / m.length;
//    }
//
//    public static double getVariance(double[] data)
//    {
//        double mean = getMean();
//        double temp = 0;
//        for(double a :data)
//            temp += (a-mean)*(a-mean);
//        return temp/size;
//    }
//
//    public static double getStdDev(double[] data)
//    {
//        return Math.sqrt(getVariance(data));
//    }
//
//    public void ArrayList<Double> kFoldValidation(File[] fileDir){
//        int size = fileDir.length;
//        int breaker = size / 5;
//        ArrayList<ArrayList<File>> testFiles = new ArrayList<ArrayList<File>>();
//        for (int i = 0; i < 6; i++) {
//            ArrayList<File> test = new ArrayList<File>();
//            testFiles.add(test);
//        }
//        int start = -1;
//        for (int i = 0; i < size; i++) { // creates sub files
//            if (i % breaker == 0){
//                start ++;
//                testFiles.get(start).add(fileDir[i]);
//            }
//            else{
//                testFiles.get(start).add(fileDir[i]);
//            }
//        }
//        System.out.println(testFiles);
//
//        for (int i = 0; i < 2; i++) {
//            for (int j = 0; j < 5; j++) {
//                for (File i: fileDir.removeAll(testFiles.get(j))) {
//                    readFile(i);
//                    // REMEMBER TO PRINT MEAN AND std dev
//                }
//                for (File i: testFiles.get(j)){
//                    //test
//                    // REMEMBER TO PRINT MEAN AND std dev
//                }
//            }
//        }
//    }

    public static void main(String[] args){

        boolean train = false;
        boolean test = false;
        boolean male = false, female = false;
        int i = 0;
        try {
            while(i < args.length){
                if (args[i].equalsIgnoreCase("-test")){
                    test = true;
                    i++;
                }
                else if (args[i].equalsIgnoreCase("-train")){
                    train = true;
                    i++;
                }
                else if (args[i].equalsIgnoreCase("male")){
                    male = true;
                    i++;
                }
                else if (args[i].equalsIgnoreCase("Female")){
                    female = true;
                    i++;
                }
                else if (args[i].equalsIgnoreCase("Test")){
                    test = true;
                    i++;
                }
                else {
                    i++;
                    //throw new IllegalArgumentException();
                    System.out.println(args[i]);
                }
            }
        }
        catch(Exception e) {
            System.err.println("Unknown Command. Please use allowed commands.");
            System.exit(1);
        }

        if (train && test && male && female && test){
            // Do something
        }

        else if (train && male && female) {
            Network NN = new Network();
            File[] males = new File("./Male/").listFiles();
            File[] females = new File("./Female/").listFiles();
            File[] testDir = new File("./Test/").listFiles();

            ArrayList<File> people = new ArrayList<File>();
            Map<File, Double> map = new HashMap<File, Double>();

            for (File m : males) {
                people.add(m);
                map.put(m, .9);
            }
            for (File f : females) {
                people.add(f);
                map.put(f, .1);
            }
            for (int x = 0; x < 20; x++) {

                Collections.shuffle(people);

                for (File p : people) {
                    if (p.getName().toLowerCase().endsWith(".txt")) {
                        ArrayList<Double> values = readFile(p);
                        NN.train(values, map.get(p));
                    }
                }
            }

            System.out.println("\n");
            for (File m : testDir) {
                if (m.getName().toLowerCase().endsWith((".txt"))) {
                    ArrayList<Double> values = readFile(m);
                    if (values != null) {
                        NN.test(values);
                    }
                }
            }
        }

        else if (test && test) {

            // Do something
        }

        else {
            //System.out.println("Wrong arguments supplied.");
            System.exit(3);
        }
    }
}