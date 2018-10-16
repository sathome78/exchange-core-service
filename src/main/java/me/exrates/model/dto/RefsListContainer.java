package me.exrates.model.dto;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * Created by maks on 10.04.2017.
 */
@Log4j2
@Data
public class RefsListContainer {

    private List<ReferralInfoDto> referralInfoDtos;
    private int onPage;
    private int currentPage;
    private int currentLevel;
    private int totalSize;
    private int totalPages;
    List<ReferralProfitDto> referralProfitDtos;

    public RefsListContainer(List<ReferralInfoDto> referralInfoDtos, int onPage, int currentPage, int totalSize) {
        this.referralInfoDtos = referralInfoDtos;
        this.onPage = onPage;
        this.currentPage = currentPage;
        this.totalSize = totalSize;
        this.totalPages = countTotalPages();
    }

    public RefsListContainer(List<ReferralInfoDto> referralInfoDtos, int level) {
        this.referralInfoDtos = referralInfoDtos;
        this.currentLevel = level;
        totalPages = 1;
    }

    public RefsListContainer(List<ReferralInfoDto> referralInfoDtos) {
        this.referralInfoDtos = referralInfoDtos;
    }

    private int countTotalPages() {
        log.warn("Refs {} {}", onPage, totalSize);
        if (onPage < 0 || totalSize <= 0) {
            return 1;
        }
        return (int) Math.ceil((double) totalSize / onPage);
    }
}
