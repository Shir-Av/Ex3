package dataStructure;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class DGraph implements graph, Serializable {
	public HashMap<Integer,node_data> nodes;
	public HashMap<Integer,HashMap<Integer,edge_data>> graph;
	private static int MC = 0;
	private static int NODE_SIZE = 0;
	private static int EDGE_SIZE = 0;

	public DGraph()
	{
		this.nodes = new HashMap<Integer,node_data>();
		this.graph = new HashMap<Integer,HashMap<Integer,edge_data>>();
		this.NODE_SIZE = 0;
		this.MC=0;
		this.EDGE_SIZE=0;

	}

	@Override
	public node_data getNode(int key)
	{
		return nodes.get(key);
	}

	@Override
	public edge_data getEdge(int src, int dest)
	{
		if (src == dest)
		{
			return null;
		}
		else if (nodes.get(src) == null || nodes.get(dest) == null)
		{
			return null;
		}
		else return graph.get(src).get(dest);
	}

	@Override
	public void addNode(node_data n)
	{
		if (!this.nodes.containsKey(n.getKey())) {
			nodes.put(n.getKey(), n);
			NODE_SIZE++;
			MC++;
		}
		else System.out.println("ERROR: this key node already exist");
	}

	@Override
	public void connect(int src, int dest, double w)
	{
		if (!nodes.containsKey(src) || !nodes.containsKey(dest))
		{
			System.out.println("ERROR: src/dest node does not exist");
			return;
		}
		EdgeData e = new EdgeData((NodeData) nodes.get(src), (NodeData) nodes.get(dest), w);
		HashMap<Integer,edge_data> temp = this.graph.get(src);
		if(temp == null)
		{
			temp = new HashMap<Integer, edge_data>();
			temp.put(dest, e);
			graph.put(src, temp);
		}
		else
		{
			temp.put(dest, e);
			graph.put(src, temp);
		}
		EDGE_SIZE++;
		MC++;

	}

	@Override
	public Collection<node_data> getV()
	{
		return this.nodes.values();
	}

	@Override
	public Collection<edge_data> getE(int node_id)
	{
		HashMap<Integer, edge_data> srcEdges = this.graph.get(node_id);
		if(nodes.get(node_id) == null)
		{
			System.out.println("ERROR: this node does not exist");
			return null;
		}
		else if (srcEdges == null)
		{
			System.out.println("ERROR: there are no edges getting out of this given node");
			return null;
		}
		else return graph.get(node_id).values();
	}

	@Override
	public node_data removeNode(int key)
	{
		if (!nodes.containsKey(key))
		{
			System.out.println("ERROR: the given node does not exist");
			return null;
		}
		else
		{
			if(!this.graph.get(key).values().isEmpty())
			{
				EDGE_SIZE -= this.graph.get(key).values().size();
				graph.remove(key);

			}
			for (HashMap<Integer,edge_data> temp: this.graph.values())
			{
				if(temp.containsKey(key))
				{
					temp.remove(key);
					EDGE_SIZE--;

				}
			}
		}
		MC++;
		NODE_SIZE--;
		return nodes.remove(key);
	}

	@Override
	public edge_data removeEdge(int src, int dest)
	{
		if(this.graph.get(src).get(dest) == null)
		{
			System.out.println("ERROR: the given edge does not exist");
			return null;
		}
		else
		{
			edge_data temp = this.graph.get(src).remove(dest);
			EDGE_SIZE--;
			MC++;
			return temp;
		}

	}

	@Override
	public int nodeSize()
	{
		return NODE_SIZE;
	}

	@Override
	public int edgeSize()
	{
		return EDGE_SIZE;
	}

	@Override
	public int getMC()
	{
		return MC;
	}


}
