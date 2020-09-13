package models;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Polygon {
    //Цвет
    private Color color;
    //Список вершин
    private ArrayList<Point> points;
    //Список рёбер
    private ArrayList<Line> lines;
    //Число сканирующих строк
    private Integer deltaY;
    private Integer minY;

    //Уравнение
    private double a;
    private double b;
    private double c;
    private double d;

    //Ребра
    private Line leftActiveLine;
    private Line rightActiveLine;
    public Polygon(ArrayList<Point> points, int r,int g,int b){
        this.points = new ArrayList<>(points);
        this.color = Color.rgb(r,g,b,1);
        createLines();
        countMinAndDelta();
        countUrav();
    }

    private void countMinAndDelta(){
        int min = Integer.MAX_VALUE,max = Integer.MIN_VALUE;
        for(Point point : points){
            if(point.y < min)
                min = point.y;
            if(point.y > max)
                max = point.y;
        }
        minY = min;
        deltaY = max - min;
    }

    private void createLines(){
        lines = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            lines.add(new Line(points.get(i), points.get(i + 1)));

        }
        //Последнее ребро
        lines.add(new Line(points.get(points.size() - 1), points.get(0)));
    }

    /*
     *  Расчёт коэффициентов a, b, c уравнения плоскости
     */
    private void countUrav() {
        double a = 0;
        double b = 0;
        double c = 0;
        int size = points.size();
        for (int i = 0; i < size - 1; i++) {
            a += (points.get(i).y - points.get(i + 1).y) * (points.get(i).z + points.get(i + 1).z);
            b += (points.get(i).z - points.get(i + 1).z) * (points.get(i).x + points.get(i + 1).x);
            c += (points.get(i).x - points.get(i + 1).x) * (points.get(i).y + points.get(i + 1).y);
        }
        a += (points.get(size - 1).y - points.get(0).y) * (points.get(size - 1).z + points.get(0).z);
        b += (points.get(size - 1).z - points.get(0).z) * (points.get(size - 1).x + points.get(0).x);
        c += (points.get(size - 1).x - points.get(0).x) * (points.get(size - 1).y + points.get(0).y);

        this.a = a;
        this.b = b;
        this.c = c;

        this.d = -(a * points.get(0).x + b * points.get(0).y + c * points.get(0).z);
    }

    /*
     *	Проверка активных рёбер многоугольника
     *	@param line: сканирующая строка
     */
    public void checkingActiveLines(int line) {
        Line leftLine = getLeftActiveLine();
        Line rightLine = getRightActiveLine();

        rightLine = checkLineCrossY(rightLine, line);
        leftLine = checkLineCrossY(leftLine, line);

        if (leftLine.getX() > rightLine.getX()) {
            setLeftActiveLine(rightLine);
            setRightActiveLine(leftLine);
        }

        else {
            setLeftActiveLine(leftLine);
            setRightActiveLine(rightLine);
        }

    }

    /*
     *  Проверка ребра на пересечение прямой y = yLine
     */
    private Line checkLineCrossY(Line activeLine, int yLine) {
        //	Проверяем, если сканирующая строка входит в диапазон значений координаты y начальной и конечной точки ребра

        if (activeLine != null && ((activeLine.getStart().y <= yLine && yLine <= activeLine.getEnd().y) ||
                (activeLine.getEnd().y <= yLine && yLine <= activeLine.getStart().y))) {
            return activeLine;
        }
        //Если это ребро не подходит, ищем другое
        for (int i = 0; i < lines.size(); i++) {


            if (lines.get(i).getDeltaY() <= 0) {
                //lines.remove(i--);
                continue;
            }

            if ((lines.get(i).getStart().y == yLine - 1 && lines.get(i).getEnd().y > yLine) ||
                    (lines.get(i).getEnd().y == yLine - 1 && lines.get(i).getStart().y > yLine)) {

                activeLine = lines.get(i);
                activeLine.countNewX();
                lines.remove(i);
                break;

            }

            if ((lines.get(i).getStart().y == yLine && lines.get(i).getEnd().y > yLine) ||
                    (lines.get(i).getEnd().y == yLine && lines.get(i).getStart().y > yLine)) {

                activeLine = lines.get(i);
                lines.remove(i);
                break;

            }

        }
        return activeLine;
    }


    public ArrayList<Line> getLines() {
        return lines;
    }

    public void setLines(ArrayList<Line> lines) {
        this.lines = lines;
    }

    public Integer getDeltaY() {
        return deltaY;
    }

    public void subDeltaY() {
        this.deltaY -= 1;
    }

    public Integer getMinY() {
        return minY;
    }

    public void setMinY(Integer minY) {
        this.minY = minY;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public Line getLeftActiveLine() {
        return leftActiveLine;
    }

    public void setLeftActiveLine(Line leftActiveLine) {
        this.leftActiveLine = leftActiveLine;
    }

    public Line getRightActiveLine() {
        return rightActiveLine;
    }

    public void setRightActiveLine(Line rightActiveLine) {
        this.rightActiveLine = rightActiveLine;
    }


    public Color getColor() {
        return color;
    }
}
