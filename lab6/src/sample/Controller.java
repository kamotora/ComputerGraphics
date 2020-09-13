package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import static sample.ImageUtils.getY;

public class Controller implements Initializable {
    @FXML
    CheckBox isRandomColorCheckBox;
    @FXML
    RadioButton isLaplasRB;
    @FXML
    RadioButton isSobelRB;
    @FXML
    TextField powerRezkField;
    @FXML
    TextField mField;
    @FXML
    TextField nField;
    @FXML
    TextField mFieldRezk;
    @FXML
    TextField nFieldRezk;
    @FXML
    Canvas canvas;
    @FXML
    TextField pointNoiseField;
    @FXML
    TextField lineNoiseField;
    @FXML
    TextField okrNoiseField;
    BufferedImage img;
    int width,height;
    //MyColor[][] arr;

    private final static int NOISE_STEP = 50;
    private final static int ALPHA = 200;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadImage();
    }

    @FXML
    private void loadImage()
    {
        try {
            img = ImageIO.read(new File("4.png"));
            width = img.getWidth();
            height = img.getHeight();
            /*
            arr = new MyColor[width][height];

            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++) {
                    Color color = new Color( img.getRGB(x, y));
                    arr[x][y] = new MyColor(color.getRed(), color.getGreen(), color.getBlue());
                }
             */
            canvas.setWidth(width);
            canvas.setHeight(height);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        //make3Graphs();
        printImg();
    }

    private void printImg()
    {
        Image image = SwingFXUtils.toFXImage(img, null);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.fillRect(0, 0, width, height);
        gc.drawImage(image, 0, 0);
    }

    private int value(int v)
    {
        if (v < 0)
            v = 0;
        if (v > 255)
            v = 255;
        return v;
    }

    public void noise(){
        // Значения по умолчанию
        int countPoints = 500,  countLines = 10,  countOrks = 5;
        try{
            countPoints = Integer.parseInt(pointNoiseField.getText());
            countLines = Integer.parseInt(lineNoiseField.getText());
            countOrks = Integer.parseInt(okrNoiseField.getText());
        }catch (NumberFormatException exp){
            System.out.println("Ошибка при чтении параметров для шума. Берем значения по умолчанию");
        }
        img = applyNoise(img,countPoints,countLines,countOrks);
        try{
            ImageIO.write(img,"png",new File("noisedImage.png"));
        }catch (Exception e){
            System.out.println("Не удалось сохранить картинку");
        }
        printImg();
    }
    public BufferedImage applyNoise(BufferedImage input, int countPoints, int countLines, int countOrks)
    {
        int width = input.getWidth();
        int height = input.getHeight();
        BufferedImage res = new BufferedImage(width,height,input.getType());
        for(int x = 0; x < width; x++)
            for(int y = 0; y < height; y++)
                res.setRGB(x,y,input.getRGB(x,y));
        Random random = new Random();
        // Шумим точками
        for (int i = 0; i < countPoints; i++)
        {
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                Color color = new Color(input.getRGB(x,y));
                int r = color.getRed() + ((int)(random.nextGaussian()* NOISE_STEP) * (random.nextInt(2)-1));
                int g = color.getGreen() + ((int)(random.nextGaussian()* NOISE_STEP) * (random.nextInt(2)-1));
                int b = color.getBlue() + ((int)(random.nextGaussian()* NOISE_STEP) * (random.nextInt(2)-1));
                res.setRGB(x,y,new Color(value(r),value(g),value(b),color.getAlpha()).getRGB());
        }
        // Шумим линиями

        for (int i = 0; i < countLines; i++)
        {
            int startLine = random.nextInt(width);
            int finishLine = startLine + random.nextInt(width - startLine);
            int y = random.nextInt(height);
            for(int x = startLine; x < finishLine; x++){
                int randomColor;
                if(isRandomColorCheckBox.isSelected())
                    randomColor = random.nextInt();
                else
                    randomColor = Color.BLACK.getRGB();
                Color color = new Color(input.getRGB(x,y));
                //int r = color.getRed() + ((int)(random.nextGaussian()* NOISE_STEP/2) * (random.nextInt(2)-1));
                //int g = color.getGreen() + ((int)(random.nextGaussian()* NOISE_STEP/2) * (random.nextInt(2)-1));
                //int b = color.getBlue() + ((int)(random.nextGaussian()* NOISE_STEP/2) * (random.nextInt(2)-1));
                res.setRGB(x,y,randomColor);
            }
        }


        Graphics2D graphics2D = res.createGraphics();
        // Шумим окружностями
        for (int i = 0; i < countOrks; i++)
        {
            int x = random.nextInt(width - 100) ;
            int y = random.nextInt(height - 100);
            int size = random.nextInt(100);

            int randomColor;
            if(isRandomColorCheckBox.isSelected())
                randomColor = random.nextInt();
            else
                randomColor = Color.BLACK.getRGB();
            graphics2D.setColor(new Color(randomColor));
            graphics2D.setStroke(new BasicStroke(1));
            graphics2D.drawOval(x,y,size,size);
        }


        //Возвращаем отфильтрованное изображение
        return res;
    }

    public void GaussFilter(ActionEvent actionEvent) {
        final double GAUSS_BLUR_POWER = 0.5;
        // Значения по умолчанию
        int n = 3,  m = 3;
        try{
            n = Integer.parseInt(nField.getText());
            m = Integer.parseInt(mField.getText());
            if(n % 2 != 1 || m % 2 != 1){
                System.err.println("Размерности должны быть нечетными. Берем по умолчанию 3x3");
                n = m = 3;
            }
        }catch (NumberFormatException exp){
            System.err.println("Ошибка при чтении параметров для шума. Берем значения по умолчанию");
        }
        double[][] kernel = new double[n][m];
        //Формируем ядро
        final double constValue = 2 * GAUSS_BLUR_POWER * GAUSS_BLUR_POWER;
        int correctI = n / 2;
        int correctJ = m / 2;
        System.out.println();
        for(int i = 0; i < n; i++){
            for(int j = 0 ; j < m; j++){
                int iQuad = (i - correctI) * (i - correctI);
                int jQuad = (j - correctJ) * (j - correctJ);
                kernel[i][j] = Math.exp(-((iQuad + jQuad) / constValue ));
               System.out.printf("%.6f ",kernel[i][j]);
            }
            System.out.println();
        }

        img = ImageUtils.applyFilter(img,kernel);
        printImg();
    }

    public void MedianFilter(ActionEvent actionEvent) {
        // Значения по умолчанию
        int n = 3,  m = 3;
        try{
            n = Integer.parseInt(nField.getText());
            m = Integer.parseInt(mField.getText());
            if(n % 2 != 1 || m % 2 != 1){
                System.err.println("Размерности должны быть нечетными. Берем по умолчанию 3x3");
                n = m = 3;
            }
        }catch (NumberFormatException exp){
            System.err.println("Ошибка при чтении параметров для шума. Берем значения по умолчанию");
        }
        img = ImageUtils.applyMedianFilter(img,n,m);
        printImg();
    }

    public void addRezkost(ActionEvent actionEvent) {
        // Значения по умолчанию
        int n = 3,  m = 3;
        double k = 2;
        try{
            n = Integer.parseInt(nFieldRezk.getText());
            m = Integer.parseInt(mFieldRezk.getText());
            k = Double.parseDouble(powerRezkField.getText());
            if(n % 2 != 1 || m % 2 != 1){
                System.err.println("Размерности должны быть нечетными. Берем по умолчанию 3x3");
                n = m = 3;
            }
        }catch (NumberFormatException exp){
            System.err.println("Ошибка при чтении параметров для шума. Берем значения по умолчанию");
        }
        double[][] kernel = new double[n][m];
        int size = n*m - 1;
        for(int i = 0; i < n; i++)
            for(int j = 0; j < m; j++)
                kernel[i][j] = -k/size;
        kernel[n/2][m/2] = k + 1;
        img = ImageUtils.applyFilter(img,kernel);
        printImg();
    }

    public void findBounds(ActionEvent actionEvent) {
        MedianFilter(actionEvent);
        //double[][] kernelSobelY = {{-1, -1, -1},{0,0,0}, {1,1,1}};
        //double[][] kernelSobelX = {{-1, 0, 1},{-1,0,1}, {-1,0,1}};
        double[][] kernelLaplasian = {{0,1,0},{1,-4,1},{0,1,0}};
        gray();

        //if(isSobelRB.isSelected()){
            //img = ImageUtils.applyFilter(img,kernelSobelY);
            //img = ImageUtils.applyFilter(img,kernelSobelX);
        //}else
            img = ImageUtils.applyFilterGranit(img,kernelLaplasian);

        printImg();
    }

    public void Tisnenie(ActionEvent actionEvent) {
        MedianFilter(actionEvent);
        double[][] kernel = {{0,1,0},{1,0,-1},{0,-1,0}};
        gray();
        img = ImageUtils.applyFilter(img,kernel);

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++){
                Color color = new Color(img.getRGB(x,y));
                int r = color.getRed() + 128;
                int g = color.getGreen() + 128;
                int b = color.getBlue() + 128;
                img.setRGB(x,y,new Color(value(r),value(g),value(b)).getRGB());
            }


        printImg();
    }


    public void gray(){
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++){
                Color color = new Color(img.getRGB(x,y));
                int bright = (int) getY(color.getRed(),color.getGreen(),color.getBlue());
                img.setRGB(x, y, new Color( value(bright), value(bright), value(bright), ALPHA).getRGB());
            }
    }
    public void BW()
    {
        final int GRANITSA = 180;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
            {
                Color color = new Color(img.getRGB(x,y));
                int res;
                if (color.getRed() > GRANITSA || color.getGreen() > GRANITSA || color.getBlue()  > GRANITSA) {
                    res = 255;
                }
                else {
                    res = 0;
                }

                img.setRGB(x, y, new Color(res,res,res, 200).getRGB());
            }

     }


}
