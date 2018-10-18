package com.example.franc.italianpoliticsclosely;

import android.content.Context;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;
import java.util.List;
import java.util.Objects;


public class IpcAdapter extends ArrayAdapter<Ipc> {

    /**
     * Constructs a new {@link IpcAdapter}.
     *
     * @param context of the app
     * @param ipcs    is the list of news, which is the data source of the adapter
     */
    IpcAdapter(Context context, List<Ipc> ipcs) {
        super(context, 0, ipcs);
    }


    // Returns a list item view that displays information about the news at the given position in the Ipc list.

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.ipc_list_item, parent, false);
        }

        //Find the TextViews in the list_item.xml
        TextView title = convertView.findViewById(R.id.title);
        TextView date = convertView.findViewById(R.id.date);
        TextView section = convertView.findViewById(R.id.section);
        TextView author = convertView.findViewById(R.id.author);

        // Get the item position of the TextViews in Ipc
        Ipc currentIpc = getItem(position);

        // Set the TextViews
        assert currentIpc != null;
        title.setText(currentIpc.getTitle());
        date.setText(currentIpc.getDate());
        section.setText(currentIpc.getSection());
        author.setText (currentIpc.getTagsString());

        return convertView;
    }
}

