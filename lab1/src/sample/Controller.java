package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import matrix.Dots;
import matrix.MathMatrix;
import matrix.BasicMatrix;

import java.awt.*;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Timer;

import static java.lang.Thread.sleep;


public class Controller implements Initializable {
    //положение начала координат на экране
    static final int WIDTH = 630;
    static final int HEIGHT = 560;
    public static final  int NULL_COORD_X = 100;
    public static final  int NULL_COORD_Y = 200;
    //public static final  int NULL_COORD_X = HEIGHT / 2;
    //public static final  int NULL_COORD_Y = WIDTH / 2;
    double [][] dots;
    double [] curCoord = {0,0,0};
    double [] curScale = {1,1,1};
    double [] curAngle = {0,0,0};
    public static double [][] axis = {
            {0,0,0,1},
            {200,0,0,1},
            {0,200,0,1},
            {0,0,200,1}
    };

    @FXML
    private Group group;

    @FXML
    private RadioButton simpleRB;

    @FXML
    private RadioButton perspectiveRB;

    @FXML
    private Label curCoordLabel;

    @FXML
    private Label curScaleLabel;

    @FXML
    private Label curAngleLabel;

    @FXML
    public TextField scaleX;
    @FXML
    public TextField scaleY;
    @FXML
    public TextField scaleZ;
    @FXML
    public TextField angleX;
    @FXML
    public TextField angleY;
    @FXML
    public TextField angleZ;
    public TextField moveX;
    public TextField moveY;
    public TextField moveZ;

    public Button stopAnimateButton;


    /*
    * Алгоритм плавного проецирования:
    * угол для косоугольного плавно с 45 до 90
    * переключаемся на перспективу с С = 300
    * идем плавно до 100
    * */
    public void perChecked() {
        if(perspectiveRB.isSelected()) {
            simpleRB.setSelected(false);
        }else{
            simpleRB.setSelected(true);
        }
        update(dots);

    }

