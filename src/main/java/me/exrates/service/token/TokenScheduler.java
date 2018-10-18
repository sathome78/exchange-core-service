package me.exrates.service.token;

import me.exrates.exception.UnRegisteredUserDeleteException;
import me.exrates.model.dto.TemporalToken;
import me.exrates.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TokenScheduler {
    private static final Logger LOGGER = LogManager.getLogger(TokenScheduler.class);
    public static final String TRIGGER_GROUP = "token";
    public static final Integer TOKEN_LIFE_TIME_DAYS = 1;

    private Scheduler scheduler = null;
    private static TokenScheduler tokenScheduler = null;

    public static TokenScheduler getTokenScheduler() {
        return tokenScheduler;
    }

    @Autowired
    protected UserService userService;

    @PostConstruct
    private void init() {
        try {
            TokenScheduler.tokenScheduler = this;
            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            LOGGER.debug("TokenScheduler is started ");
        } catch (SchedulerException e) {
            LOGGER.error("error while TokenScheduler init " + e.getLocalizedMessage());
        }
    }

    @PreDestroy
    private void destroy() {
        try {
            scheduler.shutdown(true);
            LOGGER.debug("TokenScheduler is stoped");
        } catch (SchedulerException e) {
            LOGGER.error("error while TokenScheduler destroy " + e.getLocalizedMessage());
        }
    }

    private List<TemporalToken> tokens = new ArrayList<>();

    /**
     * collects all tokens and schedules job for it deleting when it expires
     */
    public List<JobKey> initTrigers() {
        List<JobKey> jobsInQueue = getAllJobKeys();
        Integer jobsInQueueCount = jobsInQueue == null ? 0 : jobsInQueue.size();
        tokens = userService.getAllTokens();
        try {
            for (TemporalToken token : tokens) {
                Trigger trigger = scheduler.getTrigger(new TriggerKey(String.valueOf(token.getId()), TRIGGER_GROUP));
                if (trigger == null) {
                    Date startDate = Date.from(token.getDateCreation()
                            .plusDays(TOKEN_LIFE_TIME_DAYS).atZone(ZoneId.systemDefault()).toInstant());
                    trigger = TriggerBuilder.newTrigger()
                            .withIdentity(String.valueOf(token.getId()), TRIGGER_GROUP)
                            .startAt(startDate)
                            .withSchedule(
                                    SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow()
                            )
                            .build();
                    JobDetail job = JobBuilder.newJob(ClearTokenJob.class)
                            .withIdentity(String.valueOf(token.getId() + ":" + token.getUserId() + "::" + token.getTokenType().getTokenType()), TRIGGER_GROUP)
                            .usingJobData("tokenId", token.getId())
                            .usingJobData("tokenValue", token.getValue())
                            .usingJobData("tokenUser", token.getUserId())
                            .usingJobData("tokenDateCreation", token.getDateCreation().toString())
                            .build();

                    scheduler.scheduleJob(job, trigger);
                }
            }
            LOGGER.debug(String.format("expired token scheduler: queued %s job(s)" + "\n" + "  in queue now %s jobs : %s",
                    (scheduler.getJobKeys(GroupMatcher.jobGroupEquals(TRIGGER_GROUP)).size() - jobsInQueueCount),
                    scheduler.getJobKeys(GroupMatcher.jobGroupEquals(TRIGGER_GROUP)).size(),
                    getAllJobKeys()));
        } catch (SchedulerException e) {
            LOGGER.error("error while token clean triggers init " + e.getLocalizedMessage());
        }
        return getAllJobKeys();
    }

    public List<JobKey> reInitTriggers() {
        LOGGER.error("expired token scheduler: start re init ");
        try {
            scheduler.unscheduleJobs(scheduler.getTriggerKeys(GroupMatcher.groupEquals(TRIGGER_GROUP)).stream().collect(Collectors.toList()));
        } catch (SchedulerException e) {
            LOGGER.error("error while token clean triggers re init " + e.getLocalizedMessage());
        }
        return initTrigers();
    }

    public List<JobKey> getAllJobKeys() {
        List<JobKey> jobs = null;
        try {
            jobs = scheduler.getJobKeys(GroupMatcher.groupEquals(TRIGGER_GROUP))
                    .stream()
                    .collect(Collectors.toList());
        } catch (SchedulerException e) {
            LOGGER.error("error while token jobs list retrieving: " + e.getLocalizedMessage());
            jobs = null;
        }
        return jobs;
    }

    /**
     * deletes all jobs related with token through userId and token type
     */
    public List<JobKey> deleteJobsRelatedWithToken(TemporalToken token) {
        List<JobKey> jobs = null;
        try {
            jobs = scheduler.getJobKeys(GroupMatcher.groupEquals(TRIGGER_GROUP))
                    .stream()
                    .filter(e -> e.toString().contains(token.getUserId() + "::" + token.getTokenType().getTokenType()))
                    .collect(Collectors.toList());
            if (scheduler.deleteJobs(jobs)) {
                LOGGER.debug(String.format("removed %s jobs in the token tracking scheduler : %s" + "\n" + "  in queue now %s jobs remain",
                        jobs.size(), jobs, scheduler.getJobKeys(GroupMatcher.jobGroupEquals(TRIGGER_GROUP)).size()));
            }
        } catch (SchedulerException e) {
            LOGGER.error("error while token jobs deleting: " + e.getLocalizedMessage());
            jobs = null;
        }
        return jobs;
    }

    protected boolean deleteExpiredToken(String token) throws UnRegisteredUserDeleteException {
        return userService.deleteExpiredToken(token);
    }

    @PreDestroy
    public void shutdown() {
        if (scheduler != null) {
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
                LOGGER.error(e);
            }
        }
    }
}
