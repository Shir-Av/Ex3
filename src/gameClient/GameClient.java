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
import utils.StdDraw;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GameClient extends Thread{

    public game_service game;
    public ArrayList<Fruit> fruits;
    public ArrayList<Robot> robots;
    graph g = new DGraph();
    public static int gameLevel;
    public ArrayList<edge_data> ef = new ArrayList<>();
    private Graph_Algo gAlgo = new Graph_Algo();
    public static final double EPS1 = 0.0000009, EPS2 = EPS1+EPS1, EPS=EPS2;
    public String gPic = "";
    static int d = 0;
    MyGameGUI myGui;

    /**
     * build new game by calling the initGraph function
     */
    public GameClient (int level)
    {
        gameLevel = level;
        initGraph(level);
    }

    /**
     * this function create a new game for this level,
     * this function starts a new automatic game.
     */
    public void startAutomaticGame (){
        set_automatic_game();
        game.startGame();
        myGui.t.start();
        String results = game.toString();
        System.out.println("Game Over: " + results);
    }

    /**
     * this function set and update the fruits and the robots lists.
     * this function also gives the robots their first position (closest to the fruits).
     */
    public void set_automatic_game()
    {
        int robotNum = 0;
        try {
            JSONObject info = new JSONObject(game.toString());
            JSONObject jRob = info.getJSONObject("GameServer");
            robotNum = jRob.getInt("robots");
            String g = jRob.getString("graph");
            this.gPic = g;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        while (robotNum > 0)
        {
            if (gameLevel == 16){
                game.addRobot(25);
                robotNum--;
            }
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
    }

    /**
     * this function gets information about the current level from the server
     * and initializing the graph of this level.
     * @param level
     */
    public void initGraph(int level) {
        this.game = Game_Server.getServer(level);
        String graph = Game_Server.getServer(level).getGraph();
        this.g = new DGraph(graph);
        this.gAlgo.init(g);
        this.fruits = new ArrayList<Fruit>();
        initFruits();
        this.robots = new ArrayList<Robot>();
    }

    /**
     * this function initializing the fruits list, it gets information about the fruits from the server
     * and updating the fruits list.
     * this function also sorts the fruits list.
     */
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

    /**
     * this function initializing the robots list, it gets information about the robots from the server
     * and updating the robots list.
     */
    public void initRobots()
    {
        this.robots.clear();
        List<String> robotString = game.getRobots();
        for (String s : robotString) {
            Robot r = new Robot(s);
            this.robots.add(r);
        }
    }

    /**
     * this function gets fruit and returns the edge of this fruit.
     * @param f
     * @return
     */
    public edge_data edgeWithFruit(Fruit f)
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
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        for (edge_data e : edges) {
            if (isOnEdge(f.location,e,f.getType(),this.g)) {
                return e;
            }
        }
        return null;

    }

    /**
     * this function gets robot's src position and return the closest edge with fruit on it.
     * this edge will be the next destination for the robot.
     * @param robSrc
     * @return
     */
    public edge_data nextEdge(int robSrc)
    {
        Fruit fruit = new Fruit();
        int ansSrc = robSrc;
        int ansDest = robSrc;
        int ansSrc2 = robSrc;
        int ansDest2 =robSrc;
        double minPath = Double.MAX_VALUE;
        initFruits();
        for (Fruit f : this.fruits)
        {
            if (f.getTag() == 1){
                continue;
            }

            edge_data edgeOfFruit = edgeWithFruit(f);
            double shortest = this.gAlgo.shortestPathDist(robSrc,edgeOfFruit.getSrc());
            if (shortest < minPath)
            {
                minPath = shortest;
                ansSrc2 = ansSrc;
                ansDest2 = ansDest;
                ansSrc = edgeOfFruit.getSrc() ;
                ansDest = edgeOfFruit.getDest();
                fruit = f;
            }
            else if(ansSrc2 == ansDest2){
                ansSrc2 = edgeOfFruit.getSrc() ;
                ansDest2 = edgeOfFruit.getDest();
            }

        }
        fruit.setTag(1);
        edge_data e = g.getEdge(ansSrc,ansDest);
        if (ef.contains(e)){
            e = g.getEdge(ansSrc2,ansDest2);
        }
        if (!ef.contains(e)) {
            ef.add(e);
           // System.out.println(ef);
        }

        return e;
    }
    /**
     * this function gets src and dest and returns list of nodes of the shortest path between these two.
     * @param src
     * @param dest
     * @return
     */
    public List<node_data> shortestPathToFruit(int src, int dest){
        return this.gAlgo.shortestPath(src, dest);
    }

    /**
     * this function moves all the robots to their next destination,
     * using the algorithms of shortest path.
     */
    public void moveRobots ()
    {
        List<String> moveList = this.game.move();
        boolean b = false;
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
                        edge_data e = nextEdge(srcR);
                        dest = e.getSrc();


                        if (dest == srcR) {
                            game.chooseNextEdge(rId, e.getDest());
                        }
                        else //if the list isn't empty than run on the node list to the end and than set the dest to be the next node on ths list
                        {
                            List<node_data> nodesSrcToDest = shortestPathToFruit(srcR, dest);
                            for (node_data n : nodesSrcToDest)
                            {
                                dest = n.getKey();
                                game.chooseNextEdge(rId, dest);
                            }
                            dest = e.getDest();
                            game.chooseNextEdge(rId, dest);
                        }
                    }
                    if (i == 0)
                    {
                        b = true;
                        d = dest;
                    }
                    if (b && i == 1 && d == dest)
                    {
                        game.chooseNextEdge(rId, 24);
                    }

                  /*  System.out.println("Turn to node: "+dest+"  time to end:"+(time/1000));
                    System.out.println(ttt);*/
                } catch (JSONException e) {
                    System.out.println("fail here");
                    e.printStackTrace();
                }
            }
            ef.clear();
        }
    }


    /**
     * these three boolean functions check and return true if a given fruits is on a given edge.
     * return false otherwise.
     */

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
}