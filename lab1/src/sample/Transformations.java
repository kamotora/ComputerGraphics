package sample;

import matrix.MathMatrix;
import matrix.BasicMatrix;

import java.util.Arrays;

public class Transformations {

    private static double midX,midY,midZ;
    public static double[][] move(double [][] dots, double x, double y, double z) {
        return MathMatrix.multiple(dots, BasicMatrix.getTransferMatrix(x,y,z));
    }

    public static double[][] moveToStart(double [][] dots){
        midX = getMiddleAxis(dots,0);
        midY = getMiddleAxis(dots,1);
        midZ = getMiddleAxis(dots,2);
        //перемещаем к началу координат
        return move(dots,-midX,-midY,-midZ);
    }

    public static double[][] moveBack(double [][] dots){
        return move(dots,midX,midY,midZ);
    }
    public static double getMinAxis(double [][] dots, int type){
        double min = dots[0][type];
        for(int i = 1; i < dots.length;i++)
            if(min > dots[i][type])
                min =  dots[i][type];
        return min;
    }

    public static double getMaxAxis(double [][] dots, int type){
        double max = dots[0][type];
        for(int i = 1; i < dots.length;i++)
            if(max < dots[i][type])
                max =  dots[i][type];
        return max;
    }

    // вычисление средней точки по x,y,z
    public static double getMiddleAxis (double [][] dots, int type) {
        return (getMaxAxis(dots,type)+getMinAxis(dots,type))/2;
    }

    //Масштабирование
    public static double[][] scale(double [][] dots, double x, double y, double z){
        //double [][] transformedMatrix = moveToStart(dots);
        //масштабируем
        return  MathMatrix.multiple(dots,BasicMatrix.getScaleMatrix(x,y,z));
        //перемещаем на исходную
        //return moveBack(transformedMatrix);
    }

    public static double[][] rotateX(double [][] dots, double angleX){
        return   MathMatrix.multiple(dots,BasicMatrix.getRotateMatrixX(Math.toRadians(angleX)));
    }

    public static double[][] rotateY(double [][] dots, double angleY){
        return   MathMatrix.multiple(dots,BasicMatrix.getRotateMatrixY(Math.toRadians(angleY)));
    }

    public static double[][] rotateZ(double [][] dots, double angleZ){
        return   MathMatrix.multiple(dots,BasicMatrix.getRotateMatrixZ(Math.toRadians(angleZ)));
    }

    public static double[][] reflectionX(double [][] dots){
        return MathMatrix.multiple(dots,BasicMatrix.getMirrorXMatrix());
    }

    public static double[][] reflectionY(double [][] dots){
        return MathMatrix.multiple(dots,BasicMatrix.getMirrorYMatrix());
    }

    public static double[][] reflectionZ(double [][] dots){
        return MathMatrix.multiple(dots,BasicMatrix.getMirrorZMatrix());
    }

    public static double[][] cabinet(double[][] dots){
        return MathMatrix.multiple(dots,BasicMatrix.getCabinetMatrix());
    }

    public static double[][] perspective(double[][] dots){
        //double [][] res = new double[dots.length][];
        //for(int i = 0; i < dots.length; i++){
        //     double [] newLine =  MathMatrix.multiple(dots[i],BasicMatrix.getPerspectiveMatrix());
        //     res[i] = newLine;
        //}
        //dots = res;
        dots = MathMatrix.multiple(dots,BasicMatrix.getPerspectiveMatrix());
        /*
        Т.к. изменилось h
        Приведём координаты к виду (x / (1 - z/c) y / (1 - z/c) 0 1)
        */

        BasicMatrix.printMatrix(dots,"3");
        for(int i = 0; i < dots.length;i++){
            for(int j = 0; j < 4; j++)
                dots[i][j] /= dots[i][3];
        }

        BasicMatrix.printMatrix(dots,"4");
        return dots;

    }
}
