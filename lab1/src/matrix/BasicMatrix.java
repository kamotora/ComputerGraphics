package matrix;

import java.util.Arrays;

public class BasicMatrix {

    //Центр проецирования для перспективы
    public static double C = 120;


    //Угол проецирования для кабинетной проекции
    public static double angle = 45;

    // получить матрицу вращения вокруг оси X на угол phi
    public static double[][] getRotateMatrixX (double phi) {
        double[][] matrix = {
                {1,              0,             0, 0},
                {0,  Math.cos(phi), Math.sin(phi), 0},
                {0, -Math.sin(phi), Math.cos(phi), 0},
                {0,              0,             0, 1}
        };
        return matrix;
    }

    // получить матрицу вращения вокруг оси Y на угол zen
    public static double[][] getRotateMatrixY (double zen) {
        double[][] matrix = {
                {Math.cos(zen), 0, -Math.sin(zen), 0},
                {            0, 1,              0, 0},
                {Math.sin(zen), 0,  Math.cos(zen), 0},
                {            0, 0,              0, 1}
        };
        return matrix;
    }

    // получить матрицу вращения вокруг оси Z на угол ksi
    public static double[][] getRotateMatrixZ (double ksi) {
        double[][] matrix = {
                { Math.cos(ksi), Math.sin(ksi), 0, 0},
                {-Math.sin(ksi), Math.cos(ksi), 0, 0},
                {             0,             0, 1, 0},
                {             0,             0, 0, 1}
        };
        return matrix;
    }

    // получить матрицу растяжения
    public static double[][] getScaleMatrix (double xScale, double yScale, double zScale) {
        double[][] matrix = {
                {xScale,      0,      0, 0},
                {     0, yScale,      0, 0},
                {     0,      0, zScale, 0},
                {     0,      0,      0, 1}
        };
        return matrix;
    }

    // получить матрицу отражения относительно оси Z (плоскость XY)
    public static double[][] getMirrorZMatrix () {
        double[][] matrix = {
            {1, 0,  0, 0},
            {0, 1,  0, 0},
            {0, 0, -1, 0},
            {0, 0,  0, 1}
        };
        return matrix;
    }

    // получить матрицу отражения относительно оси X (плоскость YZ)
    public static double[][] getMirrorXMatrix () {
        double[][] matrix = {
                {-1, 0,  0, 0},
                { 0, 1,  0, 0},
                { 0, 0,  1, 0},
                { 0, 0,  0, 1}
        };
        return matrix;
    }

    // получить матрицу отражения относительно оси Y (плоскость XZ)
    public static double[][] getMirrorYMatrix () {
        double[][] matrix = {
                { 1,  0,  0, 0},
                { 0, -1,  0, 0},
                { 0,  0,  1, 0},
                { 0,  0,  0, 1}
        };
        return matrix;
    }

    // получить матрицу переноса на вектор (xTr, yTr, zTr)
    public static double[][] getTransferMatrix (double xTr, double yTr, double zTr) {
        double[][] matrix = {
                {  1,   0,   0, 0},
                {  0,   1,   0 ,0},
                {  0,   0,   1, 0},
                {xTr, yTr, zTr, 1}
        };
        return matrix;
    }

    // кабинетная проекция
    public static double[][] getCabinetMatrix() {
        double[][] matrix = {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {-0.5*Math.cos(Math.toRadians(angle)), 0.5*Math.sin(Math.toRadians(angle)), 0, 0},
                {0, 0, 0, 1}
        };
        return matrix;
    }

    // перспективная проекция
    public static double[][] getPerspectiveMatrix () {
        //С в 3 строке, т.к. проекция на ось Z
        double[][] matrix = {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, -1/C},
                {0, 0, 0, 1}
        };
        return matrix;
    }

    public static void printMatrix(double [][] matrix, String matrixName){
        System.out.println();
        System.out.println(matrixName);
        for(int i = 0; i < matrix.length; i++)
            System.out.println(Arrays.toString(matrix[i]));
    }
}
