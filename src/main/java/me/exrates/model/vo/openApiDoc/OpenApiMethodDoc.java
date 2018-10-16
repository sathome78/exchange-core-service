package me.exrates.model.vo.openApiDoc;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.exrates.model.vo.openApiDoc.OpenApiMethodGroup.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Getter
@ToString
public enum OpenApiMethodDoc {
    TICKER( PUBLIC,
            "apiDoc.public.ticker", GET,
            "/public/ticker",
            Collections.singletonList(new OpenApiDocParamItem("currency_pair", "apiDoc.public.ticker.cpName", true)),

            Arrays.asList(
                    new OpenApiDocResponseItem("id", "apiDoc.public.ticker.id"),
                    new OpenApiDocResponseItem("name", "apiDoc.public.ticker.cpName"),
                    new OpenApiDocResponseItem("last", "apiDoc.public.ticker.last"),
                    new OpenApiDocResponseItem("lowestAsk", "apiDoc.public.ticker.lowestAsk"),
                    new OpenApiDocResponseItem("highestBid", "apiDoc.public.ticker.highestBid"),
                    new OpenApiDocResponseItem("percentChange", "apiDoc.public.ticker.percentChange"),
                    new OpenApiDocResponseItem("baseVolume", "apiDoc.public.ticker.baseVolume"),
                    new OpenApiDocResponseItem("quoteVolume", "apiDoc.public.ticker.quoteVolume"),
                    new OpenApiDocResponseItem("high", "apiDoc.public.ticker.high"),
                    new OpenApiDocResponseItem("low", "apiDoc.public.ticker.low")
            )),

    ORDER_BOOK(PUBLIC,
            "apiDoc.public.orderBook", GET,
            "/public/orderbook/{currency_pair}",
            Collections.singletonList(new OpenApiDocParamItem("order_type", "apiDoc.public.orderType", true)), Arrays.asList(new OpenApiDocResponseItem("amount", "apiDoc.public.amount"),
                    new OpenApiDocResponseItem("rate", "apiDoc.public.orderbook.rate"))),

    HISTORY(PUBLIC,
            "apiDoc.public.history", GET,
            "/public/history/{currency_pair}",
            Collections.singletonList(new OpenApiDocParamItem("period", "apiDoc.public.history.period", true)), Arrays.asList(new OpenApiDocResponseItem("order_id", "apiDoc.public.orderId"),
                    new OpenApiDocResponseItem("amount", "apiDoc.public.amount"),
                    new OpenApiDocResponseItem("price", "apiDoc.public.orderbook.rate"),
                    new OpenApiDocResponseItem("total", "apiDoc.public.history.total"),
                    new OpenApiDocResponseItem("order_type", "apiDoc.public.orderType"),
                    new OpenApiDocResponseItem("date_acceptance", "apiDoc.public.history.dateAcceptance"))),

    CURRENCY_PAIRS(PUBLIC,
            "apiDoc.public.currencyPairs", GET,
            "/public/currency_pairs",
            Collections.emptyList(), Arrays.asList( new OpenApiDocResponseItem("name", "apiDoc.public.ticker.cpName"),
                    new OpenApiDocResponseItem("url_symbol", "apiDoc.public.currencyPairs.urlSymbol"))),


    BALANCES(USER_INFO, "apiDoc.userInfo.balances", GET,
            "/user/balances",
            Collections.emptyList(), Arrays.asList(new OpenApiDocResponseItem("currency_name", "apiDoc.userInfo.balances.currencyName"),
            new OpenApiDocResponseItem("active_balance", "apiDoc.userInfo.balances.activeBalance"),
            new OpenApiDocResponseItem("reserved_balance", "apiDoc.userInfo.balances.reservedBalance"))),

    USER_OPEN_ORDERS(USER_INFO, "apiDoc.userInfo.openOrders", GET,
            "/user/orders/open",
            Collections.singletonList(new OpenApiDocParamItem("currency_pair", "apiDoc.userInfo.orders.currencyPair", true)), Arrays.asList(new OpenApiDocResponseItem("order_id", "apiDoc.userInfo.orders.id"),
                    new OpenApiDocResponseItem("currency_pair", "apiDoc.userInfo.orders.currencyPair"),
                    new OpenApiDocResponseItem("amount", "apiDoc.userInfo.orders.amount"),
                    new OpenApiDocResponseItem("order_type", "apiDoc.userInfo.orders.orderType"),
                    new OpenApiDocResponseItem("price", "apiDoc.userInfo.orders.price"),
                    new OpenApiDocResponseItem("date_created", "apiDoc.userInfo.orders.dateCreated"))),

