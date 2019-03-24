package com.wdong.config;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class ReportConfig {

    private static boolean doReport = false;

    private static ArrayList<Long> ts = new ArrayList<>();
    private static ArrayList<Long> tj = new ArrayList<>();

    public static void addRecord(long ts, long tj) {
        ReportConfig.ts.add(ts);
        ReportConfig.tj.add(tj);
    }

    public static int getRecordsLength() {
        return ts.size();
    }

    public static List<String> toStringRecords() {

        ArrayList<String> ret = new ArrayList<>();
        for (int i = 0; i < ts.size(); i++) {
            ret.add(String.format("%d,%d", ts.get(i), tj.get(i)));
        }
        return ret;
    }

    public static double getAverageTS() {
        OptionalDouble average = ts.stream().mapToLong(Long::valueOf).average();
        return average.isPresent() ? average.getAsDouble() / 1_000_000.0 : -1;
    }

    public static double getAverageTJ() {
        OptionalDouble average = tj.stream().mapToLong(Long::valueOf).average();
        return average.isPresent() ? average.getAsDouble() / 1_000_000.0 : -1;
    }

    public static boolean isStart() {
        return doReport;
    }

    public static void start() {
        doReport = true;
        ts = new ArrayList<>();
        tj = new ArrayList<>();
    }

    public static void stop() {
        doReport = false;
    }

//    private static class Record {
//        private long ts;
//        private long tj;
//
//        Record(long ts, long tj) {
//            this.ts = ts;
//            this.tj = tj;
//        }
//
//        long getTs() {
//            return ts;
//        }
//
//        long getTj() {
//            return tj;
//        }
//}
}