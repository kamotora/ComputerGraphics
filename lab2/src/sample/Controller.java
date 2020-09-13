package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import models.Point;
import models.Polygon;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    @FXML
    private Canvas canvas;

    @FXML
    private javafx.scene.control.Label yLabel;

    @FXML
    private TextField pauseField;

    public static ArrayList<Polygon> input() {
        ArrayList <Polygon> polygons = new ArrayList<>();
        ArrayList <Point> points = new ArrayList<>();
        ArrayList<ArrayList<Integer>> numbers = new ArrayList<ArrayList<Integer>>();
        ArrayList<String> lines = new ArrayList<>();
        //Чтение с файла в lines
        try {
            Files.lines(Paths.get("src/data/input.dat"), StandardCharsets.UTF_8).forEach(lines::add);
        }
        catch (Exception exp){
            System.out.println("Ошибка чтения с файла");
            System.out.println(exp.getMessage());
        }

        //Описание многоугольников разделено #
        for(String line : lines){
            if((line.length() == 0 || line.contains("#")) && (!numbers.isEmpty())){
                for(int i = 0; i < numbers.size() - 1; i++){
                    Integer[] point = numbers.get(i).toArray(new Integer[3]);
                    points.add(new Point(point[0],point[1],point[2]));
                }
                Integer[] color = numbers.get(numbers.size()-1).toArray(new Integer[3]);
                polygons.add( new Polygon(points,color[0],color[1],color[2]));
                points.clear();
                numbers.clear();
            }
            else{
                ArrayList<Integer> temp = new ArrayList<>();
                for(String str: line.split(" "))
                    temp.add(Integer.parseInt(str));
                numbers.add(temp);
            }
        }
        return polygons;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.canvas = canvas;
        Main.yLabel = yLabel;
        Main.pauseField = pauseField;
    }


}