    USER_CLOSED_ORDERS(USER_INFO, "apiDoc.userInfo.closedOrders", GET,
            "/user/orders/closed",
            Arrays.asList(new OpenApiDocParamItem("currency_pair", "apiDoc.userInfo.orders.currencyPair", true),
                    new OpenApiDocParamItem("limit", "apiDoc.userInfo.orders.limit", true),
                    new OpenApiDocParamItem("offset", "apiDoc.userInfo.orders.offset", true)), Arrays.asList(new OpenApiDocResponseItem("order_id", "apiDoc.userInfo.orders.id"),
                    new OpenApiDocResponseItem("currency_pair", "apiDoc.userInfo.orders.currencyPair"),
                    new OpenApiDocResponseItem("amount", "apiDoc.userInfo.orders.amount"),
                    new OpenApiDocResponseItem("order_type", "apiDoc.userInfo.orders.orderType"),
                    new OpenApiDocResponseItem("price", "apiDoc.userInfo.orders.price"),
                    new OpenApiDocResponseItem("date_created", "apiDoc.userInfo.orders.dateCreated"),
                    new OpenApiDocResponseItem("date_accepted", "apiDoc.userInfo.orders.dateAccepted"))),

    COMMISSIONS(USER_INFO, "apiDoc.userInfo.commissions", GET,
            "/user/commissions",
            Collections.emptyList(), Arrays.asList(new OpenApiDocResponseItem("input", "apiDoc.userInfo.commissions.input"),
            new OpenApiDocResponseItem("output", "apiDoc.userInfo.commissions.output"),
            new OpenApiDocResponseItem("sell", "apiDoc.userInfo.commissions.sell"),
            new OpenApiDocResponseItem("buy", "apiDoc.userInfo.commissions.buy"),
            new OpenApiDocResponseItem("transfer", "apiDoc.userInfo.commissions.transfer"))),

    OPEN_ORDERS(ORDERS, "apiDoc.orders.open", GET,
            "/orders/open/{order_type}",
            Arrays.asList(new OpenApiDocParamItem("order_type", "apiDoc.userInfo.orders.orderType", false),
                    new OpenApiDocParamItem("currency_pair", "apiDoc.userInfo.orders.currencyPair", false)), Arrays.asList(new OpenApiDocResponseItem("id", "apiDoc.userInfo.orders.id"),
                    new OpenApiDocResponseItem("amount", "apiDoc.userInfo.orders.amount"),
                    new OpenApiDocResponseItem("price", "apiDoc.userInfo.orders.price"),
                    new OpenApiDocResponseItem("order_type", "apiDoc.userInfo.orders.orderType"))),

    CREATE_ORDER(ORDERS, "apiDoc.orders.create", POST,
            "/orders/create",
            Arrays.asList(new OpenApiDocParamItem("currency_pair", "apiDoc.userInfo.orders.currencyPair", false),
                    new OpenApiDocParamItem("order_type", "apiDoc.userInfo.orders.orderType", false),
                    new OpenApiDocParamItem("amount", "apiDoc.userInfo.orders.amount", false),
                    new OpenApiDocParamItem("price", "apiDoc.userInfo.orders.price", false)), Arrays.asList(new OpenApiDocResponseItem("created_order_id", "apiDoc.orders.create.createdorderId"),
                    new OpenApiDocResponseItem("auto_accepted_quantity", "apiDoc.orders.create.autoAcceptedQuantity"),
                    new OpenApiDocResponseItem("partially_accepted_amount", "apiDoc.orders.create.partiallyAcceptedAmount"))),

    CANCEL_ORDER(ORDERS, "apiDoc.orders.cancel", POST,
            "/orders/cancel",
            Collections.singletonList(new OpenApiDocParamItem("order_id", "apiDoc.orders.cancel.orderId", false)), Collections.singletonList(new OpenApiDocResponseItem("success", "apiDoc.orders.success"))),

    ACCEPT_ORDER(ORDERS, "apiDoc.orders.accept", POST,
            "/orders/accept",
            Collections.singletonList(new OpenApiDocParamItem("order_id", "apiDoc.orders.accept.orderId", false)), Collections.singletonList(new OpenApiDocResponseItem("success", "apiDoc.orders.success")))

    ;

    private OpenApiMethodGroup methodGroup;
    private String nameCode;
    private HttpMethod httpMethod;
    private String relativeUrl;
    private List<OpenApiDocParamItem> requestParams;
    private String responseDescriptionCode;
    private List<OpenApiDocResponseItem> responseFields;

    OpenApiMethodDoc(OpenApiMethodGroup methodGroup, String nameCode, HttpMethod httpMethod, String relativeUrl, List<OpenApiDocParamItem> requestParams, String responseDescriptionCode, List<OpenApiDocResponseItem> responseFields) {
        this.methodGroup = methodGroup;
        this.nameCode = nameCode;
        this.httpMethod = httpMethod;
        this.relativeUrl = relativeUrl;
        this.requestParams = requestParams;
        this.responseDescriptionCode = responseDescriptionCode;
        this.responseFields = responseFields;
    }

    OpenApiMethodDoc(OpenApiMethodGroup methodGroup, String nameCode, HttpMethod httpMethod, String relativeUrl, List<OpenApiDocParamItem> requestParams, List<OpenApiDocResponseItem> responseFields) {
        this(methodGroup, nameCode, httpMethod, relativeUrl, requestParams, nameCode.concat(".response"), responseFields);
    }
}
