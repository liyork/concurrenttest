package com.wolf.concurrenttest.mtadp.balking;

import java.util.Scanner;

/**
 * Description:
 *
 * @author 李超
 * @date 2019/02/04
 */
public class DocumentTest {

    public static void main(String[] args) {

        new Thread(new DocumentEditTask(), "DocumentEditThread").start();
    }

    static class DocumentEditTask implements Runnable {

        private final Scanner scanner = new Scanner(System.in);

        @Override
        public void run() {

            int times = 1;

            Document document = Document.create("xx/abc/dbe/e.txt");
            while (true) {

                String input = scanner.next();
                if ("quit".equals(input)) {

                    document.close();
                    break;
                }

                document.edit(input);

                if (times == 5) {
                    document.save();
                    times = 1;
                }

                times++;
            }
        }
    }
}
