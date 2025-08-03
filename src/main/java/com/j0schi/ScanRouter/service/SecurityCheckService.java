package com.j0schi.ScanRouter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityCheckService {
    private final PortScannerService portScanner;

    @Autowired
    public SecurityCheckService(PortScannerService portScanner) {
        this.portScanner = portScanner;
    }

    public String checkWanSecurity(String host) {
        List<Integer> ports = List.of(22, 23, 80, 443, 7547);
        boolean isSecure = ports.stream()
                .noneMatch(port -> portScanner.isPortOpen(host, port));

        return isSecure ?
                "✅ Критичные порты закрыты" :
                "⛔ Обнаружены открытые критические порты";
    }
}