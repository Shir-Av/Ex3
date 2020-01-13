package gameClient;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import algorithms.graph_algorithms;
import dataStructure.*;
import elements.Fruit;
import elements.Robot;
import netscape.javascript.JSObject;
import oop_dataStructure.OOP_DGraph;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Point3D;
import  java.awt.event.MouseEvent;
import  java.awt.event.MouseListener;
import  java.awt.event.ActionEvent;
import  java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.awt.image.*;

public class MyGameGUI extends JFrame implements ActionListener ,MouseListener {

    game_service game;
    private ArrayList<Fruit> fruits;
    private ArrayList<Robot> robots;
    private int MC;
    Timer timer;
    graph g;
    boolean graphInit = false;
    boolean insertRobot = false;

    MyGameGUI()
    {
        initGUI();
    }

    public void initGUI() {
        JFrame frame = new JFrame();
        this.setTitle("My game");
        this.setMenuBar(createMenuBar());
        this.setSize(1500, 900);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JScrollPane pane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setContentPane(pane);
        // timer = new Timer(10,this);
        this.addMouseListener(this);
        this.setVisible(true);
    }

    public void initGraph(int level) {
        this.game = Game_Server.getServer(level);
        String graph = Game_Server.getServer(level).getGraph();
        this.g = new DGraph(graph);
        this.fruits = new ArrayList<Fruit>();
        List<String> fruitsString = Game_Server.getServer(level).getFruits();
        for (String s : fruitsString) {
            Fruit f = new Fruit(s);
            this.fruits.add(f);
        }

            this.robots = new ArrayList<Robot>();
        System.out.println(Game_Server.getServer(level).getRobots());
            List<String> robotString = Game_Server.getServer(level).getRobots();
            for (String s : robotString)
            {
                Robot r = new Robot(s);
                this.robots.add(r);
            }

        graphInit = true;
    }

    private MenuBar createMenuBar() {
        MenuBar MenuBar = new MenuBar();
        Menu play = new Menu("play");
        play.addActionListener(this);
        MenuBar.add(play);
        MenuItem Start_manual_game = new MenuItem("Start_manual_game");
        Start_manual_game.addActionListener(this);
        play.add(Start_manual_game);
        MenuItem Start_automatic_game = new MenuItem("Start_automatic_game");
        Start_automatic_game.addActionListener(this);
        play.add(Start_automatic_game);
        this.addMouseListener(this);
        return MenuBar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String str = e.getActionCommand();
        if (str.equals("Start_manual_game")) Start_manual_game();
        else if (str.equals("Start_automatic_game")) Start_automatic_game();
        repaint();
    }

    private void Start_automatic_game()
    {
        this.initGUI();
        String chooseLevel = JOptionPane.showInputDialog(this, "Please select level 0-23");
        int level = Integer.parseInt(chooseLevel);
        initGraph(level);
        ArrayList<edge_data> fruitOnEdges =  edgeFruitList(fruits, g);
        int robotNum = 0;
        try {
            JSONObject info = new JSONObject(game.toString());
            JSONObject jRob = info.getJSONObject("GameServer");
             robotNum = jRob.getInt("robots");
        }
        catch (Exception e)
        {
        }

        while (robotNum > 0)
        {
            int i = fruitOnEdges.size()-1;
            game.addRobot(fruitOnEdges.get(i).getSrc());


//            System.out.println(game.getRobots());
//            System.out.println(game.getFruits());
            repaint();
            robotNum--;
            i--;
            if (i < 0)
            {

                break;
            }
            insertRobot = true;
            initGraph(level);
            repaint();
        }





    }

