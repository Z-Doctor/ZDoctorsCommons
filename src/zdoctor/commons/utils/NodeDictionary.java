package zdoctor.commons.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public abstract class NodeDictionary<K, V> {
	protected HashMap<K[], V> database = new HashMap<>();

	protected final Node<K, V> nodeTree = new Node<>();

	public boolean register(K[] keys, V value) {
		if (keys == null || keys.length <= 0)
			return false;

		if (hasKey(keys))
			return false;

		int index = 0;
		NodeIterator<K, V> nodeIterator = iterator();
		Node<K, V> currentNode = nodeTree;

		if (nodeIterator.nodesAvaiable()) {
			while (index < keys.length && nodeIterator.hasNode(keys[index])) {
				currentNode = nodeIterator.nextNode(keys[index]);
				index++;
			}
		} else {
			currentNode = new Node<>(keys[index]);
			nodeTree.addChild(currentNode);
			index++;
		}

		while (index < keys.length) {
			Node<K, V> node = new Node<K, V>(keys[index]);
			currentNode.addChild(node);
			currentNode = node;
			index++;
		}

		currentNode.setValue(value);
		currentNode.setKey(keys);

		database.put(keys, value);

		return true;
	}

	public boolean hasKey(K[] keys) {
		if (keys == null || keys.length <= 0)
			return false;

		if (database.size() <= 0)
			return false;

		int index = 0;
		NodeIterator<K, V> nodeIterator = iterator();
		Node<K, V> currentNode = nodeTree;
		while (index < keys.length) {
			if (nodeIterator.nodesAvaiable() && nodeIterator.hasNode(keys[index])) {
				currentNode = nodeIterator.nextNode(keys[index++]);
			} else
				return false;
		}

		return currentNode.getValue() != null;
	}

	public boolean hasValue(V value) {
		return database.values().contains(value);
	}

	public K[] getKeyOfValue(V value) {
		if (database.values().contains(value)) {
			for (K[] keys : database.keySet()) {
				V temp = database.get(keys);
				if (temp == value || value.equals(temp))
					return keys;
			}
		}
		return null;
	}

	public V lookUp(K[] keys) {
		if (hasKey(keys))
			return lookUpNode(keys).getValue();
		return null;
	}

	public Node<K, V> lookUpNode(K[] keys) {
		if (keys.length <= 0)
			return null;

		int index = 0;
		NodeIterator<K, V> nodeIterator = iterator();
		Node<K, V> currentNode = null;
		if (nodeIterator.nodesAvaiable()) {
			while (index < keys.length && nodeIterator.hasNode(keys[index])) {
				currentNode = nodeIterator.nextNode(keys[index++]);
			}
		}

		return currentNode;
	}

	public Node<K, V> getNodeTree() {
		return nodeTree;
	}

	public NodeIterator<K, V> iterator() {
		return new NodeIterator<>(nodeTree);
	}

	public int count() {
		return database.size();
	}

	/**
	 * Given a partial key, will search for keys that are similar
	 * 
	 * @param partialKey
	 * @return similar keys
	 */
	public abstract K[][] searchSimilarKeys(K[] partialKey);

	/**
	 * Given a partial value, will search for all the values that are similar
	 * 
	 * @param partialValue
	 * @return similar values
	 */
	public abstract V[] searchSimilarValues(V partialValue);

	public static class Node<K, V> {

		protected Node<K, V> parent;
		protected NodeTree<K, V> children = new NodeTree<>();

		protected K fragment;

		protected K[] key;
		protected V value;

		public Node() {
			// TODO Auto-generated constructor stub
		}

		public Node(K fragment) {
			this.fragment = fragment;
		}

		public void setKey(K[] key) {
			this.key = key;
		}

		public void setValue(V value) {
			this.value = value;
		}

		public V getValue() {
			return value;
		}

		public Node<K, V> getChild(K b) {
			return getChild(new Node<K, V>(b));
		}

		public Node<K, V> getChild(Node<K, V> node) {
			return children.get(children.indexOf(node));
		}

		public boolean hasChild(K b) {
			return hasChild(new Node<K, V>(b));
		}

		public boolean hasChild(Node<K, V> node) {
			return children.contains(node);
		}

		public boolean addChild(Node<K, V> node) {
			node.parent = this;

			if (children == null)
				children = new NodeTree<>();

			return children.add(node);
		}

		public NodeIterator<K, V> iterator() {
			return new NodeIterator<>(this);
		}

		public void forEach(Consumer<Node<K, V>> action) {
			children.forEach(action);
		}

		@Override
		@SuppressWarnings("rawtypes")
		public boolean equals(Object obj) {
			if (obj instanceof Node) {
				return ((Node) obj).fragment == this.fragment;
			}
			return super.equals(obj);
		}

		@Override
		public String toString() {
			return "Node[Fragment=" + fragment + ", Value=" + value + ", Key=" + key + ")";
		}

		public K[] getKey() {
			return key;
		}
	}

	public static class NodeTree<K, V> extends ArrayList<Node<K, V>> {
		private static final long serialVersionUID = 1817358323521937616L;

//		public boolean contains(K b) {
//			return super.contains(new Node<K, V>(b));
//		}
	}

	public static class NodeIterator<K, V> {

		Node<K, V> currentNode;

		public NodeIterator(Node<K, V> startNode) {
			currentNode = startNode;
		}

		public boolean nodesAvaiable() {
			return currentNode.children != null && currentNode.children.size() > 0;
		}

		public boolean hasNode(K b) {
			return nodesAvaiable() && currentNode.hasChild(b);
		}

		public boolean hasNode(Node<K, V> node) {
			return nodesAvaiable() && currentNode.hasChild(node);
		}

		public Node<K, V> nextNode(K b) {
			currentNode = currentNode.getChild(b);
			return currentNode;
		}

		public Node<K, V> nextNode(Node<K, V> node) {
			currentNode = currentNode.getChild(node);
			return currentNode;
		}

		public Node<K, V> previousNode() {
			if (currentNode.parent != null)
				currentNode = currentNode.parent;
			return currentNode;
		}

		public Node<K, V> getCurrentNode() {
			return currentNode;
		}

		public NodeTree<K, V> getChildren() {
			return currentNode.children;
		}

	}

}
