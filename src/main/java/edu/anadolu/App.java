package edu.anadolu;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

/**
 * Hello world!
 */
@SuppressWarnings("ALL")
public class App {

    public static void main(String[] args) {

        Params params;
        try {
            params = CliFactory.parseArguments(Params.class, args);
        } catch (ArgumentValidationException e) {
            System.out.println(e.getMessage());
            return;
        }


        mTSP best = null;
        int minCost = Integer.MAX_VALUE;

/** I prefered to use parallel streams to create 100_000 solution and finding best so I substract the
 * for part which included in the repository.
 *      for (int i = 0; i < 100_000; i++) {
 *
 *             mTSP mTSP = new mTSP(params.getNumDepots(), params.getNumSalesmen());
 *
 *             mTSP.randomSolution();
 *             mTSP.validate();
 *             //mTSP.print(false);
 *
 *             final int cost = mTSP.cost();
 *
 *             // System.out.println("Total cost is " + cost);
 *
 *             if (cost < minCost) {
 *                 best = mTSP;
 *                 minCost = cost;
 *             }
 *         }
 * */


        /** Nearest Neigborhood Method*/
        if (params.nearestNeighbor()) {
            best = new mTSP(params.getNumDepots(), params.getNumSalesmen());
            best.nearestN(params.selectedCity() - 1);
            best.validate();
            /** If you open this line before running on the IDE it will record the initial cost to Statistics.xlsx file*/
            //printExcel(new Object[][]{{"Nearest Neighbourhood Initial " + params.selectedCity(), best.cost}});
        }
        /** Random Solution Method*/
        else if (params.randomSolution()) {
            ConcurrentLinkedQueue<mTSP> solutions = new ConcurrentLinkedQueue<>();
            long startTime = System.nanoTime();

            /**Creating 100_000 solution with parallel streama*/
            IntStream.range(1, 100_000)
                    .boxed()
                    .parallel()
                    .forEach(integer -> {
                                mTSP mTSP = new mTSP(params.getNumDepots(), params.getNumSalesmen());
                                mTSP.randomSolution();
                                mTSP.validate();
                                solutions.add(mTSP);
                            }
                    );

            long estimatedTime = System.nanoTime() - startTime;
            double convert = (double) estimatedTime / 1_000_000_000;
            System.out.println("Creating 100_000 solution --> " + convert + " seconds");

            /** Finding best in 100_000 solution */
            best = solutions.stream().min(Comparator.comparingInt(mTSP::cost)).get();

            /** If you open this line before running on the IDE it will record the initial cost to Statistics.xlsx file*/
            //printExcel(new Object[][]{{"Random Solution Initial", best.cost}});
        } else {
            throw new RuntimeException("You should choose random or nearest neighborhood option. If you choose nearest neighborhood option please enter a city.");
        }


        if (best != null) {
            best.print(params.getVerbose());
            System.out.println("**Total cost is " + best.cost());
        }
        System.out.println("-------------------------------------------------------");

        /** Trying to make the solution better */
        Counters counter = new Counters();
        mTSP copy;

        /** Improvement operations */
        for (int i = 0; i < 5_000_000; i++) {

            /**Copy of the best solution*/
            copy = new mTSP(best);

            int result = copy.improveSolution();
            if (copy.cost() < best.cost()) {
                best = new mTSP(copy);
                counter.increment(result);
            } else {
                copy = new mTSP(best);
            }
        }

        best.print(params.getVerbose());
        System.out.println("**Total cost is " + best.cost() + "\n");
        System.out.println(counter.toString());
        best.writeJSONFILE(best.selectedOption, counter);

        /** If you open this line before running on the IDE it will record the final cost to Statistics.xlsx file*/
        //printExcel(new Object[][]{{"Final", best.cost}, {"-"}});

    }

    private static void printExcel(Object[][] bookData) {
        String excelFilePath = "Statistics.xlsx";

        try (FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
        ) {
            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet sheet = workbook.getSheetAt(0);


            int rowCount = sheet.getLastRowNum();

            for (Object[] aBook : bookData) {
                Row row = sheet.createRow(++rowCount);

                int columnCount = 0;

                Cell cell = row.createCell(columnCount);
                cell.setCellValue(rowCount);

                for (Object field : aBook) {
                    cell = row.createCell(++columnCount);
                    if (field instanceof String) {
                        cell.setCellValue((String) field);
                    } else if (field instanceof Integer) {
                        cell.setCellValue((Integer) field);
                    }
                }

            }

            inputStream.close();

            FileOutputStream outputStream = new FileOutputStream("Statistics.xlsx");
            workbook.write(outputStream);
            workbook.close();

        } catch (IOException | EncryptedDocumentException | InvalidFormatException ex) {
            ex.printStackTrace();
        }
    }
}
