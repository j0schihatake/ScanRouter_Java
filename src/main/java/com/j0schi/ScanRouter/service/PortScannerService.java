package com.j0schi.ScanRouter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
public class PortScannerService {
    private static final int TCP_TIMEOUT = 2000; // Увеличили таймаут до 2 секунд
    private static final int THREADS = 50; // Уменьшили потоки для стабильности

    public List<Integer> scanTcpPorts(String host, String portRange) {
        // Добавим проверку доступности хоста
        if (!isHostReachable(host)) {
            throw new RuntimeException("Хост " + host + " недоступен!");
        }

        String[] parts = portRange.split("-");
        int startPort = Integer.parseInt(parts[0]);
        int endPort = Integer.parseInt(parts[1]);

        return scanPortsWithRetry(host, startPort, endPort, 2); // 2 попытки
    }

    private List<Integer> scanPortsWithRetry(String host, int startPort, int endPort, int retries) {
        List<Integer> openPorts = new ArrayList<>();
        for (int i = 0; i < retries; i++) {
            openPorts = doScan(host, startPort, endPort);
            if (!openPorts.isEmpty()) break;
            try { Thread.sleep(1000); } catch (InterruptedException e) { break; }
        }
        return openPorts;
    }

    private List<Integer> doScan(String host, int startPort, int endPort) {
        List<Integer> openPorts = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        for (int port = startPort; port <= endPort; port++) {
            final int currentPort = port;
            executor.submit(() -> {
                if (isPortOpen(host, currentPort)) {
                    openPorts.add(currentPort);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return openPorts;
    }

    private boolean isHostReachable(String host) {
        try {
            return InetAddress.getByName(host).isReachable(3000);
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isPortOpen(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), TCP_TIMEOUT);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}