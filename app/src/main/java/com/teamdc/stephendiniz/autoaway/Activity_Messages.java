package com.teamdc.stephendiniz.autoaway;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.teamdc.stephendiniz.autoaway.classes.Message;
import com.teamdc.stephendiniz.autoaway.classes.MessageListArrayAdapter;
import com.teamdc.stephendiniz.autoaway.classes.MessageService;

import static com.teamdc.stephendiniz.autoaway.classes.Utils.*;

public class Activity_Messages extends ListActivity {
    private static final String TAG = "Messages";

    private final String messagesFile = "awayMessages.txt";

    private List<Message> messages = new ArrayList<Message>();

    private MessageService messageService;
    Resources r;
    Dialog dialog;
    SharedPreferences prefs;

    static final int MESSAGE_ERROR_EXISTS = 0;
    static final int MESSAGE_ERROR_BLANK = 1;
    static final int MESSAGE_ADDED = 2;
    static final int MESSAGE_SAVED = 3;

    static final int CONTEXT_MENU_EDIT = 0;
    static final int CONTEXT_MENU_REMOVE = 1;

    final String THEME_PREF = "themePreference";

    @SuppressLint("NewApi")
    public void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        messageService = new MessageService(this);

        if (android.os.Build.VERSION.SDK_INT >= 14) {
            if (prefs.getString(THEME_PREF, "LIGHT").equals("LIGHT"))
                setTheme(R.style.HoloLight);
            else
                setTheme(R.style.HoloDark);
        }

        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= 11)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        messages = messageService.readMessagesFromFile();

        setListAdapter(new MessageListArrayAdapter(this, asListable(messages)));

        registerForContextMenu(getListView());
    }

    public void onResume() {
        super.onResume();

        r = getResources();
    }

    public void onPause() {
        super.onPause();

        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_messages, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_messages_add:

                dialog = new Dialog(this);

                dialog.setContentView(R.layout.messages_add);
                dialog.setTitle(r.getString(R.string.prompt_message_title));

                Button pButton = (Button) dialog.findViewById(R.id.dialog_messagesButtonPositive_add);
                pButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        EditText eTitle = (EditText) dialog.findViewById(R.id.dialog_messagesTitleEdit_add);
                        EditText eContent = (EditText) dialog.findViewById(R.id.dialog_messagesContentEdit_add);

                        String title = eTitle.getText().toString().trim();
                        String content = eContent.getText().toString().trim();

                        if (title == null || "".equals(title) || content == null || "".equals(content)){
                            showTheMessage(MESSAGE_ERROR_BLANK, null);
                        } else if (titleExists(title)) {
                            showTheMessage(MESSAGE_ERROR_EXISTS, null);
                        } else {
                            messages.add(new Message(title, content));
                            showTheMessage(MESSAGE_ADDED, title);
                            dialog.cancel();

                            messageService.saveContactsToFile(messages);

                            startActivity(getIntent());
                            finish();
                        }
                    }

                });

                Button nButton = (Button) dialog.findViewById(R.id.dialog_messagesButtonNegative_add);
                nButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.show();
                return true;
            case R.id.menu_messages_alphabetize:

                Collections.sort(messages, new Comparator<Message>() {
                    public int compare(Message lhs, Message rhs) {
                        return lhs.getTitle().compareTo(rhs.getTitle());
                    }
                });

                messageService.saveContactsToFile(messages);

                Toast.makeText(this, r.getString(R.string.prompt_messages_alphabetized), Toast.LENGTH_LONG).show();

                finish();
                startActivity(getIntent());
                return true;
            case android.R.id.home:
                Intent parentActivityIntent = new Intent(this, Activity_Main.class);
                parentActivityIntent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(parentActivityIntent);
                finish();
                return true;
        }

        return true;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(messages.get(info.position).getTitle());
        String[] menuItems = {r.getString(R.string.menu_edit), r.getString(R.string.menu_remove)};

        for (int i = 0; i < menuItems.length; i++)
            menu.add(Menu.NONE, i, i, menuItems[i]);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        final int iId = info.position;
        switch (menuItemIndex) {
            case CONTEXT_MENU_EDIT:
                dialog = new Dialog(this);

                dialog.setContentView(R.layout.messages_edit);
                dialog.setTitle(r.getString(R.string.menu_edit) + " " + messages.get(iId).getTitle());

                Button pButton = (Button) dialog.findViewById(R.id.dialog_messagesButtonPositive_edit);
                pButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        EditText eTitle = (EditText) dialog.findViewById(R.id.dialog_messagesTitleEdit_edit);
                        EditText eContent = (EditText) dialog.findViewById(R.id.dialog_messagesContentEdit_edit);

                        if (eTitle.getText().toString().equals("") || eTitle.getText().toString().equals(null) || eContent.getText().toString().equals("") || eContent.getText().toString().equals(null))
                            showTheMessage(MESSAGE_ERROR_BLANK, null);

                        else {
                            if ((messages.get(iId).getTitle().equals(eTitle.getText().toString())) && (messages.get(iId).getContent().equals(eContent.getText().toString())))
                                dialog.cancel();

                            else {
                                messages.get(iId).setInfo(eTitle.getText().toString().trim(), eContent.getText().toString().trim());
                                showTheMessage(MESSAGE_SAVED, eTitle.getText().toString().trim());
                                dialog.cancel();
                                messageService.saveContactsToFile(messages);

                                Log.i(TAG, "\"" + messages.get(iId).getTitle() + "\" edited successfully");
                                startActivity(getIntent());
                                finish();
                            }
                        }
                    }
                });

                Button nButton = (Button) dialog.findViewById(R.id.dialog_messagesButtonNegative_edit);
                nButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.show();
                EditText eTitle = (EditText) dialog.findViewById(R.id.dialog_messagesTitleEdit_edit);
                eTitle.setText(messages.get(iId).getTitle());
                EditText eContent = (EditText) dialog.findViewById(R.id.dialog_messagesContentEdit_edit);
                eContent.setText(messages.get(iId).getContent());
                break;

            case CONTEXT_MENU_REMOVE:
                Log.i(TAG, "\"" + messages.get(info.position).getTitle() + "\" removed");
                messages.remove(info.position);
                messageService.saveContactsToFile(messages);

                startActivity(getIntent());
                finish();
                break;
        }
        return true;
    }

    public void showTheMessage(int id, String extra) {
        String message = "";

        switch (id) {
            case MESSAGE_ERROR_BLANK:
                message = r.getString(R.string.prompt_error_message_blank);
                break;

            case MESSAGE_ERROR_EXISTS:
                message = r.getString(R.string.prompt_error_message_exists);
                break;

            case MESSAGE_ADDED:
                message = "\'" + extra + "\'" + " " + r.getString(R.string.prompt_added);
                break;

            case MESSAGE_SAVED:
                message = "\'" + extra + "\'" + " " + r.getString(R.string.prompt_message_saved);
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public boolean titleExists(String title) {
        for(Message message : messages){
            if(title.equals(message.getTitle())){
                return true;
            }
        }
        return false;
    }

}
