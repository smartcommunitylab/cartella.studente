package it.smartcommunitylab.csengine.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.csengine.model.Course;
import it.smartcommunitylab.csengine.model.statistics.KPI;
import it.smartcommunitylab.csengine.model.statistics.POI;
import it.smartcommunitylab.csengine.model.statistics.StudentProfile;
import it.smartcommunitylab.csengine.storage.RepositoryManager;

@RestController
public class StatisticsController {

	@Autowired
	private RepositoryManager dataManager;
	
	@GetMapping("/api/statistics/institutes")
	public List<POI> getInstitute(@RequestParam(required=false) String ordine, @RequestParam(required=false) String tipologia, @RequestParam(required=false) Double[] coordinates, @RequestParam(required=false) Double radius) throws Exception {
		List<POI> result = dataManager.findTeachingUnit(ordine, tipologia, coordinates, radius);
		return result;
	}
	
	@GetMapping("/api/statistics/courses")
	public List<Course> getInstituteCourses(@RequestParam(required=false) String teachingUnitId, @RequestParam(required=false) String schoolYear) throws Exception {
		List<Course> result = dataManager.findCourses(teachingUnitId, schoolYear);
		return result;
	}	
	
	@GetMapping("/api/statistics/kpi")
	public List<KPI> getInstituteKPIs(@RequestParam(required=false) String teachingUnitId, @RequestParam(required=false) String schoolYear) throws Exception {
		List<KPI> result = dataManager.getInstituteKPIs(teachingUnitId, schoolYear);
		return result;
	}	
	
	@GetMapping("/api/statistics/student")
	public StudentProfile getStudentProfile(@RequestParam(required=false) String studentId) throws Exception {
		StudentProfile result = dataManager.getStudentProfile(studentId);
		return result;
	}		
	
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
