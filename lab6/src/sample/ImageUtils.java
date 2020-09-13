package sample;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;

public class ImageUtils {
    public static BufferedImage applyFilter(BufferedImage input, double[][] kernel)
    {
        int width = input.getWidth();
        int height = input.getHeight();
        BufferedImage res = new BufferedImage(width,height,input.getType());
        int kernelWidth = kernel.length;
        int kernelHeight = kernel[0].length;

        //Производим вычисления
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                double resPixel = 0, kSum = 0;
                double r = 0,g = 0, b = 0;
                for (int i = 0; i < kernelWidth; i++)
                {
                    for (int j = 0; j < kernelHeight; j++)
                    {
                        int pixelPosX = x + (i - (kernelWidth / 2));
                        int pixelPosY = y + (j - (kernelHeight / 2));
                        if (pixelPosX < 0)
                            pixelPosX *= -1;
                        if (pixelPosY < 0)
                            pixelPosY *= -1;
                        if(pixelPosX >= width)
                            pixelPosX = (pixelPosX - width) * 2;
                        if(pixelPosY >= height)
                            pixelPosY = (pixelPosY - height) * 2;

                        double kernelVal = kernel[i][j];
                        kSum += kernelVal;
                        Color color = new Color(input.getRGB(pixelPosX,pixelPosY));
                        r += color.getRed() * kernelVal;
                        g += color.getGreen() * kernelVal;
                        b += color.getBlue() * kernelVal;
                        //resPixel += input.getRGB(pixelPosX,pixelPosY) * kernelVal;
                    }
                }
                //System.out.printf("%3d;",(int)r);
                if (kSum <= 0)
                    kSum = 1;

                // Нормировка
                resPixel /= kSum;
                r /= kSum;
                b /= kSum;
                g /= kSum;
                res.setRGB(x,y,new Color(value(r),value(g),value(b)).getRGB());
                //Делаем новое изображение
                //res.setRGB(x,y,(int)resPixel);

            }
            //System.out.println();
        }
        //Возвращаем отфильтрованное изображение
        return res;
    }


    public static BufferedImage applyMedianFilter(BufferedImage input,int n, int m)
    {
        int width = input.getWidth();
        int height = input.getHeight();
        BufferedImage res = new BufferedImage(width,height,input.getType());

        //Производим вычисления
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                double resPixel = 0, kSum = 0;
                double r = 0,g = 0, b = 0;

                int[] medianKernel = new int[n*m];
                for (int i = 0; i < n; i++)
                {
                    for (int j = 0; j < m; j++)
                    {
                        int pixelPosX = x + (i - (n / 2));
                        int pixelPosY = y + (j - (m / 2));
                        if (pixelPosX < 0)
                            pixelPosX *= -1;
                        if (pixelPosY < 0)
                            pixelPosY *= -1;
                        if(pixelPosX >= width)
                            pixelPosX = (pixelPosX - width) * 2;
                        if(pixelPosY >= height)
                            pixelPosY = (pixelPosY - height) * 2;

                        medianKernel[i*m+j] = input.getRGB(pixelPosX,pixelPosY);
                    }
                }
                Arrays.sort(medianKernel);
                res.setRGB(x,y,medianKernel[medianKernel.length/2]);

            }
        }
        //Возвращаем отфильтрованное изображение
        return res;
    }

    public static int value(double v)
    {
        if (v < 0)
            return 0;
        if (v > 255)
            return 255;
        return (int)v;
    }

    public static int value(int v)
    {
        if (v < 0)
            v = 0;
        if (v > 255)
            v = 255;
        return v;
    }

    public static double getY(double r,double g, double b){
        return 0.299*r + 0.5876*g + 0.114*b;
    }

    public static BufferedImage applyFilterGranit(BufferedImage input, double[][] kernel)
    {
        int width = input.getWidth();
        int height = input.getHeight();
        BufferedImage res = new BufferedImage(width,height,input.getType());
        int kernelWidth = kernel.length;
        int kernelHeight = kernel[0].length;

        //Производим вычисления
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                double resPixel = 0, kSum = 0;
                double r = 0,g = 0, b = 0;
                for (int i = 0; i < kernelWidth; i++)
                {
                    for (int j = 0; j < kernelHeight; j++)
                    {
                        int pixelPosX = x + (i - (kernelWidth / 2));
                        int pixelPosY = y + (j - (kernelHeight / 2));
                        if (pixelPosX < 0)
                            pixelPosX *= -1;
                        if (pixelPosY < 0)
                            pixelPosY *= -1;
                        if(pixelPosX >= width)
                            pixelPosX = (pixelPosX - width) * 2;
                        if(pixelPosY >= height)
                            pixelPosY = (pixelPosY - height) * 2;

                        double kernelVal = kernel[i][j];
                        kSum += kernelVal;
                        Color color = new Color(input.getRGB(pixelPosX,pixelPosY));
                        r += color.getRed() * kernelVal;
                        g += color.getGreen() * kernelVal;
                        b += color.getBlue() * kernelVal;
                        //resPixel += input.getRGB(pixelPosX,pixelPosY) * kernelVal;
                    }
                }
                //System.out.printf("%3d;",(int)r);
                if (kSum <= 0)
                    kSum = 1;

                // Нормировка
                resPixel /= kSum;
                r /= kSum;
                b /= kSum;
                g /= kSum;
                //if(r < 20)
                //    r=g=b=0;
                res.setRGB(x,y,new Color(value(r),value(g),value(b)).getRGB());
                //Делаем новое изображение
                //res.setRGB(x,y,(int)resPixel);

            }
            //System.out.println();
        }
        //Возвращаем отфильтрованное изображение
        return res;
    }


}
