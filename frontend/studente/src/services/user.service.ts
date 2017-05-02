import {Injectable} from '@angular/core'
import {WebAPIConnectorService} from './webAPIConnector.service';
import {Exam} from '../classes/Exam.interface';
import {Registration} from '../classes/Registration.interface';

@Injectable()
export class UserService  {
  private exams: Exam[]=[];
  private registrations: Registration[]=[];
  constructor(private webAPIConnector: WebAPIConnectorService) {
};
  getUserExams() {
    this.webAPIConnector.getExams().then(exams=>this.exams=exams);
  }
  getUserRegistrations() {
        this.webAPIConnector.getRegistrations().then(registrations=>this.registrations=registrations);

  }
  getUserTraining(){
    //get exams
    //get registrations
  }
  getUserExperiences() {

  }
  getUserData() {

  }

}
