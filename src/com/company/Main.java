package com.company;
import java.io.*;
import java.util.*;

 class Main {

     static double [] cell = new double[3] ;
     static String [] auxiliary;
     static double[] angles = new double[3];
     static ArrayList<double[]> coordinates_file = new ArrayList<double[]>();
    static String name = "VO2_mp-1102963";

    static double add_angle(String line){
        auxiliary = line.split("\\s+");
        double angle = Double.parseDouble(auxiliary[1]);

        angle =   Math.toRadians(Double.parseDouble(auxiliary[1]));
        return angle;
    }

    static double[] setCoordinates(String line){
        double [] axis = new double[4];
        auxiliary = line.split("\\s+");
       if (auxiliary[1].contains("V")){
           axis[0] = 1;
       }
        if (auxiliary[1].contains("O")){
            axis[0] = 2;
        }

        axis[1] = Double.parseDouble(auxiliary[4]);
        axis[2] = Double.parseDouble(auxiliary[5]);
        axis[3] = Double.parseDouble(auxiliary[6]);

        return axis;

    }

static void fileWriting(double [][] final_coordinates, double[] names){
    FileWriter wr = null;
    try {
        wr = new FileWriter(new File("D://VOx//struktury//"+name));
        for (int i = 0; i<final_coordinates.length;i++){
            if(names[i] == 1){
            wr.write("V 1 ");}
            if(names[i] == 2){
                wr.write("O -1 ");}

            wr.write(final_coordinates[i][0]+" ");
            wr.write(final_coordinates[i][1]+" ");
            wr.write(final_coordinates[i][2]+ "\n");

            }
        wr.flush();
    } catch (IOException e) {
        e.printStackTrace();
    }


}

   static double multiplyMatricesCell(double[][] firstMatrix, double[][] secondMatrix, int row, int col) {
         double cell = 0;
         for (int i = 0; i < secondMatrix.length; i++) {
             cell += firstMatrix[row][i] * secondMatrix[i][col];
         }
         return cell;
     }

   public static void main(String[] args) {
	// write your code here
       String line;

       int atoms_sum = 0;
       double n1 = 5;
       double n2 = 5;
       double n3 = 5;

        try (BufferedReader reader = new BufferedReader(new FileReader("D://VOx//struktury//"+name+"_conventional_standard"))) {
          do{
            line = reader.readLine();
            if(line.contains("cell_length_a")){
            auxiliary = line.split("\\s+");
                cell[0] = Double.parseDouble(auxiliary[1]);
            }
            if(line.contains("cell_length_b")){
                auxiliary = line.split("\\s+");
                cell[1] = Double.parseDouble(auxiliary[1]);
            }
            if(line.contains("cell_length_c")){
                auxiliary = line.split("\\s+");
                cell[2] = Double.parseDouble(auxiliary[1]);
            }

            if(line.contains("_cell_angle_alpha")){
                angles[0] = add_angle(line);
            }
            if(line.contains("_cell_angle_beta")){
                angles[1] = add_angle(line);
            }
            if(line.contains("_cell_angle_gamma")){
                angles[2] = add_angle(line);
            }

            if(line.contains("atom_site_occupancy")){
              line=  reader.readLine();

              while(line != null){
                  coordinates_file.add(setCoordinates(line));
                  line=  reader.readLine();
              }


            }



        }while(line != null);
        } catch (Exception e) {
            e.printStackTrace();
        } //end of the reading of the file


       double[][] coordinates = coordinates_file.toArray(new double[coordinates_file.size()][4]);


       double[][] unit_cell_matrix = new double[3][3];
       unit_cell_matrix[0][0] = cell[0]*n1;
       unit_cell_matrix[0][1] = 0;
       unit_cell_matrix[0][2] = 0;

       System.out.println(cell[1]+"   "+Math.cos(angles[2])+"  "+n2);
       unit_cell_matrix[1][0] = cell[1]*Math.cos(angles[2])*n2;
       unit_cell_matrix[1][1] = cell[1]*Math.sin(angles[2])*n2;
       unit_cell_matrix[1][2] = 0;

       double x =cell[2]*Math.cos(angles[1]);
       double y = (1.0/Math.sin(angles[2]))*((cell[2]*Math.cos(angles[0]))-(x*Math.cos(angles[2])));
       unit_cell_matrix[2][0] = x*n3;
       unit_cell_matrix[2][1] = y*n3;
       unit_cell_matrix[2][2] = Math.sqrt((cell[2]*cell[2])-(x*x)-(y*y))*n3;

//calculation of final coordinates

       double [][] final_coordinates  = new double[(int)(coordinates.length*n1*n2*n3)][3];
       double [] names   = new double[(int)(coordinates.length*n1*n2*n3)];
int fin_line=0;


    for(int lineNo = 0; lineNo < coordinates.length;lineNo++){
        for (int m1 = 0; m1<n1;m1++){
            for (int m2 = 0; m2<n2;m2++){
                for (int m3 = 0; m3< n3; m3++){
                    names[fin_line] = coordinates[lineNo][0];
                    final_coordinates[fin_line][0] = (coordinates[lineNo][1]/n1)+(m1/n1);
                    final_coordinates[fin_line][1] = (coordinates[lineNo][2]/n2)+(m2/n2);
                    final_coordinates[fin_line][2] = (coordinates[lineNo][3]/n3)+(m3/n3);
                    fin_line++;
                }
            }
        }

    }

    double [][] finish = new double[final_coordinates.length][unit_cell_matrix.length];
    for (int row=0;row<final_coordinates.length;row++){
        for (int col =0; col<3 ;col++ ){
            finish[row][col] = multiplyMatricesCell(final_coordinates, unit_cell_matrix, row, col);
        }
    }

fileWriting(finish,names);





    }
}
