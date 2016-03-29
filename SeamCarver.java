import java.awt.Color;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.ResizingArrayStack;

public class SeamCarver {
    private Picture pic;
    private double[][] P_energy, Inv_energy;
    
    public SeamCarver(Picture picture) {
        pic = new Picture(picture);
        P_energy = new double[pic.height()][pic.width()];
        Inv_energy = new double[pic.width()][pic.height()];

        initEnergy();
     
    }
    
    public int[] findVerticalSeam() {
        double[][] dist = new double[pic.height()][pic.width()];
        int[][] verticTo = new int[pic.height()][pic.width()];
        
        for(int i = 1; i < pic.height(); i++ ) {
            for(int j = 0; j < pic.width(); j++) {
                dist[i][j] = -1;
            }
        }
        
        for(int j = 0; j < pic.width(); j++) {
            dist[0][j] = P_energy[0][j];
        }
        
        for(int j = 0; j < pic.width(); j++) {
            verticTo[0][j] = 0;
        }
        
        vertSP(P_energy, dist, verticTo);
        
        ResizingArrayStack<Integer> rev_route = new ResizingArrayStack<Integer>();
        int endPoint = minIndex(dist[pic.height() - 1]);
        rev_route.push(endPoint);

        int next = verticTo[pic.height() - 1][endPoint];

        for(int i = pic.height() - 2; i >= 0; i--) {
            rev_route.push(next);
            next = verticTo[i][next];
        }
        
        int[] route = new int[pic.height()];
        for(int i = 0; i < pic.height(); i++) 
            route[i] = rev_route.pop();
         
        return route;         
    }
    
    private int minIndex(double[] array) {
        double min = array[0];
        int loc = 0;
        
        for(int i = 1; i < array.length; i++) {
            if(array[i] < min) {
                loc = i;
                min = array[i];            
            }        
        }
        return loc;
    }
    
    private void vertSP(double[][] energy, double[][] dist, int[][] verticTo) {
        for(int i = 0; i < energy.length - 1; i++) { // for next row
            for(int j = 0; j < energy[0].length; j++) {
                int[] curPoint = new int[] {i, j};
                int[] nextPs = adj(j, energy);
                for(int nP : nextPs) {
                    refresh(curPoint, new int[] {i + 1, nP}, energy, dist, verticTo);
                }
            }
        }
        
    }
    
    private int[] adj(int x, double[][] energy) {
        // for point at column x, give it adjacency neighbors at next row
        // Constrained by width columns<-> width, 
        if(x == 0) {
            return new int[] {0, 1};
        } else if(x == energy[0].length - 1) {
            return new int[] {energy[0].length - 2, energy[0].length - 1};
        } else {
            return new int[] { x - 1, x, x + 1};
        }      
    }
    
    private void refresh(int[] vFrom, int[] vTo, double[][] energy, double[][] dist, int[][] verticTo) {
        // vFrom[0] for row, vFrom[1] for column
        if(dist[vFrom[0]][vFrom[1]] + energy[vTo[0]][vTo[1]] < dist[vTo[0]][vTo[1]] || dist[vTo[0]][vTo[1]] < 0) {
            verticTo[vTo[0]][vTo[1]] = vFrom[1];  // record the above row column
            dist[vTo[0]][vTo[1]] = dist[vFrom[0]][vFrom[1]] + energy[vTo[0]][vTo[1]];
        }
    }
    
    public double energy(int x, int y) {
        // at column x, row y
        return P_energy[y][x]; // energy per row is picture per row
    }
    
    private void initEnergy() {
        for(int i = 0; i < pic.height(); i++) { // Iterator for rows
            for(int j = 0; j < pic.width(); j++) { // Iterator for Columns
                if(j == 0 || j == pic.width() - 1  || i == 0 || i == pic.height() - 1)
                    P_energy[i][j] = 1000;
                else
                    P_energy[i][j] = Math.sqrt(centerDiff(j, i));
            }
        }
    }
        
    private int centerDiff(int col, int row) {
        Color above = pic.get(col, row - 1);
        Color below = pic.get(col, row + 1);      
        int bdiff = above.getBlue() - below.getBlue();
        int rdiff = above.getRed() - below.getRed();
        int gdiff = above.getGreen() - below.getGreen();
        int vertDiff = bdiff * bdiff + rdiff * rdiff + gdiff * gdiff;

        above = pic.get(col - 1, row);
        below = pic.get(col + 1, row);    
        bdiff = above.getBlue() - below.getBlue();
        rdiff = above.getRed() - below.getRed();
        gdiff = above.getGreen() - below.getGreen();
        int horiDiff = bdiff * bdiff + rdiff * rdiff + gdiff * gdiff;
        
        return vertDiff + horiDiff;        
    }
    
    public int width() {
        return pic.width();
    }
    
    public int height() {
        return pic.height();
    }
    
    public Picture picture() {
        return pic;
    }
    
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        StdOut.printf("image is %d pixels wide by %d pixels high.\n", picture.width(), picture.height());
        
        SeamCarver sc = new SeamCarver(picture);
        
        StdOut.printf("Vroute \n");  
        int[] vroute = sc.findVerticalSeam();
        for(int item : vroute)
            StdOut.printf("%3d ", item);
    }
}
