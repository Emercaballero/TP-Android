package com.teamdc.stephendiniz.autoaway.classes;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.teamdc.stephendiniz.autoaway.R;

import java.util.List;

public class MessageListContactArrayAdapter extends ArrayAdapter<Contact> {

    private Activity context;
    private List<Contact> contacts;

    public MessageListContactArrayAdapter(Activity context, List<Contact> contacts){
        super(context, R.layout.message_list, contacts);

        this.context = context;
        this.contacts = contacts;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.message_list, null, true);
		
		TextView textTitle = (TextView) rowView.findViewById(R.id.dl_Title);
		TextView textContent = (TextView) rowView.findViewById(R.id.dl_Content);

        Contact contact = contacts.get(position);

        textTitle.setText(contact.getName());
        textContent.setText(contact.getNumber());

		return rowView;
	}
}