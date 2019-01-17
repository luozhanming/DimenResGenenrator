package com.example.administrator.dimenresgenenrator;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ReferenceAdapter extends RecyclerView.Adapter<ReferenceAdapter.ViewHolder> {

    private List<Integer> datas;

    public ReferenceAdapter(List<Integer> integers) {
        datas = integers;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View root = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_referece, viewGroup, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final Integer integer = datas.get(i);
        viewHolder.text.setText(integer + "dp");
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas.remove(integer);
                notifyItemRemoved(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void addReference(int reference) {
        this.datas.add(reference);
        notifyDataSetChanged();
    }

    public List<Integer> getDatas() {
        return datas;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
