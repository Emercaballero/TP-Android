package com.teamdc.stephendiniz.autoaway.classes;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.teamdc.stephendiniz.autoaway.classes.Utils.readFile;
import static com.teamdc.stephendiniz.autoaway.classes.Utils.saveToFile;

/**
 * Created by sscotti on 5/20/15.
 */
public class MessageService {

    private static final String TAG = "MessageService";
    private static final String MESSAGES_FILENAME = "awayMessages.txt";

    private Context context;

    public MessageService(Context context){
        this.context = context;
    }

    public List<Message> readMessagesFromFile() {

        List<Message> messages = new ArrayList<Message>();

        File inFile = context.getFileStreamPath(MESSAGES_FILENAME);

        if (inFile.exists()) {
            try {

                messages = readFile(MESSAGES_FILENAME, context.openFileInput(MESSAGES_FILENAME), new RegisterDeserializer<Message>() {
                    public Message deserialize(String line) {
                        String[] split = line.split(",");
                        return new Message(split[0], split[1]);
                    }
                });

            } catch (java.io.FileNotFoundException exception) {
                Log.e(TAG, "FileNotFoundException caused by " + MESSAGES_FILENAME, exception);
            } catch (IOException exception) {
                Log.e(TAG, "IOException caused by buffreader.readLine()", exception);
            }

        }

        return messages;
    }


    public void saveContactsToFile(List<Message> messages){
        try {
            saveToFile(MESSAGES_FILENAME, context.openFileOutput(MESSAGES_FILENAME, 0), messages, new RegisterSerializer<Message>() {
                public String serialize(Message object) {
                    return String.format("%s,%s", object.getTitle(), object.getContent());
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "IOException caused by trying to access " + MESSAGES_FILENAME, e);
        }
    }


}
