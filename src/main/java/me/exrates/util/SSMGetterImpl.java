package me.exrates.util;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class SSMGetterImpl implements SSMGetter {


    @Autowired
    AWSSimpleSystemsManagement awsSimpleSystemsManagement;

    private static Logger logger = LoggerFactory.getLogger(SSMGetterImpl.class);

    private LoadingCache<String, String> customerCache = CacheBuilder.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<String, String>() {
                       @Override
                       public String load(String id) {
                           logger.info("Lookup ssm");
                           return lookupSSM(id);
                       }
                   }
            );

    public String lookup(String password) {
        try {
            return customerCache.get(password);
        } catch (ExecutionException e) {
            logger.error(MessageFormat.format("Ssm is missing value, Error message {0}", e.getMessage()), e);
            throw new RuntimeException(e);
        }
    }

    private String lookupSSM(String key) {
        logger.info("Getting value from ssm for key" + key);

        GetParameterRequest getParameterRequest = new GetParameterRequest().
                withName(key).
                withWithDecryption(Boolean.TRUE);

        try {
            return awsSimpleSystemsManagement.getParameter(getParameterRequest).getParameter().getValue();
        } catch (RuntimeException e) {
            logger.error(MessageFormat.format("SSM is missing value for key {0}", key));
            logger.error(MessageFormat.format("error message {0}", e.getMessage()), e);
            throw e;
        }
    }

}
