package com.cusufcan.javakursprojeodevi.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.cusufcan.javakursprojeodevi.model.Art;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ArtDao {
    @Query("SELECT id, name FROM Art")
    Flowable<List<Art>> getArtsOnlyIdAndName();
    
    @Query("SELECT * FROM Art WHERE id = :id")
    Flowable<Art> getArtsById(int id);
    
    @Insert
    Completable insert(Art art);
    
    @Delete
    Completable delete(Art art);
}
