package me.exrates.model.onlineTableDto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.util.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * Created by Valk on 19.04.16.
 */
public class OrderWideListDto extends OnlineTableDto {
    private int id;
    private int userId;
    private String operationType;
    private OperationType operationTypeEnum;
    private String stopRate; /*for stop orders*/
    private String exExchangeRate;
    private String amountBase;
    private String amountConvert;
    private int comissionId;
    private String commissionFixedAmount;
    private String amountWithCommission;
    private int userAcceptorId;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateCreation;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateAcception;
    private OrderStatus status;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateStatusModification;
    private String commissionAmountForAcceptor;
    private String amountWithCommissionForAcceptor;
    /**/
    private int currencyPairId;
    private String currencyPairName;
    private String statusString;
    private OrderBaseType orderBaseType;


    /*constructors*/

    public OrderWideListDto() {
        this.needRefresh = true;
    }

    public OrderWideListDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    /*hash*/

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (dateAcception != null ? dateAcception.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
    /*getters setters*/

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getExExchangeRate() {
        return exExchangeRate;
    }

    public void setExExchangeRate(String exExchangeRate) {
        this.exExchangeRate = exExchangeRate;
    }

    public String getAmountBase() {
        return amountBase;
    }

    public void setAmountBase(String amountBase) {
        this.amountBase = amountBase;
    }

    public String getAmountConvert() {
        return amountConvert;
    }

    public void setAmountConvert(String amountConvert) {
        this.amountConvert = amountConvert;
    }

    public int getComissionId() {
        return comissionId;
    }

    public void setComissionId(int comissionId) {
        this.comissionId = comissionId;
    }

    public String getCommissionFixedAmount() {
        return commissionFixedAmount;
    }

    public void setCommissionFixedAmount(String commissionFixedAmount) {
        this.commissionFixedAmount = commissionFixedAmount;
    }

    public String getAmountWithCommission() {
        return amountWithCommission;
    }

    public void setAmountWithCommission(String amountWithCommission) {
        this.amountWithCommission = amountWithCommission;
    }

    public int getUserAcceptorId() {
        return userAcceptorId;
    }

    public void setUserAcceptorId(int userAcceptorId) {
        this.userAcceptorId = userAcceptorId;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateAcception() {
        return dateAcception;
    }

    public void setDateAcception(LocalDateTime dateAcception) {
        this.dateAcception = dateAcception;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getDateStatusModification() {
        return dateStatusModification;
    }

    public void setDateStatusModification(LocalDateTime dateStatusModification) {
        this.dateStatusModification = dateStatusModification;
    }

    public String getCommissionAmountForAcceptor() {
        return commissionAmountForAcceptor;
    }

    public void setCommissionAmountForAcceptor(String commissionAmountForAcceptor) {
        this.commissionAmountForAcceptor = commissionAmountForAcceptor;
    }

    public String getAmountWithCommissionForAcceptor() {
        return amountWithCommissionForAcceptor;
    }

    public void setAmountWithCommissionForAcceptor(String amountWithCommissionForAcceptor) {
        this.amountWithCommissionForAcceptor = amountWithCommissionForAcceptor;
    }

    public String getCurrencyPairName() {
        return currencyPairName;
    }

    public void setCurrencyPairName(String currencyPairName) {
        this.currencyPairName = currencyPairName;
    }

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }

    public int getCurrencyPairId() {
        return currencyPairId;
    }

    public void setCurrencyPairId(int currencyPairId) {
        this.currencyPairId = currencyPairId;
    }

    public String getStopRate() {
        return stopRate;
    }

    public void setStopRate(String stopRate) {
        this.stopRate = stopRate;
    }

    public OrderBaseType getOrderBaseType() {
        return orderBaseType;
    }

    public void setOrderBaseType(OrderBaseType orderBaseType) {
        this.orderBaseType = orderBaseType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public OperationType getOperationTypeEnum() {
        return operationTypeEnum;
    }

    public void setOperationTypeEnum(OperationType operationTypeEnum) {
        this.operationTypeEnum = operationTypeEnum;
    }

    public String getOperationType() {
        return operationType;
    }
}
