package com.j0schi.ScanRouter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "scan-router.target")
public class ScanConfig {
    private String host;
    private boolean autoScan;
    private String tcpPorts;
    private String nmapArgs;

    // Геттеры и сеттеры
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public boolean isAutoScan() { return autoScan; }
    public void setAutoScan(boolean autoScan) { this.autoScan = autoScan; }

    public String getTcpPorts() { return tcpPorts; }
    public void setTcpPorts(String tcpPorts) { this.tcpPorts = tcpPorts; }

    public String getNmapArgs() { return nmapArgs; }
    public void setNmapArgs(String nmapArgs) { this.nmapArgs = nmapArgs; }
}
