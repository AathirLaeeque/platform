package com.leucine.streem.util;

import java.util.List;

/**
 * Utility to detect cycle in a graph
 */
public final class CycleDetectionUtil {
  private CycleDetectionUtil() {}

  /**
   * method uses DFS algorithm to check for a cycle in a graph
   * @param adjacencyList stores an array list of all the adjacent vertices for that vertex, represented as a graph
   * @param vertex current vertex in the recursion cycle
   * @param visited visited array to avoid revisiting a vertex if it is already visited in the recursion cycle
   * @param recStack recursion stack array to detect cycle
   * @return
   */
  private static boolean checkCycle(List<List<Integer>> adjacencyList, int vertex, boolean[] visited, boolean[] recStack) {
    if (recStack[vertex]) {
      return true;
    }

    if (visited[vertex]) {
      return false;
    }

    visited[vertex] = true;
    recStack[vertex] = true;
    List<Integer> adjacentVertices = adjacencyList.get(vertex);

    for (Integer av: adjacentVertices) {
      if (checkCycle(adjacencyList, av, visited, recStack)) {
        return true;
      }
    }

    recStack[vertex] = false;
    return false;
  }

  /**
   * @param adjacencyList stores an array list of all the adjacent vertices for that vertex
   *                      e.g vertex 0 -> [1, 2, 3]
   *                          vertex 1 -> [2]
   *                          vertex 2 -> [3]
   *                          vertex 3 -> []
   * @param totalVertices total vertices
   * @return
   */
  public static boolean isCyclic(List<List<Integer>> adjacencyList, int totalVertices) {
    boolean[] visited = new boolean[totalVertices];
    boolean[] recStack = new boolean[totalVertices];

    for (int i = 0; i < totalVertices; i++) {
      if (checkCycle(adjacencyList, i, visited, recStack)) {
        return true;
      }
    }
    return false;
  }
}
