package hu.smartcampus.access.calendar;

import hu.smartcampus.access.SOAPClient;
import hu.smartcampus.entities.Event;
import hu.smartcampus.entities.EventRankResult;
import hu.smartcampus.entities.FilterItem;
import hu.smartcampus.entities.RankType;
import hu.smartcampus.entities.RankValue;
import hu.smartcampus.entities.User;
import hu.unideb.inf.cipher.DES;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

public class CalendarAccessRemote {

	private final SimpleDateFormat ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
	private static final String NAMESPACE = "http://calendar.ws.smartcampus.inf.unideb.hu/";
	private final SOAPClient client = new SOAPClient(NAMESPACE);
	private List<Event> events = null;

	private Calendar ISO8601(String str) {
		try {
			str = str.substring(0, 22) + str.substring(23);
			Calendar r = Calendar.getInstance();
			r.setTime(ISO8601.parse(str));
			return r;
		} catch (ParseException pe) {
			pe.printStackTrace();
			return null;
		} catch (StringIndexOutOfBoundsException se) {
			se.printStackTrace();
			return null;
		}
	}

	private String ISO8601(Calendar c) {
		String s = ISO8601.format(c.getTime());
		return (s.substring(0, 22) + ":" + s.substring(22));
	}

	public Event getEvent(long eventId, String sessionId) throws Exception {
		SOAPClient client = new SOAPClient(NAMESPACE);
		SoapObject request = client.newRequest("getEvents");
		SoapObject pi = new SoapObject();
		pi.addProperty("item", eventId);
		request.addProperty("eventIds", pi);
		request.addProperty("sessionId", sessionId);
		Object result = client.call(request);
		if (result == null || !(result instanceof SoapObject)) {
			return null;
		}
		return toItemList((SoapObject) result, false).get(0);
	}

	private List<Event> toItemList(SoapObject response, boolean isNearby) {
		events = new ArrayList<Event>(response.getPropertyCount());
		for (int i = 0; i < response.getPropertyCount(); i++) {
			SoapObject item = (SoapObject) response.getProperty(i);
			int eventId = Integer.parseInt(item.getPrimitivePropertySafelyAsString("id"));
			String title = item.getPrimitivePropertySafelyAsString("title");
			String description = item.getPrimitivePropertySafelyAsString("description");
			Calendar eventStart = ISO8601(item.getPropertyAsString("eventStart"));
			Calendar eventEnd = ISO8601(item.getPropertyAsString("eventEnd"));
			Event r = new Event(eventId, title, description, eventStart, eventEnd);
			if (isNearby) {
				r.setNearby(true);
			}
			boolean editable = (Boolean.valueOf(item.getPrimitivePropertySafelyAsString("editable")));
			r.setProvided(Boolean.valueOf(item.getPrimitivePropertySafelyAsString("provided")));
			r.setEditable(editable);
			events.add(r);
			PropertyInfo pp = new PropertyInfo();
			for (int p = 0; p < item.getPropertyCount(); p++) {
				if (item.getProperty(p) instanceof SoapObject) {
					SoapObject so = (SoapObject) item.getProperty(p);
					item.getPropertyInfo(p, pp);
					if ("categories".equals(pp.getName())) {
						r.addCategory(Long.valueOf(so.getPropertyAsString("id")), so.getPropertyAsString("name"));
					}
					if ("providers".equals(pp.getName())) {
						r.addProvider(Long.valueOf(so.getPropertyAsString("id")), so.getPropertyAsString("name"));
					}
					if ("locations".equals(pp.getName())) {
						r.addLocation(Long.valueOf(so.getPropertyAsString("id")), so.getPropertyAsString("name"));
					}
					if ("marker".equals(pp.getName())) {
						r.setMarkerType(Integer.valueOf(so.getPropertyAsString("type")));
						if (so.hasProperty("comment"))
							r.setMarkerRemark(so.getPropertyAsString("comment"));
					}
					if ("rankResult".equals(pp.getName())) {
						r.setRankable(true);
						EventRankResult err = new EventRankResult();
						err.setRankId(Long.valueOf(so.getPropertyAsString("rankId")));
						err.setCount(Long.valueOf(so.getPropertyAsString("count")));
						if (err.getCount() > 0)
							err.setRankValue(Double.valueOf(so.getPropertyAsString("rankValue")));
						err.setTitle(so.getPropertyAsString("title"));
						r.addRankResult(err);
					}
				}
			}
		}
		return events;
	}

