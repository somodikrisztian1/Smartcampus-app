package hu.smartcampus.views.dialogs;

import hu.smartcampus.R;
import hu.smartcampus.adapters.MenuArrayAdapter;
import hu.smartcampus.dao.UserDAOImpl;
import hu.smartcampus.functions.ApplicationFunctions;
import hu.smartcampus.functions.SystemFunctions;
import hu.smartcampus.views.activities.ActivityMain;
import hu.smartcampus.views.toaster.Toaster;

import org.androidannotations.annotations.EFragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

@EFragment(R.layout.profile_picture_select)
public class DialogProfilePicture extends DialogFragment implements OnClickListener {

	private static final int REQUEST_IMAGE_CAPTURE = 0;
	private static final int SELECT_PHOTO = 100;
	private ImageView profilePictureView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		getDialog().setTitle("Kép kiválasztása");
		Button pictureFromCamera = (Button) view.findViewById(R.id.pictureFromCamera);
		pictureFromCamera.setOnClickListener(this);
		Button pictureFromGallery = (Button) view.findViewById(R.id.pictureFromGallery);
		pictureFromGallery.setOnClickListener(this);
		profilePictureView = (ImageView) view.findViewById(R.id.viewProfilePicture);

		// ha be van jelentkezve és van is képe, csak akkor a hozzá tartozó
		// képet tölti be
		if (ApplicationFunctions.getInstance().getUserFunctions().getLoggedInUser().profilePicture != null)
			profilePictureView.setImageBitmap(ApplicationFunctions.getInstance().getUserFunctions().getLoggedInUser().profilePicture);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.pictureFromCamera) {
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			}
		} else if (v.getId() == R.id.pictureFromGallery) {
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, SELECT_PHOTO);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		try { // TODO majd kivenni
			// csak akkor lép be ide, ha mentetted a képet a kamerával
			if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
				// csak akkor lép be ide, ha mentetted a képet a kamerával
				Bundle extras = data.getExtras();
				Bitmap imageBitmap = (Bitmap) extras.get("data"); // bitmap-ot
																	// lementi
																	// ide
				setImage(imageBitmap);
			}

			else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode != Activity.RESULT_CANCELED) {
				// nem cancelled
				Bundle extras = data.getExtras();
				Bitmap imageBitmap = (Bitmap) extras.get("data"); // bitmap-ot
																	// lementi
																	// ide
				setImage(imageBitmap);
			}

			// gallériából választja ki
			else if (requestCode == SELECT_PHOTO && resultCode == Activity.RESULT_OK) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();
				Bitmap imageBitmap = BitmapFactory.decodeFile(filePath);
				setImage(imageBitmap);
			}
			// else
			// {
			// if (imageBitmap == null)
			// Toast.makeText(this, "Nem lett profilkép kiválasztva/készítve",
			// Toast.LENGTH_LONG).show();
			// }
		} catch (Exception e) {
			Toaster.otherException(getActivity());
		}
	}

	private void setImage(Bitmap imageBitmap) {
		TypedValue out = new TypedValue();
		getResources().getValue(R.dimen.profilePicThumbnailSize, out, true);
		int thumbnailSize = Integer.parseInt((String) out.coerceToString());
		Bitmap thumbnail = ThumbnailUtils.extractThumbnail(imageBitmap, MenuArrayAdapter.text.getCurrentHeight() + thumbnailSize,
				MenuArrayAdapter.text.getCurrentHeight() + thumbnailSize); // átméretezi
																			// a
																			// képet
		UserDAOImpl ud = new UserDAOImpl(getActivity().getApplicationContext());
		ud.updateProfilePic(SystemFunctions.bitMapToString(thumbnail));
		Drawable profilePic = new BitmapDrawable(getResources(), thumbnail);
		ActivityMain.top.profilePicture = profilePic;
		if (ApplicationFunctions.getInstance().getUserFunctions().getLoginSatus()) {
			ApplicationFunctions.getInstance().getUserFunctions().getLoggedInUser().profilePicture = thumbnail;
		}
		profilePictureView.setImageBitmap(imageBitmap);
	}

}