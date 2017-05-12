import {Injectable} from '@angular/core'
import {WebAPIConnectorService} from './webAPIConnector.service';
import {Exam} from '../classes/Exam.class';
import {Registration} from '../classes/Registration.class';
import {Stage} from '../classes/Stage.class';
import {Activity} from '../classes/Activity.class';
import {Event} from '../classes/Event.class';
import {Certification} from '../classes/Certification.class';
import { ExperienceTypes } from '../assets/conf/expTypes'
import  {ExperienceContainer} from '../classes/ExperienceContainer.class'

@Injectable()
export class UserService  {
  private exams: Exam[]=[];
  private registrations: Registration[]=[];
  private stages:ExperienceContainer[]=[];
  private activities:Activity[]=[];
  private events:Event[]=[];
  private certifications:Certification[]=[];
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
  })
  }
  getUserStages():Promise<ExperienceContainer[]> {
     return new Promise<ExperienceContainer[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences('84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_STAGE).then(experiences=>{
        //take only stages
        this.stages=[];
        for (var i=0; i<experiences.length;i++){
          this.stages.push(experiences[i].experience);
        }
        resolve(this.stages)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
  addStage(stage:ExperienceContainer): Promise<ExperienceContainer> {
         return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.addExperience(stage.attributes,'84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_STAGE).then(stage=>{

        resolve(stage)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
     updateStage(stage:ExperienceContainer): Promise<ExperienceContainer> {
         return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.updateExperience(stage,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(stage=>{

        resolve(stage)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
    deleteStage(stage:Stage): Promise<Stage> {
         return new Promise<Stage>((resolve, reject) => {
      this.webAPIConnector.deleteExperience(stage.id,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(stage=>{

        resolve(stage)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
  getUserActivities():Promise<Activity[]> {
     return new Promise<Activity[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences('84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_ACTIVITY).then(activities=>{
       this.activities=activities;
        resolve(this.activities)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
    getUserEvents():Promise<Event[]> {
     return new Promise<Event[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences('84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_EVENT).then(events=>{
       this.events=events;
        resolve(this.events)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
      getUserCertifications():Promise<Certification[]> {
     return new Promise<Certification[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences('84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_CERT).then(certifications=>{
       this.certifications=certifications;
        resolve(this.certifications)

  }).catch((error: any):any => {
       reject()

     })
  })
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
