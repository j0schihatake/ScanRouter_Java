package com.j0schi.ScanRouter.service;

import com.j0schi.ScanRouter.config.ScanRouterConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PortScannerService {
    private final ScanRouterConfig config;
    private static final int SCAN_TIMEOUT_MS = 300;
    private static final int THREAD_POOL_SIZE = 200;

    public Map<String, Object> fullScan(String host) {
        Map<String, Object> result = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.execute(() -> result.put("quick-scan", quickScan(host)));
        executor.execute(() -> result.put("full-scan", fullPortScan(host)));
        executor.execute(() -> {
            try {
                result.put("nmap-scan", nmapScan(host));
            } catch (Exception e) {
                result.put("nmap-error", "Nmap not available: " + e.getMessage());
            }
        });

        executor.shutdown();
        try {
            executor.awaitTermination(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return result;
    }

    public List<Integer> quickScan(String host) {
        return scanPortRange(host, config.getTarget().getScan().getQuickPorts());
    }

    public List<Integer> fullPortScan(String host) {
        return scanPortRange(host, config.getTarget().getScan().getFullPorts());
    }

    private List<Integer> scanPortRange(String host, String portRange) {
        List<Integer> ports = parsePorts(portRange);
        if (ports == null || ports.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> openPorts = new CopyOnWriteArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        ports.forEach(port -> executor.execute(() -> {
            if (isPortOpen(host, port)) {
                openPorts.add(port);
            }
        }));

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return openPorts.stream().sorted().collect(Collectors.toList());
    }

    List<Integer> parsePorts(String ports) {
        if (ports == null || ports.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(ports.split(","))
                .flatMap(part -> {
                    String[] range = part.trim().split("-");
                    if (range.length == 1) {
                        try {
                            return Stream.of(Integer.parseInt(range[0]));
                        } catch (NumberFormatException e) {
                            return Stream.empty();
                        }
                    }
                    try {
                        int start = Integer.parseInt(range[0]);
                        int end = Integer.parseInt(range[1]);
                        return IntStream.rangeClosed(start, end).boxed();
                    } catch (NumberFormatException e) {
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());
    }

    boolean isPortOpen(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), SCAN_TIMEOUT_MS);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<Integer> nmapScan(String host) throws IOException {
        try {
            Process process = new ProcessBuilder()
                    .command("nmap", "-T4", "-p-", "--open", host)
                    .start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                return reader.lines()
                        .filter(line -> line.matches("^\\d+/tcp\\s+open\\s+.*"))
                        .map(line -> Integer.parseInt(line.split("/")[0]))
                        .sorted()
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new IOException("Nmap scan failed. Ensure nmap is installed and in PATH", e);
        }
    }
}