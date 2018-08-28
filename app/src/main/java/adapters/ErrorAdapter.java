package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pi.novobyte.com.pimp.R;

public class ErrorAdapter extends  RecyclerView.Adapter<ErrorAdapter.ViewHolder>{
    String  message = "Could not find anydata.";
    public ErrorAdapter() {
    }
    public ErrorAdapter(String message) {
        this.message = message;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.no_connection,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.msg.setText(message);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView msg ;
         ViewHolder(View itemView) {

            super(itemView);
            msg = itemView.findViewById(R.id.msg);
        }
    }
}
