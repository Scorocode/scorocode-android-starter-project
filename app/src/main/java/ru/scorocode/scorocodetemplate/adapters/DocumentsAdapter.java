package ru.scorocode.scorocodetemplate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackRemoveDocument;
import ru.profit_group.scorocode_sdk.Responses.data.ResponseRemove;
import ru.profit_group.scorocode_sdk.scorocode_objects.Document;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;
import ru.profit_group.scorocode_sdk.scorocode_objects.Query;
import ru.scorocode.scorocodetemplate.R;
import ru.scorocode.scorocodetemplate.activities.DocumentActivity;
import ru.scorocode.scorocodetemplate.activities.ListActivity;

/**
 * Created by Peter Staranchuk.
 */

public class DocumentsAdapter extends BaseAdapter {

    private Context context;
    private List<DocumentInfo> documents;
    private LayoutInflater inflater;
    private int layoutRes;

    public DocumentsAdapter(Context context, List<DocumentInfo> documents, int layoutRes) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
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
        holder.tvDocumentName.setText(document.get(context.getString(R.string.documentNameField)).toString());

        holder.ivEdit.setOnClickListener(onEditListener(document));
        holder.ivDelete.setOnClickListener(onDeleteListener(document));
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.tvDocumentId) TextView tvDocumentId;
        @BindView(R.id.tvDocumentName) TextView tvDocumentName;
        @BindView(R.id.ivEdit) ImageView ivEdit;
        @BindView(R.id.ivDelete) ImageView ivDelete;
        @BindView(R.id.swipeLayout) SwipeLayout swipeLayout;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        }
    }

    private View.OnClickListener onEditListener(final DocumentInfo document) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentActivity.editDocument(context, document);
            }
        };
    }

    private View.OnClickListener onDeleteListener(final DocumentInfo document) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelectedDocument(document);
            }
        };
    }

    private void removeSelectedDocument(DocumentInfo clickedDocument) {
        //To remove document from server (from collection) you should:
        //1. create new Query class. You should also specify collection name in constructor
        Query query = new Query(context.getString(R.string.collectionName));

        //2. find document in collection using one of Query methods
        //(in this case we searching for document with particular id)
        query.equalTo("_id", clickedDocument.getId());

        //3. Use removeDocument() method of Query class
        query.removeDocument(new CallbackRemoveDocument() {
            @Override
            public void onRemoveSucceed(ResponseRemove responseRemove) {
                //after removable process you can perform some actions
                Toast.makeText(context, R.string.decument_removed, Toast.LENGTH_SHORT).show();
                ((ListActivity) context).refreshList();
            }

            @Override
            public void onRemoveFailed(String errorCode, String errorMessage) {
                //if remove process failed you can handle this situation.
                //you can also see code and message of error
                Toast.makeText(context, R.string.error_during_doc_removal, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
