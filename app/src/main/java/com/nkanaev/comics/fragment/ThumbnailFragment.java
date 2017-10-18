package com.nkanaev.comics.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nkanaev.comics.R;
import com.nkanaev.comics.activity.ReaderActivity;
import com.nkanaev.comics.managers.LocalComicHandler;
import com.nkanaev.comics.managers.Utils;
import com.nkanaev.comics.model.Comic;
import com.nkanaev.comics.parsers.Parser;
import com.nkanaev.comics.parsers.ParserFactory;
import com.squareup.picasso.Picasso;

public class ThumbnailFragment extends Fragment {
    public static int LastSelection = -1;
    public static Comic CurrentComic;

    private ReaderActivity mActivity;
    private Comic mComic;
    private LocalComicHandler mComicHandler;

    private Picasso mPicasso;
    private RecyclerView mImageListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (ReaderActivity) getActivity();
        mComic = CurrentComic;

        Parser mParser = ParserFactory.create(mComic.getFile());
        mComicHandler = new LocalComicHandler(mParser);
        mPicasso = new Picasso.Builder(getActivity())
                .addRequestHandler(mComicHandler)
                .build();
    }

    private int calculateNumColumns() {
        int deviceWidth = Utils.getDeviceWidth(getActivity());
        int columnWidth = getActivity().getResources().getInteger(R.integer.grid_comic_column_width);

        return Math.round((float) deviceWidth / columnWidth);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_thumbnail, container, false);
        getActivity().findViewById(R.id.reader_inner_layout).setVisibility(View.GONE);
        LastSelection = -1;

        int numColumns = calculateNumColumns();
        int spacing = (int) getResources().getDimension(R.dimen.grid_margin);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), numColumns);

        mImageListView = (RecyclerView) view.findViewById(R.id.thumbnail_grid);
        mImageListView.setHasFixedSize(true);
        mImageListView.setLayoutManager(layoutManager);
        mImageListView.setAdapter(new ThumbnailFragment.ImageListGridAdapter());
        mImageListView.addItemDecoration(new ThumbnailFragment.GridSpacingItemDecoration(numColumns, spacing));

        return view;
    }

    private final class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int mSpanCount;
        private int mSpacing;

        public GridSpacingItemDecoration(int spanCount, int spacing) {
            mSpanCount = spanCount;
            mSpacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % mSpanCount;

            outRect.left = mSpacing - column * mSpacing / mSpanCount;
            outRect.right = (column + 1) * mSpacing / mSpanCount;

            if (position < mSpanCount) {
                outRect.top = mSpacing;
            }
            outRect.bottom = mSpacing;
        }
    }

    private final class ImageListGridAdapter extends RecyclerView.Adapter {
        @Override
        public int getItemCount() {
            return mComic.getTotalPages();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context ctx = viewGroup.getContext();
            View view = LayoutInflater.from(ctx)
                    .inflate(R.layout.card_thumbnail, viewGroup, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ImageViewHolder holder = (ImageViewHolder) viewHolder;
            holder.setupImage(i);
        }
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImage;
        private TextView mTitleTextView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            mTitleTextView = (TextView) itemView.findViewById(R.id.thumbnail_title);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        public void setupImage(int index) {
            mTitleTextView.setText(Integer.toString(index + 1));
            mPicasso.load(mComicHandler.getPageUri(index))
                    .into(mImage);
        }

        @Override
        public void onClick(View v) {
            LastSelection = getAdapterPosition() + 1;
            mActivity.popFragment();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mImageListView.scrollToPosition(mComic.getCurrentPage() - 1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPicasso.shutdown();
        getActivity().findViewById(R.id.reader_inner_layout).setVisibility(View.VISIBLE);
    }
}
