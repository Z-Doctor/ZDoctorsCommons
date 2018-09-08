package zdoctor.commons.utils.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class NodeDictionary<T> {
	HashMap<T, byte[]> database = new HashMap<>();

	protected final Node<T> nodeTree = new Node<>();

	public void register(String key, T value) {
		register(key.getBytes(), value);
	}

	public void register(byte[] keys, T value) {
		if (keys.length <= 0)
			return;

		int index = 0;
		NodeIterator<T> nodeIterator = iterator();
		Node<T> currentNode = nodeTree;

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
			Node<T> node = new Node<T>(keys[index]);
			currentNode.addChild(node);
			currentNode = node;
			index++;
		}

		currentNode.setValue(value);
		currentNode.setFullKey(keys);
		database.put(value, keys);
	}

	public boolean hasKey(String key) {
		return hasKey(key.getBytes());
	}

	public boolean hasKey(byte[] keys) {
		if (keys.length <= 0)
			return false;

		int index = 0;
		NodeIterator<T> nodeIterator = iterator();
		Node<T> currentNode = nodeTree;
		if (nodeIterator.nodesAvaiable()) {
			while (index < keys.length && nodeIterator.hasNode(keys[index])) {
				currentNode = nodeIterator.nextNode(keys[index++]);
			}
		} else
			return false;
		return currentNode.getValue() != null;
	}

	public boolean hasValue(T value) {
		return database.containsKey(value);
	}

	public byte[] getKey(T value) {
		return database.get(value);
	}

	public T lookUp(String key) {
		return lookUp(key.getBytes());
	}

	public T lookUp(byte[] keys) {
		if (hasKey(keys))
			return lookUpNode(keys).getValue();
		return null;
	}

	public Object[] findMatches(byte[] fragment) {
		ArrayList<T> matches = new ArrayList<>();

		database.values().forEach(entry -> {
			for (int i = 0; i < fragment.length; i++) {
				if (entry.length <= i)
					return;
				if (entry[i] != fragment[i])
					return;
			}
			matches.add(lookUp(entry));
		});

		if (matches.size() <= 0) {
			database.keySet().forEach(value -> {
				byte[] compare = value.toString().getBytes();
				for (int i = 0; i < fragment.length; i++) {
					if (compare.length <= i)
						return;
					if (compare[i] != fragment[i])
						return;
				}
				matches.add(value);
			});
		}

		return matches.toArray();
	}

	public Node<T> lookUpNode(String key) {
		return lookUpNode(key.getBytes());
	}

	public Node<T> lookUpNode(byte[] keys) {
		if (keys.length <= 0)
			return null;

		int index = 0;
		NodeIterator<T> nodeIterator = iterator();
		Node<T> currentNode = null;
		if (nodeIterator.nodesAvaiable()) {
			while (index < keys.length && nodeIterator.hasNode(keys[index])) {
				currentNode = nodeIterator.nextNode(keys[index++]);
			}
		}

		return currentNode;
	}

	public Node<T> getNodeTree() {
		return nodeTree;
	}

	public NodeIterator<T> iterator() {
		return new NodeIterator<>(nodeTree);
	}

	public int count() {
		return database.size();
	}

	public static class Node<T> {

		protected Node<T> parent;
		protected NodeTree<T> children = new NodeTree<>();

		protected byte key;

		protected byte[] fullKey;
		protected T value;

		public Node() {
		}

		public Node(byte key) {
			this.key = key;
		}

		public void setValue(T value) {
			this.value = value;
		}

		public void setFullKey(String fullKey) {
			this.fullKey = fullKey.getBytes();
		}

		public void setFullKey(byte[] fullKey) {
			this.fullKey = fullKey;
		}

		public T getValue() {
			return value;
		}

		public Node<T> getChild(byte b) {
			return getChild(new Node<T>(b));
		}

		public Node<T> getChild(Node<T> node) {
			return children.get(children.indexOf(node));
		}

		public boolean hasChild(byte b) {
			return hasChild(new Node<T>(b));
		}

		public boolean hasChild(Node<T> node) {
			return children.contains(node);
		}

		public boolean addChild(Node<T> node) {
			node.parent = this;

			if (children == null)
				children = new NodeTree<>();

			return children.add(node);
		}

		public NodeIterator<T> iterator() {
			return new NodeIterator<>(this);
		}

		public void forEach(Consumer<Node<T>> action) {
			children.forEach(action);
		}

		@Override
		@SuppressWarnings("rawtypes")
		public boolean equals(Object obj) {
			if (obj instanceof Node) {
				return ((Node) obj).key == this.key;
			}
			return super.equals(obj);
		}

		@Override
		public String toString() {
			if (Character.isAlphabetic(key) || Character.isDigit(key))
				return "Node: '" + (char) key + "'";
			return "Node: (" + key + ")";
		}

		public byte[] getKey() {
			return fullKey;
		}
	}

	public static class NodeTree<T> extends ArrayList<Node<T>> {
		private static final long serialVersionUID = 1817358323521937616L;

		public boolean contains(byte b) {
			return super.contains(new Node<T>(b));
		}
	}

	public static class NodeIterator<T> {

		Node<T> currentNode;

		public NodeIterator(Node<T> startNode) {
			currentNode = startNode;
		}

		public boolean nodesAvaiable() {
			return currentNode.children != null && currentNode.children.size() > 0;
		}

		public boolean hasNode(byte b) {
			return nodesAvaiable() && currentNode.hasChild(b);
		}

		public boolean hasNode(Node<T> node) {
			return nodesAvaiable() && currentNode.hasChild(node);
		}

		public Node<T> nextNode(byte b) {
			currentNode = currentNode.getChild(b);
			return currentNode;
		}

		public Node<T> nextNode(Node<T> node) {
			currentNode = currentNode.getChild(node);
			return currentNode;
		}

		public Node<T> previousNode() {
			if (currentNode.parent != null)
				currentNode = currentNode.parent;
			return currentNode;
		}

		public Node<T> getCurrentNode() {
			return currentNode;
		}

		public NodeTree<T> getChildren() {
			return currentNode.children;
		}

	}

}
