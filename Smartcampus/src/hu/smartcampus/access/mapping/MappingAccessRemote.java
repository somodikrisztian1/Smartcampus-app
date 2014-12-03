package hu.smartcampus.access.mapping;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpsTransportSE;
import org.ksoap2.transport.Transport;

public class MappingAccessRemote {

	private static final String NAMESPACE = "http://wifi.smartcampus.inf.unideb.hu/";

	public List<String> roomList(String ssid, String bssid, int level) throws Exception {
		SoapObject response = null;
		SoapObject request = new SoapObject(NAMESPACE, "roomList");
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
		envelope.encodingStyle = SoapSerializationEnvelope.XSD;
		envelope.setOutputSoapObject(request);
		PropertyInfo pi = new PropertyInfo();
		pi.setName("ssid");
		pi.setValue(ssid);
		request.addProperty(pi);
		SoapObject so = new SoapObject();
		SoapObject so1 = new SoapObject();
		so1.addProperty("bssid", bssid);
		so1.addProperty("level", level);
		so.addProperty("item", so1);

		// SoapObject so2 = new SoapObject();
		// so2.addProperty("bssid","64:d9:89:1c:3d:70");
		// so2.addProperty("level",22);
		// so.addProperty("item",so2);

		pi = new PropertyInfo();
		pi.setName("bssidList");
		pi.setValue(so);
		request.addProperty(pi);

		pi = new PropertyInfo();
		pi.setName("strict");
		pi.setValue(false);
		request.addProperty(pi);

		Transport ht = new HttpsTransportSE("www.inf.unideb.hu", 443, "/smartcampus/bssid/mapping?wsdl", 10000);
		ht.call('"' + NAMESPACE + "roomList" + '"', envelope);
		response = (SoapObject) envelope.getResponse();
		if (response != null) {
			PropertyInfo p2 = new PropertyInfo();
			ArrayList<String> result = new ArrayList<String>(response.getPropertyCount());
			boolean isFirst = true;
			boolean wasRoom = false;
			for (int i = 0; i < response.getPropertyCount(); i++) {
				response.getPropertyInfo(i, p2);
				SoapObject item = (SoapObject) response.getProperty(i);
				PropertyInfo pp = new PropertyInfo();
				for (int p = 0; p < item.getPropertyCount(); p++) {
					item.getPropertyInfo(p, pp);
					if (p2.getName().equals("room")) {
						wasRoom = true;
						if (pp.getName().equals("id")) {
							result.add(pp.getValue().toString().split("@")[0]);
						}
					} else if (isFirst && p2.getName().equals("area")) {
						if (pp.getName().equals("title")) {
							isFirst = false;
							result.add(pp.getValue().toString());
						}
					}
				}
			}
			if (!wasRoom && result.size() > 0) {
				result.add(result.get(0));
			}
			return result;
		} else {
			return null;
		}
	}
}