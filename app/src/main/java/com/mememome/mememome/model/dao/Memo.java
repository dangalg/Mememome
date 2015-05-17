package com.mememome.mememome.model.dao;

import com.mememome.mememome.convertions.StringConvertions;
import com.orm.SugarRecord;

/**
 * created by dangal on 4/9/15.
 */
public class Memo  extends SugarRecord<Memo> {

    private String name;
    private String text;
    private long created;
    private long updated;
    private String rev;
    private String hash;
    private long memoGroupId;
    private String localFilePath;
    private String dropboxFilePath;

    public Memo() {
    }

    public Memo(String name,
                String text,
                long created,
                long updated,
                String rev,
                String hash,
                long memoGroupId,
                String localFilePath,
                String dropboxFilePath) {
        this.name = name;
        this.text = text;
        this.created = created;
        this.updated = updated;
        this.rev = rev;
        this.hash = hash;
        this.memoGroupId = memoGroupId;
        this.localFilePath = localFilePath;
        this.dropboxFilePath = dropboxFilePath;
    }

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_CREATED = "created";
    public static final String COLUMN_UPDATED = "updated";
    public static final String COLUMN_REV = "rev";
    public static final String COLUMN_hash = "hash";
    public static final String COLUMN_GROUP_ID = "memo_group_id";
    public static final String COLUMN_LOCAL_FILE_PATH = "local_file_path";
    public static final String COLUMN_DROPBOX_FILE_PATH = "dropbox_file_path";

    // region Properties

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setUpdated(System.currentTimeMillis());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        setUpdated(System.currentTimeMillis());
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getMemoGroupId() {
        return memoGroupId;
    }

    public void setMemoGroupId(long memoGroupId) {
        this.memoGroupId = memoGroupId;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getDropboxFilePath() {
        return dropboxFilePath;
    }

    public void setDropboxFilePath(String dropboxFilePath) {
        this.dropboxFilePath = dropboxFilePath;
    }

    // endregion Properties

}
