package proj.me.wardrobe.dialogs;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.List;

import proj.me.wardrobe.R;
import proj.me.wardrobe.databinding.DialogItemBinding;
import proj.me.wardrobe.dialogs.model.DialogModel;
import proj.me.wardrobe.helper.Utils;


public class CustomAdapter_Dialog extends RecyclerView.Adapter<CustomAdapter_Dialog.ViewHolder_Dialog>{

	List<String> data;
	LayoutInflater inflater;
    int colorPosition;
	DialogCallback dialogCallback;
	Context context;

	public CustomAdapter_Dialog(List<String> data, DialogCallback dialogCallback)
	{
		this.dialogCallback = dialogCallback;
		this.data=data;
		context = dialogCallback.getDialogContext();
	}

	@Override
	public ViewHolder_Dialog onCreateViewHolder(ViewGroup parent, int viewType) {
		if(inflater == null) inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return new ViewHolder_Dialog(inflater.inflate(R.layout.dialog_item, null), dialogCallback);
	}

	@Override
	public void onBindViewHolder(ViewHolder_Dialog holder, int position) {
		if( position == colorPosition ) holder.dialogModel.backgroundColor.set(Utils.getVersionColor(context, R.color.title_of_selected_forms));
		else holder.dialogModel.backgroundColor.set(Utils.getVersionColor(context, R.color.white));
		holder.dialogModel.text.set(data.get(position));
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	static class ViewHolder_Dialog extends RecyclerView.ViewHolder implements View.OnClickListener{
		DialogModel dialogModel;
		DialogCallback dialogCallback;
		public ViewHolder_Dialog(View itemView, DialogCallback dialogCallback) {
			super(itemView);
			DialogItemBinding dialogItemBinding = DataBindingUtil.bind(itemView);
			dialogModel = new DialogModel();
			dialogItemBinding.setClickHandler(this);
			dialogItemBinding.setDialogModel(dialogModel);
			this.dialogCallback = dialogCallback;
		}

		@Override
		public void onClick(View view) {
			//send to dialog
			dialogCallback.selectedItem(dialogModel.text.get());
		}
	}

}