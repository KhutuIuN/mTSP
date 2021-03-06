package edu.anadolu;

import com.lexicalscope.jewel.cli.Option;

public interface Params {

    @Option(description = "number of depots", shortName = "d", longName = "depots", defaultValue = "5")
    int getNumDepots();

    @Option(description = "number of salesmen per depot", shortName = {"s"}, longName = {"salesmen", "vehicles"}, defaultValue = "2")
    int getNumSalesmen();

    @Option(description = "use city names when displaying/printing", shortName = "v", longName = "verbose")
    boolean getVerbose();

    @Option(helpRequest = true, description = "display help", shortName = "h")
    boolean getHelp();

    @Option(description = "get a solution nearest neighborhood unbalanced", shortName = "n", longName = "nearestNeighborhood")
    boolean nearestNeighbor();

    @Option(description = "Selected city for nearest neighborhood", shortName = "c", longName = "city", defaultValue = "38")
    int selectedCity();

    @Option(description = "get a solution by random", shortName = "r", longName = "random")
    boolean randomSolution();

}
