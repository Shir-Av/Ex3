package algorithms;

import java.io.*;
import java.util.*;

import dataStructure.*;

/**
 * This empty class represents the set of graph-theory algorithms
 * which should be implemented as part of Ex2 - Do edit this class.
 * @author
 *
 */
public class Graph_Algo implements graph_algorithms, Serializable{
	public graph graph_algo;

	public Graph_Algo()
	{

		this.graph_algo = new DGraph();
	}

	public Graph_Algo(graph graph_algo)
	{
		this.graph_algo = graph_algo;
	}

	@Override
	public void init(graph g)
	{
		this.graph_algo = g;

	}

	@Override
	public void init(String file_name)
	{
		try
		{
			FileInputStream file = new FileInputStream(file_name);
			ObjectInputStream in = new ObjectInputStream(file);

			this.graph_algo = (graph) in.readObject();
			in.close();
			file.close();
			System.out.println("Object has been deserialized");



		} catch (IOException ex)
		{
			System.out.println("IOException is caught");
		} catch (ClassNotFoundException ex)
		{
			System.out.println("ClassNotFoundException is caught");
		}

	}

	@Override
	public void save(String file_name)
	{
		try
		{
			FileOutputStream file = new FileOutputStream(file_name);
			ObjectOutputStream out = new ObjectOutputStream(file);

			out.writeObject(graph_algo);

			out.close();
			file.close();

			System.out.println("Object has been serialized");
		} catch (IOException ex)
		{
			System.out.println("IOException is caught");
			ex.printStackTrace();

		}

	}

	@Override
	public boolean isConnected()
	{
		if(this.graph_algo.nodeSize() == 1)
		{
			return true;
		}
		if (this.hasEdges())
		{
			for (node_data n : this.graph_algo.getV())
			{
				this.initTag();
				int src = n.getKey();
				ArrayList<Integer> newCol = new ArrayList<Integer>();
				if (!hasAllPaths(src, newCol))
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private void initTag()
	{
		for (node_data n : this.graph_algo.getV())
		{
			n.setTag(0);
			n.setWeight(Integer.MAX_VALUE);
			n.setInfo("");
		}
	}

	private boolean hasEdges()
	{
		for (node_data n : this.graph_algo.getV())
		{
			int i = n.getKey();
			Collection<edge_data> temp = this.graph_algo.getE(i);
			if (temp == null)
			{
				return false;
			}
		}
		return true;
	}

	private boolean hasAllPaths(int src, ArrayList<Integer> newCol)
	{
		Collection<edge_data> temp = this.graph_algo.getE(src);
		this.graph_algo.getNode(src).setTag(1);
		if (!newCol.contains(src))
		{
			newCol.add(src);
		}

		if (newCol.size() == this.graph_algo.nodeSize())
		{
			return true;
		}
		for (edge_data e : temp)
		{
			node_data d = this.graph_algo.getNode(e.getDest());
			if (!newCol.contains(d.getKey()))
			{
				newCol.add(d.getKey());
			}
		}
		for (int i=0 ; i<newCol.size() ; i++)
		{
			node_data d = this.graph_algo.getNode(newCol.get(i));
			if (d.getTag() == 0)
			{
				return hasAllPaths(d.getKey(), newCol);
			}
		}
		return false;
	}

	@Override
	public double shortestPathDist(int src, int dest)
	{
		this.initTag(); //set all tags to 0
		double weight;
		node_data srcNode = this.graph_algo.getNode(src);
		node_data destNode = this.graph_algo.getNode(dest);
		srcNode.setWeight(0);

		while (srcNode.getKey() != destNode.getKey() && srcNode.getTag() == 0)
		{
			Collection <edge_data> edges = this.graph_algo.getE(srcNode.getKey());
			if(!edges.isEmpty())
			{
				for (edge_data e: edges)
				{
					node_data tmpDest = this.graph_algo.getNode(e.getDest());
					weight = srcNode.getWeight() + e.getWeight();
					if(weight < tmpDest.getWeight() && tmpDest.getTag() == 0)
					{
						tmpDest.setInfo("" + srcNode.getKey());
						tmpDest.setWeight(weight);
					}
				}
			}
			srcNode.setTag(1);
			srcNode = minWeight();

		}
		return srcNode.getWeight();
	}

	private node_data minWeight () // returns the node with the min weight and tag 0 in the collection
	{
		Collection <node_data> node = this.graph_algo.getV();
		node_data tmpN = new NodeData(this.graph_algo.getV().size());
		for(node_data n: node)
		{
			if (n.getTag() == 0 && n.getWeight() < tmpN.getWeight())
			{
				tmpN = n;

			}
		}
		return tmpN;
	}

	@Override
	public List<node_data> shortestPath(int src, int dest)
	{
		shortestPathDist(src, dest);
		List<node_data> path = new LinkedList<node_data>();

		node_data nodeDest = this.graph_algo.getNode(dest);
		while (nodeDest.getKey() != src)
		{
			path.add(nodeDest);
			nodeDest = this.graph_algo.getNode(Integer.parseInt(nodeDest.getInfo()));
		}
		path.add(this.graph_algo.getNode(src));
		Collections.reverse(path);
		return path;
	}

	@Override
	public List<node_data> TSP(List<Integer> targets)
	{
		List  <node_data> TSPlist = new LinkedList<node_data>();
		int src = 0;
		int dest = 0;
		if (targets.size() == 0)
		{
			System.out.println("EROR: the targets list is empty");
			return null;
		}
		else if(targets.size() == 1)
		{
			src = targets.get(0);
			TSPlist.add(this.graph_algo.getNode(src));
			return TSPlist;
		}
		else
		{
			for (int i = 0; i < targets.size()-1; i++)
			{
				src = targets.get(i);
				dest = targets.get(i+1);
				node_data srcNode = this.graph_algo.getNode(src);
				node_data destNode = this.graph_algo.getNode(dest);
				if (this.graph_algo.getV().contains(srcNode) && this.graph_algo.getV().contains(destNode))
				{
					List <node_data> temp = new LinkedList<node_data>();
					temp = shortestPath(src, dest);
					for (int j = 0; j < temp.size()-1 ; j++)
					{
						//node_data node = temp.get(i);
						TSPlist.add(temp.get(j));
					}
				}
				else
				{
					System.out.println("EROR: some of the nodes does nor exist");
				}
			}
			TSPlist.add(this.graph_algo.getNode(targets.get(targets.size()-1)));
		}
		return TSPlist;
	}

	@Override
	public graph copy()
	{
		graph temp = new DGraph();
		Collection<node_data> Nodes = graph_algo.getV();

		for (node_data Node : Nodes)
		{
			temp.addNode(Node);
		}
		for (node_data Node : Nodes)
		{
			Collection<edge_data> Edges = graph_algo.getE(Node.getKey());

			for (edge_data Edge : Edges)
			{
				temp.connect(Edge.getSrc(), Edge.getDest(), Edge.getWeight());
			}
		}
		return temp;
	}

}