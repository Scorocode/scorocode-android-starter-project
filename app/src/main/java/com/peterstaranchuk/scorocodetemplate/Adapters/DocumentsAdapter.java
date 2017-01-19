package com.peterstaranchuk.scorocodetemplate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.peterstaranchuk.scorocodetemplate.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.profit_group.scorocode_sdk.scorocode_objects.Document;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;

/**
 * Created by Peter Staranchuk.
 */

public class DocumentsAdapter extends BaseAdapter {

    private List<DocumentInfo> documents;
    private LayoutInflater inflater;
    private int layoutRes;

    public DocumentsAdapter(Context context, List<DocumentInfo> documents, int layoutRes) {
        this.inflater = LayoutInflater.from(context);
        this.documents = (documents == null? new ArrayList<DocumentInfo>() : documents);
        this.layoutRes = layoutRes;
    }

    @Override
    public int getCount() {
        return documents.size();
    }

    @Override
    public Object getItem(int position) {
        if(position < documents.size()) {
            return documents.get(position);
        } else {
            return new Document("");
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(layoutRes, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        DocumentInfo document = (DocumentInfo) getItem(position);

        //you can get id of document using getId() method
        holder.tvDocumentId.setText(document.getId());
        //you can get any field content from document by fields't name using get() method
        holder.tvDocumentName.setText(document.get("documentName").toString());

        return view;
    }

    static class ViewHolder {
        @BindView(R.id.tvDocumentId) TextView tvDocumentId;
        @BindView(R.id.tvDocumentName) TextView tvDocumentName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
