import org.jgrapht.Graph;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.util.SupplierUtil;

import java.util.*;
import java.util.function.Supplier;

import static java.util.Arrays.*;


public class PageRanker {

    private Graph<Integer, DefaultEdge> graph_;
    private int vertexCount_;
    private double rank_;
    private int id_;


    /**
     * Creates a PageRanker Object which will generate a random unweighted directed graph with no loops.
     * Creates the graph with the desired number of pages which are denoted as vertices and links which are edges
     *
     * @param pages the number of pages or vertices for the graph
     * @param links the number of links or edges for the graph
     */
    public PageRanker(int pages, int links, long seed) {


        Supplier<Integer> vSupplier = new Supplier<Integer>() {
            @Override
            public Integer get() {
                return id_++;
            }
        };

        // Generate a random graph based on number of requested pages and links
        graph_ = new DirectedMultigraph<Integer, DefaultEdge>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);
        GnmRandomGraphGenerator<Integer, DefaultEdge> graphGenerator = new GnmRandomGraphGenerator<Integer, DefaultEdge>(pages, links, seed);
        graphGenerator.generateGraph(graph_);

        /* -------- BEGIN UNCOMMENT FOR 4 nodes with distinct edges ---------*/
//        // add vertices to graph
//        graph_.addVertex(0);
//        graph_.addVertex(1);
//        graph_.addVertex(2);
//        graph_.addVertex(3);
//
//
//        // create edges between vertices
//        graph_.addEdge(0,1);
//        graph_.addEdge(0,2);
//        graph_.addEdge(0,3);
//
//        graph_.addEdge(1,0);
//        graph_.addEdge(1,3);
//
//        graph_.addEdge(2,3);
//
//        graph_.addEdge(3,1);
//        graph_.addEdge(3,2);
//
//

        /* -------- END UNCOMMENT FOR 4 nodes with distinct edges ---------*/

        // initialize vertex count accordingly
        vertexCount_ = graph_.vertexSet().size();
        rank_ = ((double) 1) / vertexCount_;
    }


    /**
     * Uses the rank matrix multiplied by the rank vector in order to calculate page rank for a given amount of iterations.
     * @param Iterations the number of times desired to apply the page rank algorithm (greater number yields better results)
     * @return the vector representation of the page ranks
     */
    public double[] rank(int Iterations) {

        Double[][] matrix = createRankMatrix();
        double[] rankVector = new double[vertexCount_]; // the vector to be multiplied with the initial rank matrix
        double vertexRank; // rank for each vertex in graph

        // create initial rank matrix determinant on amount of vectors
        for (int i = 0; i < vertexCount_; i++) {
            rankVector[i] = rank_;
        }

        // continue for given iterations
        for (int i = 0; i < Iterations; i++) {
            System.out.println("ITERATION " + (i+1)+ ")");
            for (int col = 0; col < matrix.length; col++) {
                vertexRank = 0;
                for (int row = 0; row < matrix.length; row++) {
                    // matrix multiplication with the rank vector
                    vertexRank += (matrix[row][col] * rankVector[row]);
                }
                rankVector[col] = vertexRank;
                // print out all the ranks
                System.out.println(col + ": " + vertexRank);
            }
            System.out.println("--------------------------------");
        }
        rankAsInts(rankVector);

        return rankVector;
    }


    /**
     * Private helper method to number and print the rankings from least to greatest
     * by ordering the decimals from least (1 - highest rank) to greatest (n - least rank).
     *
     * @param rankVector the rank vector of fractions given by the rank method
     * @return the integer array containing the final rankings
     */
    private void rankAsInts(double[] rankVector){

        // initial unsorted map of rank values
        Map<Integer, Double> mapVectorsToRanks = new HashMap<>();
        // sorted map of rank values
        LinkedHashMap<Integer, Double> sortedMap = new LinkedHashMap<>();
        int index = 0;
        for (double rankAtIndex : rankVector){
            mapVectorsToRanks.put(index,rankAtIndex);
            index++;
        }

        // sort the new map
        mapVectorsToRanks.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));


        // replace sorted values with ordered numbers
        double order = 1;
        for (int i : sortedMap.keySet()){
            sortedMap.replace(i,order);
            order++;
        }

        // print out final page rankings
        System.out.println("Final Page Rankings: ");

        // String formatting for visual purposes
        String toPrint = sortedMap.toString().replace(".0","");
        toPrint = toPrint.replace("="," ==> #");
        System.out.println(toPrint);


    }

    /**
     * Private helper method which creates the initial rank matrix based on a directed graph.
     *
     * @return the initial rank matrix
     */
    private Double[][] createRankMatrix() {

        // initial rank matrix
        Double[][] matrix = new Double[vertexCount_][vertexCount_];

        // iterate matrix and set all values to zero
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                matrix[i][j] = Double.valueOf(0);
            }
        }

        // traverse each vertex
        for (int v : graph_.vertexSet()) {

            // obtain all edges for each vertex
            Set<DefaultEdge> targetEdges = graph_.outgoingEdgesOf(v);

            // total number of edges from current vertex
            int edgeCount = graph_.outgoingEdgesOf(v).size();

            // iterate through the edges
            for (DefaultEdge e : targetEdges) {

                // make the desired rank matrix slot by slot
                int outgoingEdge = parseEdge(e);
                matrix[v][outgoingEdge] = ((double) 1) / edgeCount;
            }

        }
        
        /* UNCOMMENT THIS LINE TO SEE PRINTED RANK MATRIX */
       // printMatrix(matrix);
        return matrix;
    }


    /**
     * Private helper method that will get the vertex number a given edge points to
     * @param e the edge to consider
     * @return the integer value of the vertex the edge points to
     */
    private int parseEdge(DefaultEdge e) {
        // String Representation of edge
        String edgeString = e.toString();

        // start and end values on where to parse the edge string
        int parseEnd = edgeString.indexOf(')');
        int parseStart = edgeString.lastIndexOf(' ');

        // parse the edge string
        edgeString = edgeString.substring(parseStart+1,parseEnd);

        // convert the string to an integer value
        int edgeToNum = Integer.parseInt(edgeString);

        return edgeToNum;
    }

    /**
     * Method which prints out a 2D array of Doubles
     * @param matrix the matrix or Double 2D to print out
     */
    private void printMatrix(Double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            System.out.print("| ");
            for (int j = 0; j < matrix.length; j++) {
                System.out.print( + matrix[j][i] + " ");
            }
            System.out.print(" |");
            System.out.println();
        }
    }


}
