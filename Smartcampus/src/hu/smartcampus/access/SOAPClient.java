package hu.smartcampus.access;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpsTransportSE;
import org.ksoap2.transport.Transport;


public class SOAPClient {
	
	private final String namespace;
	
	public SOAPClient(String namespace) {
		this.namespace = namespace;
	}
	
	public SoapObject newRequest(String method) {
		SoapObject request = new SoapObject(namespace, method);
		return request;
	}
	
	public Object call(SoapObject request) throws Exception {
			String soapAction = request.getNamespace() + request.getName();
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
			new MarshalBase64().register(envelope);
			envelope.dotNet = false;
			envelope.setOutputSoapObject(request);
			Transport ht = 
					new HttpsTransportSE("www.inf.unideb.hu",443,"/smartcampus/CalendarService/?wsdl", 10000);
//			Log.d("lol", "env: " + envelope.bodyOut);
			ht.call('"'+soapAction+'"', envelope);
//			Log.d("lol", "env: " + envelope.bodyIn);
			return envelope.getResponse();
	}
	
}
