package hu.smartcampus.templates;

import hu.smartcampus.R;
import hu.smartcampus.views.fragments.FragmentWelcome_;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;

public class MainMenuItem {
	public static final MainMenuItem defaultPage = new MainMenuItem(R.string.titleEvents, FragmentWelcome_.class, R.drawable.binocular);
	public String displayName;
	public int textId;
	public Class<? extends Fragment> fragmentClass;
	public int iconId;
	public Drawable profilePicture;

	public MainMenuItem(int textId, Class<? extends Fragment> targetFragment, int iconId) {
		this.textId = textId;
		this.fragmentClass = targetFragment;
		this.iconId = iconId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MainMenuItem other = (MainMenuItem) obj;
		if (fragmentClass == null) {
			if (other.fragmentClass != null)
				return false;
		} else if (!fragmentClass.equals(other.fragmentClass))
			return false;
		if (textId != other.textId)
			return false;
		return true;
	}

}