package hu.smartcampus;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import android.app.Application;
import android.content.Context;

public class SmartcampusApplication extends Application {

	private static Context context;

	private void loadtoKeystore() {
		try {
			// Get an instance of the Bouncy Castle KeyStore format
			KeyStore trusted = KeyStore.getInstance("BKS");
			// Get the raw resource, which contains the keystore with
			// your trusted certificates (root and any intermediate certs)
			InputStream in = context.getResources().openRawResource(R.raw.mykeystore);
			try {
				// Initialize the keystore with the provided trusted
				// certificates
				// Also provide the password of the keystore
				trusted.load(in, "mysecret".toCharArray());
			} finally {
				in.close();
			}
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			// Initialise the TMF as you normally would, for example:
			tmf.init(trusted);
			TrustManager[] trustManagers = tmf.getTrustManagers();
			final X509TrustManager origTrustmanager = (X509TrustManager) trustManagers[0];
			TrustManager[] wrappedTrustManagers = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return origTrustmanager.getAcceptedIssuers();
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
					origTrustmanager.checkClientTrusted(certs, authType);
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
					try {
						origTrustmanager.checkServerTrusted(certs, authType);
					} catch (CertificateExpiredException e) {
						e.printStackTrace();
					} catch (CertificateException e) {
						e.printStackTrace();
					}
				}
			} };
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, wrappedTrustManagers, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		loadtoKeystore();
	}

	public static Context getAppContext() {
		return context;
	}

}