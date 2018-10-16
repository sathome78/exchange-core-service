package me.exrates.model.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ValkSam
 */
public enum InvoiceActionTypeButtonEnum {
    CONFIRM_USER_BUTTON {{
        getProperty().put("tableIdListOnly", new String[]{"inputoutput-table", "unconfirmed-refills-table"});
    }},
    CONFIRM_ADMIN_BUTTON {{
        getProperty().put("tableIdListOnly", new String[]{"withdrawalTable"});
    }},
    REVOKE_BUTTON {{
        getProperty().put("tableIdListOnly", new String[]{"inputoutput-table", "transfer-table", "unconfirmed-refills-table"});
    }},
    SHOW_CODE_BUTTON {{
        getProperty().put("tableIdListOnly", new String[]{"inputoutput-table"});
    }},
    REVOKE_ADMIN_BUTTON {{
        getProperty().put("tableIdListOnly", new String[]{"voucherTable"});
    }},
    ACCEPT_BUTTON {{
        getProperty().put("tableIdListOnly", new String[]{"withdrawalTable", "refillTable"});
    }},
    DECLINE_BUTTON {{
        getProperty().put("tableIdListOnly", new String[]{"withdrawalTable"});
    }},
    DECLINE_HOLDED_BUTTON {{
        getProperty().put("tableIdListOnly", new String[]{"withdrawalTable", "refillTable"});
    }},
    POST_BUTTON {{
        getProperty().put("tableIdListOnly", new String[]{"withdrawalTable"});
    }},
    POST_HOLDED_BUTTON {{
        getProperty().put("tableIdListOnly", new String[]{"withdrawalTable"});
    }},
    ACCEPT_HOLDED_BUTTON {{
        getProperty().put("tableIdListOnly", new String[]{"refillTable"});
    }},
    TAKE_TO_WORK_BUTTON {{
        getProperty().put("tableIdListOnly", new String[]{"withdrawalTable", "refillTable"});
    }},
    RETURN_FROM_WORK_BUTTON {{
        getProperty().put("tableIdListOnly", new String[]{"withdrawalTable", "refillTable"});
    }};

    private Map<String, Object> property = new HashMap<>();

    InvoiceActionTypeButtonEnum() {
        property.put("buttonId", this.name().toLowerCase());
        property.put("buttonTitle", "action.button.".concat(this.name()));
        property.put("tableIdListOnly", new String[]{});
    }

    public Map<String, Object> getProperty() {
        return property;
    }
}
