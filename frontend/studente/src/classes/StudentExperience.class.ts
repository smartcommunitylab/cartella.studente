import {Document} from './Document.class'
import {ExperienceContainer} from './ExperienceContainer.class'
import {Student} from './Student.class'

export class StudentExperience {
  documents:Document [];
  document:Document; //tmp
  experience:ExperienceContainer;
  experienceId:string;
  id:string;
  student:Student;
  studentId:string;
}
