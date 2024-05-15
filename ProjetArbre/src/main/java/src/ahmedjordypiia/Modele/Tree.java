package src.ahmedjordypiia.Modele;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tree {
    private Map<Integer, Node> nodes = new HashMap<>();

    public void readNodesCSV(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                Node node = new Node(fields);
                nodes.put(node.getId(), node);
            }
        }
    }

    public void readLinksCSV(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                int sourceNodeId = Integer.parseInt(fields[0]);
                int targetNodeId = Integer.parseInt(fields[1]);
                Node sourceNode = nodes.get(sourceNodeId);
                Node targetNode = nodes.get(targetNodeId);
                if (sourceNode != null && targetNode != null) {
                    sourceNode.addChildren(targetNode);
                }
            }
        }
    }

    public ArrayList<Node> getNodes() {
        return new ArrayList<>(nodes.values());
    }

//    public Node searchNode(String name) {
//        for (Node node : nodes) {
//            if (node.getName().equalsIgnoreCase(name)) {
//                return node;
//            }
//        }
//        return null;
//    }

    private static void printTree(Node node, int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append("  "); // Ajoute deux espaces pour chaque niveau
        }
        System.out.println(indent + node.getName());
        for (Node child : node.getChildren()) {
            printTree(child, level + 1);
        }
    }

    public Node searchNode(String name) {
        for (Node node : nodes.values()) {
            if (node.getName().equalsIgnoreCase(name)) {
                return node;
            }
        }
        return null;
    }

    public void printNodeInfo (Node node) {
        System.out.println("Node name: " + node.getName());
        System.out.println("Node ID: " + node.getId());
        System.out.println("Node childNodes: " + node.getChildNodes());
        System.out.println("Node leafNodes: " + node.getLeafNodes());
        System.out.println("Node tolorLink: " + node.getTolorgLink());
        System.out.println("Node extinct: " + node.getExtinct());
        System.out.println("Node confidence: " + node.getConfidence());
        System.out.println("Node phylesis: " + node.getPhylesis());
    }

    //test searchNodes in a main
    public static void main(String[] args) {
        Tree tree = new Tree();
        try {
            tree.readNodesCSV("src/main/resources/src/ahmedjordypiia/treeoflife_nodes.csv");
            tree.readLinksCSV("src/main/resources/src/ahmedjordypiia/treeoflife_links.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Node node = tree.searchNode("viruses");
        if (node != null) {
            tree.printNodeInfo(node);
        } else {
            System.out.println("Node not found");
        }
    }


}