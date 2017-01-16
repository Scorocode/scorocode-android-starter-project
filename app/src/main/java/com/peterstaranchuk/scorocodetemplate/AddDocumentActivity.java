package com.peterstaranchuk.scorocodetemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackDocumentSaved;
import ru.profit_group.scorocode_sdk.scorocode_objects.Document;
import rx.functions.Action1;

public class AddDocumentActivity extends AppCompatActivity {

    @BindView(R.id.etDocumentId) EditText etDocumentId;
    @BindView(R.id.etDocumentName) EditText etDocumentName;
    @BindView(R.id.etDocumentContent) EditText etDocumentContent;
    @BindView(R.id.etDocumentComment) EditText etDocumentComment;
    @BindView(R.id.btnAddDocument) Button btnAddDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document);
        ButterKnife.bind(this);

        InputHelper.disableButton(btnAddDocument);

        Action1<CharSequence> action = new Action1<CharSequence>() {
            @Override
            public void call(CharSequence charSequence) {
                if(InputHelper.isNotEmpty(etDocumentName) && InputHelper.isNotEmpty(etDocumentContent)) {
                    InputHelper.enableButton(btnAddDocument);
                } else {
                    InputHelper.disableButton(btnAddDocument);
                }
            }
        };

        InputHelper.checkForEmptyEnter(etDocumentName, action);
        InputHelper.checkForEmptyEnter(etDocumentContent, action);
        //comment is optional so we don't check it
    }

    @OnClick(R.id.btnAddDocument)
    public void onBtnAddDocumnetClicked(View addDocumentButton) {
        Document document = new Document(getString(R.string.collectionName));
        String documentName = InputHelper.getStringFrom(etDocumentName);
        String documentContent = InputHelper.getStringFrom(etDocumentContent);
        String documentComment = InputHelper.getStringFrom(etDocumentComment);

        document.setField(getString(R.string.documentNameField), documentName);
        document.setField(getString(R.string.documentContentField), documentContent);
        document.setField(getString(R.string.documentCommentField), documentComment);

        document.saveDocument(new CallbackDocumentSaved() {
            @Override
            public void onDocumentSaved() {
                Toast.makeText(AddDocumentActivity.this, R.string.document_saved, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onDocumentSaveFailed(String errorCode, String errorMessage) {
                Toast.makeText(AddDocumentActivity.this, R.string.error_during_document_saving, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void display(Context context) {
        context.startActivity(new Intent(context, AddDocumentActivity.class));
    }
}
