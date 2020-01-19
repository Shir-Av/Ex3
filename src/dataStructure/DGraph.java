package dataStructure;

import oop_elements.OOP_NodeData;
import oop_utils.OOP_Point3D;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Point3D;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

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

	/**
	 * creates a dgraph from a given string
	 * @param sGraph
	 */
	public DGraph(String sGraph) {
		try {
			this.nodes = new HashMap<Integer,node_data>();
			this.graph = new HashMap<Integer,HashMap<Integer,edge_data>>();
			NODE_SIZE = 0;
			/*Scanner scanner = new Scanner(new File(file_name));
			String jsonString = scanner.useDelimiter("\\A").next();
			scanner.close();*/
			JSONObject graph = new JSONObject(sGraph);
			JSONArray nodes = graph.getJSONArray("Nodes");
			JSONArray edges = graph.getJSONArray("Edges");

			int i;
			int src;
			for(i = 0; i < nodes.length(); ++i) {
				src = nodes.getJSONObject(i).getInt("id");
				String pos = nodes.getJSONObject(i).getString("pos");
				Point3D p = new Point3D(pos);
				this.addNode(new NodeData(src, p));
			}

			for(i = 0; i < edges.length(); ++i) {
				src = edges.getJSONObject(i).getInt("src");
				int dest = edges.getJSONObject(i).getInt("dest");
				double w = edges.getJSONObject(i).getDouble("w");
				this.connect(src, dest, w);
			}
		} catch (Exception var12) {
			var12.printStackTrace();
		}

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

	/**
	 * adds a new node to the graph
	 * @param n
	 */
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

	/**
	 * Connect an edge with weight w between node src to node dest.
	 * @param src - the source of the edge.
	 * @param dest - the destination of the edge.
	 * @param w - positive weight representing the cost (aka time, price, etc) between src-->dest.
	 */
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

	/**
	 *
	 * @return a collection of all the nodes
	 */
	@Override
	public Collection<node_data> getV()
	{
		return this.nodes.values();
	}

	/**
	 *
	 * @param node_id
	 * @return a collection of the edgws in the graph
	 */
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

	/**
	 * removes a given node cy the key id
	 * @param key
	 * @return
	 */
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

	/**
	 * removes a given edge  by a given edge src and edge dest
	 * @param src
	 * @param dest
	 * @return
	 */
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