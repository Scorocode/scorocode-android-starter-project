package ru.scorocode.scorocodetemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackFindDocument;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;
import ru.profit_group.scorocode_sdk.scorocode_objects.Query;
import ru.scorocode.scorocodetemplate.R;
import ru.scorocode.scorocodetemplate.adapters.DocumentsAdapter;

public class ListActivity extends AppCompatActivity {

    @BindView(R.id.lvDocuments) ListView lvDocuments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    public void refreshList() {
        //To get all documents from collection you should:
        //1.Create new object of Query class
        Query query = new Query(getString(R.string.collectionName));

        //2.Don't specify any criteria of search (so it will searching for all documents).
        //3.Use findDocument() method of Query class
        query.findDocuments(new CallbackFindDocument() {
            @Override
            public void onDocumentFound(final List<DocumentInfo> documentInfos) {
                //As a result you have list of DocumentInfo objects
                //All information about documents stored in this class
                DocumentsAdapter adapter = new DocumentsAdapter(ListActivity.this, documentInfos, R.layout.item_document);

                lvDocuments.setAdapter(adapter);
                lvDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        DocumentActivity.showDocument(ListActivity.this, documentInfos.get(position));
                    }
                });
            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                //You can handle case if no documents were found.
                //You can also see error code and message if searching process was failed
                Toast.makeText(ListActivity.this, R.string.errorDuringDocumentLoading, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void display(Context context) {
        context.startActivity(new Intent(context, ListActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addDocument:
                DocumentActivity.createNewDocument(this);
                return true;

            case R.id.refreshList:
                refreshList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
