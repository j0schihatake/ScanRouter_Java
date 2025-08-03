package com.j0schi.ScanRouter.controller;

import com.j0schi.ScanRouter.service.PortScannerService;
import com.j0schi.ScanRouter.service.VulnerabilityCheckService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/scan")
public class ScanController {
    private final PortScannerService portScanner;
    private final VulnerabilityCheckService vulnChecker;

    public ScanController(PortScannerService portScanner,
                          VulnerabilityCheckService vulnChecker) {
        this.portScanner = portScanner;
        this.vulnChecker = vulnChecker;
    }

    @GetMapping("/full/{host}")
    public Map<String, Object> fullScan(@PathVariable String host) {
        Map<String, Object> result = portScanner.fullScan(host);
        result.put("vulnerabilities", vulnChecker.checkAll(host));
        return result;
    }

    @GetMapping("/ports/{host}")
    public Map<String, Object> scanPorts(@PathVariable String host) {
        return portScanner.fullScan(host);
    }

    @GetMapping("/vuln/{host}")
    public Map<String, String> checkVulnerabilities(@PathVariable String host) {
        return vulnChecker.checkAll(host);
    }
}