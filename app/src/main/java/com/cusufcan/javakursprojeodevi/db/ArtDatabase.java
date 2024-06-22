package com.cusufcan.javakursprojeodevi.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.cusufcan.javakursprojeodevi.model.Art;

@Database(entities = {Art.class}, version = 1)
public abstract class ArtDatabase extends RoomDatabase {
    public abstract ArtDao artDao();
}
