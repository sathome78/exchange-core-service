package me.exrates.dao;

import java.util.List;

public interface PhraseTemplateDao {
    List<String> findByTopic(Integer topicId);
}
