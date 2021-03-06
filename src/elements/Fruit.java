package elements;

import dataStructure.node_data;
import org.json.JSONObject;
import utils.Point3D;

import java.util.Collection;

public class Fruit {
    public Point3D location;
    private double value;
    private int type;
    private String img;
    private int tag ;

    public Fruit(String s) {
        try {
            JSONObject Fruits = new JSONObject(s);
            JSONObject fruit = Fruits.getJSONObject("Fruit");
            String pos = fruit.getString("pos");
            this.location = new Point3D(pos);
            this.value = fruit.getDouble("value");
            this.type = fruit.getInt("type");
            if (this.type == 1) {
                this.img = "apple.png";
            } else {
                this.img = "banana.png";
            }
            this.tag = 0;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    public Fruit (){
        this.value = 0;
        this.type = 0;
        this.location = null;
        this.img = null;
        this.tag = 0;
    }


    public Point3D getLocation()
    {
        return location;
    }

    public void setLocation(Point3D location)
    {
        this.location = location;
    }

    public double getValue()
    {
        return value;
    }

    public void setValue(double value)
    {
        this.value = value;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getImg()
    {
        return img;
    }

    public void setImg(String img)
    {
        this.img = img;
    }

    public String toString() {
        return this.toJSON();
    }

    public int getTag()
    {
        return tag;
    }

    public void setTag(int tag)
    {
        this.tag = tag;
    }

    public String toJSON() {

        String ans = "{\"Fruit\":{\"value\":" + this.value + "," + "\"type\":" + type + "," + "\"pos\":\"" + this.location.toString() + "\"" + "}" + "}";
        return ans;
    }
}