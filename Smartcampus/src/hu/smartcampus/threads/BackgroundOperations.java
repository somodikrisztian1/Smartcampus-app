package hu.smartcampus.threads;

import hu.smartcampus.dao.EventDAO;
import hu.smartcampus.dao.EventsDAOImpl;
import hu.smartcampus.dao.UserDAOImpl;
import hu.smartcampus.entities.Event;
import hu.smartcampus.entities.FilterItem;
import hu.smartcampus.entities.RankType;
import hu.smartcampus.entities.User;
import hu.smartcampus.functions.ApplicationFunctions;
import hu.smartcampus.templates.CustomActionBarActivity;
import hu.smartcampus.utilities.OrientationLocker;
import hu.smartcampus.views.activities.ActivityMain;
import hu.smartcampus.views.fragments.FragmentDetails;
import hu.smartcampus.views.fragments.FragmentLogin;
import hu.smartcampus.views.fragments.FragmentRatings;
import hu.smartcampus.views.fragments.FragmentTitles;
import hu.smartcampus.views.fragments.FragmentWelcome;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import android.support.v4.app.Fragment;
import android.view.View;

@EBean
public class BackgroundOperations {

	@Background
	public void openSession(String username, String password, Fragment fragment) {
		CustomActionBarActivity activity = (CustomActionBarActivity) fragment.getActivity();
		activity.showDialogAndLock();
		try {
			boolean success = ApplicationFunctions.getInstance().getCalendarFunctions().openSession(username, password);
			if (!success) {
				if (fragment instanceof FragmentLogin) {
					FragmentLogin frag = (FragmentLogin) fragment;
					frag.afterLogin(false);
				}
			} else {
				if (fragment instanceof FragmentLogin) {
					FragmentLogin frag = (FragmentLogin) fragment;
					getSessionInfo(activity);
					frag.afterLogin(true);
				}
			}
		} catch (Exception e) {
			activity.handleError(e);
		}
		activity.cancelDialogAndUnlock();
	}

	private void getSessionInfo(CustomActionBarActivity activity) {
		try {
			String sessionId = ApplicationFunctions.getInstance().getUserFunctions().getLoggedInUser().getSessionId();
			String[] result = ApplicationFunctions.getInstance().getCalendarFunctions().getSessionInfo(sessionId);
			User loggedUser = ApplicationFunctions.getInstance().getUserFunctions().getLoggedInUser();
			loggedUser.setDisplayName(result[0]);
			loggedUser.setUserName(result[1]);
			UserDAOImpl ud = new UserDAOImpl(activity.getApplicationContext());
			ud.insertUser(loggedUser);
			ActivityMain.setTopMenuName(result[0]);
		} catch (Exception e) {
			activity.handleError(e);
		}
	}

	@Background
	public void listMarked(Fragment fragment) {
		CustomActionBarActivity activity = (CustomActionBarActivity) fragment.getActivity();
		activity.showDialogAndLock();
		List<Event> markedEvents = null;
		boolean success = false;
		try {
			String sessionId = ApplicationFunctions.getInstance().getUserFunctions().getLoggedInUser().getSessionId();
			markedEvents = ApplicationFunctions.getInstance().getCalendarFunctions().listMarked(sessionId);
			EventDAO dao = new EventsDAOImpl(activity.getApplicationContext());
			dao.insertMarkedEvents(markedEvents);
			success = true;
		} catch (Exception e) {
			activity.handleError(e);
		}
		if (success) {
			if (fragment instanceof FragmentTitles) {
				FragmentTitles frag = (FragmentTitles) fragment;
				frag.updateAdapter(markedEvents);
			}
		} else {
			if (fragment instanceof FragmentTitles) {
				FragmentTitles frag = (FragmentTitles) fragment;
				frag.loadFromLocal();
			}
		}
		activity.cancelDialogAndUnlock();
	}