    private ArrayList<edge_data> edgeFruitList(List<Fruit> fruitList, graph g)
    {
        double EPS = 0.03;
        ArrayList<edge_data> edges = new ArrayList<edge_data>();
        ArrayList<edge_data> edgeFruits = new ArrayList<edge_data>();
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
            }
            for (Fruit f: fruitList)
            {
                for (edge_data e: edges)
                {
                   double x1 = g.getNode(e.getSrc()).getLocation().x();
                   double y1 = g.getNode(e.getSrc()).getLocation().y();
                   double x2 = g.getNode(e.getDest()).getLocation().x();
                   double y2 = g.getNode(e.getDest()).getLocation().y();
                   double edgeDist = distance2Points(x1, y1, x2, y2);

                   double fx = f.location.x();
                   double fy = f.location.y();

                   double fruitToDist = distance2Points(fx, fy, x2, y2);
                   double fruitToSrc = distance2Points(fx, fy, x1, y1);

                   if((Math.abs(fruitToDist + fruitToSrc - edgeDist)) <= EPS)
                   {
                       edgeFruits.add(e);
                   }
                }
        }
        return edgeFruits;
    }
    public double distance2Points(double x1, double y1, double x2, double y2)
    {
        return Math.abs(Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)));
    }

    private void Start_manual_game()
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
            JOptionPane.showMessageDialog(this, "You have " + robotNum + " robots to place. \n GO!");
        } catch (Exception e) {

        }


    }

    public void paint(Graphics g) {
        super.paint(g);
        if (graphInit) {
            this.Draw(g);
        }
    }

    public void Draw(Graphics g) {

        Graphics2D g1 = (Graphics2D) g;
        Point3D minP = minPoint();
        Point3D maxp = maxPoint();
        for (node_data n : this.g.getV()) // This method return a pointer (shallow copy) for the  collection representing all the nodes in the graph
        {
            Point3D node_src = n.getLocation();
            Point3D currNodeScaledData = ScaleToFrame(n.getLocation(), minP, maxp);
            g.setColor(Color.DARK_GRAY);
            g.fillOval(currNodeScaledData.ix() - 5, currNodeScaledData.iy() - 5, 15, 15); //draw a point in the x,y location
            String keyName = "";
            keyName += n.getKey(); //sets a string with the key of each point
            g.setColor(Color.BLUE);
            g.setFont(new Font("deafult", Font.BOLD, 20));
            g.drawString(keyName, currNodeScaledData.ix() - 5, currNodeScaledData.iy() - 5);
            String tagInfoWeight = "";
            tagInfoWeight += ("(tag: " + n.getTag() + " \n" + "info: " + n.getInfo() + "\n" + " weight: " + n.getWeight() + ")");
            String loc = "";
            loc += n.getLocation();
            g.setColor(Color.BLACK);
            g.setFont(new Font("deafult", Font.BOLD, 14));

            // Draw all edges came out of the node:
            if (this.g.getE(n.getKey()) != null) {
                for (edge_data e : this.g.getE(n.getKey())) //return a pointer (shallow copy) for the collection representing all the edges getting out of the given node
                {
                    Point3D node_dest = this.g.getNode(e.getDest()).getLocation();
                    Point3D destNodeScaledData = ScaleToFrame(this.g.getNode(e.getDest()).getLocation(), minP, maxp);

                    double xSrc = currNodeScaledData.x();
                    double ySrc = currNodeScaledData.y();
                    double xDest = destNodeScaledData.x();
                    double yDest = destNodeScaledData.y();

                    g.setColor(Color.DARK_GRAY);
                    g.drawLine((int) xSrc, (int) ySrc, (int) xDest, (int) yDest);
                    g.setColor(Color.BLACK);
                    g.setColor(Color.magenta);
                    double xPoint = 0;
                    double yPoint = 0;
                    if (xSrc < xDest && ySrc < yDest) {
                        xPoint = xSrc + (Math.abs(xSrc - xDest) * 0.8);
                        yPoint = ySrc + (Math.abs(ySrc - yDest) * 0.8);
                    } else if (ySrc >= yDest && xSrc >= xDest) {
                        xPoint = xSrc - (Math.abs(xSrc - xDest) * 0.8);
                        yPoint = ySrc - (Math.abs(ySrc - yDest) * 0.8);
                    } else if (ySrc >= yDest && xSrc <= xDest) {
                        xPoint = xSrc + (Math.abs(xSrc - xDest) * 0.8);
                        yPoint = ySrc - (Math.abs(ySrc - yDest) * 0.85);
                    } else if (ySrc < yDest && xSrc > xDest) {
                        xPoint = xSrc - (Math.abs(xSrc - xDest) * 0.88);
                        yPoint = ySrc + (Math.abs(ySrc - yDest) * 0.85);
                    }

                    g.fillOval((int) xPoint, (int) yPoint, 15, 15);
                    g.setFont(new Font("deafult", Font.BOLD, 15));
                    g.setColor(Color.BLACK);
                    g.drawString("" + Math.round(e.getWeight() * 100.0) / 100.0, (int) xPoint - 20, (int) yPoint);
                }
            }
        }

        for (Fruit f : this.fruits) {
            ImageIcon img = new ImageIcon(f.getImg());
            Image i = img.getImage();
            Point3D fruitScaledData = ScaleToFrame(f.location, minP, maxp);
            g.drawImage(i, fruitScaledData.ix() -5, fruitScaledData.iy() - 10, 30, 30, null);
        }

            for (Robot r : this.robots) {
                ImageIcon img = new ImageIcon(r.getImg());
                Image i = img.getImage();
                Point3D robotscaledData = ScaleToFrame(r.location, minP, maxp);
                g.drawImage(i, robotscaledData.ix() - 5, robotscaledData.iy() - 10, 30, 30, null);
            }


    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

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
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


    private Point3D minPoint() {
        if (this.g.getV().size() == 0) {
            return null;
        }

        double min_x = Double.MAX_VALUE;
        double min_y = Double.MAX_VALUE;

        for (node_data n : this.g.getV()) {
            if (n.getLocation().x() < min_x) {
                min_x = n.getLocation().x();
            }
            if (n.getLocation().y() < min_y) {
                min_y = n.getLocation().y();
            }
        }
        return new Point3D(min_x, min_y);
    }

    private Point3D maxPoint() {
        if (this.g.getV().size() == 0) {
            return null;
        }
        double max_x = Double.MIN_VALUE;
        double max_y = Double.MIN_VALUE;

        for (node_data n : this.g.getV()) {
            if (n.getLocation().x() > max_x) {
                max_x = n.getLocation().x();
            }
            if (n.getLocation().y() > max_y) {
                max_y = n.getLocation().y();
            }
        }
        return new Point3D(max_x, max_y);
    }

    private Point3D ScaleToFrame(Point3D location, Point3D minPoint, Point3D maxPoint) {
        double r_min_x = minPoint.x();
        double r_max_x = maxPoint.x();
        double r_min_y = minPoint.y();
        double r_max_y = maxPoint.y();

        double t_min_x = 200;
        double t_max_x = this.getWidth() - 100;
        double t_min_y = 200;
        double t_max_y = this.getHeight() - 50;

        double x = location.x();
        double y = location.y();

        double res_x = ((x - r_min_x) / (r_max_x - r_min_x)) * (t_max_x - t_min_x) + t_min_x;
        double res_y = ((y - r_min_y) / (r_max_y - r_min_y)) * (t_max_y - t_min_y) + t_min_y;

        return new Point3D(res_x, res_y);
    }



    public static void main(String[] args) {
         MyGameGUI gg = new MyGameGUI();


    }
}
