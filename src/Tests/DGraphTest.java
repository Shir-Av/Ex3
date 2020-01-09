package Tests;

import algorithms.Graph_Algo;
import algorithms.graph_algorithms;
import dataStructure.DGraph;
import dataStructure.NodeData;
import dataStructure.graph;
import dataStructure.node_data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.annotations.BeforeTest;
import utils.Point3D;

import static org.junit.jupiter.api.Assertions.*;

class DGraphTest {
    DGraph g;
    NodeData n0 =new NodeData(0, new Point3D(-10, 4));
    NodeData n1 =new NodeData(1, new Point3D(-5, 10));
    NodeData n2 =new NodeData(2, new Point3D(10, 10));
    NodeData n3 =new NodeData(3, new Point3D(9, -1));
    NodeData n4 =new NodeData(4, new Point3D(-4, 0));
    @BeforeEach
    public void createNewgraph() {
        g = new DGraph();
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
    }
    @Test
    void getNode() {
        assertEquals(1,g.getNode(1).getKey());
        assertEquals(2,g.getNode(2).getKey());
        assertEquals(3,g.getNode(3).getKey());
    }

    @Test
    void getEdge() {
        assertEquals("Src: 1  --->  dest: 4",g.getEdge(1,4).toString());
        assertEquals("Src: 2  --->  dest: 3",g.getEdge(2,3).toString());
        assertEquals("Src: 4  --->  dest: 2",g.getEdge(4,2).toString());
    }

    @Test
    void addNode() {
        int numOfNodes = g.nodeSize();
        assertEquals(numOfNodes,g.nodeSize());
        node_data n =  new NodeData(10);
        g.addNode(n);
        assertEquals(numOfNodes+1,g.nodeSize());
        assertEquals("[Id: 0, Id: 1, Id: 2, Id: 3, Id: 4, Id: 10]",g.getV().toString());
    }

    @Test
    void connect() {
        int numOfEdges = g.edgeSize();
        assertEquals(numOfEdges,g.edgeSize());
        assertEquals("[Src: 4  --->  dest: 1, Src: 4  --->  dest: 2, Src: 4  --->  dest: 3]",g.getE(4).toString());
        g.connect(4,0,1);
        assertEquals(numOfEdges+1,g.edgeSize());
        assertEquals("[Src: 4  --->  dest: 0, Src: 4  --->  dest: 1, Src: 4  --->  dest: 2, Src: 4  --->  dest: 3]",g.getE(4).toString());
    }

    @Test
    void getV() {
        assertEquals("[Id: 0, Id: 1, Id: 2, Id: 3, Id: 4]",g.getV().toString());
    }

    @Test
    void getE() {
        assertEquals("[Src: 1  --->  dest: 2, Src: 1  --->  dest: 4]",g.getE(1).toString());
    }

    @Test
    void removeNode() {
        int numOfNodes = g.nodeSize();
        assertEquals(numOfNodes,g.nodeSize());
        assertEquals("[Id: 0, Id: 1, Id: 2, Id: 3, Id: 4]",g.getV().toString());
        assertEquals("[Src: 1  --->  dest: 2, Src: 1  --->  dest: 4]",g.getE(1).toString());
        g.removeNode(2);
        assertEquals(numOfNodes-1,g.nodeSize());
        assertEquals("[Id: 0, Id: 1, Id: 3, Id: 4]",g.getV().toString());
        assertEquals("[Src: 1  --->  dest: 4]",g.getE(1).toString());
    }

    @Test
    void removeEdge() {
        int numOfEdges = g.edgeSize();
        assertEquals(numOfEdges,g.edgeSize());
        assertEquals("[Src: 4  --->  dest: 1, Src: 4  --->  dest: 2, Src: 4  --->  dest: 3]",g.getE(4).toString());
        g.removeEdge(4,2);
        assertEquals(numOfEdges-1,g.edgeSize());
        assertEquals("[Src: 4  --->  dest: 1, Src: 4  --->  dest: 3]",g.getE(4).toString());
    }

    @Test
    void nodeSize() {
        assertEquals(5,g.nodeSize());

    }

    @Test
    void edgeSize() {
        assertEquals(10,g.edgeSize());
    }

    @Test
    void getMC() {
        assertEquals(15,g.getMC());
    }
}