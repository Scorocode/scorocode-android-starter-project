package com.peterstaranchuk.scorocodetemplate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackFindDocument;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackRemoveDocument;
import ru.profit_group.scorocode_sdk.Responses.data.ResponseRemove;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;
import ru.profit_group.scorocode_sdk.scorocode_objects.Query;

public class ListActivity extends AppCompatActivity {

    @BindView(R.id.lvDocuments)
    ListView lvDocuments;

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
                    public void onDocumentFound(final List<DocumentInfo> documentInfos) {
                        DocumentsAdapter adapter = new DocumentsAdapter(ListActivity.this, documentInfos, R.layout.item_document);
                        lvDocuments.setAdapter(adapter);

                        lvDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                final View dialogView = LayoutInflater.from(ListActivity.this).inflate(R.layout.item_action_view, null);

                                new AlertDialog.Builder(ListActivity.this)
                                        .setTitle(R.string.choose_action)
                                        .setView(dialogView)
                                        .setPositiveButton(R.string.continue_action, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                performActionWithDocument(dialogView, documentInfos, position);
                                            }
                                        })
                                        .setNegativeButton(R.string.close_action, null)
                                        .setCancelable(false)
                                        .show();
                            }
                        });
                    }

                    @Override
                    public void onDocumentNotFound(String errorCode, String errorMessage) {
                        Toast.makeText(ListActivity.this, R.string.errorDuringDocumentLoading, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void performActionWithDocument(View dialogView, List<DocumentInfo> documentInfos, int position) {
        final RadioGroup rgChooseItems = ButterKnife.findById(dialogView, R.id.rgChooseItems);
        DocumentInfo selectedDocument = documentInfos.get(position);

        switch (rgChooseItems.getCheckedRadioButtonId()) {
            case R.id.rbOpen:
                DocumentActivity.showDocument(ListActivity.this, selectedDocument);
                break;

            case R.id.rbEdit:
                DocumentActivity.editDocument(ListActivity.this, selectedDocument);
                break;

            case R.id.rbRemove:
                removeSelectedDocument(selectedDocument);
                break;

            default:
                Toast.makeText(ListActivity.this, R.string.no_item_selected, Toast.LENGTH_SHORT).show();
        }
    }

    private void removeSelectedDocument(DocumentInfo clickedDocument) {
        new Query(getString(R.string.collectionName))
                .equalTo("_id", clickedDocument.getId())
                .removeDocument(new CallbackRemoveDocument() {
                    @Override
                    public void onRemoveSucceed(ResponseRemove responseRemove) {
                        Toast.makeText(ListActivity.this, R.string.decument_removed, Toast.LENGTH_SHORT).show();
                        refreshList();
                    }

                    @Override
                    public void onRemoveFailed(String errorCode, String errorMessage) {
                        Toast.makeText(ListActivity.this, R.string.error_during_doc_removal, Toast.LENGTH_SHORT).show();
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
