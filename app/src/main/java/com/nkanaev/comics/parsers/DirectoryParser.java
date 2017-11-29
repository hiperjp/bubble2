package com.nkanaev.comics.parsers;


import com.nkanaev.comics.managers.NaturalOrderComparator;
import com.nkanaev.comics.managers.Utils;

import java.io.*;
import java.util.*;

public class DirectoryParser implements Parser {
    private ArrayList<File> mFiles = new ArrayList<>();
    private boolean mSorted = false;

    @Override
    public void parse(File dir) throws IOException {
        if (!dir.isDirectory()) {
            throw new IOException("Not a directory: " + dir.getAbsolutePath());
        }

        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : dir.listFiles()) {
                if (f.isDirectory()) {
                    throw new IOException("Probably not a comic directory");
                }
                if (Utils.isImage(f.getAbsolutePath())) {
                    mFiles.add(f);
                }
            }
        }
    }

    private void sort() {
        if (mSorted)
            return;

        Collections.sort(mFiles, new NaturalOrderComparator() {
            @Override
            public String stringValue(Object o) {
                return ((File) o).getName();
            }
        });
        mSorted = true;
    }

    @Override
    public int numPages() {
        return mFiles.size();
    }

    @Override
    public InputStream getPage(int num) throws IOException {
        sort();
        return new FileInputStream(mFiles.get(num));
    }

    @Override
    public String getType() {
        return "dir";
    }

    @Override
    public void destroy() throws IOException {
    }
}
