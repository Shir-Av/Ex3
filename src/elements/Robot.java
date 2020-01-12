package elements;

import org.json.JSONObject;
import utils.Point3D;

public class Robot {

    public int id;
    public int value;
    public double speed;
    public int src;
    public int dest;
    public Point3D location;
    private static int i = 0;

    public Robot()
    {
        this.id  = i++;
        this.value = 0;
        this.speed = 0;
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
}

