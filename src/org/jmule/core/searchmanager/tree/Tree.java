/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2008 JMule team ( jmule@jmule.org / http://jmule.org )
 *
 *  Any parts of this program derived from other projects, or contributed
 *  by third-party developers are copyrighted by their respective authors.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package org.jmule.core.searchmanager.tree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created on Oct 26, 2008
 * @author binary256
 * @version $Revision: 1.1 $
 * Last changed by $Author: binary256_ $ on $Date: 2008/10/28 21:06:40 $
 */
public class Tree {

	public static final String DATA_KEY = "Data1";

	private Node root = null;
	private Node last_node = null;
	
	private Map<NodeValue, Node> nodes = new HashMap<NodeValue,Node>();
	
	public Tree(String searchString) {
		NodeValue value = NodeValue.FILE_NAME;
		value.setValue(DATA_KEY, searchString);
		root = new Node(value);
		last_node = root;
		nodes.put(root.getKey(), root);
	}
	/**
	 * Add node to tree only if node not exist or update node key if node exist
	 * @param value
	 */
	public void addNodeIfNeed(NodeValue value) {
		Node n = getNode(value);
		if (n != null)
			n.setKey(value);
		else 
			add(value);
	}
	
	private void add(NodeValue value) {
		Node r_node = new Node(last_node.getKey());
		
		nodes.remove(r_node.getKey());
		nodes.put(r_node.getKey(), r_node);
		
		last_node.setLeftChild(r_node);
		last_node.setKey(NodeValue.AND);
		Node new_node = new Node(value);
		
		nodes.put(new_node.getKey(), new_node);
		
		last_node.setRightChild(new_node);
		last_node = new_node;
	}

	public Node getNode(NodeValue value) {
		return nodes.get(value);
	}
	
	public List<NodeValue> traverse() {
		List<NodeValue> list = new LinkedList<NodeValue>();
		List<NodeValue> result = traverse(root);
		if (result == null) return null;
		list.addAll(result);
		return list;
	}
	
	private List<NodeValue> traverse(Node node) {
		if (node == null) return null;
		List<NodeValue> list = new LinkedList<NodeValue>();
		List<NodeValue> result;
		
		list.add(node.getKey());
		result = traverse(node.getLeftChild());
		if (result != null) { list.addAll(result); result.clear(); }
		
		result = traverse(node.getRightChild());
		if (result != null) list.addAll(result);
		
		return list;
	}
}
