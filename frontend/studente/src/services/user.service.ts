import {Injectable} from '@angular/core'
import {WebAPIConnectorService} from './webAPIConnector.service';
import {Exam} from '../classes/Exam.class';
import {Registration} from '../classes/Registration.class';
import {Stage} from '../classes/Stage.class';
import {Activity} from '../classes/Activity.class';
import {Event} from '../classes/Event.class';
import {Student} from '../classes/Student.class';
import {Certification} from '../classes/Certification.class';
import { ExperienceTypes } from '../assets/conf/expTypes'
import  {StudentExperience} from '../classes/StudentExperience.class'
import  {ExperienceContainer} from '../classes/ExperienceContainer.class'
@Injectable()
export class UserService  {
  private exams: StudentExperience[]=[];
  private student: Student=new Student();
  private registrations: Registration[]=[];
  private stages:StudentExperience[]=[];
  private activities:StudentExperience[]=[];
  private events:StudentExperience[]=[];
  private certifications:StudentExperience[]=[];
  constructor(private webAPIConnector: WebAPIConnectorService) {
};
  getUserExams():Promise<StudentExperience[]> {
    return new Promise<StudentExperience[]>((resolve, reject) => {
      this.webAPIConnector.getExams('84f01dc1-694d-40eb-9296-01ca5014ef5d').then(experiences=>{
       this.exams=[];
       for (var i=0; i<experiences.length;i++){
          this.exams.push(experiences[i].experience);
        }
        resolve(this.exams)

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
  getUserStages():Promise<StudentExperience[]> {
     return new Promise<StudentExperience[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences('84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_STAGE).then(experiences=>{
        //take only stages
         this.stages=experiences;
//        this.stages=[];
//        for (var i=0; i<experiences.length;i++){
//          this.stages.push(experiences[i].experience);
//        }
        resolve(this.stages)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
  addStage(stage:StudentExperience): Promise<ExperienceContainer> {
         return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.addExperience(stage.experience.attributes,'84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_STAGE).then(stage=>{

        resolve(stage)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
     updateStage(stage:StudentExperience): Promise<ExperienceContainer> {
         return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.updateExperience(stage,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(stage=>{

        resolve(stage)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
    deleteStage(stage:StudentExperience): Promise<Stage> {
         return new Promise<Stage>((resolve, reject) => {
      this.webAPIConnector.deleteExperience(stage.experienceId,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(stage=>{

        resolve(stage)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
getUserEvents():Promise<StudentExperience[]> {
     return new Promise<StudentExperience[]>((resolve, reject) => {
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
  addEvent(event:StudentExperience): Promise<StudentExperience> {
         return new Promise<StudentExperience>((resolve, reject) => {
      this.webAPIConnector.addExperience(event.experience.attributes,'84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_EVENT).then(event=>{

        resolve(event)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
     updateEvent(event:StudentExperience): Promise<StudentExperience> {
         return new Promise<StudentExperience>((resolve, reject) => {
      this.webAPIConnector.updateExperience(event,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(event=>{

        resolve(event)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
    deleteEvent(event:StudentExperience): Promise<Event> {
         return new Promise<Event>((resolve, reject) => {
      this.webAPIConnector.deleteExperience(event.experienceId,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(event=>{
        resolve(event)
  }).catch((error: any):any => {
       reject()

     })
  })
  }
  getUserActivities():Promise<StudentExperience[]> {
     return new Promise<StudentExperience[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences('84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_ACTIVITY).then(experiences=>{
        //take only stages
         this.activities=experiences;
//        this.activities=[];
//        for (var i=0; i<experiences.length;i++){
//          this.activities.push(experiences[i].experience);
//        }
        resolve(this.activities)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
  addActivity(activity:StudentExperience): Promise<ExperienceContainer> {
         return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.addExperience(activity.experience.attributes,'84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_ACTIVITY).then(activity=>{

        resolve(activity)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
     updateActivity(activity:StudentExperience): Promise<ExperienceContainer> {
         return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.updateExperience(activity,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(activity=>{

        resolve(activity)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
    deleteActivity(activity:StudentExperience): Promise<Activity> {
         return new Promise<Stage>((resolve, reject) => {
      this.webAPIConnector.deleteExperience(activity.experienceId,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(stage=>{

        resolve(stage)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
      getUserCertifications():Promise<StudentExperience[]> {
     return new Promise<StudentExperience[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences('84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_CERT).then(experiences=>{
        this.certifications=experiences;
//        this.certifications=[];
//        for (var i=0; i<experiences.length;i++){
//          this.certifications.push(experiences[i].experience);
//        }
        resolve(this.certifications)

  }).catch((error: any):any => {
       reject()

     })
  })
  }

    addCertification(certification:StudentExperience): Promise<ExperienceContainer> {
         return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.addExperience(certification.experience.attributes,'84f01dc1-694d-40eb-9296-01ca5014ef5d',ExperienceTypes.EXP_TYPE_CERT).then(certification=>{

        resolve(certification)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
     updateCertification(certification:StudentExperience): Promise<ExperienceContainer> {
         return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.updateExperience(certification,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(certification=>{

        resolve(certification)

  }).catch((error: any):any => {
       reject()

     })
  })
  }
    deleteCertification(certification:StudentExperience): Promise<Certification> {
         return new Promise<Certification>((resolve, reject) => {
      this.webAPIConnector.deleteExperience(certification.experienceId,'84f01dc1-694d-40eb-9296-01ca5014ef5d').then(certification=>{
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
  getUserInfo() :Promise<any> {
     return new Promise<any>((resolve, reject) => {
      this.webAPIConnector.getUserInfo('84f01dc1-694d-40eb-9296-01ca5014ef5d').then(student=>{
        this.student=student;

        resolve(this.student)
  }).catch((error: any):any => {
       reject()

     })
  })
  }
    saveUserInfo(student:Student) :Promise<any> {
     return new Promise<any>((resolve, reject) => {
      this.webAPIConnector.updateUserInfo(student, '84f01dc1-694d-40eb-9296-01ca5014ef5d').then(student=>{
        this.student=student;

        resolve(this.student)
  }).catch((error: any):any => {
       reject()

     })
  })
  }
  createCertificate(experience):Promise<any> {
     return new Promise<any>((resolve, reject) => {
      this.webAPIConnector.createCertificate(experience, '84f01dc1-694d-40eb-9296-01ca5014ef5d').then(response=>{

        resolve(response.experienceId)
  }).catch((error: any):any => {
       reject()

     })
  })
  }
    deleteCertificate(experience):Promise<any> {
     return new Promise<any>((resolve, reject) => {
      this.webAPIConnector.deleteCertificate(experience, '84f01dc1-694d-40eb-9296-01ca5014ef5d').then(response=>{

        resolve(response.experienceId)
  }).catch((error: any):any => {
       reject()

     })
  })
  }
}
