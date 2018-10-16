package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.TransactionDao;
import me.exrates.model.User;
import me.exrates.model.enums.*;
import me.exrates.model.main.*;
import me.exrates.model.onlineTableDto.AccountStatementDto;
import me.exrates.util.BigDecimalProcessing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Log4j2
@Repository
public final class TransactionDaoImpl implements TransactionDao {

    private static final Logger LOGGER = LogManager.getLogger(TransactionDaoImpl.class);

    protected static RowMapper<Transaction> transactionRowMapper = (resultSet, i) -> {

        final OperationType operationType = OperationType.convert(resultSet.getInt("TRANSACTION.operation_type_id"));

        Currency currency = null;
        try {
            resultSet.findColumn("CURRENCY.id");
            currency = new Currency();
            currency.setId(resultSet.getInt("CURRENCY.id"));
            currency.setName(resultSet.getString("CURRENCY.name"));
            currency.setDescription(resultSet.getString("CURRENCY.description"));
        } catch (SQLException e) {
            //NOP
        }

        Merchant merchant = null;
        try {
            resultSet.findColumn("MERCHANT.id");
            if (resultSet.getObject("MERCHANT.id") != null) {
                merchant = new Merchant();
                merchant.setId(resultSet.getInt("MERCHANT.id"));
                merchant.setName(resultSet.getString("MERCHANT.name"));
                merchant.setDescription(resultSet.getString("MERCHANT.description"));
            }
        } catch (SQLException e) {
            //NOP
        }

        ExOrder order = null;
        try {
            resultSet.findColumn("EXORDERS.id");
            if (resultSet.getObject("EXORDERS.id") != null) {
                order = new ExOrder();
                order.setId(resultSet.getInt("EXORDERS.id"));
                order.setUserId(resultSet.getInt("EXORDERS.user_id"));
                order.setCurrencyPairId(resultSet.getInt("EXORDERS.currency_pair_id"));
                order.setOperationType(resultSet.getInt("EXORDERS.operation_type_id") == 0 ? null : OperationType.convert(resultSet.getInt("EXORDERS.operation_type_id")));
                order.setExRate(resultSet.getBigDecimal("EXORDERS.exrate"));
                order.setAmountBase(resultSet.getBigDecimal("EXORDERS.amount_base"));
                order.setAmountConvert(resultSet.getBigDecimal("EXORDERS.amount_convert"));
                order.setCommissionFixedAmount(resultSet.getBigDecimal("EXORDERS.commission_fixed_amount"));
                order.setDateCreation(resultSet.getTimestamp("EXORDERS.date_creation") == null ? null : resultSet.getTimestamp("EXORDERS.date_creation").toLocalDateTime());
                order.setDateAcception(resultSet.getTimestamp("EXORDERS.date_acception") == null ? null : resultSet.getTimestamp("EXORDERS.date_acception").toLocalDateTime());
            }
        } catch (SQLException e) {
            //NOP
        }

        WithdrawRequest withdraw = null;
        try {
            resultSet.findColumn("WITHDRAW_REQUEST.id");
            if (resultSet.getObject("WITHDRAW_REQUEST.id") != null) {
                withdraw = new WithdrawRequest();
                withdraw.setId(resultSet.getInt("WITHDRAW_REQUEST.id"));
                withdraw.setWallet(resultSet.getString("WITHDRAW_REQUEST.wallet"));
                withdraw.setDestinationTag(resultSet.getString("WITHDRAW_REQUEST.destination_tag"));
                withdraw.setUserId(resultSet.getInt("WITHDRAW_REQUEST.user_id"));
                withdraw.setRecipientBankName(resultSet.getString("WITHDRAW_REQUEST.recipient_bank_name"));
                withdraw.setRecipientBankCode(resultSet.getString("WITHDRAW_REQUEST.recipient_bank_code"));
                withdraw.setUserFullName(resultSet.getString("WITHDRAW_REQUEST.user_full_name"));
                withdraw.setRemark(resultSet.getString("WITHDRAW_REQUEST.remark"));
                withdraw.setAmount(resultSet.getBigDecimal("WITHDRAW_REQUEST.amount"));
                withdraw.setCommissionAmount(resultSet.getBigDecimal("WITHDRAW_REQUEST.commission"));
                withdraw.setCommissionId(resultSet.getInt("WITHDRAW_REQUEST.commission_id"));
                withdraw.setStatus(WithdrawStatusEnum.convert(resultSet.getInt("WITHDRAW_REQUEST.status_id")));
                withdraw.setDateCreation(resultSet.getTimestamp("WITHDRAW_REQUEST.date_creation").toLocalDateTime());
                withdraw.setStatusModificationDate(resultSet.getTimestamp("WITHDRAW_REQUEST.status_modification_date").toLocalDateTime());
                withdraw.setCurrency(currency);
                withdraw.setMerchant(merchant);
                withdraw.setAdminHolderId(resultSet.getInt("WITHDRAW_REQUEST.admin_holder_id"));
            }
        } catch (SQLException e) {
            //NOP
        }

        RefillRequest refill = null;
        try {
            resultSet.findColumn("REFILL_REQUEST.id");
            if (resultSet.getObject("REFILL_REQUEST.id") != null) {
                refill = new RefillRequest();
                refill.setId(resultSet.getInt("REFILL_REQUEST.id"));
                refill.setUserId(resultSet.getInt("REFILL_REQUEST.user_id"));
                refill.setRemark(resultSet.getString("REFILL_REQUEST.remark"));
                refill.setAmount(resultSet.getBigDecimal("REFILL_REQUEST.amount"));
                refill.setCommissionId(resultSet.getInt("REFILL_REQUEST.commission_id"));
                refill.setStatus(RefillStatusEnum.convert(resultSet.getInt("REFILL_REQUEST.status_id")));
                refill.setDateCreation(resultSet.getTimestamp("REFILL_REQUEST.date_creation").toLocalDateTime());
                refill.setStatusModificationDate(resultSet.getTimestamp("REFILL_REQUEST.status_modification_date").toLocalDateTime());
                refill.setCurrencyId(resultSet.getInt("REFILL_REQUEST.currency_id"));
                refill.setMerchantId(resultSet.getInt("REFILL_REQUEST.merchant_id"));
                refill.setMerchantTransactionId(resultSet.getString("REFILL_REQUEST.merchant_transaction_id"));
                refill.setRecipientBankName(resultSet.getString("INVOICE_BANK.name"));
                refill.setRecipientBankAccount(resultSet.getString("INVOICE_BANK.account_number"));
                refill.setRecipientBankRecipient(resultSet.getString("INVOICE_BANK.recipient"));
                refill.setAdminHolderId(resultSet.getInt("REFILL_REQUEST.admin_holder_id"));
                refill.setConfirmations(resultSet.getInt("confirmations"));
                /**/
                refill.setAddress(resultSet.getString("RRA.address"));
                /**/
                refill.setPayerBankName(resultSet.getString("RRP.payer_bank_name"));
                refill.setPayerBankCode(resultSet.getString("RRP.payer_bank_code"));
                refill.setPayerAccount(resultSet.getString("RRP.payer_account"));
                refill.setRecipientBankAccount(resultSet.getString("RRP.payer_account"));
                refill.setUserFullName(resultSet.getString("RRP.user_full_name"));
                refill.setReceiptScan(resultSet.getString("RRP.receipt_scan"));
                refill.setReceiptScanName(resultSet.getString("RRP.receipt_scan_name"));
                refill.setRecipientBankId(resultSet.getInt("RRP.recipient_bank_id"));
            }
        } catch (SQLException e) {
            //NOP
        }

        Commission commission = null;
        try {
            resultSet.findColumn("COMMISSION.id");
            commission = new Commission();
            commission.setId(resultSet.getInt("COMMISSION.id"));
            commission.setOperationType(operationType);
            commission.setValue(resultSet.getBigDecimal("COMMISSION.value"));
            commission.setDateOfChange(resultSet.getTimestamp("COMMISSION.date"));
        } catch (SQLException e) {
            //NOP
        }

        CompanyWallet companyWallet = null;
        try {
            resultSet.findColumn("COMPANY_WALLET.id");
            companyWallet = new CompanyWallet();
            companyWallet.setBalance(resultSet.getBigDecimal("COMPANY_WALLET.balance"));
            companyWallet.setCommissionBalance(resultSet.getBigDecimal("COMPANY_WALLET.commission_balance"));
            companyWallet.setCurrency(currency);
            companyWallet.setId(resultSet.getInt("COMPANY_WALLET.id"));
        } catch (SQLException e) {
            //NOP
        }

        Wallet userWallet = null;
        try {
            resultSet.findColumn("WALLET.id");
            userWallet = new Wallet();
            userWallet.setActiveBalance(resultSet.getBigDecimal("WALLET.active_balance"));
            userWallet.setReservedBalance(resultSet.getBigDecimal("WALLET.reserved_balance"));
            userWallet.setId(resultSet.getInt("WALLET.id"));
            userWallet.setCurrencyId(currency.getId());
            User user = new User();
            user.setId(resultSet.getInt("user_id"));
            user.setEmail(resultSet.getString("user_email"));
            userWallet.setUser(user);
        } catch (SQLException e) {
            //NOP
        }

        Transaction transaction = new Transaction();
        transaction.setId(resultSet.getInt("TRANSACTION.id"));
        transaction.setAmount(resultSet.getBigDecimal("TRANSACTION.amount"));
        transaction.setCommissionAmount(resultSet.getBigDecimal("TRANSACTION.commission_amount"));
        transaction.setDatetime(resultSet.getTimestamp("TRANSACTION.datetime").toLocalDateTime());
        transaction.setCommission(commission);
        transaction.setCompanyWallet(companyWallet);
        transaction.setUserWallet(userWallet);
        transaction.setOperationType(operationType);
        transaction.setMerchant(merchant);
        transaction.setOrder(order);
        transaction.setCurrency(currency);
        transaction.setWithdrawRequest(withdraw);
        transaction.setRefillRequest(refill);
        transaction.setProvided(resultSet.getBoolean("provided"));
        Integer confirmations = (Integer) resultSet.getObject("confirmation");
        transaction.setConfirmation(confirmations);
        TransactionSourceType sourceType = resultSet.getString("source_type") == null ?
                null : TransactionSourceType.convert(resultSet.getString("source_type"));
        transaction.setSourceType(sourceType);
        transaction.setSourceId(resultSet.getInt("source_id"));
        return transaction;
    };

