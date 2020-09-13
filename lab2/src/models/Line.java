package models;


public class Line {
    private Point start;
    private Point end;

    //Пересечение ребра с текущей сканирующей строкой
    private double x;

    //приращение X в интервале между соседними сканирующими строками
    private double deltaX;

    //если overflow >= 1, x += 1
    private double overflow;

    //Число сканирующих строк, пересекаемых ребром
    private int deltaY;

    public Line(Point start, Point end){
        this.start = start;
        this.end = end;
        deltaY =  Math.abs(end.y - start.y);
        if (deltaY == 0) {
            deltaX = Double.MAX_VALUE;
        }

        else {
            deltaX = (double) (end.x - start.x) / (end.y - start.y);
        }

        if (start.y < end.y)
            x = start.x;
        else
            x = end.x;


    }


    /*
     *  Расчёт нового значения координаты x с учётом старого значения и с учётом приращения
     */
    public void countNewX() {
        x += deltaX;

    }

    public int getX() {
        return (int)x;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(double deltaX) {
        this.deltaX = deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public void subDeltaY() {
        this.deltaY -= 1;
    }

    public Point getStart() {
        return start;
    }


    public Point getEnd() {
        return end;
    }

}
