package it.smartcommunitylab.csengine.storage;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.model.Registration;
import it.smartcommunitylab.csengine.model.Student;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;

public class StudentRepositoryImpl implements StudentRepositoryCustom {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Student> findByInstitute(String instituteId, String schoolYear, Pageable pageable) {
		// TODO check aggregation pipeline
		MatchOperation matchOp = new MatchOperation(Criteria.where("instituteId").is(instituteId).and("schoolYear").is(schoolYear));
		GroupOperation groupOp = new GroupOperation(Fields.fields("studentId")).first("$student").as("student");
		ProjectionOperation projectOp = new ProjectionOperation(Fields.fields("student"));
		LimitOperation limitOp = new LimitOperation(pageable.getPageSize());
		SkipOperation skipOp = new SkipOperation(pageable.getPageSize()*pageable.getPageNumber());
		SortOperation sortOp = new SortOperation(pageable.getSort());
		Aggregation aggregation = Aggregation.newAggregation(matchOp, groupOp, limitOp, projectOp, skipOp, sortOp);
		AggregationResults<Student> result = mongoTemplate.aggregate(aggregation, Registration.class, Student.class);
		return result.getMappedResults();
	}
	
	@Override
	public List<Student> findByCertifier(String certifierId, Pageable pageable) {
		// TODO check aggregation pipeline
		MatchOperation matchOp = new MatchOperation(Criteria.where(
				"experience.attributes." + Const.ATTR_CERTIFIERID).is(certifierId));
		GroupOperation groupOp = new GroupOperation(Fields.fields("studentId")).first("$student").as("student");
		ProjectionOperation projectOp = new ProjectionOperation(Fields.fields("student"));
		LimitOperation limitOp = new LimitOperation(pageable.getPageSize());
		SkipOperation skipOp = new SkipOperation(pageable.getPageSize()*pageable.getPageNumber());
		SortOperation sortOp = new SortOperation(pageable.getSort());
		Aggregation aggregation = Aggregation.newAggregation(matchOp, groupOp, limitOp, projectOp, skipOp, sortOp);
		AggregationResults<Student> result = mongoTemplate.aggregate(aggregation, Registration.class, Student.class);
		return result.getMappedResults();
	}

	@Override
	public List<Student> findByExperience(String experienceId, String instituteId,
			String schoolYear, Pageable pageable) {
		// TODO check aggregation pipeline
		Criteria criteria = Criteria.where("experienceId").is(experienceId);
		if(Utils.isNotEmpty(instituteId)) {
			criteria = criteria.andOperator(new Criteria(
					"experience.attributes." + Const.ATTR_INSTITUTEID).is(instituteId));
		}
		if(Utils.isNotEmpty(schoolYear)) {
			criteria = criteria.andOperator(new Criteria(
					"experience.attributes." + Const.ATTR_SCHOOLYEAR).is(schoolYear));
		}
		MatchOperation matchOp = new MatchOperation(criteria);
		GroupOperation groupOp = new GroupOperation(Fields.fields("studentId")).first("$student").as("student");
		ProjectionOperation projectOp = new ProjectionOperation(Fields.fields("student"));
		LimitOperation limitOp = new LimitOperation(pageable.getPageSize());
		SkipOperation skipOp = new SkipOperation(pageable.getPageSize()*pageable.getPageNumber());
		SortOperation sortOp = new SortOperation(pageable.getSort());
		Aggregation aggregation = Aggregation.newAggregation(matchOp, groupOp, limitOp, projectOp, skipOp, sortOp);
		AggregationResults<Student> result = mongoTemplate.aggregate(aggregation, Registration.class, Student.class);
		return result.getMappedResults();
	}

	@Override
	public void deleteByExperience(String experienceId) {
		// TODO Auto-generated method stub
	}
}
