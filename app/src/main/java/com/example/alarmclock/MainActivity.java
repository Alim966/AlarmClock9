package com.example.alarmclock;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnItemSelected, View.OnClickListener {
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private DatabaseHelper databaseHelper;
    private LinearLayout layoutNoAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteAdapter = new NoteAdapter(MainActivity.this, this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(noteAdapter);

        layoutNoAlarm = findViewById(R.id.layoutNoAlarm);
        databaseHelper = DatabaseHelper.getInstance(this);

        getDataForAdapter();

        findViewById(R.id.fab).setOnClickListener(this);

    }
    //Данный метод вызывается при создании Активити

    public void getDataForAdapter() {
        List<Note> notes = databaseHelper.getNotes();
        if (notes == null || notes.size() == 0) {
            layoutNoAlarm.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            noteAdapter.setNotes(notes);
            layoutNoAlarm.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
    //Получение данныйх для адаптера

    private void showDialogConfirmDelete(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Удалить будильник")
                .setMessage("Вы хотите удалить этот напоминание?")
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Удалить!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.deleteNote(note.getId());
                        getDataForAdapter();
                        AlarmHelper.cancelAlarm(MainActivity.this, note);
                        Toast.makeText(MainActivity.this, "Удалено!", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //показ диалогового окна для удаления

    private int[] getTimeFromTimePicker(TimePicker timePicker) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new int[]{timePicker.getHour(), timePicker.getMinute()};
        } else {
            return new int[]{timePicker.getCurrentHour(), timePicker.getCurrentMinute()};
        }
    }//Получение времени

    private AlertDialog.Builder buildDialogInsert() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_insert_update, null, false);
        final TimePicker timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        final EditText etTitle = view.findViewById(R.id.etMessage);
        return new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = etTitle.getText().toString().trim();
                        int hour = getTimeFromTimePicker(timePicker)[0];
                        int minute = getTimeFromTimePicker(timePicker)[1];
                        if (title.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Сообщение не может быть пустым", Toast.LENGTH_SHORT).show();
                        } else {
                            databaseHelper.insertNote(new Note(hour, minute, title));
                            AlarmHelper.setAlarm(MainActivity.this, databaseHelper.getNewNoteBeInsert());
                            getDataForAdapter();
                            Toast.makeText(MainActivity.this, "Сохранено!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }//Создание диалого окна для создания будильника

    private AlertDialog.Builder buildDialogUpdate(final Note note) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_insert_update, null, false);
        final TimePicker timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        final EditText etTitle = view.findViewById(R.id.etMessage);
        etTitle.setText(note.getMessage());
        setTimeForTimePicker(timePicker, note.getHour(), note.getMinute());
        return new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = etTitle.getText().toString().trim();
                        int hour = getTimeFromTimePicker(timePicker)[0];
                        int minute = getTimeFromTimePicker(timePicker)[1];
                        if (title.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Сообщение не может быть пустым", Toast.LENGTH_SHORT).show();
                        } else {
                            Note noteUpdate = new Note(note.getId(), hour, minute, title, note.isState());
                            databaseHelper.updateNote(noteUpdate);
                            if (note.isState() == true){
                                AlarmHelper.cancelAlarm(MainActivity.this, note);
                                AlarmHelper.setAlarm(MainActivity.this, noteUpdate);
                            }
                            getDataForAdapter();
                            Toast.makeText(MainActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }//Создание диалого окна для редактирования будильника

    @Override
    public void onItemClickListener(Note note) {
        AlertDialog dialogUpdate = buildDialogUpdate(note).create();
        dialogUpdate.show();
    }//Всплывающее окно при нажатии

    @Override
    public void onItemLongClickListener(Note note) {
        showDialogConfirmDelete(note);
    }
    //Всплывающее окно при долгом нажатии для удаления

    @Override
    public void onClick(View v) {
        AlertDialog dialogInsert = buildDialogInsert().create();
        dialogInsert.show();
    }//Добавление новой заметки

    private void setTimeForTimePicker(TimePicker timePicker, int hour, int minute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        } else {
            timePicker.setCurrentHour(hour);
            timePicker.setCurrentMinute(minute);
        }
    }
    //Время

    @Override
    protected void onRestart() {
        super.onRestart();
        getDataForAdapter();
    }
}//Вызов метода при возобновлении работы приложения
