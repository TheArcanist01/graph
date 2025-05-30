
package graph;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;

public class Graph {

    // klasa wierzcholkow
    public static class Node {
        int Number;
        ArrayList<Node> Connections;
        
        // Djikstra
        boolean Visited;
        int [] Distances;

        public Node (int Number){
            this.Number = Number;
            this.Connections = new ArrayList<>();
            this.Visited = false;
            this.Distances = new int[10]; //max 10 części podziału
            
            for (int i = 0; i < 10; i++) {
            	Distances[i] = 999999;
            }
            
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(Number).append(" -> ");
            for (Node n : Connections) {
                sb.append(n.Number).append(" ");
            }
            return sb.toString().trim();
        }
        
        public int getNumber () {
        	return Number;
        }
        
        public ArrayList<Node> getConnections () {
        	return Connections;
        }
        
        public int getIndexedDistance (int DistanceIndex) {
        	return Distances[DistanceIndex];
        }
        
        public boolean getVisited () {
        	return Visited;
        }
        
        public void setVisited (boolean ifVisited) {
        	Visited = ifVisited;
        }
        
        public void setDistance(int distance, int distanceArrayIndex) {
        	Distances[distanceArrayIndex] = distance;
        }
    }


    // wszystko do grafu
    int Height = 0;
    int Width = 0;
    ArrayList<Node> Nodes;
    ArrayList<Integer> NodeIndexes;
    ArrayList<Integer> AdjacencyMatrix;

    public Graph () {
        this.Nodes = new ArrayList<>();
        this.NodeIndexes = new ArrayList<>();
    }

    public void readtxt (String name) {
        boolean ifMatrix = true; //poczatek pliku to macierz indeksow, a potem dopiero polaczenia
        int index = 0; //indeks w NodeIndexes
        int nodeNumber = 0; 
        String line = null;
        String rememberedLine = null; //potrzebne do konkretnego miejsca po przeczytaniu macierzy
        try {
            FileReader read = new FileReader(name);
            BufferedReader buffer = new BufferedReader(read);
            while ( (line = buffer.readLine()) != null){
                line = line.trim(); //usuwa biale znaki z poczatku i konca
                if (line.isEmpty()) continue;

                if (line.matches("\\[.*\\]")) { 
                    line = line.replace("[", "").replace("]", "").trim();
                    String [] content = line.split("\\s+");
                    Width = content.length;
                    for (String ifNode : content) { //patrzymy gdzie sa wierzcholki
                        if ( ifNode.equals("1.")) {
                            Node node = new Node(nodeNumber);
                            nodeNumber++;
                            Nodes.add(node);
                            NodeIndexes.add(index);
                        }
                        index++;
                    }
                } else {
                    ifMatrix = false;
                    rememberedLine = line;
                    break; 
                }
                Height++;
            }
            if (rememberedLine != null && rememberedLine.contains("-")) {
                String[] connection = rememberedLine.split("-");
                int previous = Integer.parseInt(connection[0].trim());
                int next = Integer.parseInt(connection[1].trim());
                if (previous < Nodes.size() && next < Nodes.size()) {
                    Nodes.get(previous).Connections.add(Nodes.get(next));
                }
            }
            while ( (line = buffer.readLine()) != null){
                if (!ifMatrix){
                    if (line.isEmpty()) continue;
                    line = line.trim();
                    if (!line.contains("-")) continue;

                    String[] connection = line.split("-");
                    int previous = Integer.parseInt(connection[0].trim()); 
                    int next = Integer.parseInt(connection[1].trim());

                    if (previous < Nodes.size() && next < Nodes.size()) {
                        Nodes.get(previous).Connections.add(Nodes.get(next));
                    }
                }
            }
            buffer.close();
            read.close();
        } catch (Exception e) {
            System.err.println("Problem z plikiem tekstowym: " + e.getMessage());
            System.exit(1);
        }
    }

