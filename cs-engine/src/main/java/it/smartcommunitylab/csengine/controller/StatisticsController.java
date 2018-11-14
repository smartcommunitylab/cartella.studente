package it.smartcommunitylab.csengine.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.aac.model.AccountProfile;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.model.statistics.CourseData;
import it.smartcommunitylab.csengine.model.statistics.KPI;
import it.smartcommunitylab.csengine.model.statistics.POI;
import it.smartcommunitylab.csengine.model.statistics.StudentProfile;
import it.smartcommunitylab.csengine.storage.RepositoryManager;

@RestController
public class StatisticsController extends AuthController {

	@Autowired
	private RepositoryManager dataManager;
	
	@GetMapping("/api/statistics/teachingUnits")
	public List<POI> getTeachingUnits(@RequestParam(required=false) String ordine, @RequestParam(required=false) String tipologia, @RequestParam(required=false) Double[] coordinates, @RequestParam(required=false) Double radius, @RequestParam(required=false) String schoolYear) throws Exception {
		List<POI> result = dataManager.findTeachingUnits(ordine, tipologia, coordinates, radius, schoolYear);
		return result;
	}
	
	@GetMapping("/api/statistics/institutes")
	public List<POI> getInstitutes(@RequestParam(required=false) String ordine, @RequestParam(required=false) String tipologia, @RequestParam(required=false) Double[] coordinates, @RequestParam(required=false) Double radius, @RequestParam(required=false) String schoolYear) throws Exception {
		List<POI> result = dataManager.findInstitutes(ordine, tipologia, coordinates, radius, schoolYear);
		return result;
	}	
	
	@GetMapping("/api/statistics/courses")
	public List<CourseData> getInstituteCourses(@RequestParam(required=false) String teachingUnitId, @RequestParam(required=false) String instituteId, @RequestParam(required=false) String schoolYear) throws Exception {
		List<CourseData> result = dataManager.findCourses(teachingUnitId, instituteId, schoolYear);
		return result;
	}	
	
	@GetMapping("/api/statistics/kpi")
	public List<KPI> getInstituteKPIs(@RequestParam(required=true) String instituteId, @RequestParam(required=true) String schoolYear) throws Exception {
		List<KPI> result = dataManager.getInstituteKPIs(instituteId, schoolYear);
		return result;
	}	

	@GetMapping("/api/statistics/profile/student")
	public StudentProfile geStudenttProfile(HttpServletRequest request) throws Exception {
		AccountProfile profile = this.getAccoutProfile(request);
		
		if (profile != null) {
			String cf = this.getCF(profile);
			
			if (cf == null) {
				throw new UnauthorizedException("CF for profile is null");
			}
			
			StudentProfile result = dataManager.getStudentProfile(cf);
			return result;
		} else {
			throw new UnauthorizedException("Profile not found");
		}
	}		
	 
	
//	@GetMapping("/api/statistics/student")
//	public StudentProfile getStudentProfile(@RequestParam(required=false) String studentId) throws Exception {
//		StudentProfile result = dataManager.getStudentProfile(studentId);
//		return result;
//	}		
	
//	@GetMapping("/api/statistics/count")
//	public void count() {
//		dataManager.countOrphansTeachingUnits();
//	}		
	
	
	@SuppressWarnings("serial")
	HttpHeaders createHeaders() {
		return new HttpHeaders() {
			{
			}
		};
	}	
	
}
