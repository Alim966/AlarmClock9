package com.example.alarmclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseHelper(context);
        return instance;
    }
    //Инициализация DatabaseHelper

    private DatabaseHelper(@Nullable Context context) {
        super(context, "db_alarm_note", null, 1);
    }
    //Настройка бд

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "create table notes (id integer primary key autoincrement, hour integer, minute integer, message text, state integer)";
        db.execSQL(create);
    }
    //Создание таблицы

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists notes");
        onCreate(db);
    }//Вызывается при обновлении бд

    public void insertNote(Note note){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hour", note.getHour());
        values.put("minute", note.getMinute());
        values.put("message", note.getMessage());
        values.put("state", note.isState() ? 1 : 0);
        database.insert("notes", null, values);
        database.close();
    }
    //Запись данных в БД

    public void deleteNote(int id){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("notes", "id=?", new String[]{id + ""});
        database.close();
    }
    //Удаление данных из БД

    public void updateNote(Note note){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hour", note.getHour());
        values.put("minute", note.getMinute());
        values.put("message", note.getMessage());
        values.put("state", note.isState() ? 1 : 0);
        database.update("notes", values, "id=?", new String[]{note.getId() + ""});
        database.close();
    }
    //обновление бд

    public Note getNewNoteBeInsert(){
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        String sql = "select * from notes order by id asc";
        Cursor cursor = database.rawQuery(sql, null);
        while(cursor.moveToNext()){
            Note note = new Note(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getInt(4) == 1 ? true : false
            );
            notes.add(note);
        }
        database.close();
        return notes.get(notes.size() - 1);
    }
    //Упорядочивание данных

    public List<Note> getNotes(){
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        String sql = "select * from notes order by id desc";
        Cursor cursor = database.rawQuery(sql, null);
        while(cursor.moveToNext()){
            Note note = new Note(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getInt(4) == 1 ? true : false
            );
            notes.add(note);
        }
        database.close();
        return notes;
    }
    //Получить список заметок

    public Note getNoteById(int id){
        SQLiteDatabase database = this.getReadableDatabase();
        String sql = "select * from notes where id = \'" + id + "\'";
        Cursor cursor = database.rawQuery(sql, null);
        while(cursor.moveToNext()){
            return new Note(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getInt(4) == 1 ? true : false
            );
        }
        database.close();
        return null;
    }
    //Получение заметки по ID

}
