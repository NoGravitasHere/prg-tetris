package ga;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Pontus Söderlund
 */
public final class Genome {

    //***********************
    // Variables
    //***********************
    private String id;
    private int movesTaken, fitness;
    private double rowsCleared;         //The number of rows cleared
    private double weightedHeight;      //The height of the highest collumn
    private double cumulativeHeight;    //The sum of all heights
    private double holes;               //The number of holes
    private double bumpiness;           //The bumpiness of the board
    private double fitnessPerMove;

    //***********************
    // Constructor(s)
    //***********************
    public Genome(String values) {
        stringToValues(values);
    }

    //***********************
    // Support Methods
    //***********************
    public void stringToValues(String string) {

        StringBuilder sb = new StringBuilder(string);
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == ' ') {
                sb.deleteCharAt(i);
            }
        }

        rowsCleared = Double.valueOf(sb.substring(0, sb.indexOf(",") - 1));
        sb.delete(0, sb.indexOf(",") + 1);

        weightedHeight = Double.valueOf(sb.substring(0, sb.indexOf(",") - 1));
        sb.delete(0, sb.indexOf(",") + 1);

        cumulativeHeight = Double.valueOf(sb.substring(0, sb.indexOf(",") - 1));
        sb.delete(0, sb.indexOf(",") + 1);

        holes = Double.valueOf(sb.substring(0, sb.indexOf(",") - 1));
        sb.delete(0, sb.indexOf(",") + 1);

        bumpiness = Double.valueOf(sb.substring(0, sb.length()));
        sb.delete(0, sb.indexOf(",") + 1);
    }

    public void incrementMovesTaken() {
        this.movesTaken++;
    }

    @Override
    public String toString() {
        String s = "";
        s += "ID: " + id;
        s += ", Moves Taken: " + movesTaken;
        s += ", Fitness: " + fitness + " ";
        s += "\n" + rowsCleared;
        s += ", " + weightedHeight;
        s += ", " + cumulativeHeight;
        s += ", " + holes;
        s += ", " + bumpiness;
        return s;
    }

    //***********************
    // Getter Methods
    //***********************
    public String getValues() {
        String s = "";
        s += rowsCleared;
        s += "," + weightedHeight;
        s += "," + cumulativeHeight;
        s += "," + holes;
        s += "," + bumpiness;
        return s;
    }

    public int getFitness() {
        return fitness;
    }

    public int getMovesTaken() {
        return movesTaken;
    }

    public String getId() {
        return id;
    }

    public double getFitnessPerMove() {
        return Math.round((double) fitness / (double) movesTaken * 1000.0) / 1000.0;
    }

    public double getRowsCleared() {
        return rowsCleared;
    }

    public double getWeightedHeight() {
        return weightedHeight;
    }

    public double getCumulativeHeight() {
        return cumulativeHeight;
    }

    public double getHoles() {
        return holes;
    }

    public double getBumpiness() {
        return bumpiness;
    }

    //***********************
    // Setter Methods
    //***********************
    public void setMovesTaken(int movesTaken) {
        this.movesTaken = movesTaken;
    }

    public void setRowsCleared(double rowsCleared) {
        this.rowsCleared = rowsCleared;
    }

    public void setWeightedHeight(double weightedHeight) {
        this.weightedHeight = weightedHeight;
    }

    public void setCumulativeHeight(double cumulativeHeight) {
        this.cumulativeHeight = cumulativeHeight;
    }

    public void setHoles(double holes) {
        this.holes = holes;
    }

    public void setBumpiness(double bumpiness) {
        this.bumpiness = bumpiness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public void setId(String id) {
        this.id = id;
    }
}
