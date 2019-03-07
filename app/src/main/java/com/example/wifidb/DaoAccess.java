package com.example.wifidb;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
    public interface DaoAccess {
        @Query("SELECT * FROM Entries")
        List<Entry> fetchAllEntries();
        @Insert
        void insertEntry (Entry entry);
        @Query("SELECT * FROM Entries WHERE sno = :sno")
        Entry fetchEntry (int sno);
        @Delete
        void deleteEntry (Entry entry);
        @Query("DELETE FROM Entries WHERE sno = (SELECT MAX(sno) FROM Entries)")
        void deleteLastEntry();
        @Query(value = "SELECT MAX(sno) FROM Entries")
        int lastNum ();
    }

