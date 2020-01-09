package Tests;

import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.NodeData;
import dataStructure.graph;
import dataStructure.node_data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Point3D;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Graph_AlgoTest {
     Graph_Algo gAlgo ;
     graph g;
    NodeData n0 =new NodeData(0, new Point3D(-10, 4));
    NodeData n1 =new NodeData(1, new Point3D(-5, 10));
    NodeData n2 =new NodeData(2, new Point3D(10, 10));
    NodeData n3 =new NodeData(3, new Point3D(9, -1));
    NodeData n4 =new NodeData(4, new Point3D(-4, 0));
    @BeforeEach
    void init() {
        g = new DGraph();
        gAlgo = new Graph_Algo();
        g.addNode(n0);
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addNode(n4);
        g.connect(0, 1, 10);
        g.connect(0, 4, 5);
        g.connect(1, 4, 2);
        g.connect(1, 2, 1);
        g.connect(2, 3, 4);
        g.connect(3, 0, 7);
        g.connect(3, 2, 6);
        g.connect(4, 1, 3);
        g.connect(4, 2, 9);
        g.connect(4, 3, 2);
        gAlgo.init(g);
    }

    @Test
    void testInitAndSave() {
        gAlgo.save("TestGraph.txt");
        Graph_Algo initFrom = new Graph_Algo();
        initFrom.init("TestGraph.txt");

        graph copied = initFrom.copy();

        assertEquals(g.getV().toString(),copied.getV().toString());
        assertEquals(g.getE(0).toString(),copied.getE(0).toString());

    }


    @Test
    void isConnected() {
        assertTrue(gAlgo.isConnected());
        g.removeEdge(2,3);
        assertFalse(gAlgo.isConnected());
    }

    @Test
    void shortestPathDist() {
        assertEquals(19,gAlgo.shortestPathDist(2,1));
        assertEquals(15,gAlgo.shortestPathDist(3,1));
        assertEquals(16,gAlgo.shortestPathDist(2,4));
    }

    @Test
    void shortestPath() {
        assertEquals("[Id: 2, Id: 3, Id: 0, Id: 4, Id: 1]",gAlgo.shortestPath(2,1).toString());
        assertEquals("[Id: 3, Id: 0, Id: 4, Id: 1]",gAlgo.shortestPath(3,1).toString());
        assertEquals("[Id: 2, Id: 3, Id: 0, Id: 4]",gAlgo.shortestPath(2,4).toString());
    }

    @Test
    void TSP() {
        List<Integer> t1 = new ArrayList<>();
        t1.add(n1.getKey());
        t1.add(n3.getKey());
        t1.add(n0.getKey());
        t1.add(n4.getKey());
       assertEquals("[Id: 1, Id: 4, Id: 3, Id: 0, Id: 4]",gAlgo.TSP(t1).toString());

        List<Integer> t2 = new ArrayList<>();
        t2.add(n2.getKey());
        t2.add(n0.getKey());
        t2.add(n1.getKey());
        assertEquals("[Id: 2, Id: 3, Id: 0, Id: 4, Id: 1]",gAlgo.TSP(t2).toString());

        List<Integer> t3 = new ArrayList<>();
        t3.add(n0.getKey());
        t3.add(n1.getKey());
        t3.add(n3.getKey());
        assertEquals("[Id: 0, Id: 4, Id: 1, Id: 4, Id: 3]",gAlgo.TSP(t3).toString());
    }

    @Test
    void copy() {
        graph copy = gAlgo.copy();
        assertEquals(g.getV().toString(),copy.getV().toString());
        copy.removeNode(0);
        assertNotEquals(g.getV().toString(),copy.getV().toString());
    }
}