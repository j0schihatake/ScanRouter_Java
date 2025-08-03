package com.j0schi.ScanRouter;

import com.j0schi.ScanRouter.config.ScanConfig;
import com.j0schi.ScanRouter.service.PortScannerService;
import com.j0schi.ScanRouter.service.VulnerabilityCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartupRunner implements CommandLineRunner {

    private final ScanConfig scanConfig;
    private final PortScannerService portScannerService;
    private final VulnerabilityCheckService vulnerabilityCheckService;

    public StartupRunner(ScanConfig scanConfig, PortScannerService portScannerService,
                         VulnerabilityCheckService vulnerabilityCheckService) {
        this.scanConfig = scanConfig;
        this.portScannerService = portScannerService;
        this.vulnerabilityCheckService = vulnerabilityCheckService;
    }

    @Override
    public void run(String... args) {
        if (scanConfig.isAutoScan()) {
            log.info("🔄 Автоматическое сканирование {}...", scanConfig.getHost());

            // Используем метод scanTcpPorts с диапазоном портов
            var openPorts = portScannerService.scanTcpPorts(
                    scanConfig.getHost(),
                    scanConfig.getTcpPorts() // Используем строку с диапазоном из конфига
            );
            log.info("🔓 Открытые TCP-порты: {}", openPorts);

            // Проверка уязвимостей
            String vulnReport = vulnerabilityCheckService.checkSSH(scanConfig.getHost()) + "\n" +
                    vulnerabilityCheckService.checkWebInterface(scanConfig.getHost());
            log.info("🛡️ Результаты проверки уязвимостей:\n{}", vulnReport);
        }
    }
}