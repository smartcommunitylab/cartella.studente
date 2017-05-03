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
  getUserExams():Promise<Exam[]> {
    return new Promise<Exam[]>((resolve, reject) => {
      this.webAPIConnector.getExams('84f01dc1-694d-40eb-9296-01ca5014ef5d').then(exams=>{
       this.exams=exams;
      resolve(this.exams);

  }).catch((error: any):any => {
       reject()

     })
  })}
  getUserRegistrations():Promise<Registration[]> {
     return new Promise<Registration[]>((resolve, reject) => {
      this.webAPIConnector.getRegistrations('84f01dc1-694d-40eb-9296-01ca5014ef5d').then(registrations=>{
       this.registrations=registrations;
        resolve(this.registrations)

  }).catch((error: any):any => {
       reject()

     })
  })}
  getUserTraining(){
    //get exams
    //get registrations
  }
  getUserExperiences() {

  }
  getUserData() {

  }

}
