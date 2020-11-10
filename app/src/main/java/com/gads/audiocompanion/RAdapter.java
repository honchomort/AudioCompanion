package com.gads.audiocompanion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RAdapter extends RecyclerView.Adapter<RAdapter.ViewHolder>{

    RecyclerView mRecyclerView;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> urls = new ArrayList<>();
    Context context;

    public RAdapter(RecyclerView recyclerView, ArrayList<String> items, Context context) {
        mRecyclerView = recyclerView;
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.audio_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.mAudioTitle.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void update(String fileName, String url) {
        items.add(fileName);
        urls.add((url));
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mAudioTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAudioTitle = (TextView) itemView.findViewById(R.id.audioTitle);

            //itemView.setOnClickListener(new View.OnClickListener() {
            //    @Override
             //   public void onClick(View view) {

            //    }
           // });

        }
    }
}
