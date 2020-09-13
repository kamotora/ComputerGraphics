package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;

import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.ColorNames;
import models.Line;
import models.Polygon;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {

    //Ширина
    public static final int WIDTH = 400;
    //Высота
    public static final int HEIGHT = 500;
    //Текущая скан.строка
    public static int y = 0;
    public static int pause = 100;
    public static TextField pauseField;

    int[] zBuffer = new int[WIDTH];
    Color[] screenBuffer = new Color[WIDTH];

    ArrayList<Polygon> polygons = new ArrayList<>();
    ArrayList<Polygon> activePolygons = new ArrayList<>();
    public static Canvas canvas;
    public static Label yLabel;
    private TimerTask task = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        Scene scene = new Scene(root, WIDTH+150, HEIGHT);
        primaryStage.setTitle("Удаление невидимых граней");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        reset();
        Timer timer = new Timer();
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case BACK_SPACE:
                    reset();
                    yLabel.setText("Y = " + y);
                    break;
                case SPACE:
                    work();
                break;
                case T:
                    reloadTask();
                    timer.schedule(task,0,pause);
                break;
                case P:
                    task.cancel();
                    timer.purge();
            }
        });
    }

    public void reset() {
        polygons = Controller.input();
        activePolygons.clear();
        task = null;
        canvas.getGraphicsContext2D().clearRect(0,0,WIDTH,HEIGHT);
        y = 0;
    }

    public void reloadTask(){
        try {
            pause = Integer.parseInt(pauseField.getText());
        }catch (Exception e){
            System.out.println("Не удалось прочитать задержку. Ставим по умолчанию");
            pause = 100;
        }
        task = new TimerTask() {
            @Override
            public void run() {
                work();
            }
        };
    }
    public void work(){
        Platform.runLater(() -> {
            System.out.println("\nY = " + y);
            if (y < WIDTH) {
                yLabel.setText("Y = " + y);
                lineScanning(y);

                if (drawLine(y)) {
                    printInfo();
                }
                y++;

                ;
            } else{
                if(task != null)
                    task.cancel();
                System.out.println("Всё");
            }
        });
    }

    public void lineScanning(int y) {

        //	Инициализация буферов
        for (int i = 0; i < WIDTH; i++) {
            screenBuffer[i] = Color.WHITE;
            zBuffer[i] = Integer.MIN_VALUE;
        }

        /*
         *	Добавление активного многоугольника
         *  Если минимальное значение координаты y многоугольника
         *	Совпадает с текущей сканирующей строкой - заносим многоугольник в список активных
         */
        for (Polygon polygon : polygons) {

            if (polygon.getMinY() == y) {
                activePolygons.add(polygon);
            }

        }

        //	Проверка активных рёбер многоугольников
        for (Polygon activePolygon : activePolygons) {
            activePolygon.checkingActiveLines(y);
        }

        for (Polygon activePolygon : activePolygons) {
            //	Получаем пару рёбер текущего активного многоугольника
            Line leftEdge = activePolygon.getLeftActiveLine();
            Line rightEdge = activePolygon.getRightActiveLine();

            //	Для данной пары устанавливаем значение x + deltaX
            leftEdge.countNewX();
            rightEdge.countNewX();

            //	Получаем значение пересечения пары рёбер с текущей сканирующей строкой
            int xLeft = leftEdge.getX();
            int xRight = rightEdge.getX();


            //  Глубина
            double z = -(activePolygon.getA() * xLeft + activePolygon.getB() * y + activePolygon.getD()) /
                    activePolygon.getC();


            for (int x = xLeft; x <= xRight; x++) {

                //Сравниваем z(x, y) фигуры с z в буфере
                //Если больше, на экране эта фигура
                if (z > zBuffer[x]) {

                    zBuffer[x] = (int) (z);
                    screenBuffer[x] = activePolygon.getColor();

                }

                //Считаем след.z
                z -= (activePolygon.getA() / activePolygon.getC());
            }
        }

         for (int i = 0; i < activePolygons.size(); i++) {
             activePolygons.get(i).subDeltaY();

            if (activePolygons.get(i).getDeltaY() == 0) {
                activePolygons.remove(activePolygons.get(i));
                i--;
            }

        }

    }


    public static void main(String[] args) {
        launch(args);
    }

    public boolean drawLine(int y) {
        //	Наносим многоугольники на полотно
        boolean wasDrawing = false;
        for (int i = 0; i < WIDTH; i++) {
            if (screenBuffer[i] != Color.WHITE) {
                wasDrawing = true;
                canvas.getGraphicsContext2D().getPixelWriter().setColor(i, y, screenBuffer[i]);
            }
        }
        return wasDrawing;
    }

    public void printInfo() {
        System.out.println("Z - буфер: ");
        int start = 1, finish = 1;
        for (int i = 0; i < WIDTH; i++) {
            if(i != WIDTH - 1 && zBuffer[i] == zBuffer[i+1]){
                finish++;
            }
            else{
                if(start != finish){
                    if (zBuffer[i] == Integer.MIN_VALUE)
                        System.out.printf("%s[от %dpx до %dpx], ","-INF",start,finish);
                    else
                        System.out.printf("%d[от %dpx до %dpx], ",zBuffer[i],start,finish);
                }
                else
                    if (zBuffer[i] == Integer.MIN_VALUE)
                        System.out.printf("%s, ","-INF");
                    else
                        System.out.printf("%d, ",zBuffer[i]);
                start = finish = i+1;
            }
        }
        System.out.println();
        start = finish = 1;
        System.out.println("Буфер экрана: ");
        for (int i = 0; i < WIDTH; i++) {
            if(i != WIDTH - 1 && screenBuffer[i] == screenBuffer[i+1]){
                finish++;
            }
            else{
                if(start != finish){
                    System.out.printf("%4s[от %3dpx до %3dpx],", ColorNames.getNameColor(screenBuffer[i]),start,finish);
                }
                else{
                    System.out.printf("%4s,", ColorNames.getNameColor(screenBuffer[i]));
                }
                start = finish = i+1;
            }
        }
        System.out.println();
    }

}