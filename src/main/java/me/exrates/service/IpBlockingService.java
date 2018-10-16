package me.exrates.service;

import me.exrates.model.enums.IpTypesOfChecking;

public interface IpBlockingService {
    void checkIp(String clientIpAddress, IpTypesOfChecking openApi);

    void failureProcessing(String clientIpAddress, IpTypesOfChecking openApi);

    void successfulProcessing(String clientIpAddress, IpTypesOfChecking openApi);
}
