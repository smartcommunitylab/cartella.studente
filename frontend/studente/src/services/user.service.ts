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
  private activities:ExperienceContainer[]=[];
  private events:ExperienceContainer[]=[];
  private certifications:ExperienceContainer[]=[];
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
getUserEvents():Promise<ExperienceContainer[]> {
     return new Promise<ExperienceContainer[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences('84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_EVENT).then(experiences=>{
        //take only stages
        this.events=[];
        for (var i=0; i<experiences.length;i++){
          this.events.push(experiences[i].experience);
        }
        resolve(this.events)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
  addEvent(event:ExperienceContainer): Promise<ExperienceContainer> {
         return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.addExperience(event.attributes,'84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_EVENT).then(event=>{

        resolve(event)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
     updateEvent(event:ExperienceContainer): Promise<ExperienceContainer> {
         return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.updateExperience(event,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(event=>{

        resolve(event)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
    deleteEvent(event:Event): Promise<Event> {
         return new Promise<Event>((resolve, reject) => {
      this.webAPIConnector.deleteExperience(event.id,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(event=>{
        resolve(event)
  }).catch((error: any):any => {
       reject()

     })
  })
  }
  getUserActivities():Promise<ExperienceContainer[]> {
     return new Promise<ExperienceContainer[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences('84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_ACTIVITY).then(experiences=>{
        //take only stages
        this.activities=[];
        for (var i=0; i<experiences.length;i++){
          this.activities.push(experiences[i].experience);
        }
        resolve(this.activities)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
  addActivity(activity:ExperienceContainer): Promise<ExperienceContainer> {
         return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.addExperience(activity.attributes,'84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_ACTIVITY).then(activity=>{

        resolve(activity)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
     updateActivity(activity:ExperienceContainer): Promise<ExperienceContainer> {
         return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.updateExperience(activity,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(activity=>{

        resolve(activity)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
    deleteActivity(activity:Activity): Promise<Activity> {
         return new Promise<Stage>((resolve, reject) => {
      this.webAPIConnector.deleteExperience(activity.id,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(stage=>{

        resolve(stage)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
      getUserCertifications():Promise<ExperienceContainer[]> {
     return new Promise<ExperienceContainer[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences('84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_CERT).then(experiences=>{
        this.certifications=[];
        for (var i=0; i<experiences.length;i++){
          this.certifications.push(experiences[i].experience);
        }
        resolve(this.certifications)

  }).catch((error: any):any => {
       reject()

     })
  })
  }

    addCertification(certification:ExperienceContainer): Promise<ExperienceContainer> {
         return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.addExperience(certification.attributes,'84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_CERT).then(certification=>{

        resolve(certification)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
     updateCertification(certification:ExperienceContainer): Promise<ExperienceContainer> {
         return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.updateExperience(certification,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(certification=>{

        resolve(certification)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
    deleteCertification(certification:Certification): Promise<Certification> {
         return new Promise<Certification>((resolve, reject) => {
      this.webAPIConnector.deleteExperience(certification.id,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(certification=>{
        resolve(certification)
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