    public void simpleChecked() {
        if(simpleRB.isSelected()) {
            perspectiveRB.setSelected(false);
        }else{
            perspectiveRB.setSelected(true);
        }
        update(dots);
    }
    //****/
    @FXML
    private void animate(){
        System.out.println("Start animation");
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call(){
                int iterations;
                double oldC = BasicMatrix.C;
                double oldAngle = BasicMatrix.angle;
                boolean back = false;
                BasicMatrix.C = 450;
                for (iterations = 0; true; iterations++) {
                    final double[][] transDots;
                    if (isCancelled()) {
                        BasicMatrix.C = oldC;
                        BasicMatrix.angle = oldAngle;
                        System.out.println("Stop animation");
                        break;
                    }

                    try {
                        Thread.sleep(100);
                        if(!back){
                            if(BasicMatrix.angle < 90) {
                                BasicMatrix.angle += 1;
                                transDots = Transformations.cabinet(dots);
                                Platform.runLater(() -> draw(transDots));
                            }
                            else if(BasicMatrix.C > 100){
                                BasicMatrix.C -= 10;
                                transDots = Transformations.perspective(dots);
                                Platform.runLater(() -> draw(transDots));
                            }
                            else {
                                back = true;
                            }
                        }
                        else{
                            if(BasicMatrix.C < 450 ) {
                                BasicMatrix.C += 10;
                                transDots = Transformations.perspective(dots);
                                Platform.runLater(() -> draw(transDots));
                            }
                            else if(BasicMatrix.angle > 45){
                                BasicMatrix.angle -= 1;
                                transDots = Transformations.cabinet(dots);
                                Platform.runLater(() -> draw(transDots));
                            }
                            else
                                cancel();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return iterations;
            }
        };
        stopAnimateButton.setOnAction(event -> task.cancel());
        new Thread(task).start();
    }

    private void update(double [][] dots){
        if(simpleRB.isSelected()){
            draw(Transformations.cabinet(dots));
        }
        else{
            draw(Transformations.perspective(dots));
        }
    }
    private void draw(double [][] dots){
        group.getChildren().clear();
        double[][] transDots = dots;
        for(int i = 0; i < Dots.edgeP.length; i++){
            int start = Dots.edgeP[i][0];
            int end = Dots.edgeP[i][1];

            Line line = new Line();
            line.setStartX(transDots[start][0] + NULL_COORD_X);
            line.setStartY(-transDots[start][1] + NULL_COORD_Y);
            line.setEndX(transDots[end][0] + NULL_COORD_X);
            line.setEndY(-transDots[end][1] + NULL_COORD_Y);
            line.setStrokeWidth(3);
            line.setStroke(Color.BLACK);
            group.getChildren().add(line);
        }

        //рисуем оси координат
        double[][] transAxis;
        if(simpleRB.isSelected()){
            transAxis = Transformations.cabinet(axis);
        }
        else{
            transAxis = MathMatrix.multiple(axis,BasicMatrix.getPerspectiveMatrix());
        }
        final Color [] colorsAxis = {Color.RED,Color.GREEN,Color.BLUE};
        for (int i = 0; i < 3; i++) {
            Line line = new Line();
            line.setStartX(transAxis[0][0] + NULL_COORD_X);
            line.setStartY(-transAxis[0][1] + NULL_COORD_Y);
            line.setEndX(transAxis[i+1][0] + NULL_COORD_X);
            line.setEndY(-transAxis[i+1][1] + NULL_COORD_Y);
            line.setStrokeWidth(1);
            line.setStroke(colorsAxis[i]);
            group.getChildren().add(line);

            line = new Line();
            line.setStartX(transAxis[0][0] + NULL_COORD_X);
            line.setStartY(-transAxis[0][1] + NULL_COORD_Y);
            line.setEndX(-transAxis[i+1][0] + NULL_COORD_X);
            line.setEndY(transAxis[i+1][1] + NULL_COORD_Y);
            line.setStrokeWidth(1);
            line.setStroke(colorsAxis[i]);


            group.getChildren().add(line);


        }
        /*
        for (int i = 0; i < transDots.length-1; i++) {
            Line line = new Line();
            line.setStartX(transDots[i][0] + NULL_COORD_X);
            line.setStartY(-transDots[i][1] + NULL_COORD_Y);
            line.setEndX(transDots[i+1][0] + NULL_COORD_X);
            line.setEndY(-transDots[i+1][1] + NULL_COORD_Y);
            line.setStrokeWidth(3);
            line.setStroke(Color.BLACK);
            group.getChildren().add(line);
        }
        */
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dots = Dots.dotsP;
        dots = Transformations.moveToStart(dots);
        dots = Transformations.scale(dots, 1.5, 1.5, 1.5);
        for(int i = 0; i < curScale.length;i++)
            curScale[i] *= 2;
        //dots = Transformations.rotateZ(dots,180);
        update(dots);
        curAngleLabel.setText(Arrays.toString(curAngle));
        curCoordLabel.setText(Arrays.toString(curCoord));
        curScaleLabel.setText(Arrays.toString(curScale));
    }

    private double parseMovingCoordinates(TextField textField) throws NumberFormatException{
        double coord;
        try{
            coord = Double.parseDouble(textField.getText());
        }
        catch (NumberFormatException exp){
            textField.setStyle("-fx-text-fill: red;");
            throw new NumberFormatException();
        }
        return coord;
    }

    private double parseScaleCoordinates(TextField textField) throws NumberFormatException{
        double coord;
        try{
            coord = Double.parseDouble(textField.getText());
            if(coord <= 0)
                throw new NumberFormatException();
        }
        catch (NumberFormatException exp){
            textField.setStyle("-fx-text-fill: red;");
            throw new NumberFormatException();
        }
        return coord;
    }

    private double parseRotateAngle(TextField textField) throws NumberFormatException{
        double angle;
        try{
            angle = Double.parseDouble(textField.getText());
            angle %= 360;
        }
        catch (NumberFormatException exp){
            textField.setStyle("-fx-text-fill: red;");
            throw new NumberFormatException();
        }
        return angle;
    }

    public void moveAction(ActionEvent actionEvent) {
        double x,y,z;

        moveX.setStyle("-fx-text-fill: black;");
        moveY.setStyle("-fx-text-fill: black;");
        moveZ.setStyle("-fx-text-fill: black;");

        try {
            x = parseMovingCoordinates(moveX);
            y = parseMovingCoordinates(moveY);
            z = parseMovingCoordinates(moveZ);
        }
        catch (NumberFormatException exp){
            System.out.println("Неудачный ввод координат для перемещения");
            return;
        }
        dots = Transformations.move(dots,x,y,z);
        curCoord[0] += x;
        curCoord[1] += y;
        curCoord[2] += z;
        update(dots);
        curCoordLabel.setText(Arrays.toString(curCoord));
    }
    public void scaleAction(ActionEvent actionEvent) {
        double x,y,z;

        scaleX.setStyle("-fx-text-fill: black;");
        scaleY.setStyle("-fx-text-fill: black;");
        scaleZ.setStyle("-fx-text-fill: black;");
        try {
            x = parseScaleCoordinates(scaleX);
            y = parseScaleCoordinates(scaleY);
            z = parseScaleCoordinates(scaleZ);
        }
        catch (NumberFormatException exp){
            System.out.println("Неудачный ввод координат для масштабирования");
            return;
        }

        dots = Transformations.scale(dots,x,y,z);
        curScale[0] *= x;
        curScale[1] *= y;
        curScale[2] *= z;
        update(dots);
        curScaleLabel.setText(Arrays.toString(curScale));
    }

    public void rotateX_Action(ActionEvent actionEvent) {
        double angle;
        angleX.setStyle("-fx-text-fill: black;");
        try {
            angle = parseRotateAngle(angleX);
        }catch (NumberFormatException exp){
            System.out.println("Неудачный ввод угла для X");
            return;
        }
        dots = Transformations.rotateX(dots,angle);
        curAngle[0] += angle;
        curAngle[0] %= 360;
        update(dots);
        curAngleLabel.setText(Arrays.toString(curAngle));
    }
    public void rotateY_Action(ActionEvent actionEvent) {
        double angle;
        angleY.setStyle("-fx-text-fill: black;");
        try {
            angle = parseRotateAngle(angleY);
        }catch (NumberFormatException exp){
            System.out.println("Неудачный ввод угла для Y");
            return;
        }
        dots = Transformations.rotateY(dots,angle);
        curAngle[1] += angle;
        curAngle[1] %= 360;
        update(dots);
        curAngleLabel.setText(Arrays.toString(curAngle));
    }
    public void rotateZ_Action(ActionEvent actionEvent) {
        double angle;
        angleZ.setStyle("-fx-text-fill: black;");
        try {
            angle = parseRotateAngle(angleZ);
        }catch (NumberFormatException exp){
            System.out.println("Неудачный ввод угла для Z");
            return;
        }
        dots = Transformations.rotateZ(dots,angle);
        curAngle[2] += angle;
        curAngle[2] %= 360;
        update(dots);
        curAngleLabel.setText(Arrays.toString(curAngle));
    }
    public void reflectX_Action(ActionEvent actionEvent) {
        dots = Transformations.reflectionX(dots);
        curCoord[0] *= -1;
        update(dots);
        curCoordLabel.setText(Arrays.toString(curCoord));
    }
    public void reflectY_Action(ActionEvent actionEvent) {
        dots = Transformations.reflectionY(dots);
        curCoord[1] *= -1;
        update(dots);
        curCoordLabel.setText(Arrays.toString(curCoord));
    }
    public void reflectZ_Action(ActionEvent actionEvent) {
        dots = Transformations.reflectionZ(dots);
        curCoord[2] *= -1;
        update(dots);
        curCoordLabel.setText(Arrays.toString(curCoord));
    }

}
