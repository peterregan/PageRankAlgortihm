public class PageRankMain {


    // ------ change these two values to alter the generated graphs -------
    static final int PAGES = 5; // number of pages or vertices
    static final int LINKS = 19; // number of links or edges
    static final long SEED = 20; // number to change the randomness of what vertices the edges go to and from
    static final int ITERATIONS = 10; // number of iterations of the page rank algorithm applied

    public static void main (String[] args){

        // create a page ranker
        PageRanker p = new PageRanker(PAGES,LINKS,SEED);
        p.rank(ITERATIONS);
    }
}
