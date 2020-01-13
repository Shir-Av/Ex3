package elements;

import org.json.JSONObject;
import utils.Point3D;

import javax.swing.*;
import java.awt.*;

public class Robot {

    public int id;
    public int value;
    public double speed;
    public int src;
    public int dest;
    public Point3D location;
    private static int i = 0;
    private String img = "robot.png";

    public Robot()
    {
        this.id  = i++;
        this.value = 0;
        this.speed = 0;
        this.img = "robot.png";
    }
    public Robot(Point3D p)
    {
        this.location = p;
        this.id  = i++;
        this.img = "robot.png";
    }

    public Robot(String s) {
        try {
            JSONObject Robot = new JSONObject(s);
            JSONObject robot = Robot.getJSONObject("Robot");
            String pos = robot.getString("pos");
            this.location = new Point3D(pos);
            this.value = robot.getInt("value");
            this.id = robot.getInt("id");
            this.img = "robot.png";
            this.src =  robot.getInt("src");
            this.dest =  robot.getInt("dest");
            }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }





    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public int getDest() {
        return dest;
    }

    public void setDest(int dest) {
        this.dest = dest;
    }

    public Point3D getLocation() {
        return location;
    }

    public void setLocation(Point3D location) {
        this.location = location;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}

