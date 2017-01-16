package com.peterstaranchuk.scorocodetemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackFindDocument;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;
import ru.profit_group.scorocode_sdk.scorocode_objects.Query;

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

    private void refreshList() {
        new Query(getString(R.string.collectionName))
                .findDocuments(new CallbackFindDocument() {
                    @Override
                    public void onDocumentFound(List<DocumentInfo> documentInfos) {
                        DocumentsAdapter adapter = new DocumentsAdapter(ListActivity.this, documentInfos, R.layout.item_document);
                        lvDocuments.setAdapter(adapter);
                    }

                    @Override
                    public void onDocumentNotFound(String errorCode, String errorMessage) {
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
                AddDocumentActivity.display(this);
                return true;

            case R.id.refreshList:
                refreshList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
