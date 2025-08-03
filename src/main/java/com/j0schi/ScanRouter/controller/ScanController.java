package com.j0schi.ScanRouter.controller;

import com.j0schi.ScanRouter.service.PortScannerService;
import com.j0schi.ScanRouter.service.VulnerabilityCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scan")
public class ScanController {
    @Autowired
    private PortScannerService portScannerService;
    @Autowired
    private VulnerabilityCheckService vulnerabilityCheckService;

    @GetMapping("/tcp/{host}")
    public List<Integer> scanTcpPorts(
            @PathVariable String host,
            @RequestParam(defaultValue = "1-1000") String portRange // Теперь принимаем строку диапазона
    ) {
        return portScannerService.scanTcpPorts(host, portRange); // Используем метод с диапазоном
    }

    @GetMapping("/nmap/{host}")
    public List<Integer> scanWithNmap(
            @PathVariable String host,
            @RequestParam(defaultValue = "-T4 -p 1-1000") String args
    ) {
        //return portScannerService.scanWithNmap(host, args);
        return null;
    }

    @GetMapping("/vuln/{host}")
    public String checkVulnerabilities(@PathVariable String host) {
        return vulnerabilityCheckService.checkSSH(host) + "\n" +
                vulnerabilityCheckService.checkWebInterface(host);
    }
}
