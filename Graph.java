
package graph;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

public class Graph {

    // klasa wierzcholkow
    public static class Node {
        int Number;
        ArrayList<Node> Connections;
        
        int whichMiniGraph;
        
        // Djikstra
        boolean Visited;
        int [] Distances;
        
        public void assignMiniGraph () {
        	for (int i = 0; i < miniGraphs.size(); i++) {
        		for (int j = 0; j < miniGraphs.get(i).size(); j++) {
        			if (this.Number == miniGraphs.get(i).get(j)) {
        				this.whichMiniGraph = i;
        			}
        		}
        	}
        }
        
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
        
        public int getWhichMiniGraph () {
        	return whichMiniGraph;
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
    private static ArrayList<ArrayList<Integer>> miniGraphs;

    public Graph () {
        this.Nodes = new ArrayList<>();
        this.NodeIndexes = new ArrayList<>();
        Graph.miniGraphs = new ArrayList<>();
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
            if (!node.Connections.isEmpty()) System.out.println(node);
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

    public static int how_many_nodes(Graph g) {
        int sum = 0;
        for (Node node : g.Nodes) sum++;
        return sum;
    }

    public void partition_graph(Graph g, int k, double errorMargin, ArrayList<Integer> assigned) {
        try {
            if (errorMargin <= 0.0 || errorMargin >= 0.5) {
                System.err.println("Wrong Error Margin!");
                return;
            }
            if (k < 2 || k > how_many_nodes(g)/2){
                System.err.println("Wrong number of parts!");
                return;
            }

            int nodesCounter = how_many_nodes(g)/k;
            if (how_many_nodes(g)%k != 0) nodesCounter++;

            g.Dijkstra(0, 0);
            int distance = 0;
            int subgraph = 0; // numer podgrafu
            int index = 0; // index w tablicy podgrafu

            while (subgraph < k-1){
                distance = 0;
                index = 0;
                while (index < nodesCounter){
                    int added = 0;
                    for (int i = 0; i<how_many_nodes(g); i++){
                        if (g.Nodes.get(i).Distances[0] == distance && assigned.get(i) == 0){
                            miniGraphs.get(subgraph).add(g.Nodes.get(i).Number);
                            assigned.set(i, 1);
                            index++;
                            added = 1;
                        }
                        if (index >= nodesCounter) break;
                    }
                    if (added == 0) distance++; // nic nie dodalismy
                }
                subgraph++;
            }
            index = 0;
            for (int i = 0; i < how_many_nodes(g); i++){
                if (assigned.get(i) == 0){
                    miniGraphs.get(subgraph).add(g.Nodes.get(i).Number);
                    assigned.set(i, 1);
                }
            }
            //for (int i = 0; i < k; i++) {
            //    System.out.print("Podgraf" + i + ": ");
            //    for (int node : miniGraphs.get(i)) {
            //        System.out.print(node + " ");
            //    }
            //    System.out.println("");
            //}
            
            for (int i = 0; i < Nodes.size(); i++) {
            	Nodes.get(i).assignMiniGraph();
            }
            
        } catch (Exception e) {
            System.err.println("Can't partition the graph!");
            System.exit(1);
        }
    }
    
    // UI GRAFU W GRAPHSTREAM
    
    public static SingleGraph graphDisplay = new SingleGraph("display");
    public static String[] UIColors = {
    		"shape:circle;fill-color: red;size: 20px; text-alignment: center;",
    		"shape:circle;fill-color: green;size: 20px; text-alignment: center;",
    		"shape:circle;fill-color: blue;size: 20px; text-alignment: center;",
    		"shape:circle;fill-color: yellow;size: 20px; text-alignment: center;",
    		"shape:circle;fill-color: pink;size: 20px; text-alignment: center;",
    		"shape:circle;fill-color: orange;size: 20px; text-alignment: center;",
    		"shape:circle;fill-color: purple;size: 20px; text-alignment: center;",
    		"shape:circle;fill-color: brown;size: 20px; text-alignment: center;",
    		"shape:circle;fill-color: tomato;size: 20px; text-alignment: center;",
    		"shape:circle;fill-color: gray;size: 20px; text-alignment: center;"
    };
    
    public void UI_initialize () {
    	
    	for (int i = 0; i < Nodes.size(); i++) {
    		graphDisplay.addNode(String.valueOf(Nodes.get(i).getNumber()));
    	}
    	
		for (int i = 0; i < Nodes.size(); i++) {
			for (int j = 0; j < Nodes.get(i).Connections.size(); j++) {
				graphDisplay.addEdge(String.valueOf(Nodes.get(i).getNumber())+String.valueOf(Nodes.get(i).Connections.get(j).getNumber()), String.valueOf(Nodes.get(i).getNumber()), String.valueOf(Nodes.get(i).Connections.get(j).getNumber()));
			}
		}
		
		for (org.graphstream.graph.Node n : graphDisplay) {
			int currentNodeNumber = Integer.parseInt(n.getId());
			n.setAttribute("xy", NodeIndexes.get(currentNodeNumber) % Width, -(NodeIndexes.get(currentNodeNumber) / Width));
			n.setAttribute("ui.label", n.getId());
			n.setAttribute("ui.style", UIColors[Nodes.get(Integer.valueOf(n.getId())).getWhichMiniGraph()]);
		}

	}
	
	public ViewPanel UI_get () {
		Viewer viewer = new SwingViewer(graphDisplay, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);
		
		viewPanel.setPreferredSize(new Dimension(450, 450));
		
		return viewPanel;
	}

    public static void main (String [] args){
    	System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
    	System.setProperty("org.graphstream.ui", "swing");
    	
    	GUI graphGUI = new GUI();
    	
    	SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {				
					graphGUI.show();
					
					graphGUI.setTxtLoadButtonListener(new ActionListener () {
						@Override
						public void actionPerformed(ActionEvent e) {
							Graph g = null;
					        g = new Graph();
					        g.readtxt("C:\\Users\\szymo\\eclipse-workspace\\Jimp\\src\\graph\\" + graphGUI.getUploadTxtField().getCurrentInput());
					        int partitionCount = Integer.valueOf(graphGUI.getPartitionsInputField().getCurrentInput()); // liczba czesci do podzialu; np. 2,3,4,6
					        double margin = Float.valueOf(graphGUI.getMarginInputField().getCurrentInput()); // margines bledu
					        
					        miniGraphs = new ArrayList<ArrayList<Integer>>();
					        for (int i = 0; i < partitionCount; i++){
					            miniGraphs.add(new ArrayList<>());
					        }
					        ArrayList<Integer> assigned = new ArrayList<>();
					        for (int i = 0; i < how_many_nodes(g); i++){
					            assigned.add(0);
					        }
					        g.generateAdjMtx();
					        g.partition_graph(g, partitionCount, margin, assigned);
					        
					        graphDisplay = new SingleGraph("display");
					        g.UI_initialize();
					        
							graphGUI.setGraphFieldPanel(g.UI_get());
					        
						}
				});
			}
    	});
    }
}
