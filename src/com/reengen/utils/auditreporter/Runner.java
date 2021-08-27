package com.reengen.utils.auditreporter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Runner {

    private List<List<String>> users;
    private List<List<String>> files;
    private static int top;
    private static boolean scsv = false;
    private static boolean showLargeFiles;

    static {
        showLargeFiles = false;
    }


    public static void main(String[] args) throws IOException {
        Runner r = new Runner();
        r.loadData(args[0], args[1]);

        var argsLength = args.length;

        switch (argsLength) {

            case 3:
                if (args[2].equals("-c")) {
                    scsv = true;
                }
                break;
            case 4:
                if (args[2].equals("-c") || args[3].equals("-c")) {
                    scsv = true;
                } else if (args[2].equals("--top")) {
                    showLargeFiles = true;
                    top = Integer.parseInt(args[3]);
                }
                break;
            case 5:
                if (args[2].equals("-c") || args[3].equals("-c") || args[4].equals("-c")) {
                    scsv = true;
                }
                if (args[2].equals("--top") || args[3].equals("--top")) {
                    showLargeFiles = true;
                    if (args[2].equals("--top")) {
                        top = Integer.parseInt(args[3]);
                    } else {
                        top = Integer.parseInt(args[4]);
                    }
                }
            default:
                throw new IllegalStateException("Unexpected value: " + argsLength);
        }

        r.run();
    }


    private void run() {

        if (!showLargeFiles) {
            if (!scsv)
                printHeader();

            for (List<String> userRow : users) {
                long userId = Long.parseLong(userRow.get(0));
                String userName = userRow.get(1);
                if (!scsv) {
                    printUserHeader(userName);
                }
                for (List<String> fileRow : files) {
                    long size = Long.parseLong(fileRow.get(1));
                    String fileName = fileRow.get(2);
                    long ownerUserId = Long.parseLong(fileRow.get(3));
                    if (ownerUserId == userId) {
                        if (!scsv) {
                            printFile(fileName, size);
                        } else {
                            printFile(userName, fileName, size);
                        }

                    }
                }
            }
        } else {
            //List<String> LUserRows = new ArrayList<>();

            if (top > files.size()) {
                top = files.size();
            }
            if (!scsv) {
                printLFileHeader();
                files.sort(Comparator.comparingLong(l -> Long.parseLong(l.get(1))));

                for (int i = files.size() - 1; i > (files.size() - 1 - top); i--) {


                    List<String> fileRow = files.get(i);
                    //String fileId = fileRow.get(0);
                    long size = Long.parseLong(fileRow.get(1));
                    String fileName = fileRow.get(2);
                    long ownerUserId = Long.parseLong(fileRow.get(3));

                    for (List<String> userRow : users) {
                        long userId = Long.parseLong(userRow.get(0));
                        String userName = userRow.get(1);

                        if (ownerUserId == userId) {


                            printLFileHeader(userName, fileName, size);
                            break;

                        }
                    }
                }

            }

        }


    }

    private void loadData(String userFn, String filesFn) throws IOException {
        String line;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(userFn));
            users = new ArrayList<>();

            reader.readLine(); // skip header

            while ((line = reader.readLine()) != null) {
                users.add(Arrays.asList(line.split(",")));
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        reader = null;
        try {
            reader = new BufferedReader(new FileReader(filesFn));
            files = new ArrayList<>();

            reader.readLine(); // skip header

            while ((line = reader.readLine()) != null) {
                files.add(Arrays.asList(line.split(",")));
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void printHeader() {
        System.out.println("User Files");
        System.out.println("==========");
    }

    private void printUserHeader(String userName) {
        System.out.println("## User: " + userName);
    }

    private void printFile(String fileName, long fileSize) {
        System.out.println("* " + fileName + " ==> " + fileSize + " bytes");
    }

    private void printFile(String username, String fileName, long fileSize) {
        System.out.println(username + "," + fileName + "," + fileSize);
    }

    private void printLFileHeader() {
        System.out.println("Top #" + top + " Report");
        System.out.println("============");
    }


    private void printLFileHeader(String username, String fileName, long fileSize) {

        if (scsv) {
            System.out.println(fileName + "," + username + "," + fileSize);
        } else {
            System.out.println("* " + fileName + " ==> user " + username + "," + fileSize + " bytes");
        }


    }
}
