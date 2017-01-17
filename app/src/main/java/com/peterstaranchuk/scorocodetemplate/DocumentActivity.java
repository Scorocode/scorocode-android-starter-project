package com.peterstaranchuk.scorocodetemplate;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


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

        initScreenState();
    }

    private void initScreenState() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        switch (getMode()) {
            case SHOW_DOCUMENT:
                setShowDocumentMode();
                break;

            case EDIT_DOCUMENT:
                setEditDocumentMode();
                InputHelper.setFocusTo(etDocumentName);
                break;

            case ADD_NEW_DOCUMENT:
                setAddNewDocumentMode();
                InputHelper.setFocusTo(etDocumentName);
                break;
        }
    }

    private void setAddNewDocumentMode() {
        Action1<CharSequence> action = new Action1<CharSequence>() {
            @Override
            public void call(CharSequence charSequence) {
                if (InputHelper.isNotEmpty(etDocumentName) && InputHelper.isNotEmpty(etDocumentContent)) {
                    InputHelper.enableButton(btnAddDocument);
                } else {
                    InputHelper.disableButton(btnAddDocument);
                }
            }
        };

        InputHelper.disableButton(btnAddDocument);
        InputHelper.checkForEmptyEnter(etDocumentName, action);
        InputHelper.checkForEmptyEnter(etDocumentContent, action);
        //comment is optional so we don't check it
    }

    @NonNull
    private DocumentInfo getDocumentInfo() {
        if(getIntent() != null) {
            return (DocumentInfo) getIntent().getSerializableExtra(EXTRA_DOCUMENT_INFO);
        } else {
            return new DocumentInfo();
        }
    }

    @NonNull
    private Mode getMode() {
        if(getIntent() != null) {
            return (Mode) getIntent().getSerializableExtra(EXTRA_DOCUMENT_MODE);
        } else {
            return Mode.ADD_NEW_DOCUMENT;
        }
    }

    private void setEditDocumentMode() {
        InputHelper.enableButton(btnAddDocument);
        btnAddDocument.setText(R.string.change_document_info);
        btnAddDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDocument();
            }
        });

        loadDocument(getDocumentInfo());
    }

    private void editDocument() {
        //To edit document which already exist on server you should
        //1. Create new document object and specify name of collection (where it is located) in constructor
        final Document document = new Document(getString(R.string.collectionName));
        //2. Get document from server by it's id. You can previously find this document
        //with findDocument() method from Query class.
        document.getDocumentById(getDocumentInfo().getId(), new CallbackGetDocumentById() {
            @Override
            public void onDocumentFound(DocumentInfo documentInfo1) {
                FieldHelper fieldHelper = new FieldHelper(DocumentActivity.this);
                //3.when document found on server you can update it's field's info
                //you can use any methods from Update class for this purposes
                document.updateDocument()
                        .set(fieldHelper.nameField(), InputHelper.getStringFrom(etDocumentName))
                        .set(fieldHelper.contentField(), InputHelper.getStringFrom(etDocumentContent))
                        .set(fieldHelper.commentField(), InputHelper.getStringFrom(etDocumentComment));

                //4. After you specified all updates for document you should save it
                document.saveDocument(new CallbackDocumentSaved() {
                    @Override
                    public void onDocumentSaved() {
                        //you can do any actions after document saved
                        Toast.makeText(DocumentActivity.this, R.string.document_saved, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onDocumentSaveFailed(String errorCode, String errorMessage) {
                        //you can handle error if document was not saved. You can also see code and message of error.
                        Toast.makeText(DocumentActivity.this, R.string.error_during_document_saving, Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onDocumentNotFound(String errorCode, String errorMessage) {
                //if document not found you can handle error and see it's code and message
                Toast.makeText(DocumentActivity.this, R.string.error_during_document_saving, Toast.LENGTH_SHORT).show();                                }
        });
    }

    private void setShowDocumentMode() {
        btnAddDocument.setVisibility(View.GONE);
        etDocumentName.setEnabled(false);
        etDocumentContent.setEnabled(false);
        etDocumentComment.setEnabled(false);
        loadDocument(getDocumentInfo());
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
        //To create new document you should
        //1. Create new document object for your collection
        Document document = new Document(getString(R.string.collectionName));

        //2.Prepare info which you want to write inside document
        String documentName = InputHelper.getStringFrom(etDocumentName);
        String documentContent = InputHelper.getStringFrom(etDocumentContent);
        String documentComment = InputHelper.getStringFrom(etDocumentComment);

        //3.Set this info in document's fields
        document.setField(getString(R.string.documentNameField), documentName);
        document.setField(getString(R.string.documentContentField), documentContent);
        document.setField(getString(R.string.documentCommentField), documentComment);

        //4. Save document.
        //This document will be uploaded at your collection (which you specified in constructor).
        //For more information see scorocode documentation.
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
