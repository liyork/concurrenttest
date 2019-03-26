package com.wolf.concurrenttest.program.genetic;

import com.wolf.concurrenttest.program.genetic.concurrent.ConcurrentGeneticAlgorithm;
import com.wolf.concurrenttest.program.genetic.serial.SerialGeneticAlgorithm;

import java.nio.file.Paths;
import java.util.Date;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/28
 */
public class GeneticMain {

    public static void main(String[] args) {

        testSerial(args);
        testConcurrent(args);
    }

    private static void testSerial(String[] args) {
        Date start;
        Date end;

        Integer generations = Integer.valueOf(args[0]);
        Integer individuals = Integer.valueOf(args[1]);

        //使用了 City Distance 数据集，分别是 15（ lau15_dist）个城市和57（ kn57_dist）个城市
        for (String name : new String[]{"lau15_dist", "kn57_dist"}) {
            int[][] distanceMatrix = DataLoader.load(Paths.get("data", name + ".txt"));

            SerialGeneticAlgorithm serialGeneticAlgorithm =
                    new SerialGeneticAlgorithm(distanceMatrix, generations, individuals);

            start = new Date();
            Individual result = serialGeneticAlgorithm.calculate();
            end = new Date();

            System.out.println("===========================");
            System.out.println("Example:" + name);
            System.out.println("Generations:" + generations);
            System.out.println("Population:" + individuals);
            System.out.println("Execution Time:" + (end.getTime() - start.getTime()));
            System.out.println("Best Individual:" + name);
            System.out.println("Total Distance:" + result.getValue());
            System.out.println("===========================");
        }
    }

    private static void testConcurrent(String[] args) {
        Date start;
        Date end;

        Integer generations = Integer.valueOf(args[0]);
        Integer individuals = Integer.valueOf(args[1]);

        //使用了 City Distance 数据集，分别是 15（ lau15_dist）个城市和57（ kn57_dist）个城市
        for (String name : new String[]{"lau15_dist", "kn57_dist"}) {
            int[][] distanceMatrix = DataLoader.load(Paths.get("data", name + ".txt"));

            ConcurrentGeneticAlgorithm serialGeneticAlgorithm =
                    new ConcurrentGeneticAlgorithm(distanceMatrix, generations, individuals);

            start = new Date();
            Individual result = serialGeneticAlgorithm.calculate();
            end = new Date();

            System.out.println("===========================");
            System.out.println("Example:" + name);
            System.out.println("Generations:" + generations);
            System.out.println("Population:" + individuals);
            System.out.println("Execution Time:" + (end.getTime() - start.getTime()));
            System.out.println("Best Individual:" + name);
            System.out.println("Total Distance:" + result.getValue());
            System.out.println("===========================");
        }
    }
}
