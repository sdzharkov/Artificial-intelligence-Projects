# Artificial Intelligence Projects

Partner: Otto P. Chen https://github.com/ottopchen

## Project 1: Using A* Algorithm to Climb Mountains

Given a topographical map of Mount St. Helens, this algorithm finds the most optimal path to the summit. The A* algorithm provides a more optimal solution than Dijkstra's algorithm by taking account heuristics for certain cost paths. This Reduces the cost of searching for irrelevant nodes while finding the shortest path, and is faster/computationally more efficient that Dijkstra's algorithm. 

Dijkstra
PathCost, 505.0, Uncovered, 1147606, TimeTaken, 5202

Our Algorithm: AStarExp
PathCost, 505.0, Uncovered, 89278, TimeTaken, 551

<img src="https://github.com/sdzharkov/Artificial-intelligence-Projects/blob/master/ASTAR-ToDistribute/Dijkstra.png" width="400px" height="400px"> <img src="https://github.com/sdzharkov/Artificial-intelligence-Projects/blob/master/ASTAR-ToDistribute/Astar.png" width="400px" height="400px">


## Project 2: Connect 4 Bot

A connect four bot built with with the min-max algorithm and alpha-beta pruning. The algorithm searches deeper into the game tree until finding a consistent win or loss outcome. The algorithm takes into consideration the placement of 1, 2, and three in a rows, while also paying attention to open spots in the future. 

To Run: indicate the player by using the command -p1 of -p2. If a player isn't specified, the user will be the default player. -H and -W signify height and width parameters of the function. 

```sh
javac *.java
java Main -p2 alphabeta_NightsWatch
```

## Project 3: Neural Network

A neural network built to detect gender in a series of images. The program creates a simple forward feeding network with backpropogation. We create 15360 input nodes, 5 hidden nodes, and one output node to process the male and female directories. After 10 epocs of training we were able to predict the test training set with 100% accuracy. 

To Run: The program takes in three directories - Male, Female, and test. 
```sh
javac main.java
java main -train ./Male/ ./Female/ -test ./test/
```