    @Autowired
    MessageSource messageSource;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Transaction create(Transaction transaction) {
        final String sql = "INSERT INTO TRANSACTION (user_wallet_id, company_wallet_id, amount, commission_amount, " +
                " commission_id, operation_type_id, currency_id, merchant_id, datetime, order_id, confirmation, provided," +
                " active_balance_before, reserved_balance_before, company_balance_before, company_commission_balance_before, " +
                " source_type, " +
                " source_id, description)" +
                "   VALUES (:userWallet,:companyWallet,:amount,:commissionAmount,:commission,:operationType, :currency," +
                "   :merchant, :datetime, :order_id, :confirmation, :provided," +
                "   :active_balance_before, :reserved_balance_before, :company_balance_before, :company_commission_balance_before," +
                "   :source_type, " +
                "   :source_id, :description)";
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            final Map<String, Object> params = new HashMap<String, Object>() {
                {
                    put("userWallet", transaction.getUserWallet().getId());
                    put("companyWallet", transaction.getCompanyWallet() == null ? null : transaction.getCompanyWallet().getId());
                    put("amount", transaction.getAmount());
                    put("commissionAmount", transaction.getCommissionAmount());
                    put("commission", transaction.getCommission() == null ? null : transaction.getCommission().getId());
                    put("operationType", transaction.getOperationType().type);
                    put("currency", transaction.getCurrency().getId());
                    put("merchant", transaction.getMerchant() == null ? null : transaction.getMerchant().getId());
                    put("datetime", transaction.getDatetime() == null ? null : Timestamp.valueOf(transaction.getDatetime()));
                    put("order_id", transaction.getOrder() == null ? null : transaction.getOrder().getId());
                    put("confirmation", transaction.getConfirmation());
                    put("provided", transaction.isProvided());
                    put("active_balance_before", transaction.getActiveBalanceBefore());
                    put("reserved_balance_before", transaction.getReservedBalanceBefore());
                    put("company_balance_before", transaction.getCompanyBalanceBefore());
                    put("company_commission_balance_before", transaction.getCompanyCommissionBalanceBefore());
                    put("source_type", transaction.getSourceType() == null ? null : transaction.getSourceType().toString());
                    put("source_id", transaction.getSourceId());
                    put("description", transaction.getDescription());
                }
            };
            if (jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder) > 0) {
                transaction.setId(keyHolder.getKey().intValue());
                return transaction;
            }
        } catch (Exception e) {
            log.error("exception {}", e);
        }
        throw new RuntimeException("Transaction creating failed");
    }

    @Override
    public boolean delete(int id) {
        final String sql = "DELETE FROM TRANSACTION where id = :id";
        final Map<String, Integer> params = new HashMap<String, Integer>() {
            {
                put("id", id);
            }
        };
        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean setStatusById(Integer trasactionId, Integer statusId) {
        return false;
    }

    @Override
    public List<Transaction> getPayedRefTransactionsByOrderId(int orderId) {
        return null;
    }

    @Override
    public List<AccountStatementDto> getAccountStatement(Integer walletId, Integer offset, Integer limit, Locale locale) {
        String sql = " SELECT * " +
                "  FROM " +
                "  ( " +
                "    SELECT null AS date_time, null as transaction_id, " +
                "      WALLET.active_balance AS active_balance_before, WALLET.reserved_balance AS reserved_balance_before, " +
                "      CURRENCY.name AS operation_type_id, " +
                "      null AS amount, null AS commission_amount, " +
                "      null AS source_type, null AS source_id, " +
                "      null AS status_id, null AS merchant_name, null AS user_id" +
                "    FROM WALLET  " +
                "    JOIN CURRENCY ON CURRENCY.id=WALLET.currency_id  " +
                "    WHERE WALLET.id=:wallet_id " +
                "  UNION ALL " +
                "    (" +
                "    SELECT TRANSACTION.datetime, TRANSACTION.id, " +
                "      TRANSACTION.active_balance_before, TRANSACTION.reserved_balance_before, " +
                "      TRANSACTION.operation_type_id, " +
                "      TRANSACTION.amount, TRANSACTION.commission_amount, " +
                "      TRANSACTION.source_type, TRANSACTION.source_id, " +
                "      TRANSACTION.status_id, MERCHANT.name AS merchant_name, WALLET.user_id " +
                "    FROM TRANSACTION " +
                "    JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id " +
                "    LEFT JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id" +
                "    WHERE TRANSACTION.provided=1 AND TRANSACTION.user_wallet_id = :wallet_id " +
                "    ORDER BY -TRANSACTION.datetime ASC, -TRANSACTION.id ASC " +
                (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset) +
                "    )" +
                "  ) T " +
                "  ORDER BY -date_time ASC, -transaction_id ASC";
        final Map<String, Object> params = new HashMap<>();
        params.put("wallet_id", walletId);
        return jdbcTemplate.query(sql, params, new RowMapper<AccountStatementDto>() {
            @Override
            public AccountStatementDto mapRow(ResultSet rs, int i) throws SQLException {
                AccountStatementDto accountStatementDto = new AccountStatementDto();
                accountStatementDto.setDatetime(rs.getTimestamp("date_time") == null ? null : rs.getTimestamp("date_time").toLocalDateTime());
                accountStatementDto.setTransactionId(rs.getInt("transaction_id"));
                accountStatementDto.setActiveBalanceBefore(BigDecimalProcessing.formatLocale(rs.getBigDecimal("active_balance_before"), locale, true));
                accountStatementDto.setReservedBalanceBefore(BigDecimalProcessing.formatLocale(rs.getBigDecimal("reserved_balance_before"), locale, true));
                accountStatementDto.setOperationType(rs.getObject("date_time") == null ? rs.getString("operation_type_id") : OperationType.convert(rs.getInt("operation_type_id")).toString(messageSource, locale));
                accountStatementDto.setAmount(rs.getTimestamp("date_time") == null ? null : BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount"), locale, true));
                accountStatementDto.setCommissionAmount(rs.getTimestamp("date_time") == null ? null : BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_amount"), locale, true));
                TransactionSourceType transactionSourceType = rs.getObject("source_type") == null ? null : TransactionSourceType.convert(rs.getString("source_type"));
                accountStatementDto.setSourceType(transactionSourceType == null ? "" : transactionSourceType.toString(messageSource, locale));
                accountStatementDto.setSourceTypeId(rs.getString("source_type"));
                accountStatementDto.setSourceId(rs.getInt("source_id"));
                accountStatementDto.setTransactionStatus(rs.getObject("status_id") == null ? null : TransactionStatus.convert(rs.getInt("status_id")));
                /**/
                int otid = rs.getObject("date_time") == null ? 0 : rs.getInt("operation_type_id");
                if (otid != 0) {
                    OperationType ot = OperationType.convert(otid);
                    switch (ot) {
                        case INPUT: {
                            accountStatementDto.setActiveBalanceAfter(BigDecimalProcessing
                                    .formatLocale(BigDecimalProcessing
                                                    .doAction(rs.getBigDecimal("active_balance_before"), rs.getBigDecimal("amount"), ActionType.ADD)
                                            , locale, true));
                            accountStatementDto.setReservedBalanceAfter(accountStatementDto.getReservedBalanceBefore());
                            break;
                        }
                        case OUTPUT: {
                            accountStatementDto.setActiveBalanceAfter(BigDecimalProcessing
                                    .formatLocale(BigDecimalProcessing
                                                    .doAction(rs.getBigDecimal("active_balance_before"), rs.getBigDecimal("amount"), ActionType.SUBTRACT)
                                            , locale, true));
                            accountStatementDto.setReservedBalanceAfter(accountStatementDto.getReservedBalanceBefore());
                            break;
                        }
                        case WALLET_INNER_TRANSFER: {
                            accountStatementDto.setActiveBalanceAfter(BigDecimalProcessing
                                    .formatLocale(BigDecimalProcessing
                                                    .doAction(rs.getBigDecimal("active_balance_before"), rs.getBigDecimal("amount"), ActionType.ADD)
                                            , locale, true));
                            accountStatementDto.setReservedBalanceAfter(BigDecimalProcessing
                                    .formatLocale(BigDecimalProcessing
                                                    .doAction(rs.getBigDecimal("reserved_balance_before"), rs.getBigDecimal("amount"), ActionType.SUBTRACT)
                                            , locale, true));
                            break;
                        }
                        case MANUAL: {
                            accountStatementDto.setActiveBalanceAfter(BigDecimalProcessing
                                    .formatLocale(BigDecimalProcessing
                                                    .doAction(rs.getBigDecimal("active_balance_before"), rs.getBigDecimal("amount"), ActionType.ADD)
                                            , locale, true));
                            accountStatementDto.setReservedBalanceAfter(accountStatementDto.getReservedBalanceBefore());
                            break;
                        }
                    }
                }
                String merchantName = rs.getString("merchant_name");
                if (StringUtils.isEmpty(merchantName)) {
                    merchantName = accountStatementDto.getSourceType();
                }
                accountStatementDto.setMerchantName(merchantName);
                accountStatementDto.setWalletId(walletId);
                accountStatementDto.setUserId(rs.getInt("user_id"));
                /**/
                return accountStatementDto;
            }
        });

    }

    @Override
    public boolean updateForProvided(Transaction transaction) {
        final String sql = "UPDATE TRANSACTION " +
                " SET provided = :provided, " +
                "     active_balance_before = :active_balance_before, " +
                "     reserved_balance_before = :reserved_balance_before, " +
                "     company_balance_before = :company_balance_before, " +
                "     company_commission_balance_before = :company_commission_balance_before, " +
                "     source_type = :source_type, " +
                "     source_id = :source_id, " +
                "     provided_modification_date = NOW() " +
                " WHERE id = :id";
        final int PROVIDED = 1;
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("provided", PROVIDED);
                put("id", transaction.getId());
                put("active_balance_before", transaction.getActiveBalanceBefore());
                put("reserved_balance_before", transaction.getReservedBalanceBefore());
                put("company_balance_before", transaction.getCompanyBalanceBefore());
                put("company_commission_balance_before", transaction.getCompanyCommissionBalanceBefore());
                put("source_type", transaction.getSourceType().name());
                put("source_id", transaction.getSourceId());
            }
        };
        return jdbcTemplate.update(sql, params) > 0;
    }


}