package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.exception.BannedIpException;
import me.exrates.model.dto.LoginAttemptDto;
import me.exrates.model.enums.IpBanStatus;
import me.exrates.model.enums.IpTypesOfChecking;
import me.exrates.service.IpBlockingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentMap;

@Service
@Log4j2
public class IpBlockingServiceImpl implements IpBlockingService {



    @Value("${ban.short.attempts.num}")
    private Integer attemptsBeforeShortBan;

    @Value("${ban.long.attempts.num}")
    private Integer attemptsBeforeLongBan;

    @Value("${ban.short.attempts.period}")
    private Long periodAttemptsBeforeShortBan;

    @Value("${ban.long.attempts.period}")
    private Long periodAttemptsBeforeLongBan;

    @Value("${ban.short.time}")
    private Long shortBanTime;

    @Value("${ban.long.time}")
    private Long longBanTime;

    private final Object lock = new Object();

    private ConcurrentMap<IpTypesOfChecking, ConcurrentMap<String, LoginAttemptDto>> ipchecker;

    public IpBlockingServiceImpl() {
        ipchecker = new ConcurrentReferenceHashMap<>();
        ipchecker.put(IpTypesOfChecking.LOGIN, new ConcurrentReferenceHashMap<>());
        ipchecker.put(IpTypesOfChecking.OPEN_API, new ConcurrentReferenceHashMap<>());
    }

    @Override
    public void checkIp(String ipAddress, IpTypesOfChecking ipTypesOfChecking) {
        synchronized (lock) {
            ConcurrentMap<String, LoginAttemptDto> specificIpChecker = ipchecker.get(ipTypesOfChecking);
            if (specificIpChecker.containsKey(ipAddress)) {
                LocalDateTime currentTime = LocalDateTime.now();
                LoginAttemptDto attempt = specificIpChecker.get(ipAddress);
                if ((attempt.getStatus() == IpBanStatus.BAN_SHORT && checkBanPending(attempt, shortBanTime, currentTime))) {
                    throw new BannedIpException("IP banned: number of incorrect attempts exceeded!", shortBanTime);
                } else if (attempt.getStatus() == IpBanStatus.BAN_LONG && checkBanPending(attempt, longBanTime, currentTime)) {
                    throw new BannedIpException("IP banned: number of incorrect attempts exceeded!", longBanTime);
                }
                if (attempt.getStatus() == IpBanStatus.BAN_LONG) {
                    specificIpChecker.remove(ipAddress);
                } else {
                    attempt.setStatus(IpBanStatus.ALLOW);
                }

            }
        }
    }

    private boolean checkBanPending(LoginAttemptDto attempt, long banTimeSeconds, LocalDateTime currentTime) {
        return currentTime.isBefore(attempt.getLastAttemptTime().plusSeconds(banTimeSeconds));
    }

    @Override
    public void failureProcessing(String ipAddress, IpTypesOfChecking ipTypesOfChecking) {
        synchronized (lock) {
            ConcurrentMap<String, LoginAttemptDto> specificIpChecker = ipchecker.get(ipTypesOfChecking);
            if (!specificIpChecker.containsKey(ipAddress)) {
                specificIpChecker.put(ipAddress, new LoginAttemptDto(ipAddress));
            } else {
                LoginAttemptDto attemptDto = specificIpChecker.get(ipAddress);
                attemptDto.addNewAttempt();
                if (checkNeedToBan(attemptDto, attemptsBeforeLongBan, periodAttemptsBeforeLongBan) && attemptDto.isWasInShortBan()) {
                    attemptDto.setStatus(IpBanStatus.BAN_LONG);
                } else if (checkNeedToBan(attemptDto, attemptsBeforeShortBan, periodAttemptsBeforeShortBan)) {
                    attemptDto.setStatus(IpBanStatus.BAN_SHORT);
                    attemptDto.setWasInShortBan(true);
                }
            }
        }

    }

    private boolean checkNeedToBan(LoginAttemptDto attemptDto, int attemptsBeforeBan, long periodBeforeBan) {
        int attemptsNumber = attemptDto.getAttemptsForPeriod(periodBeforeBan);
        return attemptsNumber >= attemptsBeforeBan;
    }

    @Override
    public void successfulProcessing(String ipAddress, IpTypesOfChecking ipTypesOfChecking) {
        ConcurrentMap<String, LoginAttemptDto> ipChecker = ipchecker.get(ipTypesOfChecking);
        ipChecker.remove(ipAddress);
    }
}
