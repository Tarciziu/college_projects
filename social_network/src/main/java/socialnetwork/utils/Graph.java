package socialnetwork.utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private final Map<Long, List<Long>> adjVertices;

    // constructor
    public Graph() {
        adjVertices = new HashMap<>();
    }
    public void addVertex(Long vertex){
        adjVertices.putIfAbsent(vertex,new ArrayList<>());
    }
    // Adds an edge to an undirected graph
    public void addEdge(Long src, Long dest) {
        // Add an edge from src to dest.
        adjVertices.get(src).add(dest);

        // Since graph is undirected, add an edge from dest
        // to src also
        adjVertices.get(dest).add(src);
    }

    private void DFSUtil(Long v, Map<Long,Boolean> visited) {
        // Mark the current node as visited and print it
        visited.replace(v,Boolean.TRUE);
        // Recur for all the vertices
        // adjacent to this vertex
        for (Long x : adjVertices.get(v)) {
            if (!visited.get(x)) DFSUtil(x, visited);
        }

    }

    public int connectedComponents() {
        // Mark all the vertices as not visited
        //boolean[] visited = new boolean[adjVertices.size()];
        Map<Long,Boolean> visited = new HashMap<>();
        for(Long x:adjVertices.keySet())
            visited.putIfAbsent(x,Boolean.FALSE);
        int comps=0;
        for (Long v:adjVertices.keySet()) {
            if (!visited.get(v)) {
                // print all reachable vertices
                // from v
                DFSUtil(v, visited);
                comps++;
            }
        }
        return comps;
    }

    private void DFS(Long src,int prev_len,int[] max_len,
                     Map<Long,Boolean> visited,List<Long> community,List<Long> comFinal){
        visited.replace(src,Boolean.TRUE);
        community.add(src);
        int current_len = 0;
        for(Long i:adjVertices.get(src)){
            if(!visited.get(i)){
                current_len=prev_len+1;
                Map<Long,Boolean> visitedCopy = new HashMap<>(visited);
                DFS(i,current_len,max_len,visitedCopy,community,comFinal);
            }
            if(max_len[0]<=current_len)
            if(community.size()>comFinal.size()){
                max_len[0] = current_len;
                comFinal.clear();
                comFinal.addAll(community);
            }
            if(max_len[0]<current_len){
                max_len[0] = current_len;
                comFinal.clear();
                comFinal.addAll(community);
            }


            current_len=0;
        }
    }

    public List<Long> longestPath(){
        int[] max_len= new int[1];
        max_len[0]=-1;
        List<Long> community = new ArrayList<>();
        List<Long> comFinal = new ArrayList<>();
        for(Long y:adjVertices.keySet()){
            community.clear();
            Map<Long,Boolean> visited = new HashMap<>();
            for(Long x:adjVertices.keySet())
                visited.putIfAbsent(x,Boolean.FALSE);
            DFS(y,0,max_len,visited,community,comFinal);
        }
        System.out.println(max_len[0]);
        return comFinal;
    }
}
