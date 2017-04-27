import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation: java BaseballElimination.java
 *  Execution:   java BaseballElimination teams4.txt
 *  Dependencies (algs4.jar): In.java, Bag.java, FlowEdge.java, FlowNetwork.java,
 *                            FordFulkerson.java
 *
 *  An immutable data type BaseballElimination that represents a sports division
 *  and determines which teams are mathematically eliminated
 *
 ****************************************************************************/

public class BaseballElimination {
    private int totalTeams;
    private HashMap<String, Integer> team2i;  
    private String[] teams;    
    private int[] wins;    
    private int[] losses;    
    private int[] remaining;    
    private int[] games;
    private int[] offset;
    private boolean[] eliminated;
    private boolean[] eliminatedBy;
   
    /**
     * Initializes a baseball division from given filename in format specified.
     * 
     * @param filename String of filename in format specified
     */
    public BaseballElimination(String filename) {
        In inFile = new In(filename);
        totalTeams = inFile.readInt();
        team2i = new HashMap<String, Integer>();  
        teams = new String[totalTeams];    
        wins = new int[totalTeams];    
        losses = new int[totalTeams];    
        remaining = new int[totalTeams];    
        // half size: number of games between team A and team B is equal to
        //            number of games between team B and team A
        games = new int[totalTeams * (totalTeams - 1) / 2];
        offset = new int[totalTeams];
        eliminated = new boolean[totalTeams];
        eliminatedBy = new boolean[totalTeams * totalTeams];
             
        for (int i = 0; i < totalTeams; i++) {
            teams[i] = inFile.readString();
            team2i.put(teams[i], i);
            wins[i] = inFile.readInt();
            losses[i] = inFile.readInt();
            remaining[i] = inFile.readInt();
            int offset = 0;
            for (int j = 0; j < i; j++) {
                games[i + offset + j - 1] = inFile.readInt();
                offset += totalTeams - 3 - j;
            }
            inFile.readLine();
        }
        offset[0] = -1;
        for (int i = 1; i < totalTeams - 1; i++) {
            offset[i] = offset[i-1] + totalTeams - i - 1;
        }      
        if (totalTeams > 1)    
            checkEliminatedAll();
    }

    /**
     *  Returns the number of teams.
     *  
     *  @return number of teams
     */
    public int numberOfTeams() {
        return this.totalTeams;
    }

    /**
     * Returns an independent iterator of all teams.
     * 
     * @return independent iterator of all teams
     */
    public Iterable<String> teams() {
        return Arrays.asList(teams);
    }

    /**
     *  Returns the number of wins for given team.
     *  
     *  @param team the String of team name
     *  @return number of wins for given team
     */
    public int wins(String team) {
        if (team2i.get(team) == null)
            throw new IllegalArgumentException();
        return wins[team2i.get(team)];
    }

    /**
     *  Returns the number of losses for given team.
     *  
     *  @param team the String of team name
     *  @return number of losses for given team
     */
    public int losses(String team) {
        if (team2i.get(team) == null)
            throw new IllegalArgumentException();
        return losses[team2i.get(team)];
    }

    /**
     *  Returns the number of remaining games for given team.
     *  
     *  @param team the String of team name
     *  @return number of remaining games for given team
     */
    public int remaining(String team) {
        if (team2i.get(team) == null)
            throw new IllegalArgumentException();
        return remaining[team2i.get(team)];
    }

    /**
     *  Returns the number of number of remaining games between team1 and team2.
     *  
     *  @param team1 the String of team name
     *  @param team2 the String of team name
     *  @return number of remaining games between team1 and team2
     */
    public int against(String team1, String team2) {
        if (team2i.get(team1) == null || team2i.get(team2) == null)
            throw new IllegalArgumentException();
        int id1 = team2i.get(team1);
        int id2 = team2i.get(team2);
        if (id1 == id2) 
            return 0;
        if (id1 < id2)  
            return games[offset[id1] + id2]; 
        return games[offset[id2] + id1];
    }

    // loop over all teams and check if it is eliminated
    private void checkEliminatedAll() {
        // calculate a set of estimated score and add the teams associated with this score
        int[] est = new int[totalTeams];
        for (int id = 0; id < totalTeams; id++) {
            est[id] = wins[id] + remaining[id];
        }
        
        // loop over the teams and check if the team is eliminated by 
        // the current score record
        for (int id = 0; id < totalTeams; id++) {
            int maxwin = est[id];
            int maxwin2 = maxwin * 2;
            for (int j = 0; j < totalTeams; j++) {
                if (id != j) {
                    if (wins[j] > maxwin) {
                        eliminatedBy[id * totalTeams + j] = true;
                        eliminated[id] = true;
                    }
                    else {
                        for (int k = j+1; k < totalTeams; k++) {
                            if (wins[j] + wins[k] + games[offset[j] + k] > maxwin2) {
                                eliminatedBy[id * totalTeams + j] = true;
                                eliminatedBy[id * totalTeams + k] = true;
                                eliminated[id] = true;
                            }
                        }
                    }
                }
            }
        }
        
        // if the team is not eliminated, check it with min cut max flow
        for (int id = 0; id < totalTeams; id++) {
            if (!eliminated[id]) {
                mincut(id, est[id]);
            }
        }
    }
    
