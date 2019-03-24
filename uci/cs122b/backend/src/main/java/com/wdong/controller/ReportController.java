package com.wdong.controller;


import com.wdong.config.ReportConfig;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "api/report")
public class ReportController {

    @RequestMapping("/test")
    public @ResponseBody String test() {
        return Paths.get(String.format("performanceLog/%s.txt", "single_http_1_thread")).toAbsolutePath().toString();
    }

    @RequestMapping("/save")
    public @ResponseBody String save(@RequestParam("type") int type, @RequestParam("name") int name) {
        String instanceType = getInstanceType(type);
        String filename = getFilename(name);

        Path path = Paths.get(String.format("performanceLog/%s_%s.txt", instanceType, filename));
        try {
            Files.write(path, ReportConfig.toStringRecords(), Charset.forName("UTF-8"));

        } catch (IOException e) {
            return "no";
        }

        return "ok";
    }

    @RequestMapping("start")
    public @ResponseBody String start() {
        ReportConfig.start();
        return "ok";
    }

    @RequestMapping("status")
    public @ResponseBody String status() {
        if (ReportConfig.getRecordsLength() > 0) {
            String size = String.format("%d requests were processed\t\t\t", ReportConfig.getRecordsLength());
            String ts = String.format("Average TS: %f\t\t\t", ReportConfig.getAverageTS());
            String tj = String.format("Average TJ: %f\t\t\t", ReportConfig.getAverageTJ());
            return String.format("\n\n%s\n\n%s\n\n%s", size, ts, tj);
        }
        return "No data available";
    }

    @RequestMapping("stop")
    public @ResponseBody String stop() {
        ReportConfig.stop();
        return "stopped";
    }

    @RequestMapping("/result")
    public @ResponseBody String result(@RequestParam("type") int type, @RequestParam("name") int name) {
        try {
            String instanceType = getInstanceType(type);
            String filename = getFilename(name);
            File file = ResourceUtils.getFile(String.format("classpath:performanceLog/%s_%s.txt", instanceType, filename));
            ArrayList<String> tsResult = new ArrayList<>();
            ArrayList<String> tjResult = new ArrayList<>();

            Files.lines(file.toPath()).forEach(l -> {
                String[] arr = l.split(",");
                tsResult.add(arr[0]);
                tjResult.add(arr[1]);
            });
            OptionalDouble tsAverage = tsResult.stream().mapToDouble(Double::valueOf).average();
            OptionalDouble tjAverage = tjResult.stream().mapToDouble(Double::valueOf).average();

            String size = String.format("%d requests were saved\t\t\t", tsResult.size());
            String ts = String.format("Average TS: %f\t\t\t",
                    tsAverage.isPresent() ? tsAverage.getAsDouble() / 1_000_000.0 : -1);
            String tj = String.format("Average TJ: %f\t\t\t",
                    tjAverage.isPresent() ? tjAverage.getAsDouble() / 1_000_000.0 : -1);

            return String.format("\n\n%s\n\n%s\n\n%s", size, ts, tj);
        } catch (IOException e) {
            return "file cannot be read or not found";
        } catch (IllegalArgumentException e) {
            return "file not found";
        }
    }

    private String getInstanceType(int type) {
        if (type == 0) {
            return "master";
        } else if (type == 1) {
            return "slave";
        }
        throw new IllegalArgumentException("Unexpected type");
    }

    private String getFilename(int name) {
        if (name == 1) {
            return "single_http_1_thread";
        } else if (name == 2) {
            return "single_http_10_thread";
        } else if (name == 3) {
            return "single_https_10_thread";
        } else if (name == 4) {
            return "scaled_http_1_thread";
        } else if (name == 5) {
            return "scaled_http_10_thread";
        }
        throw new IllegalArgumentException("Unexpected name");
    }
}
