package com.j0schi.ScanRouter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class PortScannerService {
    private static final int TCP_TIMEOUT = 500;
    private static final int THREADS = 100;

    public List<Integer> scanTcpPorts(String host, int startPort, int endPort) {
        List<Integer> openPorts = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int port = startPort; port <= endPort; port++) {
            final int currentPort = port;
            futures.add(executor.submit(() -> {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(host, currentPort), TCP_TIMEOUT);
                    return currentPort;
                } catch (IOException e) {
                    return null;
                }
            }));
        }

        for (Future<Integer> future : futures) {
            try {
                Integer port = future.get();
                if (port != null) openPorts.add(port);
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
            }
        }
        executor.shutdown();
        return openPorts;
    }

    public List<Integer> scanWithNmap(String host, String args) {
        List<Integer> openPorts = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("nmap " + args + " " + host);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("open")) {
                    String portStr = line.split("/")[0];
                    openPorts.add(Integer.parseInt(portStr));
                }
            }
        } catch (IOException e) {
            log.error("Nmap error: " + e.getMessage());
        }
        return openPorts;
    }

    public List<Integer> scanTcpPorts(String host, String portRange) {
        String[] parts = portRange.split("-");
        int start = Integer.parseInt(parts[0]);
        int end = Integer.parseInt(parts[1]);
        return scanTcpPorts(host, start, end);
    }
}