package gameClient;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import algorithms.graph_algorithms;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import elements.Fruit;
import elements.Robot;
import oop_dataStructure.OOP_DGraph;
import utils.Point3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MyGameGUI extends JFrame implements ActionListener ,MouseListener{

        public graph g;
        String graph;
        private List<Fruit> fruits = new ArrayList<Fruit>();
       // private List<Robot> robots = new ArrayList<Robot>();
        private int MC;



        public MyGameGUI(int level)
        {
           this.graph = Game_Server.getServer(level).getGraph();
           this.g = new DGraph(graph);
            List<String> fruitsString = Game_Server.getServer(level).getFruits();
            for(String s : fruitsString) {
                Fruit f = new Fruit(s);
                this.fruits.add(f);
            }
        //   this.robots = Game_Server.getServer(level).getRobots();
            MC = 0;
           initGraph(g, level);

        }

        public void initGraph(graph g, int level) {
            JFrame frame = new JFrame();
            this.setTitle("my_graph");
            this.setMenuBar(createMenuBar());
            this.setSize(1500, 900);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JScrollPane pane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.setContentPane(pane);
            this.setVisible(true);
           // this.addMouseListener;


        }

        private MenuBar createMenuBar()
        {
            MenuBar MenuBar = new MenuBar();
            Menu File = new Menu("File");
            File.addActionListener(this);
            MenuBar.add(File);
            Menu Start_new_game = new Menu("Start_new_game");
            Start_new_game.addActionListener(this);
            MenuBar.add(Start_new_game);
            Menu Start_automatic_game = new Menu("Start_automatic_game");
            Start_automatic_game.addActionListener(this);
            MenuBar.add(Start_automatic_game);
            return MenuBar;
        }
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String str = e.getActionCommand();
            if (str.equals("Start_new_game")) Start_new_game();
            else if(str.equals("Start_automatic_game")) Start_automatic_game();
        }

    public void paint(Graphics g)
        {
            super.paint(g);
            this.Draw(g);
        }

        public void Draw(Graphics g)
        {
            Graphics2D g1 = (Graphics2D) g;
            Point3D minP = minPoint();
            Point3D maxp = maxPoint();
            for (node_data n : this.g.getV()) // This method return a pointer (shallow copy) for the  collection representing all the nodes in the graph
            {
                Point3D node_src = n.getLocation();
                Point3D currNodeScaledData = ScaleToFrame(n.getLocation(),minP,maxp);
                g.setColor(Color.RED);
                g.fillOval(currNodeScaledData.ix()-5, currNodeScaledData.iy()-5, 15, 15); //draw a point in the x,y location
                String keyName = "";
                keyName += n.getKey(); //sets a string with the key of each point
                g.setColor(Color.BLUE);
                g.setFont(new Font("deafult", Font.BOLD, 20));
                g.drawString(keyName, currNodeScaledData.ix()-5,currNodeScaledData.iy()-5);
                String tagInfoWeight = "";
                tagInfoWeight += ("(tag: " + n.getTag() +" \n"+ "info: " + n.getInfo() + "\n"+" weight: " + n.getWeight()+ ")");
                String loc = "";
                loc += n.getLocation();
                g.setColor(Color.BLACK);
                g.setFont(new Font("deafult", Font.BOLD, 14));

                // Draw all edges came out of the node:
                if (this.g.getE(n.getKey()) != null)
                {
                    for (edge_data e : this.g.getE(n.getKey())) //return a pointer (shallow copy) for the collection representing all the edges getting out of the given node
                    {
                        Point3D node_dest = this.g.getNode(e.getDest()).getLocation();
                        Point3D destNodeScaledData = ScaleToFrame(this.g.getNode(e.getDest()).getLocation(),minP,maxp);

                        double xSrc = currNodeScaledData.x();
                        double ySrc = currNodeScaledData.y();
                        double xDest = destNodeScaledData.x();
                        double yDest = destNodeScaledData.y();

                        g.setColor(Color.DARK_GRAY);
                        g.drawLine((int) xSrc, (int) ySrc, (int) xDest, (int) yDest);
                        g.setColor(Color.BLACK);
                        g.setColor(Color.YELLOW);
                        double xPoint = 0;
                        double yPoint = 0;
                        if (xSrc < xDest && ySrc < yDest)
                        {
                            xPoint = xSrc + (Math.abs(xSrc-xDest)*0.8);
                            yPoint = ySrc + (Math.abs(ySrc-yDest)*0.8);
                        }
                        else if (ySrc >= yDest && xSrc >= xDest)
                        {
                            xPoint = xSrc - (Math.abs(xSrc-xDest)*0.8);
                            yPoint = ySrc - (Math.abs(ySrc-yDest)*0.8);
                        }
                        else if (ySrc >= yDest && xSrc <= xDest)
                        {
                            xPoint = xSrc + (Math.abs(xSrc-xDest)*0.8);
                            yPoint = ySrc - (Math.abs(ySrc-yDest)*0.85);
                        }
                        else if (ySrc < yDest && xSrc > xDest)
                        {
                            xPoint = xSrc - (Math.abs(xSrc-xDest)*0.88);
                            yPoint = ySrc + (Math.abs(ySrc-yDest)*0.85);
                        }

                        g.fillOval((int) xPoint, (int) yPoint, 15, 15);
                        g.setFont(new Font("deafult", Font.BOLD, 15));
                        g.setColor(Color.BLACK);
                        g.drawString("" + Math.round(e.getWeight()*100.0)/100.0, (int) xPoint -20, (int) yPoint);
                    }
                }
            }
            g.setColor(Color.CYAN);
            for (Fruit f: this.fruits)
            {
                Point3D fruitScaledData = ScaleToFrame(f.location,minP,maxp);

                g.fillRect(fruitScaledData.ix()-5, fruitScaledData.iy()-5, 15, 15);
            }
        }

       /* private void drawFruits (int level)
        {
            List<String> fruitsString = Game_Server.getServer(level).getFruits();
            for(String s : fruitsString)
            {
                Fruit f = new Fruit(s);
                ImageIcon image = new ImageIcon(f.getImg());



            }
        }*/

        private Point3D minPoint()
        {
            if (this.g.getV().size() == 0)
            {
                return null;
            }

            double  min_x = Double.MAX_VALUE;
            double  min_y = Double.MAX_VALUE;

            for (node_data n : this.g.getV())
            {
                if (n.getLocation().x() < min_x)
                {
                    min_x = n.getLocation().x();
                }
                if(n.getLocation().y() < min_y)
                {
                    min_y = n.getLocation().y();
                }
            }
            return new Point3D(min_x,min_y);
        }

        private Point3D maxPoint()
        {
            if (this.g.getV().size() == 0)
            {
                return null;
            }
            double  max_x = Double.MIN_VALUE;
            double  max_y = Double.MIN_VALUE;

            for (node_data n : this.g.getV())
            {
                if (n.getLocation().x() > max_x)
                {
                    max_x = n.getLocation().x();
                }
                if(n.getLocation().y() > max_y)
                {
                    max_y = n.getLocation().y();
                }
            }
            return new Point3D(max_x,max_y);
        }

        private Point3D ScaleToFrame(Point3D location,Point3D minPoint,Point3D maxPoint)
        {
            double r_min_x = minPoint.x();
            double r_max_x = maxPoint.x();
            double r_min_y = minPoint.y();
            double r_max_y = maxPoint.y();

            double t_min_x = 200;
            double t_max_x = this.getWidth()-200;
            double t_min_y = 200;
            double t_max_y = this.getHeight()-100;

            double x = location.x();
            double y = location.y() ;

            double res_x = ((x-r_min_x)/(r_max_x-r_min_x)) * (t_max_x - t_min_x) +t_min_x;
            double res_y = ((y-r_min_y)/(r_max_y-r_min_y)) * (t_max_y - t_min_y) +t_min_y;

            return new Point3D(res_x,res_y);
        }




        public void Load()
        {
            JFileChooser chooser = new JFileChooser();
            Graph_Algo g = new Graph_Algo();
            int returnFile = chooser.showOpenDialog(this);
            if(returnFile == JFileChooser.APPROVE_OPTION)
            {
                try
                {
                    File SelectedFile = chooser.getSelectedFile();
                    g.init(SelectedFile.getAbsolutePath());
                    this.g = g.copy();
                    repaint();
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        public void save()
        {
            JFileChooser fileChooser = new JFileChooser();
            graph_algorithms ga = new Graph_Algo();
            ga.init(this.g);
            JFileChooser Choose = new JFileChooser();
            Choose.setDialogTitle("Save a file");
            int userSelection = Choose.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION)
            {
                File Save = Choose.getSelectedFile();
                String FileName= Save.getAbsolutePath();
                ga.save(FileName);
                System.out.println("Save as file: " + Save.getAbsolutePath());
            }

        }

        public void addRobot(double x, double y)
        {
            Robot rob = new Robot();
        }

      private void Start_new_game()
      {
      }


      private void Start_automatic_game()
      {
      }



    public void mouseClicked(MouseEvent e)
    {

    }


    public void mousePressed(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
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

    public static void main(String[] args) {
        game_service game = Game_Server.getServer(20);
        List<String> r= Game_Server.getServer(20).getRobots();
        System.out.println(game);
        System.out.println(r);
        MyGameGUI gg = new MyGameGUI(20);
    }
}
