package edu.anadolu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class mTSP {
    protected final int DEPOT_NUMBERS;
    protected final int ROUTE_NUMBERS;
    protected int cost;
    protected String selectedOption;
    Operations op = new Operations(this);
    protected LinkedHashMap<Integer, ArrayList<Integer>[]> solution = new LinkedHashMap<>();

    /**
     * Normal constructor
     */
    public mTSP(int depots, int salesmen) {
        int x = depots * salesmen + depots;
        if (x > 81 || depots <= 0 || salesmen <= 0) {
            throw new RuntimeException("illegal inputs!");
        }
        DEPOT_NUMBERS = depots;
        ROUTE_NUMBERS = salesmen;
    }

    /**
     * Copy constructor
     */
    public mTSP(mTSP original) {
        DEPOT_NUMBERS = original.DEPOT_NUMBERS;
        ROUTE_NUMBERS = original.ROUTE_NUMBERS;
        solution = copy(original.solution);
        cost = original.cost;
        selectedOption = original.selectedOption;
    }

    private LinkedHashMap<Integer, ArrayList<Integer>[]> copy(LinkedHashMap<Integer, ArrayList<Integer>[]> map) {
        LinkedHashMap<Integer, ArrayList<Integer>[]> new_map = new LinkedHashMap<>();
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : map.entrySet()) {
            ArrayList<Integer>[] arrayLists = new ArrayList[ROUTE_NUMBERS];
            for (int i = 0; i < entry.getValue().length; i++) {
                ArrayList<Integer> copyRoutes = new ArrayList<>();
                for (int j = 0; j < entry.getValue()[i].size(); j++) {
                    copyRoutes.add(entry.getValue()[i].get(j));
                }
                arrayLists[i] = copyRoutes;
            }
            new_map.put(entry.getKey(), arrayLists);
        }
        return new_map;
    }

    public void randomSolution() {
        int lowerBound = ((81 - DEPOT_NUMBERS) / (DEPOT_NUMBERS * ROUTE_NUMBERS));
        int extra = (81 - DEPOT_NUMBERS) % (DEPOT_NUMBERS * ROUTE_NUMBERS);
        int sum = 0;
        LinkedHashMap<Integer, ArrayList<Integer>[]> map = new LinkedHashMap<>();
        LinkedList<Integer> cities = new LinkedList<>();
        for (int i = 0; i < 81; i++) {
            cities.add(i);
        }
        Collections.shuffle(cities);

        /** Depo şehirleri seçimi */
        for (int i = 0; i < DEPOT_NUMBERS; i++) {
            ArrayList<Integer>[] arrayLists = new ArrayList[ROUTE_NUMBERS];
            int rnd_depot = cities.remove(0);
            map.put(rnd_depot, arrayLists);
            sum += 1;
        }

        /** Route şehirleri seçimi */
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : map.entrySet()) {
            for (int j = 0; j < ROUTE_NUMBERS; j++) {
                ArrayList<Integer> route_cities = new ArrayList<>();

                for (int i = 0; i < lowerBound; i++) {
                    int rnd_route = cities.remove(0);
                    route_cities.add(rnd_route);
                }
                entry.getValue()[j] = route_cities;
            }
        }

        int counter = 0;
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : map.entrySet()) {
            if (counter == extra) {
                break;
            }
            for (int j = 0; j < ROUTE_NUMBERS; j++) {
                ArrayList<Integer> route_cities = entry.getValue()[j];
                int rnd_route = cities.remove(0);
                route_cities.add(rnd_route);
                counter++;
                if (cities.size() == 0) {
                    break;
                }
            }
        }
        solution = copy(map);
        cost = calculateCost();
        selectedOption = "Random Solution";

    }

    public void nearestN(int current) {
        LinkedList<Integer> list = new LinkedList<>();
        LinkedHashMap<Integer, Boolean> map = new LinkedHashMap<>();
        for (int i = 0; i < TurkishNetwork.cities.length; i++) {
            map.put(i, false);
        }
        list.add(current);
        map.replace(current, true);

        while (list.size() != 81) {
            int initialDistance = Integer.MAX_VALUE;
            int nextDistance;
            int shortest = 0;
            for (Map.Entry<Integer, Boolean> entry : map.entrySet()) {
                if (!entry.getValue()) {
                    nextDistance = TurkishNetwork.distance[current][entry.getKey()];
                    if (nextDistance < initialDistance) {
                        initialDistance = nextDistance;
                        shortest = entry.getKey();
                    }
                }
            }
            current = shortest;
            map.replace(shortest, true);
            list.add(shortest);
        }

//      spilitRoutesEven(list);
        splitRoutes(list);


    }

    private void splitRoutes(LinkedList<Integer> allCities) {
        selectedOption = "Nearest Neighborhood unbalanced with " + allCities.get(0);

        int lowerBound = ((81 - DEPOT_NUMBERS) / (DEPOT_NUMBERS * ROUTE_NUMBERS));

        LinkedHashMap<Integer, ArrayList<Integer>[]> map = new LinkedHashMap<>();

        /** Depo şehirleri seçimi */
        for (int i = 0; i < DEPOT_NUMBERS; i++) {
            ArrayList<Integer>[] arrayLists = new ArrayList[ROUTE_NUMBERS];
            int rnd_depot = allCities.remove(0);
            map.put(rnd_depot, arrayLists);


            /** Route şehirleri seçimi */
            for (int j = 0; j < ROUTE_NUMBERS; j++) {
                ArrayList<Integer> route_cities = new ArrayList<>();

                for (int k = 0; k < lowerBound; k++) {
                    int rnd_route = allCities.remove(0);
                    route_cities.add(rnd_route);
                }
                map.get(rnd_depot)[j] = route_cities;
            }

        }

        int depot = (int) map.keySet().toArray()[DEPOT_NUMBERS - 1];

        map.get(depot)[ROUTE_NUMBERS - 1].addAll(allCities);

        solution = copy(map);
        cost = calculateCost();


    }

    private void splitRoutesEven(LinkedList<Integer> allCities) {
        selectedOption = "Nearest Neighborhood balanced with" + allCities.get(0);

        LinkedHashMap<Integer, ArrayList<Integer>[]> map = new LinkedHashMap<>();
        int product = DEPOT_NUMBERS * ROUTE_NUMBERS;
        int allMinusDepot = (81 - DEPOT_NUMBERS);
        int lowerBound = (allMinusDepot / (product));//7 for D2 R5
        for (int i = 0; i < DEPOT_NUMBERS; i++) {

            ArrayList<Integer>[] arrayLists = new ArrayList[ROUTE_NUMBERS];
            if (allCities.size() == 0) {
                break;
            }
            int depot = allCities.remove(0);
            map.put(depot, arrayLists);

            for (int j = 0; j < ROUTE_NUMBERS; j++) {
                lowerBound = allMinusDepot / product;

                ArrayList<Integer> routes = new ArrayList<>();
                for (int k = 0; k < lowerBound; k++) {
                    routes.add(allCities.remove(0));
                }

                allMinusDepot -= routes.size();
                product--;
                map.get(depot)[j] = routes;
            }
        }

        solution = copy(map);
        cost = calculateCost();
    }

    public int improveSolution() {
        int rnd = (int) (Math.random() * 5);

        switch (rnd) {
            case 0:
                op.swapNodesInRoute();
                break;
            case 1:
                if (selectedOption.equals("Random Solution") || DEPOT_NUMBERS != 1)
                    op.swapHubWithNodeInRoute();
                break;
            case 2:
                if (DEPOT_NUMBERS > 1)
                    op.swapNodesBetweenRoutes();
                break;
            case 3:
                op.insertNodeInRoute();
                break;
            case 4:
                if (DEPOT_NUMBERS >= 2) {
                    op.insertNodeBetweenRoutes();
                }
                break;
        }
        return rnd;
    }

    public void validate() {
        int counter = 0;
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {
            counter++;
            for (int i = 0; i < entry.getValue().length; i++) {
                for (int j = 0; j < entry.getValue()[i].size(); j++) {
                    if (entry.getValue()[i].get(j) <= 81 || entry.getValue()[i].get(j) >= 0) {
                        counter++;
                    } else
                        throw new RuntimeException("There is a number which is not int the Turkish Network.");
                }
            }
        }
        if (counter < 81) {
            throw new RuntimeException("Some cities are missing in the solution.");
        } else if (counter > 81) {
            throw new RuntimeException("Some cities are duplicated in the solution.");
        }
    }

    public int cost() {
        return cost;
    }

    public void print(boolean verbose) {
        if (verbose) {
            LinkedHashMap<String, ArrayList<String>[]> verboseMap = new LinkedHashMap<>();
            for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {
                ArrayList<String>[] arrayLists = new ArrayList[ROUTE_NUMBERS];
                for (int i = 0; i < entry.getValue().length; i++) {
                    ArrayList<String> copyRoutes = new ArrayList<>();
                    for (int j = 0; j < entry.getValue()[i].size(); j++) {
                        copyRoutes.add(TurkishNetwork.cities[entry.getValue()[i].get(j)]);
                    }
                    arrayLists[i] = copyRoutes;
                }
                verboseMap.put(TurkishNetwork.cities[entry.getKey()], arrayLists);
            }
            int counter = 1;
            for (Map.Entry<String, ArrayList<String>[]> depot : verboseMap.entrySet()) {
                System.out.println("Depot" + counter + ": " + depot.getKey());
                int routeNumber = 1;
                for (int i = 0; i < depot.getValue().length; i++) {
                    System.out.print("    Route" + routeNumber + ": ");
                    for (int j = 0; j < depot.getValue()[i].size(); j++) {
                        System.out.print(depot.getValue()[i].get(j));
                        if (j != depot.getValue()[i].size() - 1) {
                            System.out.print(",");
                        }
                    }
                    System.out.println();
                    routeNumber++;
                }
                counter++;
            }

        } else {
            int counter = 1;
            for (Map.Entry<Integer, ArrayList<Integer>[]> depot : solution.entrySet()) {
                System.out.println("Depot" + counter + ": " + depot.getKey());
                int routeNumber = 1;
                for (int i = 0; i < depot.getValue().length; i++) {
                    System.out.print("    Route" + routeNumber + ": ");
                    for (int j = 0; j < depot.getValue()[i].size(); j++) {
                        System.out.print(depot.getValue()[i].get(j));
                        if (j != depot.getValue()[i].size() - 1) {
                            System.out.print(",");
                        }
                    }
                    System.out.println();
                    routeNumber++;
                }
                counter++;
            }
        }
    }

    public void writeJSONFILE(String selection, Counters counters) {
        JSONObject obj = new JSONObject();
        JSONArray list = new JSONArray();

        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {

            JSONObject Obj = new JSONObject();

            obj.put("solution", list);
            Obj.put("depot", entry.getKey().toString());
            JSONArray array = new JSONArray();
            for (int i = 0; i < entry.getValue().length; i++) {

                JSONObject object = new JSONObject();
                String temp = "";

                for (int j = 0; j < entry.getValue()[i].size(); j++) {
                    if (entry.getValue()[i].size() - 1 == j) {

                        temp += entry.getValue()[i].get(j);

                    } else {
                        temp += entry.getValue()[i].get(j) + " ";

                    }
                }
                array.put(temp);
            }
            Obj.put("route", array);
            list.put(Obj);
        }
        obj.put("**TotalCost: ", cost);
        obj.put("Counters: ", counters.toString());
        Path path = null;
        if (selection.equals("Random Solution")) {
            path = Paths.get("R_" + "solution_" + "d" + DEPOT_NUMBERS + "s" + ROUTE_NUMBERS + "c" + cost + ".json");
        } else if (selection.contains("Nearest Neighborhood")) {
            String city = selection.trim().replaceAll("[^0-9]", "");
            path = Paths.get("NN_" + city + "_solution_" + "d" + DEPOT_NUMBERS + "s" + ROUTE_NUMBERS + "c" + cost + ".json");
        } else {
            System.out.println("Wrong selection.");
            return;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(obj.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected int calculateCost() {
        int cost = 0;
        int depot = 0;
        for (Map.Entry<Integer, ArrayList<Integer>[]> entry : solution.entrySet()) {
            int beginning;
            int city = 0;
            for (int i = 0; i < ROUTE_NUMBERS; i++) {
                for (int j = 0; j < entry.getValue()[i].size(); j++) {
                    if (j == 0) {
                        beginning = entry.getKey();
                        depot = entry.getKey();
                    } else {
                        beginning = entry.getValue()[i].get(j - 1);
                    }
                    city = entry.getValue()[i].get(j);
                    cost += TurkishNetwork.distance[beginning][city];
                }
                cost += TurkishNetwork.distance[city][depot];
            }
        }
        return cost;
    }
}
