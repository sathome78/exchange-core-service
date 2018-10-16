package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.UserRole;

import static me.exrates.model.enums.UserRole.BOT_TRADER;

@AllArgsConstructor
public class OrderRoleInfoForDelete {
    private OrderStatus status;
    private UserRole creatorRole;
    private UserRole acceptorRole;
    private int transactionsCount;

    public boolean mayDeleteWithoutProcessingTransactions() {
        return status == OrderStatus.CLOSED && creatorRole == BOT_TRADER && acceptorRole == BOT_TRADER && transactionsCount == 0;
    }
}
