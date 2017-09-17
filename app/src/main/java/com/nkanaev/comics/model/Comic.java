package com.nkanaev.comics.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


public class Comic implements Comparable {
    private static final String COMIC_INFORMATION_FILE = "comic-information.txt";

    private Storage mShelf;
    private int mCurrentPage;
    private int mNumPages;
    private int mId;
    private String mType;
    private File mFile;
    private String mName;
    public final long updatedAt;

    public Comic(Storage shelf, int id, String filepath, String filename,
                 String type, int numPages, int currentPage, long updatedAt) {
        mShelf = shelf;
        mId = id;
        mNumPages = numPages;
        mCurrentPage = currentPage;
        mFile = new File(filepath, filename);
        mType = type;
        this.updatedAt = updatedAt;

        mName = mFile.getName();
        try {
            // check if information file is available
            File infoFile = new File(mFile.getAbsolutePath(), COMIC_INFORMATION_FILE);
            if (infoFile.exists() && infoFile.canRead())
            {
                // TODO: refactor this if more information are to be read
                BufferedReader br = new BufferedReader(new FileReader(infoFile));
                String line = br.readLine();
                int idx = line.indexOf(':');
                if (idx > 0 && line.length() > idx + 1 && line.substring(0, idx).trim().toLowerCase().equals("name")) {
                    mName = line.substring(idx + 1).trim();
                }

                br.close();
            }
        }
        catch (Exception e) {
            // muh. use directory name
        }
    }

    public int getId() {
        return mId;
    }

    public File getFile() {
        return mFile;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public int getTotalPages() {
        return mNumPages;
    }

    public void setCurrentPage(int page) {
        mShelf.bookmarkPage(getId(), page);
        mCurrentPage = page;
    }

    public String getType() {
        return mType;
    }

    public int compareTo(Object another) {
        return mFile.compareTo(((Comic) another).getFile());
    }

    public String getName() { return mName; }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Comic) && getId() == ((Comic)o).getId();
    }
}