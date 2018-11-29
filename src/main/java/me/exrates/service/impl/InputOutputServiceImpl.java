package me.exrates.service.impl;

import me.exrates.dao.InputOutputDao;
import me.exrates.model.dto.CurrencyInputOutputSummaryDto;
import me.exrates.model.enums.InvoiceActionTypeEnum;
import me.exrates.model.enums.InvoiceOperationPermission;
import me.exrates.model.enums.InvoiceStatus;
import me.exrates.model.enums.OperationType;
import me.exrates.model.main.CacheData;
import me.exrates.model.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.service.InputOutputService;
import me.exrates.util.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;


@Service
public class InputOutputServiceImpl implements InputOutputService {

    private static final Logger log = LogManager.getLogger("inputoutput");

    @Autowired
    private MessageSource messageSource;

    @Autowired
    InputOutputDao inputOutputDao;

    //TODO
//  @Autowired
//  private MerchantService merchantService;
//
//  @Autowired
//  MerchantServiceContext merchantServiceContext;

    @Override
    @Transactional(readOnly = true)
    public List<MyInputOutputHistoryDto> getMyInputOutputHistory(
            CacheData cacheData,
            String email,
            Integer offset, Integer limit,
            Locale locale) {
        List<Integer> operationTypeList = OperationType.getInputOutputOperationsList()
                .stream()
                .map(OperationType::getType)
                .collect(Collectors.toList());
        List<MyInputOutputHistoryDto> result = inputOutputDao.findMyInputOutputHistoryByOperationType(email, offset, limit, operationTypeList, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<MyInputOutputHistoryDto>() {{
                add(new MyInputOutputHistoryDto(false));
            }};
        } else {
            setAdditionalFields(result, locale);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyInputOutputHistoryDto> getMyInputOutputHistory(
            String email,
            Integer offset, Integer limit,
            String dateFrom,
            String dateTo,
            Locale locale,
            String currency) {
        List<Integer> operationTypeList = OperationType.getInputOutputOperationsList()
                .stream()
                .map(OperationType::getType)
                .collect(Collectors.toList());
        List<MyInputOutputHistoryDto> result = inputOutputDao.findMyInputOutputHistoryByOperationType(email, offset, limit, dateFrom, dateTo, operationTypeList, locale, currency);
        setAdditionalFields(result, locale);
        return result;
    }

    private void setAdditionalFields(List<MyInputOutputHistoryDto> inputOutputList, Locale locale) {
        inputOutputList.forEach(e ->
        {
            e.setSummaryStatus(generateAndGetSummaryStatus(e, locale));
            e.setButtons(generateAndGetButtonsSet(e.getStatus(), null, false, locale));
            e.setAuthorisedUserId(e.getUserId());
        });
    }

    public List<Map<String, Object>> generateAndGetButtonsSet(
            InvoiceStatus status,
            InvoiceOperationPermission permittedOperation,
            boolean authorisedUserIsHolder,
            Locale locale) {
        if (status == null) return EMPTY_LIST;
        InvoiceActionTypeEnum.InvoiceActionParamsValue paramsValue = InvoiceActionTypeEnum.InvoiceActionParamsValue.builder()
                .authorisedUserIsHolder(authorisedUserIsHolder)
                .permittedOperation(permittedOperation)
                .build();
        return status.getAvailableActionList(paramsValue).stream()
                .filter(e -> e.getActionTypeButton() != null)
                .map(e -> new HashMap<String, Object>(e.getActionTypeButton().getProperty()))
                .peek(e -> e.put("buttonTitle", messageSource.getMessage((String) e.get("buttonTitle"), null, locale)))
                .collect(Collectors.toList());
    }

    //TODO
    private String generateAndGetSummaryStatus(MyInputOutputHistoryDto row, Locale locale) {
//    log.debug("status1 {}", row);
//    switch (row.getSourceType()) {
//      case REFILL: {
//        RefillStatusEnum status = (RefillStatusEnum) row.getStatus();
//        if (status == ON_BCH_EXAM) {
//          IRefillable merchant = (IRefillable) merchantServiceContext
//                  .getMerchantService(merchantService.findByName(row.getMerchantName()).getServiceBeanName());
//          String message;
//          Integer confirmationsCount = merchant.minConfirmationsRefill();
//          if (confirmationsCount == null) {
//            message = messageSource.getMessage("merchants.refill.TAKEN_FROM_EXAM", null, locale);
//          } else {
//            String confirmations = row.getConfirmation() == null ? "0" : row.getConfirmation().toString();
//            message = confirmations.concat("/").concat(confirmationsCount.toString());
//          }
//          return message;
//        } else {
//          return messageSource.getMessage("merchants.refill.".concat(status.name()), null, locale);
//        }
//      }
//      case WITHDRAW: {
//        WithdrawStatusEnum status = (WithdrawStatusEnum) row.getStatus();
//        return messageSource.getMessage("merchants.withdraw.".concat(status.name()), null, locale);
//      }
//      case USER_TRANSFER: {
//        TransferStatusEnum status = (TransferStatusEnum) row.getStatus();
//        return messageSource.getMessage("merchants.transfer.".concat(status.name()), null, locale);
//      }
//      default: {
//        return row.getTransactionProvided();
//      }
//    }
        return "";
    }

    public List<CurrencyInputOutputSummaryDto> getInputOutputSummary(LocalDateTime startTime, LocalDateTime endTime,
                                                                     List<Integer> userRoleIdList) {
        return inputOutputDao.getInputOutputSummary(startTime, endTime, userRoleIdList);
    }

}
