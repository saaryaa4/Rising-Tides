package tides;

import java.util.*;

/**
 * This class contains methods that provide information about select terrains 
 * using 2D arrays. Uses floodfill to flood given maps and uses that 
 * information to understand the potential impacts. 
 * Instance Variables:
 *  - a double array for all the heights for each cell
 *  - a GridLocation array for the sources of water on empty terrain 
 * 
 * @author Original Creator Keith Scharz (NIFTY STANFORD) 
 * @author Vian Miranda (Rutgers University)
 */
public class RisingTides {

    // Instance variables
    private double[][] terrain;     // an array for all the heights for each cell
    private GridLocation[] sources; // an array for the sources of water on empty terrain 

    /**
     * DO NOT EDIT!
     * Constructor for RisingTides.
     * @param terrain passes in the selected terrain 
     */
    public RisingTides(Terrain terrain) {
        this.terrain = terrain.heights;
        this.sources = terrain.sources;
    }

    /**
     * Find the lowest and highest point of the terrain and output it.
     * 
     * @return double[][], with index 0 and index 1 being the lowest and 
     * highest points of the terrain, respectively
     */
    public double[] elevationExtrema() {

        /* WRITE YOUR CODE BELOW */
        double max = terrain[0][0];
        double min = terrain[0][0];
        
        for(int row = 0; row < terrain.length; row++){
            
            for(int col = 0; col < terrain[row].length; col++){
                
                if(terrain[row][col] < min){
                    min = terrain[row][col];
                }
                
                if(terrain[row][col] > max){
                    max = terrain[row][col];
                }

            }

        }

        double[] min_max = {min, max};

        return min_max; 
    }

    /**
     * Implement the floodfill algorithm using the provided terrain and sources.
     * 
     * All water originates from the source GridLocation. If the height of the 
     * water is greater than that of the neighboring terrain, flood the cells. 
     * Repeat iteratively till the neighboring terrain is higher than the water 
     * height.
     * 
     * 
     * @param height of the water
     * @return boolean[][], where flooded cells are true, otherwise false
     */
    public boolean[][] floodedRegionsIn(double height) {
        
        /* WRITE YOUR CODE BELOW */
        boolean[][] resulting_array = new boolean[terrain.length][terrain[0].length];
        
        ArrayList<GridLocation> sourceArray = new ArrayList<>();

        //add sources to new sourceArray
        for(int loc = 0; loc < sources.length; loc++){
            sourceArray.add(sources[loc]);
            resulting_array[sources[loc].row][sources[loc].col] = true;
        }

        //while source array is not empty
        while(!sourceArray.isEmpty()){
            GridLocation temp = sourceArray.remove(0); //store first element of sourceArray

            //North
            if((temp.row > 0 ) && //North exists
            (!resulting_array[temp.row - 1][temp.col]) && //North value is not true
            (terrain[temp.row - 1][temp.col] <= height)){ //North height is less than or equal to 
                GridLocation north  = new GridLocation(temp.row - 1, temp.col);
                resulting_array[temp.row - 1][temp.col] = true;
                sourceArray.add(north);
            }

            //South
            if((temp.row < terrain.length - 1) && 
            (!resulting_array[temp.row + 1][temp.col]) && 
            (terrain[temp.row + 1][temp.col] <= height)){
                GridLocation south  = new GridLocation(temp.row + 1, temp.col);
                resulting_array[temp.row + 1][temp.col] = true;
                sourceArray.add(south);
            }

            //West
            if((temp.col > 0) &&
            (!resulting_array[temp.row][temp.col - 1]) && 
            (terrain[temp.row][temp.col - 1] <= height)){
                GridLocation west  = new GridLocation(temp.row, temp.col - 1);
                resulting_array[temp.row][temp.col -1] = true;
                sourceArray.add(west);
            }

            //East
            if((temp.col < terrain[0].length -1) &&
            (!resulting_array[temp.row][temp.col + 1]) && 
            (terrain[temp.row][temp.col + 1] <= height)){
                GridLocation east  = new GridLocation(temp.row, temp.col + 1);
                resulting_array[temp.row][temp.col + 1] = true;
                sourceArray.add(east);
            }

        }
        return resulting_array; 
    }

