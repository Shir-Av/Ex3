package gameClient;
import Server.Game_Server;
import Server.game_service;
import dataStructure.*;
import elements.Fruit;
import elements.Robot;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Point3D;
import utils.Range;
import utils.StdDraw;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MyGameGUI implements Runnable {

    game_service game;
    private ArrayList<Fruit> fruits;
    private ArrayList<Robot> robots;
    private int MC;
    Timer timer;
    graph g = new DGraph();
    private Range rangeX;
    private Range rangeY;
    boolean graphInit = false;
    boolean insertRobot = false;
    public static final double EPS1 = 0.00001, EPS2 = EPS1+EPS1, EPS=EPS2;


    public MyGameGUI()
    {
        initGUI();
        StdDraw.g = this;

    }

    public void initGUI() {
        StdDraw.setCanvasSize(1600, 1000);
        Range rX = range_x();
        Range rY = range_y();
        StdDraw.setXscale(rX.get_min() - 5, rX.get_max() + 5);
        StdDraw.setYscale(rY.get_min() - 5, rY.get_max() + 5);
        StdDraw.g=this;
    }

    public void initGraph(int level) {
        this.game = Game_Server.getServer(level);
        String graph = Game_Server.getServer(level).getGraph();
        this.g = new DGraph(graph);
        this.fruits = new ArrayList<Fruit>();
        List<String> fruitsString = Game_Server.getServer(level).getFruits();
        for (String s : fruitsString)
        {
            Fruit f = new Fruit(s);
            this.fruits.add(f);
        }

        this.fruits.sort((o1, o2) -> (int)(o2.getValue())-(int)(o1.getValue()));
        this.robots = new ArrayList<Robot>();

        StdDraw.g = this;
    }


    public void set_automatic_game(int level)
    {
        int robotNum = 0;
        try {
            JSONObject info = new JSONObject(game.toString());
            JSONObject jRob = info.getJSONObject("GameServer");
             robotNum = jRob.getInt("robots");
        }
        catch (Exception e)
        {
            System.out.println("fail here");
        }

        while (robotNum > 0)
        {
            for(Fruit f: this.fruits)
            {
               edge_data e = edgeWithFruit(f,g);
                game.addRobot(e.getSrc());
            }
            robotNum--;
        }
        List<String> robotString = game.getRobots();
        for (String s : robotString) {
            Robot r = new Robot(s);
            this.robots.add(r);
        }
        StdDraw.g = this;
    }


    private edge_data edgeWithFruit(Fruit f, graph g)
    {
        ArrayList<edge_data> edges = new ArrayList<edge_data>();
            try {
                JSONObject info = new JSONObject(game.getGraph());
                JSONArray jEdges = info.getJSONArray("Edges");
                for(int i = 0; i < jEdges.length(); ++i) {
                    int src = jEdges.getJSONObject(i).getInt("src");
                    int dest = jEdges.getJSONObject(i).getInt("dest");
                    double w = jEdges.getJSONObject(i).getDouble("w");
                    EdgeData e = new EdgeData(src, dest, w);
                    edges.add(e);
                }
            }
            catch (Exception e)
            {
                System.out.println("IOException is caught");////////////////////////////////
            }

                for (edge_data e : edges) {
                    if (isOnEdge(f.location,e,f.getType(),g)) {
                        return e;
                    }
                }
        return null;
    }
    //public void moveRobots

    public void startAutomaticGame ()
    {

    }

    public void Start_manual_game()
    {
        this.initGUI();
        String chooseLevel = JOptionPane.showInputDialog(this, "Please select level 0-23");
        int level = Integer.parseInt(chooseLevel);
        initGraph(level);
        try {
            JSONObject info = new JSONObject(game.toString());
            JSONObject jRob = info.getJSONObject("GameServer");
            int robotNum = jRob.getInt("robots");
            int i = 0;
          //  JOptionPane.showMessageDialog(this, "You have " + robotNum + " robots to place. \n GO!");
        } catch (Exception e)
            {

            }
    }

    public void drawGraph() {

        graph g = this.g;
        StdDraw.setCanvasSize(1800, 1000);
        Range x = range_x();
        Range y = range_y();
        StdDraw.setXscale(x.get_min()-0.0007, x.get_max()+0.0007);
        StdDraw.setYscale(y.get_min()-0.0007, y.get_max()+0.0007);
        StdDraw.setPenColor(Color.blue);
        StdDraw.setPenRadius(0.15);
        String s = "";
        double ScaleX = ((rangeX.get_max()-rangeX.get_min())*0.04);
        for (node_data n : this.g.getV()) {
            Point3D currNode = n.getLocation();
            StdDraw.setPenColor(new Color(113,8,125));
            StdDraw.filledCircle(currNode.x(), currNode.y(),ScaleX*0.28);
            s += Integer.toString(n.getKey());
            StdDraw.text(currNode.x() , currNode.y()+ScaleX*0.6 , s);
            s = "";
            for (edge_data e : this.g.getE(n.getKey())){
                double src_x = n.getLocation().x();
                double src_y = n.getLocation().y();
                double dest_x = this.g.getNode(e.getDest()).getLocation().x();
                double dest_y = this.g.getNode(e.getDest()).getLocation().y();
                StdDraw.setPenColor(Color.darkGray);
                StdDraw.setPenRadius(0.003);
                StdDraw.line(src_x , src_y , dest_x , dest_y);
                double w = Math.round(e.getWeight()*100.0)/100.0;
                String weight = Double.toString(w);
                StdDraw.text(src_x * 0.3 + dest_x * 0.7 , src_y * 0.3 + dest_y * 0.7 , weight);
              //  StdDraw.setPenColor(new Color(246,237,111));
                StdDraw.setPenColor(new Color(156,246,111));
                StdDraw.setPenRadius(0.15);
                StdDraw.filledCircle(src_x * 0.2 + dest_x * 0.8, src_y * 0.2 + dest_y * 0.8, ScaleX*0.18);
               // System.out.println("hi");
            }
        }
    }

    public void drawFruits ()
    {
        for (Fruit f : this.fruits)
        {
            StdDraw.picture(f.location.x() , f.location.y() , f.getImg() , 0.0005 , 0.0004);
        }
    }

    public void drawRobots()
    {
        for (Robot r : this.robots)
        {
            StdDraw.picture(r.location.x() , r.location.y() , r.getImg() , 0.0011 , 0.0010);
        }
        insertRobot = false;
    }

   /*
    @Override
    public void mousePressed(MouseEvent e) {

        int x = e.getX();
        int y = e.getY();
        System.out.println(x);
        Point3D p = new Point3D(x, y);
        Robot rob = new Robot(p);
        robots.add(rob);
        insertRobot = true;
        repaint();
    }*/


    private Range range_x() {
    Range range;
        if (this.g.getV().size() == 0) {
            range = new Range(-100,100);
            this.rangeX = range;
            return range;
        }
        double min_x = Double.MAX_VALUE;
        double max_x = Double.MIN_VALUE;
        for (node_data n : this.g.getV()) {
            if (n.getLocation().x() < min_x) {
                min_x = n.getLocation().x();
            }
            if (n.getLocation().x() > max_x) {
                max_x = n.getLocation().x();
            }
        }
        range = new Range(min_x,max_x);
        this.rangeX = range;
        return range;
    }

    private Range range_y() {
        Range range;
        if (this.g.getV().size() == 0) {
            range = new Range(-100,100);
            this.rangeX = range;
            return range;
        }

        double min_y = Double.MAX_VALUE;
        double max_y = Double.MIN_VALUE;
        for (node_data n : this.g.getV()) {
            if (n.getLocation().y() < min_y) {
                min_y = n.getLocation().y();
            }
            if (n.getLocation().y() > max_y) {
                max_y = n.getLocation().y();
            }
        }
        range = new Range(min_y,max_y);
        this.rangeX = range;
        return range;
    }

    public static boolean isOnEdge(Point3D p, Point3D src, Point3D dest)
    {
        boolean ans = false;
        double dist = src.distance2D(dest);
        double d1 = src.distance2D(p) + p.distance2D(dest);
        if(dist > d1-EPS)
        {
            ans = true;
        }
        return ans;
    }
    public static boolean isOnEdge(Point3D p, int s, int d, graph g)
    {
        Point3D src = g.getNode(s).getLocation();
        Point3D dest = g.getNode(d).getLocation();
        return isOnEdge(p, src, dest);
    }
    public static boolean isOnEdge(Point3D p, edge_data e, int type, graph g)
    {
        int src = g.getNode(e.getSrc()).getKey();
        int dest = g.getNode(e.getDest()).getKey();
        if (type < 0 && dest > src) return false;
        if (type > 0 && dest < src) return false;
        return isOnEdge(p, src, dest, g);
    }


    @Override
    public void run() {
        /*long first = System.currentTimeMillis();
        while (game.isRunning()) {
                initGUI();
            if (System.currentTimeMillis() - first >= 1000) {
                StdDraw.setPenColor();
                StdDraw.setPenRadius(0.02);
                StdDraw.text(this.gui.findRangeX().get_max(), this.gui.findRangeY().get_max() + 0.005,
                        "time to end : " + this.game.timeToEnd() / 1000);
                StdDraw.setPenRadius();
            }
            showScore();
            StdDraw.enableDoubleBuffering();
            for (int j = 0; j < this.game.getRobots().size(); j++) {
                drawRobots();
                moveRobots(this.game, this.graph, j);
                drawFruits();
            }
            StdDraw.show();

        }*/
    }

    public static void main(String[] args) {
        MyGameGUI gg = new MyGameGUI();


    }

}