    public void readbin(String name, int Height, int Width) {
        int nodeNumber = 0; // numer porzadkowy wierzcholka
        int index = 0; // indeks w tablicy nodeIndexes
        int r; // wartosc przeczytana z pliku 
        try {
            InputStream in = new FileInputStream(name);
            for (int i = 0; i < Height; i++) {
                for (int j = 0; j < Width * 2; j++) {
                    r = in.read();
                    if (r == 49){ // szukamy "1" w macierzy
                        Node node = new Node(nodeNumber);
                        nodeNumber++;
                        Nodes.add(node);
                        NodeIndexes.add(index);
                    }
                    index++;
                    if (r == -1) break;
                }
                int nl = in.read(); // \n
            }
            String number = "";
            int previous = -1;
            int next = -1;
            while ((r = in.read()) != -1) {
                if (r >= 48 && r <= 57) {
                    number += (r-48);
                } else if (r == 45){
                    previous = Integer.parseInt(number);
                    number = "";
                } else if (r == 10){
                    next = Integer.parseInt(number);
                    number = "";
                }

                if (previous < Nodes.size() && next < Nodes.size() && previous != -1 && next != -1) {
                    Nodes.get(previous).Connections.add(Nodes.get(next));
                    previous = -1;
                    next = -1;
                }
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Problem z plikiem binarnym: " + e.getMessage());
            System.exit(2);
        }
    }

    public void printGraph() {
        for (Node node : Nodes) {
            System.out.println(node);
        }
    }
    
    // macierz sąsiedztwa dla grafu
    public void generateAdjMtx() {
    	
    	
    	int mtxSize = Nodes.size() * Nodes.size();
    	ArrayList<Integer> AdjMtx = new ArrayList<Integer>(mtxSize);
    	
    	// wypełnienie tablicy zerami
    	for (int i = 0; i < mtxSize; i++) {
    		AdjMtx.add(0);
    	}
    	
    	ArrayList<Node> emptyList = new ArrayList<Node>();
    	
    	for (int i = 0; i < Nodes.size() - 1; i++) {

            if (Nodes.get(i).getConnections() == emptyList) {
                continue;
            }
            
            for (int j = 0; j < Nodes.get(i).getConnections().size(); j++) {
            	AdjMtx.set(Nodes.size() * Nodes.get(i).getNumber() + Nodes.get(i).getConnections().get(j).getNumber(), 1);
            	AdjMtx.set(Nodes.size() * Nodes.get(i).getConnections().get(j).getNumber() + Nodes.get(i).getNumber(), 1);
            }
        }
    	
    	AdjacencyMatrix = AdjMtx;
    }
    
    // print macierzy sąsiedztwa
    public void printAdjMtx () {
    	System.out.print("  ");
    	for (int i = 0; i < Nodes.size(); i++) {
    		System.out.print(i + " ");
    	}
    	System.out.print("\n");
    	for (int i = 0; i < Nodes.size(); i++) {
			System.out.print(i + " ");
    		for (int j = 0; j < Nodes.size(); j++) {
    			System.out.print(AdjacencyMatrix.get((Nodes.size() * i) + j) + " ");
    		}
    		System.out.print("\n");
    	}
    }
    
    // odległości algorytmem Dijkstry
    public void Dijkstra(int startingNodeIndex, int nodeDistancesIndex) {
    	int visitedNodes = 1;
    	Nodes.get(startingNodeIndex).setVisited(true);
    	Nodes.get(startingNodeIndex).setDistance(0, nodeDistancesIndex);
    	
    	while (visitedNodes < Nodes.size()) {
    		for (int i = 0; i < Nodes.size(); i++) {
    			if (AdjacencyMatrix.get((startingNodeIndex * Nodes.size()) + i) == 0) {
    				continue;
    			} else if ((Nodes.get(i).Visited == false) && (Nodes.get(i).getIndexedDistance(nodeDistancesIndex) > (Nodes.get(startingNodeIndex).getIndexedDistance(nodeDistancesIndex) + 1))) {
    				Nodes.get(i).setDistance(Nodes.get(startingNodeIndex).getIndexedDistance(nodeDistancesIndex) + 1, nodeDistancesIndex);
    			}
    		}
    	
    	int unvisitedNodeIndex = 0;
    	for (int i = 0; i < Nodes.size(); i++) {
    		if (Nodes.get(i).getVisited() == false) {
    			unvisitedNodeIndex = i;
    			break;
    			}
    		}
    	
    	for (int i = 0; i < Nodes.size(); i++) {
    		if ((Nodes.get(i).getVisited() == false) && (Nodes.get(i).getIndexedDistance(nodeDistancesIndex) < Nodes.get(unvisitedNodeIndex).getIndexedDistance(nodeDistancesIndex))) {
    			unvisitedNodeIndex = i;
    		}
    		

    		}
    	
    	startingNodeIndex = unvisitedNodeIndex;
    	Nodes.get(startingNodeIndex).setVisited(true);
    	visitedNodes++;
    	}
    }
    
    public void printTestDijkstra (int Index) {
    	for (int i = 0; i < Nodes.size(); i++) {
    		System.out.println(Nodes.get(i).getNumber() + " dist " + Nodes.get(i).getIndexedDistance(Index));
    	}
    }

    public static void main (String [] args){
        Graph g = new Graph();
        //g.readtxt("res.txt");
        g.readtxt("C:\\Users\\szymo\\eclipse-workspace\\Jimp\\src\\graph\\res.txt");
        g.printGraph();
        
        g.generateAdjMtx();
        g.printAdjMtx();
        
        g.Dijkstra(0, 0);
        g.printTestDijkstra(0);
    }
}