    /**
     * Checks if a given cell is flooded at a certain water height.
     * 
     * @param height of the water
     * @param cell location 
     * @return boolean, true if cell is flooded, otherwise false
     */
    public boolean isFlooded(double height, GridLocation cell) {
        
        /* WRITE YOUR CODE BELOW */
        boolean[][] resulting_array = floodedRegionsIn(height);
        boolean flooded = resulting_array[cell.row][cell.col];
        if(flooded == true){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Given the water height and a GridLocation find the difference between 
     * the chosen cells height and the water height.
     * 
     * If the return value is negative, the Driver will display "meters below"
     * If the return value is positive, the Driver will display "meters above"
     * The value displayed will be positive.
     * 
     * @param height of the water
     * @param cell location
     * @return double, representing how high/deep a cell is above/below water
     */
    public double heightAboveWater(double height, GridLocation cell) {
        
        /* WRITE YOUR CODE BELOW */
        double difference = terrain[cell.row][cell.col] - height;
        return difference; 
    }

    /**
     * Total land available (not underwater) given a certain water height.
     * 
     * @param height of the water
     * @return int, representing every cell above water
     */
    public int totalVisibleLand(double height) {
        
        /* WRITE YOUR CODE BELOW */
        boolean[][] array = floodedRegionsIn(height);
        int count = 0;

        for(int row = 0; row < array.length; row++){
            for(int col = 0; col < array[row].length; col++){
                if(!array[row][col]){
                    count++;
                }
            }
        }

        return count; 
    } 


    /**
     * Given 2 heights, find the difference in land available at each height. 
     * 
     * If the return value is negative, the Driver will display "Will gain"
     * If the return value is positive, the Driver will display "Will lose"
     * The value displayed will be positive.
     * 
     * @param height of the water
     * @param newHeight the future height of the water
     * @return int, representing the amount of land lost or gained
     */
    public int landLost(double height, double newHeight) {
        
        /* WRITE YOUR CODE BELOW */
        int difference = totalVisibleLand(height) - totalVisibleLand(newHeight);
        return difference; // substitute this line. It is provided so that the code compiles.
    }

    /**
     * Count the total number of islands on the flooded terrain.
     * 
     * Parts of the terrain are considered "islands" if they are completely 
     * surround by water in all 8-directions. Should there be a direction (ie. 
     * left corner) where a certain piece of land is connected to another 
     * landmass, this should be considered as one island. A better example 
     * would be if there were two landmasses connected by one cell. Although 
     * seemingly two islands, after further inspection it should be realized 
     * this is one single island. Only if this connection were to be removed 
     * (height of water increased) should these two landmasses be considered 
     * two separate islands.
     * 
     * @param height of the water
     * @return int, representing the total number of islands
     */
    public int numOfIslands(double height) {
        
        /* WRITE YOUR CODE BELOW */
        boolean[][] array = floodedRegionsIn(height);
        WeightedQuickUnionUF islands = new WeightedQuickUnionUF(terrain.length, terrain[0].length);

        for(int row = 0; row < terrain.length; row++){
            for(int col = 0; col < terrain[0].length; col++){
                if(!array[row][col]){
                    //check diagonal values
                    for(int i = -1; i <= 1; i++){
                        for(int j = -1; j <= 1; j++){
                            
                            //if flooded
                            if((i + row < 0) || (j + col < 0) || 
                            (i + row >= terrain.length) || (j + col >= terrain[0].length)){
                                continue;
                            }

                            //if unflooded
                            if(!array[row + i][col + j]){
                                islands.union(new GridLocation(row, col), new GridLocation(row + i, col + j));
                            }
                        }
                    }

                }

                //add new parents to Array List, traverse terrain array, and implement find
            }          
        }
        
        ArrayList<GridLocation> parents = new ArrayList<>();
        for(int r = 0; r < terrain.length; r++){
            for(int c = 0; c < terrain[0].length; c++){
                GridLocation findRoot  = islands.find(new GridLocation(r, c));
                    if(!array[r][c] && !parents.contains(findRoot)){
                        parents.add(findRoot);
                    }
                }
            }        
        return parents.size(); // substitute this line. It is provided so that the code compiles.
    }
}