	@Background
	public void logout(CustomActionBarActivity activity) {
		activity.showDialogAndLock();
		String sessionId = ApplicationFunctions.getInstance().getUserFunctions().getLoggedInUser().getSessionId();
		try {
			ApplicationFunctions.getInstance().getCalendarFunctions().closeSession(sessionId);
			ActivityMain act = (ActivityMain) activity;
			act.loginOrPersonal();
			act.onBackPressed();
		} catch (Exception e) {
			activity.handleError(e);
		}
		activity.cancelDialogAndUnlock();
	}

	@Background
	public void getEventRankTypes(int eventId, Fragment fragment) {
		CustomActionBarActivity activity = (CustomActionBarActivity) fragment.getActivity();
		activity.showDialogAndLock();
		List<RankType> eventRankTypes = null;
		boolean success = false;
		try {
			eventRankTypes = ApplicationFunctions.getInstance().getCalendarFunctions().getEventRankTypes(eventId);
			success = true;
		} catch (Exception e) {
			activity.handleError(e);
		}
		if (success) {
			if (fragment instanceof FragmentRatings) {
				FragmentRatings frag = (FragmentRatings) fragment;
				frag.updateView(eventRankTypes);
			}
		}
		activity.cancelDialogAndUnlock();
	}

	@Background
	public void categories(Fragment fragment) {
		CustomActionBarActivity activity = (CustomActionBarActivity) fragment.getActivity();
		activity.showDialogAndLock();
		ArrayList<FilterItem> categories = null;
		boolean success = false;
		try {
			categories = ApplicationFunctions.getInstance().getCalendarFunctions().categories();
			success = true;
		} catch (Exception e) {
			activity.handleError(e);
		}
		if (success) {
			if (fragment instanceof FragmentWelcome) {
				FragmentWelcome frag = (FragmentWelcome) fragment;
				frag.postCategories(categories);
			}
		}
		activity.cancelDialogAndUnlock();
	}

	@Background
	public void providers(Fragment fragment) {
		CustomActionBarActivity activity = (CustomActionBarActivity) fragment.getActivity();
		activity.showDialogAndLock();
		ArrayList<FilterItem> providers = null;
		boolean success = false;
		try {
			providers = ApplicationFunctions.getInstance().getCalendarFunctions().providers();
			success = true;
		} catch (Exception e) {
			activity.handleError(e);
		}
		if (success) {
			if (fragment instanceof FragmentWelcome) {
				FragmentWelcome frag = (FragmentWelcome) fragment;
				frag.postProviders(providers);
			}
		}
		activity.cancelDialogAndUnlock();
	}

	@Background
	public void locations(Fragment fragment) {
		CustomActionBarActivity activity = (CustomActionBarActivity) fragment.getActivity();
		activity.showDialogAndLock();
		ArrayList<FilterItem> locations = null;
		boolean success = false;
		try {
			locations = ApplicationFunctions.getInstance().getCalendarFunctions().locations();
			success = true;
		} catch (Exception e) {
			activity.handleError(e);
		}
		if (success) {
			if (fragment instanceof FragmentWelcome) {
				FragmentWelcome frag = (FragmentWelcome) fragment;
				frag.postLocations(locations);
			}
		}
		activity.cancelDialogAndUnlock();
	}

	@Background
	public void listFiltered(Calendar[] dates, HashMap<String, ArrayList<Long>> filters, Fragment fragment) {
		CustomActionBarActivity activity = (CustomActionBarActivity) fragment.getActivity();
		if (!OrientationLocker.isLocked()) {
			activity.showDialogAndLock();
		}
		List<Event> listFiltered = null;
		boolean success = false;
		try {
			User loggedInUser = ApplicationFunctions.getInstance().getUserFunctions().getLoggedInUser();
			String sessionId = null;
			if (loggedInUser != null) {
				sessionId = loggedInUser.getSessionId();
			}
			listFiltered = ApplicationFunctions.getInstance().getCalendarFunctions().listFiltered(sessionId, dates[0], dates[1], filters);
			success = true;
		} catch (Exception e) {
			activity.handleError(e);
		}
		if (success) {
			if (fragment instanceof FragmentTitles) {
				FragmentTitles frag = (FragmentTitles) fragment;
				frag.updateAdapter(listFiltered);
			}
		}
		if (OrientationLocker.isLocked()) {
			activity.cancelDialogAndUnlock();
		}
	}

