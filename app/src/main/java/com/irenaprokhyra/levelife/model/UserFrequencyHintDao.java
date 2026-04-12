package com.irenaprokhyra.levelife.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserFrequencyHintDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(UserFrequencyHint hint);

    @Query("DELETE FROM user_frequency_hints WHERE user_id = :userId")
    void deleteAllForUser(int userId);
}
