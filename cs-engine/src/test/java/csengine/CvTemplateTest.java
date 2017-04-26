package csengine;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import it.smartcommunitylab.csengine.cv.CVRegistration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CvTemplateTest {
	SimpleDateFormat sdf;
	List<CVRegistration> registrations;
	
	@Before
	public void setUp() throws Exception {
		sdf = new SimpleDateFormat("YYYY-MM-dd");
		CVRegistration registration1 = new CVRegistration();
		registration1.setDateFrom("2015-02-01");
		registration1.setDateTo("2016-05-30");
		registration1.setCourse("Corso di formazione1");
		CVRegistration registration2 = new CVRegistration();
		registration2.setDateFrom("2015-02-01");
		registration2.setDateTo("2016-05-30");
		registration2.setCourse("Corso di formazione2");
		registrations = new ArrayList<CVRegistration>();
		registrations.add(registration1);
		registrations.add(registration2);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		// 1) Load ODT file and set Velocity template engine and cache it to the registry
		InputStream in = ClassLoader.getSystemResourceAsStream("test.odt");
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in,TemplateEngineKind.Velocity);

		// 2) Create Java model context 
		IContext context = report.createContext();
		context.put("name", "world");
		context.put("registrations", registrations);

		// 3) Generate report by merging Java model with the ODT
		OutputStream out = new FileOutputStream(new File("C:\\Users\\micnori\\Documents\\Tmp\\java\\test_Out.odt"));
		report.process(context, out);
	}

}
