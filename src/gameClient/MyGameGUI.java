package gameClient;
import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.*;
import elements.Fruit;
import elements.Robot;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils.Point3D;
import utils.Range;
import utils.StdDraw;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyGameGUI  implements Runnable {

    game_service game;
    private ArrayList<Fruit> fruits;
    private ArrayList<Robot> robots;
    private int MC;
    Timer timer;
    graph g = new DGraph();
    private Graph_Algo gAlgo = new Graph_Algo();
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
        StdDraw.setCanvasSize(1900, 1000);
        Range x = range_x();
        Range y = range_y();
        StdDraw.setXscale(x.get_min()-0.0007, x.get_max()+0.0007);
        StdDraw.setYscale(y.get_min()-0.0007, y.get_max()+0.0007);
        StdDraw.g=this;
    }

    public void initGraph(int level) {
        this.game = Game_Server.getServer(level);
        String graph = Game_Server.getServer(level).getGraph();
        this.g = new DGraph(graph);
        this.gAlgo.init(g);
        this.fruits = new ArrayList<Fruit>();
        initFruits();
        this.robots = new ArrayList<Robot>();
        StdDraw.g = this;
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
        List<String> robotString = game.getRobots();
        for (String s : robotString) {
            Robot r = new Robot(s);
            this.robots.add(r);
        }
    }

    public void set_automatic_game()
    {
        String chooseLevel = JOptionPane.showInputDialog( "Please select level 0-23");
        int level = Integer.parseInt(chooseLevel);
        initGraph(level);
        initGUI();
        drawGraph();
        drawFruits();

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
                edge_data e = edgeWithFruit(f);
                game.addRobot(e.getSrc());
            }
            robotNum--;
        }
        List<String> robotString = game.getRobots();
        for (String s : robotString) {
            Robot r = new Robot(s);
            this.robots.add(r);
        }
        drawRobots();
        StdDraw.g = this;
    }


    private edge_data edgeWithFruit(Fruit f)
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
            if (isOnEdge(f.location,e,f.getType(),this.g)) {
                return e;
            }
        }
        return null;
    }

    public edge_data nextEdge(int robSrc)
    {
        int ansSrc = robSrc;
        int ansDest = robSrc;
        //Graph_Algo ga = (Graph_Algo)g;
        double minPath = Double.MAX_VALUE;
        initFruits();
        for (Fruit f : this.fruits)
        {
            edge_data edgeOfFruit = edgeWithFruit(f);
            double shortest = this.gAlgo.shortestPathDist(robSrc,edgeOfFruit.getSrc());
            if (shortest < minPath)
            {
                minPath = shortest;
                ansSrc = edgeOfFruit.getSrc() ;
                ansDest = edgeOfFruit.getDest();
            }
        }
        return this.g.getEdge(ansSrc,ansDest);
    }

    public List<node_data> shortestPathToFruit(int src, int dest){
        return this.gAlgo.shortestPath(src, dest);
    }

    public void moveRobots ()
    {
        List<String> moveList = this.game.move();
        if (moveList != null) {
            long time = this.game.timeToEnd();
            for (int i = 0; i < moveList.size(); i++) {
                String robot_json = moveList.get(i);
                try {
                    JSONObject line = new JSONObject(robot_json);
                    JSONObject ttt = line.getJSONObject("Robot");
                    int rId = ttt.getInt("id");
                    int srcR = ttt.getInt("src");
                    int dest = ttt.getInt("dest");

                    if(dest == -1)
                    {
                        dest = nextEdge(srcR).getSrc();

                        if (dest == srcR) {

                            game.chooseNextEdge(rId, nextEdge(srcR).getDest());
                        }
                        else //if the list isnt empty than run on the node list to the end and than set the dest to be the next node on ths list
                        {
                            List<node_data> nodesSrcToDest = shortestPathToFruit(srcR, dest);
                            for (node_data n : nodesSrcToDest)
                            {
                                dest = n.getKey();
                                game.chooseNextEdge(rId, dest);
                            }
                            dest = nextEdge(srcR).getDest();
                            game.chooseNextEdge(rId, dest);
                        }
                    }

                    System.out.println("Turn to node: "+dest+"  time to end:"+(time/1000));
                    System.out.println(ttt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void score() {
        try {
            String gameinfo = game.toString();
            JSONObject line = new JSONObject(gameinfo);
            JSONObject ttt = line.getJSONObject("GameServer");
            int score = ttt.getInt("grade");

            StdDraw.text(rangeX.get_max(), rangeY.get_max() + 0.004, "score : " + score);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        StdDraw.g = this;
    }


    public void startAutomaticGame (){
        set_automatic_game();
        Thread t = new Thread(this);
        game.startGame();
        t.start();
        String results = game.toString();
        System.out.println("Game Over: " + results);
        StdDraw.g = this;
    }
    public void set_manual_game()
    {
        String chooseLevel = JOptionPane.showInputDialog( "Please select level 0-23");
        int level = Integer.parseInt(chooseLevel);
        JFrame roby = new JFrame();
        initGraph(level);
        initGUI();
        drawGraph();
        drawFruits();
        StdDraw.g = this;
        int robotNum = 0;
        try
        {
            JSONObject info = new JSONObject(game.toString());
            JSONObject jRob = info.getJSONObject("GameServer");
            robotNum = jRob.getInt("robots");
            JOptionPane.showMessageDialog(roby, "You have " + robotNum + " robots to place. \n GO!");
        } catch (Exception e)
        {

        }
        for (int i=0; i< robotNum; i++) // for the first position
        {
            Robot robot = robots.get(i);
            if (robot.getDest() == -1)
            {
                String dst_str = JOptionPane.showInputDialog("Please insert robot " + robot.getId() + " first position");
                try {
                    int dest = Integer.parseInt(dst_str);
                    this.game.addRobot(dest);
                }
                catch (Exception ex)
                {
                    JOptionPane.showInputDialog("ERROR");
                }
            }
        }
        drawRobots();

    }
    public void Start_manual_game() {
        set_automatic_game();
        Thread t = new Thread(this);
        game.startGame();
        t.start();
    }
    public void moveRobotsManually()
    {
        JFrame roby = new JFrame();
        for (int i = 0; i < robots.size(); i++) {
            Robot rob = robots.get(i);
            if (rob.getDest() == -1) //if this robot doesnt have a next edge:
            {
                Object[] neighbors1 = checkNeighbors(rob.src);
                String s = (String) JOptionPane.showInputDialog(roby, "Select the next nodeId: ", "Next step",
                        JOptionPane.PLAIN_MESSAGE, null, neighbors1, neighbors1[0]);
                game.chooseNextEdge(rob.id, Integer.parseInt(s));

            }
                String results = game.toString();
                System.out.println("Game Over: " + results);
                StdDraw.g = this;

                }
            }


    public Object[] checkNeighbors (int robSrc)
    {
        Object[] neighbors2 = new Object[g.getE(robSrc).size()];
        for(edge_data e: this.g.getE(robSrc))
        {
            int j = 0;
            neighbors2[j] = e.getDest();
            j++;
            System.out.println(neighbors2);
        }

        return neighbors2;
    }

    public void drawGraph() {

        StdDraw.clear();
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.blue);
        StdDraw.setPenRadius(0.15);
        String s = "";
        double ScaleX = ((rangeX.get_max()-rangeX.get_min())*0.04);
        for (node_data n : this.g.getV()) {
            Point3D currNode = n.getLocation();
            StdDraw.setPenColor(new Color(113,8,125));
            StdDraw.filledCircle(currNode.x(), currNode.y(),ScaleX*0.1);
            s += Integer.toString(n.getKey());
            StdDraw.text(currNode.x() , currNode.y()+ScaleX*0.2 , s);
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
                StdDraw.setPenColor(new Color(156,246,111));
                StdDraw.setPenRadius(0.15);
                StdDraw.filledCircle(src_x * 0.2 + dest_x * 0.8, src_y * 0.2 + dest_y * 0.8, ScaleX*0.07);


            }
        }
    }

    public void drawFruits ()
    {
        initFruits();
        for (Fruit f : this.fruits)
        {
            StdDraw.picture(f.location.x() , f.location.y() , f.getImg() , 0.0005 , 0.0004);
        }
    }

    public void drawRobots()
    {
        initRobots();
        for (Robot r : this.robots)
        {
            StdDraw.picture(r.location.x() , r.location.y() , r.getImg() , 0.0011 , 0.0010);
        }
        insertRobot = false;
    }




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
            this.rangeY = range;
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
        this.rangeY = range;
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
        long first = System.currentTimeMillis();
        while (game.isRunning()) {
            StdDraw.setPenColor(Color.RED);
            StdDraw.setPenRadius(5);
            StdDraw.text(35 * 2500, 32 * 2500, "" + game.timeToEnd());
            if (System.currentTimeMillis() - first >= 1000) {
                StdDraw.setPenColor(Color.RED);
                StdDraw.setPenRadius(5);
                StdDraw.text(35 * 2500, 32 * 2500, "" + game.timeToEnd());
                //  StdDraw.text(rangeX.get_max()-5, rangeY.get_max() - 5,"time to end : " + this.game.timeToEnd() / 1000);

                StdDraw.setPenRadius();
            }
            score();
            moveRobots();
            drawGraph();
            drawFruits();
            drawRobots();
            StdDraw.show();
        }
        System.out.println("Game over " + this.game.toString());
    }

    public static void main(String[] args) {
        MyGameGUI gg = new MyGameGUI();


    }
    }

