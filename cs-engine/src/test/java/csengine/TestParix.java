package csengine;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:application.properties")
public class TestParix {
	@Value("${infotn.parix.endpoint}")
	private String endpoint;

	@Value("${infotn.parix.keystore}")
	private String keystore;

	@Value("${infotn.parix.keystore.password}")
	private String keystorePassword;

	@Value("${infotn.parix.user}")
	private String user;

	@Value("${infotn.parix.password}")
	private String password;
	
	SSLSocketFactory sslSocketFactory;
	
	@Before
	public void setup() throws Exception {
		//System.setProperty("javax.net.debug", "ssl");
		KeyStore ksClient = KeyStore.getInstance("PKCS12");
		ksClient.load(new FileInputStream(keystore), keystorePassword.toCharArray());
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ksClient, keystorePassword.toCharArray());
		
		TrustManager[] trust = new TrustManager[] { 
			new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
	
				@SuppressWarnings("unused")
				public boolean isServerTrusted(X509Certificate[] certs) {
					return true;
				}
	
				@SuppressWarnings("unused")
				public boolean isClientTrusted(X509Certificate[] certs) {
					return true;
				}
	
				public void checkServerTrusted(X509Certificate[] certs, String authType)
						throws CertificateException {
					return;
				}
	
				public void checkClientTrusted(X509Certificate[] certs, String authType)
						throws CertificateException {
					return;
				}
			} 
		};
		
		// Create new SSLContext using our new TrustManagerFactory
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(kmf.getKeyManagers(), trust, null);
		// Get a SSLSocketFactory from our SSLContext
		sslSocketFactory = context.getSocketFactory();
		//HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
	}

	@Test
	public void getWsdl() throws Exception {
		URL url = new URL(endpoint + "?WSDL");
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setSSLSocketFactory(sslSocketFactory);
		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		int responseCode = conn.getResponseCode();
		System.out.println(responseCode);
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}
		BufferedReader br = new BufferedReader(
				new InputStreamReader((conn.getInputStream()), Charset.defaultCharset()));
		StringBuffer response = new StringBuffer();
		String output = null;
		while ((output = br.readLine()) != null) {
			response.append(output);
		}
		conn.disconnect();
		String res = new String(response.toString().getBytes(), Charset.forName("UTF-8"));
		System.out.println(res);
	}
	
	@Test
	public void ricercaImpresePerCF() throws Exception {
		BufferedReader buf = new BufferedReader(
				new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("parix/ricercacf-request.xml"), 
						"UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = buf.readLine()) != null) {
			sb.append(line).append("\n");
		}
		String contentString = sb.toString();
		contentString = contentString.replace("{{user}}", user);
		contentString = contentString.replace("{{password}}", password);
		contentString = contentString.replace("{{cf}}", "01656690227");
		
		URL url = new URL(endpoint);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setSSLSocketFactory(sslSocketFactory);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
		conn.setRequestProperty("SOAPAction", "/RicercaImpresePerCF");
		
		OutputStream out = conn.getOutputStream();
		Writer writer = new OutputStreamWriter(out, "UTF-8");
		writer.write(contentString);
		writer.close();
		out.close();		

		int responseCode = conn.getResponseCode();
		System.out.println(responseCode);
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}
		BufferedReader br = new BufferedReader(
				new InputStreamReader((conn.getInputStream()), Charset.defaultCharset()));
		StringBuffer response = new StringBuffer();
		String output = null;
		while ((output = br.readLine()) != null) {
			response.append(output);
		}
		conn.disconnect();
		String res = new String(response.toString().getBytes(), Charset.forName("UTF-8"));
		System.out.println(res);
	}

	@Test
	public void dettaglioCompletoImpresa() throws Exception {
		BufferedReader buf = new BufferedReader(
				new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("parix/dettaglio-request.xml"), 
						"UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = buf.readLine()) != null) {
			sb.append(line).append("\n");
		}
		String contentString = sb.toString();
		contentString = contentString.replace("{{user}}", user);
		contentString = contentString.replace("{{password}}", password);
		contentString = contentString.replace("{{provincia}}", "TN");
		contentString = contentString.replace("{{rea}}", "167588");
		
		URL url = new URL(endpoint);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setSSLSocketFactory(sslSocketFactory);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
		conn.setRequestProperty("SOAPAction", "/DettaglioCompletoImpresa");
		
		OutputStream out = conn.getOutputStream();
		Writer writer = new OutputStreamWriter(out, "UTF-8");
		writer.write(contentString);
		writer.close();
		out.close();		

		int responseCode = conn.getResponseCode();
		System.out.println(responseCode);
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}
		BufferedReader br = new BufferedReader(
				new InputStreamReader((conn.getInputStream()), Charset.defaultCharset()));
		StringBuffer response = new StringBuffer();
		String output = null;
		while ((output = br.readLine()) != null) {
			response.append(output);
		}
		conn.disconnect();
		String res = new String(response.toString().getBytes(), Charset.forName("UTF-8"));
		System.out.println(res);
	}

}
