package Tests;

import dataStructure.NodeData;
import org.junit.jupiter.api.Test;
import utils.Point3D;

import static org.junit.jupiter.api.Assertions.*;

class NodeDataTest {
    Point3D p1 = new Point3D(1,2,3);
    Point3D p2 = new Point3D(4,5,6);
    Point3D p3 = new Point3D(110,220,330);
    NodeData n1 = new NodeData(15,p1,5,"first");
    NodeData n2 = new NodeData(20,p2,10,"second");
    NodeData n3 = new NodeData(25,p3,15,"third");
    @Test
    void getKey() {
        assertEquals(15,n1.getKey());
        assertEquals(20,n2.getKey());
        assertEquals(25,n3.getKey());
    }

    @Test
    void getLocation() {
        assertEquals(p1,n1.getLocation());
        assertEquals(p2,n2.getLocation());
        assertEquals(p3,n3.getLocation());
    }

    @Test
    void setLocation() {
        Point3D pNew = new Point3D(11,22,33);
        NodeData n = new NodeData(15,new Point3D(1,2,3),5,"first");
        n.setLocation(pNew);
        assertEquals(pNew,n.getLocation());
    }

    @Test
    void getWeight() {
        assertEquals(5,n1.getWeight());
        assertEquals(10,n2.getWeight());
        assertEquals(15,n3.getWeight());
    }

    @Test
    void setWeight() {
        NodeData n = new NodeData(15,new Point3D(1,2,3),5,"first");
        n.setWeight(100);
        assertEquals(100,n.getWeight());
    }

    @Test
    void getInfo() {
        assertEquals("first",n1.getInfo());
        assertEquals("second",n2.getInfo());
        assertEquals("third",n3.getInfo());
    }

    @Test
    void setInfo() {
        NodeData n = new NodeData(15,new Point3D(1,2,3),5,"first");
        n.setInfo("second");
        assertEquals("second",n.getInfo());
    }

    @Test
    void getTag() {
        assertEquals(0,n1.getTag());
        assertEquals(0,n2.getTag());
        assertEquals(0,n3.getTag());

    }

    @Test
    void setTag() {
        NodeData n = new NodeData(15,new Point3D(1,2,3),5,"tag test");
        n.setTag(1);
        assertEquals(1,n.getTag());
    }

}