	@Background
	public void getSubscribers(int eventID, Fragment fragment) {
		CustomActionBarActivity activity = (CustomActionBarActivity) fragment.getActivity();
		activity.showDialogAndLock();
		List<String> subscribers = null;
		boolean success = false;
		try {
			String sessionID = ApplicationFunctions.getInstance().getUserFunctions().getLoggedInUser().getSessionId();
			subscribers = ApplicationFunctions.getInstance().getCalendarFunctions().getSubscribers(sessionID, eventID);
			success = true;
		} catch (Exception e) {
			activity.handleError(e);
		}
		if (success) {
			if (fragment instanceof FragmentDetails) {
				FragmentDetails frag = (FragmentDetails) fragment;
				frag.showSubscribersDialog(subscribers);
			}
		}
		activity.cancelDialogAndUnlock();
	}

	@Background
	public void markEvent(int eventID, boolean isMark, Fragment fragment) {
		CustomActionBarActivity activity = (CustomActionBarActivity) fragment.getActivity();
		activity.showDialogAndLock();
		boolean b = false;
		boolean success = false;
		try {
			String sessionID = ApplicationFunctions.getInstance().getUserFunctions().getLoggedInUser().getSessionId();
			if (isMark) {
				b = ApplicationFunctions.getInstance().getCalendarFunctions().markEvent(sessionID, eventID, "");
			} else {
				b = ApplicationFunctions.getInstance().getCalendarFunctions().unmarkEvent(sessionID, eventID);
			}
			success = true;
		} catch (Exception e) {
			activity.handleError(e);
		}
		if (success) {
			if (fragment instanceof FragmentDetails) {
				FragmentDetails frag = (FragmentDetails) fragment;
				frag.afterMark(b);
			}
		}
		activity.cancelDialogAndUnlock();
	}

	@Background
	public void rankEvent(String sessionId, long eventId, long rankId, int value, String ip, View v, Fragment fragment) {
		CustomActionBarActivity activity = (CustomActionBarActivity) fragment.getActivity();
		activity.showDialogAndLock();
		boolean success = false;
		try {
			ApplicationFunctions.getInstance().getCalendarFunctions().rankEvent(sessionId, eventId, rankId, value, ip);
			success = true;
		} catch (Exception e) {
			activity.handleError(e);
		}
		if (success) {
			if (fragment instanceof FragmentRatings) {
				FragmentRatings frag = (FragmentRatings) fragment;
				frag.lockView(success, v);
			}
		}
		activity.cancelDialogAndUnlock();
	}

	@Background
	public void getSessionInfoAtStart(CustomActionBarActivity activity) {
		activity.showDialogAndLock();
		try {
			UserDAOImpl ud = new UserDAOImpl(activity.getApplicationContext());
			User user = ud.getUser();
			if (user != null && user.getSessionId() != null) {
				String[] result = null;
				result = ApplicationFunctions.getInstance().getCalendarFunctions().getSessionInfo(user.getSessionId());
				if (activity instanceof ActivityMain) {
					ActivityMain a = (ActivityMain) activity;
					if (result != null && result[0] != null && result[1] != null) {
						ApplicationFunctions.getInstance().getUserFunctions().setLoggedInUser(user);
						ActivityMain.setTopMenuName(result[0]);
						a.loginOrPersonal();
					} else {
						ud.deleteUserWrongSession();
						ApplicationFunctions.getInstance().getUserFunctions().logout();
					}
				}
			}
		} catch (Exception e) {
			activity.handleError(e);
		}
		activity.cancelDialogAndUnlock();
	}

}