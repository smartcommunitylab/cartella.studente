package csengine;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import it.smartcommunitylab.csengine.cv.CVRegistration;
import it.smartcommunitylab.csengine.model.Student;

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
	Student student;
	
	@Before
	public void setUp() throws Exception {
		sdf = new SimpleDateFormat("YYYY-MM-dd");
		
		CVRegistration registration1 = new CVRegistration();
		registration1.setDateFrom("01/02/2015");
		registration1.setDateTo("30/05/2016");
		registration1.setCourse("SCUOLA SECONDARIA DI PRIMO GRADO");
		registration1.setTeachingUnit("SCUOLA SECONDARIA DI PRIMO GRADO \"A. ECCHER DALL'ECO\" MEZZOLOMBARDO");
		registration1.setInstituteName("ISTITUTO COMPRENSIVO MEZZOLOMBARDO - PAGANELLA");
		
		CVRegistration registration2 = new CVRegistration();
		registration2.setDateFrom("01/02/2015");
		registration2.setDateTo("30/05/2016");
		registration2.setCourse("BIENNIO ALLEVAMENTO, COLTIVAZIONI, GESTIONE DEL VERDE");
		registration2.setTeachingUnit("SETTORE AGRICOLTURA E AMBIENTE");
		registration2.setInstituteName("FONDAZIONE EDMUND MACH - ISTITUTO AGRARIO SAN MICHELE ALL'ADIGE");
		
		registrations = new ArrayList<CVRegistration>();
		registrations.add(registration1);
		registrations.add(registration2);
		
		student = new Student();
		student.setName("Gino");
		student.setSurname("Rivieccio");
		student.setAddress("via del lavaman del sindec 8, 38100 Trento");
		student.setMobilePhone("388.1234567");
		student.setPhone("0461.123456");
		student.setSex("M");
		student.setBirthdate("01/03/1999");
		student.setNationality("Italiana");
		student.setEmail("gino.rivieccio@email.com");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		// 1) Load ODT file and set Velocity template engine and cache it to the registry
		InputStream in = ClassLoader.getSystemResourceAsStream("ecv_template_it.odt");
		IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in,TemplateEngineKind.Velocity);

		// 2) Create Java model context 
		IContext context = report.createContext();
		context.put("name", "world");
		context.put("registrations", registrations);
		context.put("student", student);

		// 3) Generate report by merging Java model with the ODT
		OutputStream out = new FileOutputStream(new File("C:\\Users\\micnori\\Documents\\Tmp\\java\\test_Out.odt"));
		report.process(context, out);
	}

}
