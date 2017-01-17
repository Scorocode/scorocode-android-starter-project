package com.peterstaranchuk.scorocodetemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackDocumentSaved;
import ru.profit_group.scorocode_sdk.Callbacks.CallbackGetDocumentById;
import ru.profit_group.scorocode_sdk.scorocode_objects.Document;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;
import rx.functions.Action1;

public class DocumentActivity extends AppCompatActivity {

    @BindView(R.id.etDocumentId) EditText etDocumentId;
    @BindView(R.id.etDocumentName) EditText etDocumentName;
    @BindView(R.id.etDocumentContent) EditText etDocumentContent;
    @BindView(R.id.etDocumentComment) EditText etDocumentComment;
    @BindView(R.id.btnAddDocument) Button btnAddDocument;

    public static final String EXTRA_DOCUMENT_MODE = "EXTRA_DOCUMENT_MODE";
    public static final String EXTRA_DOCUMENT_INFO = "EXTRA_DOCUMENT_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document);
        ButterKnife.bind(this);

        if(getIntent() != null) {
            Mode mode = (Mode) getIntent().getSerializableExtra(EXTRA_DOCUMENT_MODE);
            final DocumentInfo documentInfo = (DocumentInfo) getIntent().getSerializableExtra(EXTRA_DOCUMENT_INFO);

            switch (mode) {
                case SHOW_DOCUMENT:
                    btnAddDocument.setVisibility(View.GONE);
                    etDocumentName.setEnabled(false);
                    etDocumentContent.setEnabled(false);
                    etDocumentComment.setEnabled(false);
                    loadDocument(documentInfo);
                    break;

                case EDIT_DOCUMENT:
                    InputHelper.enableButton(btnAddDocument);
                    btnAddDocument.setText(R.string.change_document_info);
                    btnAddDocument.setOnClickListener(v -> {
                        final Document document = new Document(getString(R.string.collectionName));
                        document.getDocumentById(documentInfo.getId(), new CallbackGetDocumentById() {
                            @Override
                            public void onDocumentFound(DocumentInfo documentInfo1) {
                                FieldHelper fieldHelper = new FieldHelper(DocumentActivity.this);
                                document.updateDocument()
                                        .set(fieldHelper.nameField(), InputHelper.getStringFrom(etDocumentName))
                                        .set(fieldHelper.contentField(), InputHelper.getStringFrom(etDocumentContent))
                                        .set(fieldHelper.commentField(), InputHelper.getStringFrom(etDocumentComment));

                                document.saveDocument(new CallbackDocumentSaved() {
                                    @Override
                                    public void onDocumentSaved() {
                                        Toast.makeText(DocumentActivity.this, R.string.document_saved, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onDocumentSaveFailed(String errorCode, String errorMessage) {
                                        Toast.makeText(DocumentActivity.this, R.string.error_during_document_saving, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onDocumentNotFound(String errorCode, String errorMessage) {
                                Toast.makeText(DocumentActivity.this, R.string.error_during_document_saving, Toast.LENGTH_SHORT).show();                                }
                        });
                    });

                    loadDocument(documentInfo);
                    break;

                case ADD_NEW_DOCUMENT:
                    InputHelper.disableButton(btnAddDocument);
                    break;
            }
        }

        Action1<CharSequence> action = charSequence -> {
            if (InputHelper.isNotEmpty(etDocumentName) && InputHelper.isNotEmpty(etDocumentContent)) {
                InputHelper.enableButton(btnAddDocument);
            } else {
                InputHelper.disableButton(btnAddDocument);
            }
        };

        InputHelper.checkForEmptyEnter(etDocumentName, action);
        InputHelper.checkForEmptyEnter(etDocumentContent, action);
        //comment is optional so we don't check it
    }

    private void loadDocument(DocumentInfo documentInfo) {
        FieldHelper fieldHelper = new FieldHelper(this);
        etDocumentId.setText(fieldHelper.getIdFrom(documentInfo));
        etDocumentName.setText(fieldHelper.getDocumentNameFrom(documentInfo));
        etDocumentContent.setText(fieldHelper.getDocumentContentFrom(documentInfo));
        etDocumentComment.setText(fieldHelper.getDocumentCommentFrom(documentInfo));
    }

    @OnClick(R.id.btnAddDocument)
    public void onBtnAddDocumentClicked(View addDocumentButton) {
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
                Toast.makeText(DocumentActivity.this, R.string.document_saved, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onDocumentSaveFailed(String errorCode, String errorMessage) {
                Toast.makeText(DocumentActivity.this, R.string.error_during_document_saving, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void display(Context context, Mode mode, DocumentInfo documentInfo) {
        Intent intent = new Intent(context, DocumentActivity.class);
        intent.putExtra(EXTRA_DOCUMENT_INFO, documentInfo);
        intent.putExtra(EXTRA_DOCUMENT_MODE, mode);
        context.startActivity(intent);
    }

    public static void createNewDocument(Context context) {
        display(context, Mode.ADD_NEW_DOCUMENT, null);
    }

    public static void showDocument(Context context, DocumentInfo documentInfo) {
        display(context, Mode.SHOW_DOCUMENT, documentInfo);
    }

    public static void editDocument(Context context, DocumentInfo documentInfo) {
        display(context, Mode.EDIT_DOCUMENT, documentInfo);
    }

    private enum Mode {
        SHOW_DOCUMENT, EDIT_DOCUMENT, ADD_NEW_DOCUMENT
    }
}
