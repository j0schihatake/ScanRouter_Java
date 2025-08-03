package com.j0schi.ScanRouter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "scan-router")
public class ScanRouterConfig {
    private Target target;

    public static class Target {
        private String host;
        private boolean autoScan;
        private Scan scan;
        private Vulnerabilities vulnerabilities;

        // Геттеры и сеттеры
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        public boolean isAutoScan() { return autoScan; }
        public void setAutoScan(boolean autoScan) { this.autoScan = autoScan; }
        public Scan getScan() { return scan; }
        public void setScan(Scan scan) { this.scan = scan; }
        public Vulnerabilities getVulnerabilities() { return vulnerabilities; }
        public void setVulnerabilities(Vulnerabilities vulnerabilities) { this.vulnerabilities = vulnerabilities; }
    }

    public static class Scan {
        private String quickPorts;
        private String fullPorts;
        private String nmapArgs;

        public String getQuickPorts() { return quickPorts; }
        public void setQuickPorts(String quickPorts) { this.quickPorts = quickPorts; }
        public String getFullPorts() { return fullPorts; }
        public void setFullPorts(String fullPorts) { this.fullPorts = fullPorts; }
        public String getNmapArgs() { return nmapArgs; }
        public void setNmapArgs(String nmapArgs) { this.nmapArgs = nmapArgs; }
    }

    public static class Vulnerabilities {
        private boolean enabled;
        private String criticalPorts;
        private int sshTimeout;
        private int webTimeout;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getCriticalPorts() { return criticalPorts; }
        public void setCriticalPorts(String criticalPorts) { this.criticalPorts = criticalPorts; }
        public int getSshTimeout() { return sshTimeout; }
        public void setSshTimeout(int sshTimeout) { this.sshTimeout = sshTimeout; }
        public int getWebTimeout() { return webTimeout; }
        public void setWebTimeout(int webTimeout) { this.webTimeout = webTimeout; }
    }

    public Target getTarget() { return target; }
    public void setTarget(Target target) { this.target = target; }
}