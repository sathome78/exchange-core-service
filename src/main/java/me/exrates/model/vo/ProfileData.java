package me.exrates.model.vo;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by ValkSam
 */
@Getter
public class ProfileData {
  private static final Logger profileLog = LogManager.getLogger("profile");

  long threshold;
  long before;
  long time1;
  long time2;
  long time3;
  long time4;
  long time5;
  long time;

  public ProfileData(long threshold) {
    this.before = System.currentTimeMillis();
    this.threshold = threshold;
  }

  public void setTime1() {
    this.time1 = System.currentTimeMillis() - before;
  }

  public void setTime2() {
    this.time2 = System.currentTimeMillis() - before;
  }

  public void setTime3() {
    this.time3 = System.currentTimeMillis() - before;
  }

  public void setTime4() {
    this.time4 = System.currentTimeMillis() - before;
  }

  public void setTime5() {
    this.time5 = System.currentTimeMillis() - before;
  }

  public boolean isExceeded() {
    this.time = System.currentTimeMillis() - before;
    return  this.time > this.threshold;
  }

  public boolean checkAndLog(String logMessage){
    boolean isExceeded = isExceeded();
    if (isExceeded) {
      profileLog.warn(logMessage);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return "ProfileData{" +
        "time1=" + time1 +
        ", time2=" + time2 +
        ", time3=" + time3 +
        ", time4=" + time4 +
        ", time5=" + time5 +
        ", time=" + time +
        '}';
  }
}
