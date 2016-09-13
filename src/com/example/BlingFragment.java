package com.example;

import com.example.data.BlingItemCursor;
import com.example.data.BlingItemData;
import com.example.data.MySQLiteHelper;
import com.example.data.NotifyCursorLoader;
import com.example.data.RecyclerViewCursorAdapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

 
public class BlingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	// Container Activity must implement this interface
    public interface OnBlingSelectedListener {
        public void onBlingSelected(int blingId);
    }
 
    public static final String TAG = "BlingFragment";
    
    protected RecyclerView mRecyclerView;
    protected BlingAdapter mAdapter;
    protected ProgressBar mProgressBar;
    protected RecyclerView.LayoutManager mLayoutManager;
    
    private final static int LOADER_ID = 0;
    
    private OnBlingSelectedListener mCallback;
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Make sure that the container activity has implemented
        // the callback interface.
        try {
            mCallback = (OnBlingSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bling_list, container, false);
        rootView.setTag(TAG);
        
        mProgressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        
        int scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .findFirstCompletelyVisibleItemPosition();
        
        mRecyclerView.scrollToPosition(scrollPosition);
        mAdapter = new BlingAdapter(null);
        
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }
    
    @Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    	mProgressBar.setVisibility(View.VISIBLE);
    	return new BlingCursorLoader(getActivity());
    }

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mProgressBar.setVisibility(View.GONE);
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mProgressBar.setVisibility(View.GONE);
		mAdapter.swapCursor(null);
	}
 
	private static class BlingCursorLoader extends NotifyCursorLoader {

		public BlingCursorLoader(Context context) {
			super(context);
		}

		@Override
		protected Cursor fillCursor() {
			// can set specific columns to pass into BlingItemData here
			// (if was larger table)
			BlingItemData bd = new BlingItemData();
			return bd.select(null,null,MySQLiteHelper.COLUMN_BLING_ITEM_ID);
		}

		@Override
		protected Uri getNotificationUri() {
			return BlingItemData.URI;
		}
	}
    
    public class BlingAdapter extends RecyclerViewCursorAdapter<ItemHolder> {
    	
    	// start unselected, since no category selected
    	private int selectedPos = -1;   
    	
    	public BlingAdapter(Cursor cursor) {
    		super(cursor);
    	}

    	@Override
		public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(getActivity()).inflate(
					R.layout.adapter_bling_row, parent, false);
			ItemHolder holder = new ItemHolder(v);
			holder.btnBling = (Button) v.findViewById(R.id.btnBling);
			//holder.btnBling.setOnClickListener(this);
			holder.btnBling.setOnClickListener(listener);
			return holder;
		}

    	@Override
    	protected void onBindViewHolder(ItemHolder holder, Cursor cursor) {
    		final int position = cursor.getPosition();
    		BlingItemCursor blingCursor = (BlingItemCursor) cursor;
    		
    		holder.btnBling.setText(""+blingCursor.getMyInt());
			// store position in the button
			holder.btnBling.setId(blingCursor.getPosition());
			// store the cateogry id in the buttons tag
			holder.btnBling.setTag(blingCursor.getId());
			
			if (selectedPos == position) {
				// view not selected
				holder.btnBling.setBackgroundResource(R.drawable.bg_bling_select);
				holder.btnBling.setTextColor(Color.WHITE);
			} else {
				// view is selected
				holder.btnBling.setBackgroundResource(R.drawable.bg_bling);
				holder.btnBling.setTextColor(Color.BLACK);
			}
    	}
    	
    	public int getBlingAtPosition( int pos ) {
    		BlingItemCursor cursor = (BlingItemCursor)getCursor();
    		if (cursor.moveToPosition(pos)) {
    			return cursor.getId();
    		}
    		return -1;   //must be empty bling list
    	}
    	
    	
    	public void setSelectedPosition(int pos) {
    		// without the notifyItemRangeChanged, the recyclerview will
    		// scroll back up to the top after every selection
    		selectedPos = pos;
    		notifyItemRangeChanged(0, getItemCount());
    	}
    	
    	public int getSelectedPosition() {
    		return selectedPos;
    	}

		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				setSelectedPosition(v.getId());

				// Send the event to the host activity
				if (mCallback != null) {
					int blingId = (Integer) v.getTag();
					mCallback.onBlingSelected(blingId);
				}
			}
		};
    }
    	
	private static class ItemHolder extends RecyclerView.ViewHolder {

		private Button btnBling;

		public ItemHolder(View view) {
			super(view);
		}
	}
}
