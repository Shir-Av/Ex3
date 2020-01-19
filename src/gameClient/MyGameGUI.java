package gameClient;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.*;
import elements.Fruit;
import elements.Robot;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Point3D;
import utils.Range;
import utils.StdDraw;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyGameGUI extends Thread {//implements Runnable {
    public GameClient gameClient;
    public game_service game;
    private ArrayList<Fruit> fruits = new ArrayList<>();
    private ArrayList<Robot> robots = new ArrayList<>();
    private static int currLevel;
    public static Thread t;
    graph g = new DGraph();
    private Graph_Algo gAlgo = new Graph_Algo();
    private Range rangeX;
    private Range rangeY;
    boolean graphInit = false;
    boolean isAutoMode = false;
    public static final double EPS1 = 0.000001, EPS2 = EPS1+EPS1, EPS=EPS2;
    private static KML_Logger log;


    public MyGameGUI()
    {
        initGUI();
        StdDraw.g = this;

    }

    public void initGUI() {
        StdDraw.setCanvasSize(1900, 1000);
        if (graphInit) {
            rangeX = range_x();
            rangeY = range_y();
            StdDraw.setXscale(rangeX.get_min() - 0.0007, rangeX.get_max() + 0.0007);
            StdDraw.setYscale(rangeY.get_min() - 0.0007, rangeY.get_max() + 0.0007);
        }
        StdDraw.g=this;
    }

    private void scoreAndTimer() {
        try {
            String gameInfo = gameClient.game.toString();
            JSONObject line = new JSONObject(gameInfo);
            JSONObject ttt = line.getJSONObject("GameServer");
            int score = ttt.getInt("grade");
            int moves = ttt.getInt("moves");
            StdDraw.setPenColor(new Color(9,30,80));
            StdDraw.setPenRadius(0.4);
            Font font = new Font("Arial", Font.BOLD, 20);
            StdDraw.setFont(font);
            StdDraw.text(rangeX.get_max()-0.0008, rangeY.get_max() , "Score : " + score);
            StdDraw.setPenColor(new Color(14,92,35));
            StdDraw.setPenRadius(0.4);
            StdDraw.text(rangeX.get_max()-0.0008, rangeY.get_max()+ 0.0003 ,"Time to end : " +gameClient.game.timeToEnd() / 1000);
            StdDraw.setPenRadius(0.015);
            StdDraw.setPenColor(new Color(142,17,17));
            StdDraw.rectangle(rangeX.get_max()-0.0009,rangeY.get_max()+ 0.0001,0.0014,0.0004);
            StdDraw.setPenColor(new Color(255,85,0));
            StdDraw.text(rangeX.get_min()+0.00005, rangeY.get_max()+0.0005 ,"Level: "+currLevel);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        StdDraw.g = this;
    }

    public void gameMode (int mode)
    {
        String chooseLevel = JOptionPane.showInputDialog( "Please select level 0-23");
        int level = Integer.parseInt(chooseLevel);
        currLevel = level;
        this.gameClient = new GameClient(level);
        StdDraw.g = this;
        graphInit = true;
        initGUI();

        if (mode == 1){
            log =new KML_Logger(level);
            isAutoMode = true;
            t = new Thread(this);
            gameClient.startAutomaticGame(level);
            StdDraw.g = this;
        }
        else {
            log =new KML_Logger(level);
            this.game = this.gameClient.game;
            this.g = this.gameClient.g;
            initFruits();
            t = new Thread(this);
            start_manual_game(level);


        }
    }

    public void set_manual_game(int level)
    {
        drawGraph((DGraph)this.g);
        drawFruits();
        StdDraw.g = this;
        StdDraw.show();
        int robotNum = 0;
        try
        {
            JSONObject info = new JSONObject(game.toString());
            JSONObject jRob = info.getJSONObject("GameServer");
            robotNum = jRob.getInt("robots");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int j = robotNum;
        for (int i=1; i <= robotNum; i++) // for the first position
        {
            String dst_str = JOptionPane.showInputDialog("You have " + j-- + " robots to place. \n Please insert robot number " + i + " first position :");
            try {
                int dest = Integer.parseInt(dst_str);
                this.game.addRobot(dest);
            }
            catch (Exception ex)
            {
                JOptionPane.showInputDialog("ERROR");
            }
        }
        drawRobots();
        StdDraw.g = this;
    }
    public void start_manual_game(int level) {
        set_manual_game(level);
        game.startGame();
        t.start();
        StdDraw.g = this;
    }
    public void moveRobotsManually()
    {
        JFrame roby = new JFrame();
        for (Robot rob : robots) {
            if (StdDraw.isMousePressed()) {
                if (rob.getId() == robotPressed()) {
                    Object[] neighbors1 = checkNeighbors(rob.src);
                    Object s = JOptionPane.showInputDialog(roby, "Select the next nodeId for Robot number: " + rob.id, "Next step",
                            JOptionPane.PLAIN_MESSAGE, null, neighbors1, neighbors1[0]);
                    int n = (Integer)s;
                    this.game.chooseNextEdge(rob.id, n);
                    System.out.println("after choose");
                }
                StdDraw.g = this;
            }
        }
    }
    //check which robot was pressed return robot id
    public int robotPressed()
    {
        try {
            if (!game.isRunning()) {
                return -1;
            }
        }
        catch (Exception ex) {
        }
        List<String> logR = game.getRobots();
        if (logR != null)
        {
            for (int i = 0; i < logR.size(); i++) {
                String robot_json = logR.get(i);
                try {
                    JSONObject line = new JSONObject(robot_json);
                    JSONObject ttt = line.getJSONObject("Robot");
                    int id = ttt.getInt("id");
                    String pos = ttt.getString("pos");
                    Point3D pressedPoint = new Point3D(pos);
                    double xRobot = StdDraw.mouseX();
                    double yRobot = StdDraw.mouseY();
                    Point3D xyRobot = new Point3D(xRobot, yRobot);
                    if (xyRobot.distance2D(pressedPoint) <= 0.0002) {
                        return id;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }
    public Object[] checkNeighbors (int robSrc)
    {
        Object[] neighbors2 = new Object[g.getE(robSrc).size()];
        int j = 0;
        for(edge_data e: this.g.getE(robSrc))
        {
            neighbors2[j] = e.getDest();
            j++;
        }

        return neighbors2;
    }

    public void drawGraph(DGraph g) {
        StdDraw.clear();
        StdDraw.enableDoubleBuffering();
        String s = "";
        double sX = ((rangeX.get_max()-rangeX.get_min())*0.04);
        for (node_data n : g.getV()) {
            Point3D currNode = n.getLocation();
            StdDraw.setFont(StdDraw.NODES_FONT);
            StdDraw.setPenColor(new Color(113,8,125));
            StdDraw.filledCircle(currNode.x(), currNode.y(),sX*0.1);
            s += Integer.toString(n.getKey());
            StdDraw.text(currNode.x() , currNode.y()+sX*0.2 , s);
            s = "";
            for (edge_data e : g.getE(n.getKey())){
                double srcX = n.getLocation().x();
                double srcY = n.getLocation().y();
                double destX = g.getNode(e.getDest()).getLocation().x();
                double destY = g.getNode(e.getDest()).getLocation().y();
                StdDraw.setPenColor(Color.darkGray);
                StdDraw.setPenRadius(0.003);
                StdDraw.line(srcX , srcY , destX , destY);
                double w = Math.round(e.getWeight()*100.0)/100.0;
                String weight = Double.toString(w);
                StdDraw.setPenColor(Color.BLACK);
                StdDraw.setFont(StdDraw.EDGES_FONT);
                StdDraw.text(srcX * 0.3 + destX * 0.7 , srcY * 0.3 + destY * 0.7 , weight);
                StdDraw.setPenColor(new Color(156,246,111));
                // StdDraw.setPenRadius(0.15);
                StdDraw.filledCircle(srcX * 0.2 + destX * 0.8, srcY * 0.2 + destY * 0.8, sX*0.07);

            }
        }
        scoreAndTimer();
    }

    public void drawFruits ()
    {
        if (isAutoMode) {
            this.gameClient.initFruits();
            this.fruits = gameClient.fruits;
        }
        else{
            initFruits();
        }

        for (Fruit f : this.fruits)
        {
            if(f.getType() == 1){
                MyGameGUI.log.location_sampling("fruit_1",f.getLocation().toString());
            }
            else{
                MyGameGUI.log.location_sampling("fruit_-1",f.getLocation().toString());
            }
            StdDraw.picture(f.location.x() , f.location.y() , f.getImg() , 0.0005 , 0.0004);
        }
    }

    public void drawRobots()
    {
        if (isAutoMode) {
            gameClient.initRobots();
            this.robots = gameClient.robots;
        }
        else{
            initRobots();
        }

        for (Robot r : this.robots)
        {
            MyGameGUI.log.location_sampling("robot.png",r.getLocation().toString());
            StdDraw.picture(r.location.x() , r.location.y() , r.getImg() , 0.0011 , 0.0010);
        }
    }

    private Range range_x() {
        Range range;
        if (this.gameClient.g.getV().size() == 0) {
            range = new Range(-100,100);
            this.rangeX = range;
            return range;
        }
        double min_x = Double.MAX_VALUE;
        double max_x = Double.MIN_VALUE;
        for (node_data n : this.gameClient.g.getV()) {
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
        if (this.gameClient.g.getV().size() == 0) {
            range = new Range(-100,100);
            this.rangeY = range;
            return range;
        }
        double min_y = Double.MAX_VALUE;
        double max_y = Double.MIN_VALUE;
        for (node_data n : this.gameClient.g.getV()) {
            if (n.getLocation().y() < min_y) {
                min_y = n.getLocation().y();
            }
            if (n.getLocation().y() > max_y) {
                max_y = n.getLocation().y();
            }
        }
        range = new Range(min_y,max_y);
        this.rangeY = range;
        return range;
    }
    public void initFruits(){
        this.fruits.clear();
        List<String> fruitsString = this.game.getFruits();
        for (String s : fruitsString)
        {
            Fruit f = new Fruit(s);
            this.fruits.add(f);
        }
        this.fruits.sort((o1, o2) -> (int)(o2.getValue())-(int)(o1.getValue()));
    }

    public void initRobots()
    {
        this.robots.clear();
        List<String> robotString = this.game.getRobots();
        for (String s : robotString) {
            Robot r = new Robot(s);
            this.robots.add(r);
        }
    }


    @Override
    public void run() {
        if (isAutoMode)
        {
            while (gameClient.game.isRunning()) {
                try {
                    sleep(83);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.gameClient.moveRobots();
                drawGraph((DGraph) gameClient.g);
                drawFruits();
                drawRobots();
                StdDraw.show();
            }
            System.out.println("Game over " + this.gameClient.game.toString());
        }
        else {
            while (this.game.isRunning()) {
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                moveRobotsManually();
                this.game.move();
                drawGraph((DGraph) this.g);
                drawFruits();
                drawRobots();
                StdDraw.show();
            }
            System.out.println("Game over " + this.game.toString());

        }
        try {
            String gameInfo = gameClient.game.toString();
            JSONObject line = new JSONObject(gameInfo);
            JSONObject ttt = line.getJSONObject("GameServer");
            int score = ttt.getInt("grade");
            int moves = ttt.getInt("moves");
            JOptionPane.showMessageDialog(StdDraw.frame,"GAME OVER \n  Your grade is: "+ score + ".\n You made "+moves+" moves.");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log.KML_END();
    }

    public static void main(String[] args) {
        MyGameGUI gg = new MyGameGUI();


    }

    }