	public List<Event> listMarked(String sessionId) throws Exception {
		Calendar from = Calendar.getInstance();
		// from.set(Calendar.HOUR_OF_DAY, 0);
		// from.add(Calendar.MONTH, -5); //TODO kivenni
		Calendar to = Calendar.getInstance();
		to.set(Calendar.YEAR, to.get(Calendar.YEAR) + 1);
		SoapObject request = client.newRequest("listMarked");
		request.addProperty("from", ISO8601(from));
		request.addProperty("to", ISO8601(to));
		request.addProperty("sessionId", sessionId);
		Object result = client.call(request);
		if (result == null || !(result instanceof SoapObject)) {
			return new ArrayList<Event>(); // ne null
		}
		final SoapObject response = (SoapObject) result;
		return toItemList(response, false);
	}

	public boolean markEvent(String sessionId, int eventId, String comment) throws Exception {
		SoapObject request = client.newRequest("markEvent");
		request.addProperty("sessionId", sessionId);
		request.addProperty("eventId", eventId);
		request.addProperty("comment", comment);
		client.call(request);
		return true;
	}

	public boolean unmarkEvent(String sessionId, int eventId) throws Exception {
		SoapObject request = client.newRequest("unmarkEvent");
		request.addProperty("sessionId", sessionId);
		request.addProperty("eventId", eventId);
		client.call(request);
		return true;
	}

	public String[] getSessionInfo(String sessionId) throws Exception {
		SoapObject request = client.newRequest("getSessionInfo");
		request.addPropertyIfValue("sessionId", sessionId);
		final SoapObject response = (SoapObject) client.call(request); // TODO
																		// gyenge
																		// net
																		// esetén
																		// lehet
																		// unknownhostexception
																		// ami
																		// le
																		// lett
																		// ekzelve
																		// de
																		// nullt
																		// ad
																		// vissza
		if (response != null) {
			if (response.getPropertyCount() == 0) {
				return null;
			}
			String[] result = new String[2];
			result[0] = response.getPropertySafelyAsString("displayName");
			result[1] = response.getPropertySafelyAsString("userName");
			return result;
		} else {
			return null; // TODO csak üreset
		}
	}

	public List<Event> listFiltered(String sessionId, Calendar from, Calendar to, HashMap<String, ArrayList<Long>> filters) throws Exception {
		SoapObject request = client.newRequest("listFiltered");
		request.addProperty("from", ISO8601(from));
		request.addProperty("to", ISO8601(to));
		request.addProperty("sessionId", sessionId);
		request.addProperty("strict", false);
		ArrayList<Long> categoriesArray = null;
		ArrayList<Long> providersArray = null;
		ArrayList<Long> locationsArray = null;
		boolean isNearby = false;
		if (filters != null && filters.size() != 0) {
			categoriesArray = filters.get("categories");
			providersArray = filters.get("providers");
			locationsArray = filters.get("locations");
			if (filters.get("isNearby") != null) {
				isNearby = true;
			}
		}
		if (categoriesArray != null) {
			SoapObject item = new SoapObject();
			request.addProperty("categories", item);
			for (Long l : categoriesArray) {
				item.addProperty("item", l);
			}
		} else {
			request.addProperty("categories", null);
		}
		if (providersArray != null) {
			SoapObject item = new SoapObject();
			request.addProperty("providers", item);
			for (Long l : providersArray) {
				item.addProperty("item", l);
			}
		} else {
			request.addProperty("providers", null);
		}
		if (locationsArray != null) {
			SoapObject item = new SoapObject();
			request.addProperty("locations", item);
			for (Long l : locationsArray) {
				item.addProperty("item", l);
			}
		} else {
			request.addProperty("locations", null);
		}
		Object result = client.call(request);
		if (result == null || !(result instanceof SoapObject)) {
			return new ArrayList<Event>();
		}
		final SoapObject response = (SoapObject) result;
		return toItemList(response, isNearby);
	}

	public List<RankType> getEventRankTypes(long eventId) throws Exception {
		SOAPClient client = new SOAPClient(NAMESPACE);
		SoapObject request = client.newRequest("listEventRankTypes");
		request.addProperty("eventId", eventId);
		Object result = client.call(request);
		final SoapObject response = (SoapObject) result;
		List<RankType> rankTypes = new ArrayList<RankType>();
		for (int i = 0; i < response.getPropertyCount(); i++) {
			SoapObject item = (SoapObject) response.getProperty(i);
			RankType rt = new RankType();
			rt.setId(Long.parseLong(item.getPrimitivePropertySafelyAsString("id")));
			rt.setTitle(item.getPrimitivePropertySafelyAsString("title"));
			rt.setDescription(item.getPrimitivePropertySafelyAsString("description"));
			ArrayList<RankValue> values = new ArrayList<RankValue>();
			for (int p = 0; p < item.getPropertyCount(); p++) {
				PropertyInfo pp = new PropertyInfo();
				if (item.getProperty(p) instanceof SoapObject) {
					SoapObject so = (SoapObject) item.getProperty(p);
					item.getPropertyInfo(p, pp);
					if ("values".equals(pp.getName())) {
						RankValue rv = new RankValue();
						rv.setValue(Integer.valueOf(so.getPropertyAsString("value")));
						rv.setDescription(so.getPropertyAsString("description"));
						values.add(rv);
					}
				}
			}
			rt.setValues(values.toArray(new RankValue[0]));
			rankTypes.add(rt);
		}
		return rankTypes;
	}

