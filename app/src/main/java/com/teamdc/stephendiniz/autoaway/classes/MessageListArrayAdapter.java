package com.teamdc.stephendiniz.autoaway.classes;

import com.teamdc.stephendiniz.autoaway.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MessageListArrayAdapter extends ArrayAdapter<Listable> {

    private Activity context;
    private List<Listable> listableItems;

    public MessageListArrayAdapter(Activity context, List<Listable> listableItems){
        super(context, R.layout.message_list, listableItems);

        this.context = context;
        this.listableItems = listableItems;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.message_list, null, true);
		
		TextView textTitle = (TextView) rowView.findViewById(R.id.dl_Title);
		TextView textContent = (TextView) rowView.findViewById(R.id.dl_Content);

        Listable phoneContact = listableItems.get(position);
        textTitle.setText(phoneContact.getTitle());

        textContent.setText(phoneContact.getContent());

		return rowView;
	}
}