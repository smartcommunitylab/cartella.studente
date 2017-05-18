import {Experience} from './Experience.class'
import {Student} from './Student.class'
import {Certificate} from './Certificate.class'
export class ExperienceContainer {

certificate?:Certificate;
attributes:Experience;
experienceId:string;
id:string;
student:Student;
studentId:string;
constructor() {
  this.attributes = new Experience();
  this.student = new Student();
        }
}
