package proj.me.wardrobe.dialogs;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import proj.me.wardrobe.R;
import proj.me.wardrobe.frag.ActivityCallback;
import proj.me.wardrobe.helper.Utils;


public class CustomDialogFrag extends DialogFragment implements DialogCallback {

	Context context;
	ImageIntent imageIntent;
	ActivityCallback activityCallback;

	public void setImageIntent(ImageIntent imageIntent){
		this.imageIntent = imageIntent;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.context = context;
		try{
			activityCallback = (ActivityCallback) context;
		}catch (ClassCastException e){
			Utils.logMessage("activity must implement callback");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View view = inflater.inflate(R.layout.dialog_recycler, container,false);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		context = getActivity();

		List<String> data=new ArrayList<>();
		data.add("TAKE PICTURE");
		data.add("FETCH PHOTO FROM GALLERY");

		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.dialog_recycler);
		recyclerView.setLayoutManager(new LinearLayoutManager(context));


		CustomAdapter_Dialog customAdapter_dialog = new CustomAdapter_Dialog(data, this);
		recyclerView.setAdapter(customAdapter_dialog);

	}

	@Override
	public void selectedItem(String value) {
		if(value.equals("TAKE PICTURE")){
			imageIntent.takePhoto(activityCallback);
			this.dismiss();
		}else{
			imageIntent.fetchFromGallery(activityCallback);
			this.dismiss();
		}
	}

	@Override
	public Context getDialogContext() {
		return context;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		context = null;
		imageIntent = null;
		activityCallback = null;
	}
}