package com.zerotime.zerotime2020.Room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.zerotime.zerotime2020.Room.Data.UserDao;
import com.zerotime.zerotime2020.Room.Model.Complaint;

@Database(entities = {Complaint.class},version = 1,exportSchema = false)
public abstract class UserDataBase extends RoomDatabase {
public abstract UserDao getUserDao();









}
