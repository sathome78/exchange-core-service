package me.exrates.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.IpBanStatus;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.LinkedList;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class LoginAttemptDto {
    private String ip;
    private Deque<LocalDateTime> attempts;
    private boolean wasInShortBan = false;
    private IpBanStatus status = IpBanStatus.ALLOW;

    {
        attempts = new LinkedList<>();
        addNewAttempt();
    }

    public LoginAttemptDto(String ip) {
        this.ip = ip;
    }

    public int getAttemptsForPeriod(long periodInSeconds) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusSeconds(periodInSeconds);
        return (int) attempts.stream().filter(attempt -> attempt.equals(end) || attempt.isAfter(start) && attempt.isBefore(end)).count();
    }

    public void addNewAttempt() {
        attempts.offer(LocalDateTime.now());
    }

    public LocalDateTime getLastAttemptTime() {
        return attempts.peekLast();
    }

}
