package edu.anadolu;

import java.util.*;

public class Operations {
    private final mTSP mTSP;

    public Operations(mTSP mTSP) {
        this.mTSP = mTSP;
    }

    protected void swapNodesInRoute() {

        int depotIndex = (int) (Math.random() * mTSP.DEPOT_NUMBERS);
        int routeIndex = (int) (Math.random() * mTSP.ROUTE_NUMBERS);

        if (mTSP.solution.get(getMapKeyByIndex(depotIndex))[routeIndex].size() != 1) {
            int[] numbers = generateRandomNumber(mTSP.solution.get(getMapKeyByIndex(depotIndex))[routeIndex].size(), false);
            Collections.swap(mTSP.solution.get(getMapKeyByIndex(depotIndex))[routeIndex], numbers[0], numbers[1]);
            mTSP.cost = mTSP.calculateCost();
        }

    }

    protected void swapHubWithNodeInRoute() {

        int depotIndex = (int) (Math.random() * mTSP.DEPOT_NUMBERS);
        int routeIndex = (int) (Math.random() * mTSP.ROUTE_NUMBERS);
        int depot1 = getMapKeyByIndex(depotIndex);

        int counterTemp = (int) (Math.random() * mTSP.solution.get(depot1)[routeIndex].size());
        int temp = mTSP.solution.get(depot1)[routeIndex].get(counterTemp);

        mTSP.solution.get(depot1)[routeIndex].set(counterTemp, depot1);

        ArrayList<Integer>[] array = new ArrayList[mTSP.solution.get(depot1).length];
        for (int i = 0; i < array.length; i++) {
            array[i] = mTSP.solution.get(depot1)[i];
        }
        mTSP.solution.remove(depot1);
        mTSP.solution.put(temp, array);
        mTSP.cost = mTSP.calculateCost();
    }

    protected void swapNodesBetweenRoutes() {

        int[] depotIndexes;
        if (mTSP.DEPOT_NUMBERS == 1 && mTSP.ROUTE_NUMBERS > 1) {
            depotIndexes = generateRandomNumber(mTSP.DEPOT_NUMBERS, false);
        } else if (mTSP.DEPOT_NUMBERS > 1 && mTSP.ROUTE_NUMBERS == 1) {
            depotIndexes = generateRandomNumber(mTSP.DEPOT_NUMBERS, false);
        } else {
            depotIndexes = generateRandomNumber(mTSP.DEPOT_NUMBERS, true);
        }

        int[] routeIndexes = generateRandomNumber(mTSP.ROUTE_NUMBERS, false);

        int[] nodeIndexes = new int[2];

        int depot1 = getMapKeyByIndex(depotIndexes[0]);
        int depot2 = getMapKeyByIndex(depotIndexes[1]);

        int rnd1 = (int) (Math.random() * mTSP.solution.get(depot1)[routeIndexes[0]].size());
        nodeIndexes[0] = rnd1;
        int n1 = mTSP.solution.get(depot1)[routeIndexes[0]].get(rnd1);


        int rnd2 = (int) (Math.random() * mTSP.solution.get(depot2)[routeIndexes[1]].size());
        nodeIndexes[1] = rnd2;
        int n2 = mTSP.solution.get(depot2)[routeIndexes[1]].get(nodeIndexes[1]);


        mTSP.solution.get(depot1)[routeIndexes[0]].set(nodeIndexes[0], n2);

        mTSP.solution.get(depot2)[routeIndexes[1]].set(nodeIndexes[1], n1);


        mTSP.cost = mTSP.calculateCost();
    }

    protected void insertNodeInRoute() {
        int depotIndex = (int) (Math.random() * mTSP.DEPOT_NUMBERS);

        ArrayList<Integer>[] selectedDepotsRoutes = mTSP.solution.get(getMapKeyByIndex(depotIndex));

        int routeIndex = (int) (Math.random() * selectedDepotsRoutes.length);
        if (selectedDepotsRoutes[routeIndex].size() != 1) {
            int[] nodesIndexes = generateRandomNumber(selectedDepotsRoutes[routeIndex].size(), false);
            int rndIndex1 = nodesIndexes[0];
            int rndIndex2 = nodesIndexes[1];
            int number1 = selectedDepotsRoutes[routeIndex].get(rndIndex1);
            selectedDepotsRoutes[routeIndex].set(rndIndex1, -1);
            selectedDepotsRoutes[routeIndex].add(rndIndex2 + 1, number1);
            selectedDepotsRoutes[routeIndex].remove((Integer) (-1));
            mTSP.cost = mTSP.calculateCost();
        }

    }

    protected void insertNodeBetweenRoutes() {

        int[] depotIndexes = generateRandomNumber(mTSP.DEPOT_NUMBERS, false);
        int depot1 = getMapKeyByIndex(depotIndexes[0]);
        int depot2 = getMapKeyByIndex(depotIndexes[1]);
        int deleted = -1;
        int route1Index = 0;
        int route2Index;
        ArrayList<Integer> route1;
        ArrayList<Integer> route2;


        if (mTSP.solution.get(depot1)[route1Index].size() != 1) {
            route1 = mTSP.solution.get(depot1)[route1Index];
            int node = (int) (Math.random() * route1.size());
            deleted = mTSP.solution.get(depot1)[route1Index].remove(node);
        }

        if (deleted != -1) {
            route2Index = (int) (Math.random() * mTSP.solution.get(depot2).length);
            route2 = mTSP.solution.get(depot2)[route2Index];
            int node = (int) (Math.random() * route2.size());
            mTSP.solution.get(depot2)[route2Index].add(node + 1, deleted);
            mTSP.cost = mTSP.calculateCost();
        }

    }

    private int[] generateRandomNumber(int upperBound, boolean canSame) {
        int[] arr = new int[2];
        if (0 == upperBound) {
            throw new RuntimeException("Alt ve üst sınır aynı olamaz!");
        }
        if (!canSame && mTSP.ROUTE_NUMBERS > 1) {
            while (true) {
                int rnd = (int) (Math.random() * (upperBound));
                int rnd1 = (int) (Math.random() * (upperBound));
                if (rnd != rnd1) {
                    arr[0] = rnd;
                    arr[1] = rnd1;
                    break;
                }
            }
        } else {
            arr[0] = (int) (Math.random() * (upperBound ));
            arr[1] = (int) (Math.random() * (upperBound ));
        }
        return arr;
    }

    private int getMapKeyByIndex(int index) {

        return (int) mTSP.solution.keySet().toArray()[index];
    }


}