    // data type Node class contain a temporary gameID and 2 teamID
    private class Node {
        int gameID;
        int team1;
        int team2;
        
        Node(int gameID, int team1, int team2) {
            this.gameID = gameID;
            this.team1 = team1;
            this.team2 = team2;
        }
    }
    
    // check if the teamID is elimiated by the min cut max flow
    // based on a set of estimated best score
    private void mincut(int id, int estimate) {
        ArrayList<Bag<Integer>> team2games = new ArrayList<Bag<Integer>>(totalTeams);
        for (int i = 0; i < totalTeams; i++) {
            team2games.add(new Bag<Integer>());
        }
        Bag<Integer> ctTeams = new Bag<Integer>();
        Bag<Node> ctGames = new Bag<Node>();
        int[] teamVertex = new int[totalTeams];
        boolean[] hasGame = new boolean[totalTeams];
        
        for (int i = 0; i < totalTeams; i++) {
            if (i != id) {
                for (int j = i+1; j < totalTeams; j++) {
                    if (j != id && (games[offset[i] + j] > 0
                            && !eliminated[i] && !eliminated[j])) {
                        if (!hasGame[i]) {
                            hasGame[i] = true;
                            ctTeams.add(i);
                        }
                        if (!hasGame[j]) {
                            hasGame[j] = true;
                            ctTeams.add(j);
                        }
                        ctGames.add(new Node(offset[i] + j, i, j));
                    }
                }
            }
        }
        
        if (ctGames.size() == 0)
            return;
        int size = ctGames.size() + ctTeams.size() + 2;
        
        //StdOut.println(id + " " + ctGames + " " + ctTeams.size() + " " + size);
        int v = 0;
        FlowNetwork fn = new FlowNetwork(size);
        //StdOut.println(size + " " + fn.V() + " " + fn.E());
        
        //StdOut.println(id + " : " + size);
        // step 1: insert all games
        for (Node node : ctGames) {
            fn.addEdge(new FlowEdge(0, ++v, games[node.gameID]));
            //FlowEdge(int from, int to, double capacity) 
            team2games.get(node.team1).add(v);
            team2games.get(node.team2).add(v);
        }
        //StdOut.println(size + " " + fn.V() + " " + fn.E());
        
        // step 2: group all games per team
        for (int i : ctTeams) {
            teamVertex[i] = ++v;
            for (int v1 : team2games.get(i)) {
                fn.addEdge(new FlowEdge(v1, v, Double.POSITIVE_INFINITY));
             }
        }
        //StdOut.println(size + " " + fn.V() + " " + fn.E());
        
        // step 3: group all teams together
        for (int i : ctTeams) {
            fn.addEdge(new FlowEdge(teamVertex[i], size - 1, estimate - wins[i]));
        }
               
        //StdOut.println(size + " " + fn.V() + " " + fn.E());
        // step 4: min cut, FordFulkerson with flow network graph
        FordFulkerson ff = new FordFulkerson(fn, 0, size - 1);
        for (int i = 0; i < totalTeams; i++) {
            if (i != id && teamVertex[i] > 0) {
                if (ff.inCut(teamVertex[i])) {
                    eliminatedBy[id * totalTeams + i] = true;
                    eliminated[id] = true;
                }
            }
        }
        //StdOut.println("\n");
    }

    /**
     *  Returns the boolean represent the given team is eliminated.
     *  
     *  @param team the String of team name
     *  @return boolean represent the given team is eliminated
     */
    public boolean isEliminated(String team) {
        if (team2i.get(team) == null)
            throw new IllegalArgumentException();
        return eliminated[team2i.get(team)];
    }

    /**
     *  Returns an independent iterator of subset R of teams that eliminates
     *  given team; null if not eliminated.
     *  
     *  @param team the String of team name
     *  @return independent iterator of subset R of teams that eliminates
     *  given team; null if not eliminated
     */
    public Iterable<String> certificateOfElimination(String team) {
        if (team2i.get(team) == null)
            throw new IllegalArgumentException();
        Bag<String> list = new Bag<String>();
        int teamID = team2i.get(team) * totalTeams;
        for (int i = 0; i < numberOfTeams(); i++) {
            if (eliminatedBy[teamID + i]) {
                list.add(teams[i]);
            }
        }
        if (list.isEmpty())
            return null;
        else
            return list;
    }

    /**
     *  test client reads in a sports division from an input file and prints out 
     *  whether each team is mathematically eliminated and a certificate of elimination 
     *  for each team that is eliminated.
     *  
     *  @param args main function standard arguments
     */
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team))
                    StdOut.print(t + " ");
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
