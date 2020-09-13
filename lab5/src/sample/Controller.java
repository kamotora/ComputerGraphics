package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    Canvas canvas;

    BufferedImage img;
    int width,height;
    MyColor[][] arr;

    private int[] red, green, blue, bright;

    private final static double ADD_CONTRAST_COEF = 2;
    private final static double SUB_CONTRAST_COEF = 0.5;
    private final static int BRIGHT_STEP = 10;
    private final static int ALPHA = 200;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadImage();
    }

    @FXML
    private void loadImage()
    {
        try {
            img = ImageIO.read(new File("1.jpg"));
            width = img.getWidth();
            height = img.getHeight();
            arr = new MyColor[width][height];

            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++) {
                    Color color = new Color( img.getRGB(x, y));
                    arr[x][y] = new MyColor(color.getRed(), color.getGreen(), color.getBlue());
                }
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

    private double getY(double r,double g, double b){
        return 0.299*r + 0.5876*g + 0.114*b;
    }

    public void addBright(){
        changeBright(true);
    }


    public void subBright(){
        changeBright(false);
    }

    private void changeBright(boolean isNeedAdd){
        int coef = isNeedAdd ? BRIGHT_STEP : - BRIGHT_STEP;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++){
                arr[x][y].r += coef;
                arr[x][y].g += coef;
                arr[x][y].b += coef;

                img.setRGB(x, y, new Color( value(arr[x][y].r), value(arr[x][y].g), value(arr[x][y].b), ALPHA).getRGB());
            }
        printImg();
    }
    public void addConstrast(){
        changeContrast(true);
    }


    public void subConstrast(){
        changeContrast(false);
    }

    private void changeContrast(boolean isNeedAdd){
        double coef = isNeedAdd ? ADD_CONTRAST_COEF : SUB_CONTRAST_COEF;
        double avR = 0, avG = 0, avB = 0;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++){
                avR += arr[x][y].r;
                avG += arr[x][y].g;
                avB += arr[x][y].b;
            }
        int razr = (width*height);
        avR /= razr;
        avG /= razr;
        avB /= razr;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++){
                arr[x][y].r = (int) (coef * (arr[x][y].r - avR) + avR);
                arr[x][y].g = (int) (coef * (arr[x][y].g - avG) + avG);
                arr[x][y].b = (int) (coef * (arr[x][y].b - avB) + avB);
                img.setRGB(x, y, new Color( value(arr[x][y].r), value(arr[x][y].g), value(arr[x][y].b), ALPHA).getRGB());
            }
        printImg();
    }

    public void gray(){
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++){
                int bright = (int) getY(arr[x][y].r,arr[x][y].g,arr[x][y].b);
                arr[x][y].r = arr[x][y].g = arr[x][y].b = bright;
                img.setRGB(x, y, new Color( value(arr[x][y].r), value(arr[x][y].g), value(arr[x][y].b), ALPHA).getRGB());
            }
        printImg();
    }

    @FXML
    public void BW()
    {
        final int GRANITSA = 127;
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
            {
                if (arr[x][y].r > GRANITSA || arr[x][y].g > GRANITSA || arr[x][y].b > GRANITSA) {
                    arr[x][y].r = arr[x][y].g = arr[x][y].b = 255;
                }
                else {
                    arr[x][y].r = arr[x][y].g = arr[x][y].b = 0;
                }

                img.setRGB(x, y, new Color( arr[x][y].r, arr[x][y].g, arr[x][y].b, 200).getRGB());
            }

        printImg();
    }

    public void negative(){
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                arr[x][y].r = 255 - arr[x][y].r;
                arr[x][y].g = 255 - arr[x][y].g;
                arr[x][y].b = 255 - arr[x][y].b;
                img.setRGB(x, y, new Color( value(arr[x][y].r), value(arr[x][y].g), value(arr[x][y].b), ALPHA).getRGB());
            }
        printImg();
    }
    public void showRedHist(){
        make3Graphs();
        GramWindow(red, "Red");
    }

    public void showGreenHist(){
        make3Graphs();
        GramWindow(green, "Green");
    }

    public void showBlueHist(){
        make3Graphs();
        GramWindow(blue, "Blue");
    }

    public void showBrightHist(){
        make3Graphs();
        GramWindow(bright, "Bright");
    }


    private int value(int v)
    {
        if (v < 0)
            v = 0;
        if (v > 255)
            v = 255;
        return v;
    }



    private void GramWindow(int[] arr, final String name)
    {
        VBox showPane = new VBox(fill(arr));
        Scene scene = new Scene(showPane, width, height);

        Stage stage = new Stage();
        stage.setTitle(name + " histogram");
        stage.setScene(scene);
        stage.show();
    }


    private BarChart fill(int[] arr)
    {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (int i = 0; i < 256; i++)
            series.getData().add(new XYChart.Data<>("" + i, arr[i]));

        barChart.getData().add(series);
        return barChart;
    }


    private void make3Graphs()
    {
        red = new int[256];
        green = new int[256];
        blue = new int[256];
        bright = new int[256];

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
            {
                red[value(arr[x][y].r)]++;
                green[value(arr[x][y].g)]++;
                blue[value(arr[x][y].b)]++;

                int bri = (int) getY(value(arr[x][y].r), value(arr[x][y].g), value(arr[x][y].b));
                bright[bri]++;
            }
    }


}