	public void closeSession(String sessionId) throws Exception {
		SoapObject request = client.newRequest("closeSession");
		request.addPropertyIfValue("sessionId", sessionId);
		client.call(request);
	}

	public User openSession(String username, String password) throws Exception {
		SoapObject request = client.newRequest("openSession");
		request.addProperty("credential", DES.encriptStr(password));
		request.addProperty("principal", username);
		Object result = client.call(request);
		if (result == null || !(result instanceof SoapPrimitive)) {
			return null;
		}
		SoapPrimitive response = (SoapPrimitive) result;
		String sessionID = response.toString();
		if (sessionID != null) {
			return new User(sessionID);
		}
		return null;
	}

	public ArrayList<FilterItem> categories() throws Exception {
		SoapObject request = client.newRequest("categories");
		Object result = client.call(request);
		if (result == null || !(result instanceof SoapObject)) {
			return null;
		}
		final SoapObject response = (SoapObject) result;
		ArrayList<FilterItem> list = new ArrayList<FilterItem>();
		for (int i = 0; i < response.getPropertyCount(); ++i) {
			SoapObject item = (SoapObject) response.getProperty(i);
			long id = Long.parseLong(item.getPrimitivePropertyAsString("id"));
			String name = item.getPrimitivePropertyAsString("name");
			list.add(new FilterItem(id, name));
		}
		return list;
	}

	public ArrayList<FilterItem> providers() throws Exception {
		SoapObject request = client.newRequest("providers");
		Object result = client.call(request);
		if (result == null || !(result instanceof SoapObject)) {
			return null; // TODO ne nullt adjon vissza
		}
		final SoapObject response = (SoapObject) result;
		ArrayList<FilterItem> list = new ArrayList<FilterItem>();
		for (int i = 0; i < response.getPropertyCount(); ++i) {
			SoapObject item = (SoapObject) response.getProperty(i);
			long id = Long.parseLong(item.getPrimitivePropertyAsString("id"));
			String name = item.getPrimitivePropertyAsString("name");
			list.add(new FilterItem(id, name));
		}
		return list;
	}

	public ArrayList<FilterItem> locations() throws Exception {
		SoapObject request = client.newRequest("locations");
		Object result = client.call(request);
		if (result == null || !(result instanceof SoapObject)) {
			return null;
		}
		final SoapObject response = (SoapObject) result;
		ArrayList<FilterItem> list = new ArrayList<FilterItem>();
		for (int i = 0; i < response.getPropertyCount(); ++i) {
			SoapObject item = (SoapObject) response.getProperty(i);
			long id = Long.parseLong(item.getPrimitivePropertyAsString("id"));
			String name = item.getPrimitivePropertyAsString("name");
			list.add(new FilterItem(id, name));
		}
		return list;
	}

	public List<FilterItem> locationsFiltered(String roomName) throws Exception {
		SoapObject request = client.newRequest("locationsFiltered");
		request.addProperty("arg0", roomName);
		Object result = client.call(request);
		if (result == null || !(result instanceof SoapObject)) {
			return new ArrayList<FilterItem>();
		}
		final SoapObject response = (SoapObject) result;
		ArrayList<FilterItem> list = new ArrayList<FilterItem>();
		for (int i = 0; i < response.getPropertyCount(); ++i) {
			SoapObject item = (SoapObject) response.getProperty(i);
			long id = Long.parseLong(item.getPrimitivePropertyAsString("id"));
			String name = item.getPrimitivePropertyAsString("name");
			list.add(new FilterItem(id, name));
		}
		return list;
	}

	public void rankEvent(String sessionId, long eventId, long rankId, int value, String ip) throws Exception {
		SOAPClient client = new SOAPClient(NAMESPACE);
		SoapObject request = client.newRequest("addEventRank");
		request.addProperty("sessionId", sessionId);
		request.addProperty("eventId", eventId);
		request.addProperty("rankId", rankId);
		request.addProperty("value", value);
		request.addProperty("ip", ip);
		client.call(request);
	}

	public List<String> getSubscribers(String sessionId, long eventId) throws Exception {
		SOAPClient client = new SOAPClient(NAMESPACE);
		SoapObject request = client.newRequest("getSubscribers");
		request.addProperty("sessionId", sessionId);
		request.addProperty("eventId", eventId);
		Object result = client.call(request);
		final SoapObject response = (SoapObject) result;
		ArrayList<String> list = new ArrayList<String>();
		for (int p = 0; p < response.getPropertyCount(); p++) {
			list.add(response.getProperty(p).toString());
		}
		return list;
	}

}