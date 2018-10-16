package me.exrates.service.token;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.impl.matchers.GroupMatcher;

/**
 * Created by Valk on 08.04.16.
 */

public class ClearTokenJob implements Job {
    private static final Logger LOGGER = LogManager.getLogger(TokenScheduler.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobParams = jobExecutionContext
                .getJobDetail()
                .getJobDataMap();
        String message;
        String tokenString = "tokenId: " + jobParams.get("tokenId")
                + " userId: " + jobParams.get("tokenUser")
                + " : " + jobParams.get("tokenDateCreation");

        try {
            if (false/*TokenScheduler.getTokenScheduler().deleteExpiredToken(jobParams.getString("tokenValue"))*/ /*todo repair fk constrins for users delete job*/) {
                message = String.format("the expired token was deleted: %s" + "\n" + "  in queue now %s jobs remain",
                        tokenString, jobExecutionContext.getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(TokenScheduler.TRIGGER_GROUP)).size() - 1);
            } else {
                message = String.format("the expired token was NOT deleted: %s" + "\n" + "  in queue now %s jobs remain",
                        tokenString, jobExecutionContext.getScheduler().getJobKeys(GroupMatcher.jobGroupEquals(TokenScheduler.TRIGGER_GROUP)).size() - 1);
            }
            LOGGER.debug(message);
        } catch (Exception e) {
            LOGGER.error(String.format("error while job executing: %s %s", tokenString, e.getLocalizedMessage()));
        }
    }
}
