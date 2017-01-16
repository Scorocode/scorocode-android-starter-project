package com.peterstaranchuk.scorocodetemplate;

import android.app.Activity;
import android.support.annotation.NonNull;

import butterknife.BindString;
import butterknife.ButterKnife;
import ru.profit_group.scorocode_sdk.scorocode_objects.DocumentInfo;

/**
 * Created by Peter Staranchuk.
 */

public class FieldHelper {

    private String name;
    private String content;
    private String comment;

    public FieldHelper(Activity activity) {
        this.name = activity.getString(R.string.documentNameField);
        this.content = activity.getString(R.string.documentContentField);
        this.comment = activity.getString(R.string.documentCommentField);
    }

    public String nameField() {
        return name;
    }

    public String contentField() {
        return content;
    }

    public String commentField() {
        return comment;
    }

    @NonNull
    public String getIdFrom(DocumentInfo documentInfo) {
        if(documentInfo == null) {
            return "";
        }

        String id = documentInfo.getId();
        return  id == null? "" : id;
    }

    @NonNull
    public String getDocumentNameFrom(DocumentInfo documentInfo) {
        if(documentInfo == null) {
            return "";
        }

        Object documentName = documentInfo.get(nameField());
        return documentName == null? "" : documentName.toString();
    }

    @NonNull
    public String getDocumentContentFrom(DocumentInfo documentInfo) {
        if(documentInfo == null) {
            return "";
        }

        Object documentContent = documentInfo.get(contentField());
        return documentContent == null? "" : documentContent.toString();
    }

    @NonNull
    public String getDocumentCommentFrom(DocumentInfo documentInfo) {
        if(documentInfo == null) {
            return "";
        }

        Object documentComment = documentInfo.get(commentField());
        return documentComment == null? "" : documentComment.toString();
    }
}
