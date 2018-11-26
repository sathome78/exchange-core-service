package me.exrates.dao;

public interface G2faDao {

    String getGoogleAuthSecretCodeByUser(Integer userId);

    void setGoogleAuthSecretCode(Integer userId);

    void setGoogleAuthSecretCode(Integer userId, String secretCode);

    void set2faGoogleAuthenticator(Integer userId);

    void setEnable2faGoogleAuth(Integer userId, Boolean connection);

    boolean isGoogleAuthenticatorEnable(Integer userId);
}
