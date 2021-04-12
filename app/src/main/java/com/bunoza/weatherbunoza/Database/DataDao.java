package com.bunoza.weatherbunoza.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DataDao {
    @Query("SELECT * FROM data")
    List<Data> getAll();

    @Query("SELECT * FROM data WHERE id LIKE 0")
    Data getFirst();

    @Query("SELECT * FROM data WHERE id IN (:userIds)")
    List<Data> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM data WHERE city LIKE :first")
    Data findByName(String first);

    @Insert
    void insert(Data data);

    @Delete
    void delete(Data data);

    @Query("SELECT COUNT(*) FROM data")
    int getElementCount();
}