package Tests;

import com.sun.prism.paint.Color;
import dataStructure.EdgeData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EdgeDataTest {

    EdgeData e1 = new EdgeData(1,2,10);
    EdgeData e2 = new EdgeData(3,4,20);
    @Test
    void getSrc() {
        assertEquals(1,e1.getSrc());
        assertEquals(3,e2.getSrc());
    }

    @Test
    void getDest() {
        assertEquals(2,e1.getDest());
        assertEquals(4,e2.getDest());
    }

    @Test
    void getWeight() {
        assertEquals(10,e1.getWeight());
        assertEquals(20,e2.getWeight());
    }

    @Test
    void getInfo() {
        assertEquals("",e1.getInfo());
        assertEquals("",e2.getInfo());
    }

    @Test
    void setInfo() {
        EdgeData e = new EdgeData(1,2,10);
        e.setInfo("edge for test");
        assertEquals("edge for test",e.getInfo());
    }

    @Test
    void getTag() {
        assertEquals(0,e1.getTag());
        assertEquals(0,e2.getTag());
    }

    @Test
    void setTag() {
        EdgeData e3 = new EdgeData(1,2,10);
        EdgeData e4 = new EdgeData(1,2,10);
        e3.setTag(1);
        e4.setTag(1);
        assertEquals(1,e3.getTag());
        assertEquals(1,e4.getTag());


    }
}