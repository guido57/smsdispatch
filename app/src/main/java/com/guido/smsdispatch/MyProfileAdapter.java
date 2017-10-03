package com.guido.smsdispatch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Guido on 26/08/2017.
 */


public class MyProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ProfileItem> mDataset;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class FromToViewHolder extends RecyclerView.ViewHolder {

        private CheckBox cbEnabled;
        private EditText editText;
        private Button btnDelete;

        public FromToViewHolder(View v) {
            super(v);
        }

    }
    class ButtonViewHolder extends RecyclerView.ViewHolder {

        private Button btnProfileView;

        public ButtonViewHolder(View v) {
            super(v);
        }
    }
    class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView headerText;

        public HeaderViewHolder(View v) {
            super(v);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyProfileAdapter(ArrayList<ProfileItem> myDataset, Context context) {
        mDataset = myDataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        switch(viewType){
            case ItemType.From:
            case ItemType.To:
                // create a new view
                View v = (View) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profile_view, parent, false);
                // set the view's size, margins, paddings and layout parameters
                RecyclerView.ViewHolder vh = new FromToViewHolder(v);
                return vh;

            case ItemType.ButtonAddTo:
            case ItemType.ButtonAddFrom:
                // create a new view
                View vb = (View) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profile_button_view, parent, false);
                // set the view's size, margins, paddings and layout parameters
                RecyclerView.ViewHolder vhb = new ButtonViewHolder(vb);
                return vhb;

            case ItemType.HeaderFrom:
            case ItemType.HeaderTo:
                // create a new view
                View vhh = (View) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profile_header_view, parent, false);
                // set the view's size, margins, paddings and layout parameters
                RecyclerView.ViewHolder vhhh = new HeaderViewHolder(vhh);
                return vhhh;
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        switch(holder.getItemViewType()){
            case ItemType.To:
            case ItemType.From:

                FromToViewHolder fromToViewHolder = (FromToViewHolder) holder;
                EditText et = (EditText) fromToViewHolder.itemView.findViewById(R.id.editText);
                et.setText(mDataset.get(position).Text);
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        //HA HA: don't use position
                        // mDataset.get(position).Text =  s.toString();
                        //HA HA: use instead holder.getAdapterPosition
                        mDataset.get(holder.getAdapterPosition()).Text =  s.toString();
                        ProfileActivity.saveMyDataset(mDataset, mContext );
                    }
                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                    }
                });
                CheckBox enabled = (CheckBox) fromToViewHolder.itemView.findViewById(R.id.cbEnabled);
                enabled.setChecked(mDataset.get(position).Enabled);
                enabled.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mDataset.get(position).Enabled = ((CheckBox) v).isChecked();
                        ProfileActivity.saveMyDataset(mDataset, mContext );
                        notifyDataSetChanged();
                    }
                });

                Button btnDelete = (Button) fromToViewHolder.itemView.findViewById(R.id.btnDelete);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // delete this item
                        mDataset.remove(position);
                        ProfileActivity.saveMyDataset(mDataset, mContext );
                        notifyDataSetChanged();
                    }
                });

                break;

            case ItemType.ButtonAddTo:
                ButtonViewHolder buttonToViewHolder = (ButtonViewHolder) holder;
                Button btnAddTo = (Button) buttonToViewHolder.itemView.findViewById(R.id.btnProfileView);
                btnAddTo.setText(mDataset.get(position).Text);
                // btnAddTo.setText("my position is " + Integer.toString(position));
                btnAddTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // add an item below the button "SEND SMS TO"
                        ProfileItem pi = new ProfileItem();
                        pi.Type = ItemType.To;
                        pi.Enabled = true;
                        pi.Text = "new SMS recipient at position " + Integer.toString(position+1);
                        if(position == mDataset.size()-1)
                            // there's no item below
                            mDataset.add(pi); // add at the end
                        else
                            mDataset.add(position+1,pi);
                        ProfileActivity.saveMyDataset(mDataset, mContext );
                        notifyDataSetChanged();

                    }
                });
                break;
            case ItemType.ButtonAddFrom:
                ButtonViewHolder buttonFromViewHolder = (ButtonViewHolder) holder;
                Button btnAddFrom = (Button) buttonFromViewHolder.itemView.findViewById(R.id.btnProfileView);
                btnAddFrom.setText(mDataset.get(position).Text);
                // btnAddFrom.setText("my position is " + Integer.toString(position));
                btnAddFrom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // add an item below the button "Add From"
                        ProfileItem pi = new ProfileItem();
                        pi.Type = ItemType.From;
                        pi.Enabled = true;
                        pi.Text = "new SMS sender at position " + Integer.toString(position+1);
                        mDataset.add(position+1,pi);
                        ProfileActivity.saveMyDataset(mDataset, mContext );
                        notifyDataSetChanged();
                    }
                });
                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        // The ItemType is stored in attribute Type
        //
        ProfileItem pi =  mDataset.get(position);
        return pi.Type;
    }




}
