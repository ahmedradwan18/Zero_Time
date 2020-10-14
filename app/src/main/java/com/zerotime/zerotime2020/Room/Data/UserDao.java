package com.zerotime.zerotime2020.Room.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.zerotime.zerotime2020.Room.Model.Complaint;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public
interface UserDao {


    @Insert
    Completable insertComplaint(Complaint complaint);

    @Delete
    void delete(Complaint complaint);

    @Delete
    void deleteAll(List<Complaint> complaint);

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("Drop TABLE Complaint");
        }
    };

    @Query("SELECT * FROM Complaint")
    Single<List<Complaint>> getComplaints();

    @Query("SELECT COUNT(date) FROM Complaint")
    int getCount();

    @Query("DELETE FROM Complaint")
    public void nukeTable();


}
