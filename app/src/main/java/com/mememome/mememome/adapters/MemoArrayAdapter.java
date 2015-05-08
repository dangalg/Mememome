package com.mememome.mememome.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mememome.mememome.R;
import com.mememome.mememome.model.dao.Memo;

import java.util.List;

/**
 * Created by dangal on 4/11/15.
 */
public class MemoArrayAdapter extends ArrayAdapter<Memo> {

    private final Activity context;
    private final List<Memo> memos;

    static class ViewHolder {
        public TextView text;
        public ImageView image;
    }

    public MemoArrayAdapter(Activity context, List<Memo> memos) {
        super(context, R.layout.list_item_main_fragment, memos);
        this.context = context;
        this.memos = memos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_main_fragment, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.list_item_title);
            viewHolder.image = (ImageView) rowView
                    .findViewById(R.id.list_item_image);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        Memo m = memos.get(position);
        holder.text.setText(m.getName());
        holder.image.setImageResource(R.mipmap.ic_launcher);

        return rowView;
    }

}
