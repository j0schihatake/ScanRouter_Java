package com.j0schi.ScanRouter;

import com.j0schi.ScanRouter.service.PortScannerService;
import com.j0schi.ScanRouter.service.VulnerabilityCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {

    private final PortScannerService portScanner;
    private final VulnerabilityCheckService vulnChecker;

    @Value("${scan-router.target.host}")
    private String host;

    @Value("${scan-router.target.auto-scan}")
    private boolean autoScan;

    @Override
    public void run(String... args) {
        if (!autoScan) return;

        log.info("🔍 Starting full scan for " + host);

        try {
            // Сканирование портов
            Map<String, Object> scanResult = portScanner.fullScan(host);
            log.info("🔹 Quick scan: " + scanResult.get("quick-scan"));
            log.info("🔹 Full port scan: " + scanResult.get("full-scan"));
            log.info("🔹 Nmap results: " + scanResult.get("nmap-scan"));

            // Проверка уязвимостей
            Map<String, String> vulnResults = vulnChecker.checkAll(host);
            vulnResults.forEach((k, v) -> log.info("🔸 " + k + ": " + v));

        } catch (Exception e) {
            log.error("❌ Scan failed: " + e.getMessage());
        }
    }
}