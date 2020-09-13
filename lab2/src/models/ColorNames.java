package models;

import javafx.scene.paint.Color;

public class ColorNames {
    public static String getNameColor(Color color){
        if (Color.WHITE.equals(color))
            return "Бел";
        if (Color.RED.equals(color))
            return "Крас";
        if (Color.BLUE.equals(color))
            return "Син";
        if (Color.GREEN.equals(color))
            return "Зел";
        if (Color.YELLOW.equals(color))
            return "Жёлт";
        if (Color.ORANGE.equals(color))
            return "Оран";
        if (Color.GRAY.equals(color))
            return "Сер";
        return "Неизв";
    }
}
