import {Injectable} from '@angular/core'
import {WebAPIConnectorService} from './webAPIConnector.service';
import {Exam} from '../classes/Exam.class';
import {Registration} from '../classes/Registration.class';
import {Stage} from '../classes/Stage.class';
import {Activity} from '../classes/Activity.class';
import {Mobility} from '../classes/Mobility.class';
import {Event} from '../classes/Event.class';
import {Student} from '../classes/Student.class';
import {Certification} from '../classes/Certification.class';
import { ExperienceTypes } from '../assets/conf/expTypes'
import  {StudentExperience} from '../classes/StudentExperience.class'
import { ConfigService } from './config.service'
import { ExperienceContainer } from '../classes/ExperienceContainer.class'
import { Curriculum } from '../classes/Curriculum.class';

@Injectable()
export class UserService {
  private exams: StudentExperience[] = [];
  private student: Student = new Student();
  private registrations: Registration[] = [];
  private stages: StudentExperience[] = [];
  private mobilities: StudentExperience[] = [];
  private activities: StudentExperience[] = [];
  private events: StudentExperience[] = [];
  private certifications: StudentExperience[] = [];
  private userId: string = "84f01dc1-694d-40eb-9296-01ca5014ef5d";
  private consentSubject: string = "";
  constructor(private webAPIConnector: WebAPIConnectorService, private config: ConfigService) {
  };
  getUserId(): string {
    return this.userId;
  }
  setUserId(newId: string) {
    this.userId = newId;
  }
  getConsentSubject(): string {
    return this.consentSubject;
  }
  setConsentSubject(newSubject: string) {
    this.consentSubject = newSubject;
  }
  getUserExams(): Promise<StudentExperience[]> {
    return new Promise<StudentExperience[]>((resolve, reject) => {
      this.webAPIConnector.getExams(this.getUserId()).then(experiences => {
        this.exams = experiences;
        //  this.exams=[];
        //  for (var i=0; i<experiences.length;i++){
        //     this.exams.push(experiences[i].experience);
        //   }
        resolve(this.exams)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  getUserRegistrations(): Promise<Registration[]> {
    return new Promise<Registration[]>((resolve, reject) => {
      this.webAPIConnector.getRegistrations(this.getUserId()).then(registrations => {
        this.registrations = registrations;
        resolve(this.registrations)

      }).catch((error: any): any => {
        reject()
      })
    })
  }
  getUserSubjectByRegistration(registration: Registration): Promise<any[]> {
    return new Promise<any[]>((resolve, reject) => {
      this.webAPIConnector.getSubjectsByRegistration(this.getUserId(), registration.id).then(subjects => {
        resolve(subjects)
      }).catch((error: any): any => {
        reject()

      })
    })
  }
  getUserStages(): Promise<StudentExperience[]> {
    return new Promise<StudentExperience[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences(this.getUserId(), ExperienceTypes.EXP_TYPE_STAGE).then(experiences => {
        this.stages = experiences;
        resolve(this.stages)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  addStage(stage: StudentExperience): Promise<ExperienceContainer> {
    return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.addExperience(stage.experience.attributes, this.getUserId(), ExperienceTypes.EXP_TYPE_STAGE).then(stage => {

        resolve(stage)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  updateStage(stage: StudentExperience): Promise<ExperienceContainer> {
    return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.updateExperience(stage, this.getUserId()).then(stage => {

        resolve(stage)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  deleteStage(stage: StudentExperience): Promise<Stage> {
    return new Promise<Stage>((resolve, reject) => {
      this.webAPIConnector.deleteExperience(stage.experienceId, this.getUserId()).then(stage => {

        resolve(stage)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  getUserJobs(): Promise<StudentExperience[]> {
    return new Promise<StudentExperience[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences(this.getUserId(), ExperienceTypes.EXP_TYPE_JOB).then(experiences => {
        this.stages = experiences;
        resolve(this.stages)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  addJob(job: StudentExperience): Promise<ExperienceContainer> {
    return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.addExperience(job.experience.attributes, this.getUserId(), ExperienceTypes.EXP_TYPE_JOB).then(job => {

        resolve(job)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  updateJob(stage: StudentExperience): Promise<ExperienceContainer> {
    return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.updateExperience(stage, this.getUserId()).then(stage => {

        resolve(stage)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  deleteJob(stage: StudentExperience): Promise<Stage> {
    return new Promise<Stage>((resolve, reject) => {
      this.webAPIConnector.deleteExperience(stage.experienceId, this.getUserId()).then(stage => {

        resolve(stage)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  getUserMobilities(): Promise<StudentExperience[]> {
    return new Promise<StudentExperience[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences(this.getUserId(), ExperienceTypes.EXP_TYPE_MOBILITY).then(experiences => {
        this.mobilities = experiences;
        resolve(this.mobilities)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  addMobility(mobility: StudentExperience): Promise<ExperienceContainer> {
    return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.addExperience(mobility.experience.attributes, this.getUserId(), ExperienceTypes.EXP_TYPE_MOBILITY).then(mobility => {

        resolve(mobility)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  updateMobility(mobility: StudentExperience): Promise<ExperienceContainer> {
    return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.updateExperience(mobility, this.getUserId()).then(mobility => {

        resolve(mobility)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  deleteMobility(mobility: StudentExperience): Promise<Stage> {
    return new Promise<Stage>((resolve, reject) => {
      this.webAPIConnector.deleteExperience(mobility.experienceId, this.getUserId()).then(mobility => {

        resolve(mobility)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  getUserEvents(): Promise<StudentExperience[]> {
    return new Promise<StudentExperience[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences(this.getUserId(), ExperienceTypes.EXP_TYPE_EVENT).then(experiences => {
        //take only stages
        this.events = [];
        for (var i = 0; i < experiences.length; i++) {
          this.events.push(experiences[i].experience);
        }
        resolve(this.events)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  addEvent(event: StudentExperience): Promise<StudentExperience> {
    return new Promise<StudentExperience>((resolve, reject) => {
      this.webAPIConnector.addExperience(event.experience.attributes, this.getUserId(), ExperienceTypes.EXP_TYPE_EVENT).then(event => {

        resolve(event)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  updateEvent(event: StudentExperience): Promise<StudentExperience> {
    return new Promise<StudentExperience>((resolve, reject) => {
      this.webAPIConnector.updateExperience(event, this.getUserId()).then(event => {

        resolve(event)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  deleteEvent(event: StudentExperience): Promise<Event> {
    return new Promise<Event>((resolve, reject) => {
      this.webAPIConnector.deleteExperience(event.experienceId, this.getUserId()).then(event => {
        resolve(event)
      }).catch((error: any): any => {
        reject()

      })
    })
  }
  getUserActivities(): Promise<StudentExperience[]> {
    return new Promise<StudentExperience[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences(this.getUserId(), ExperienceTypes.EXP_TYPE_ACTIVITY).then(experiences => {
        //take only stages
        this.activities = experiences;
        resolve(this.activities)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  addActivity(activity: StudentExperience): Promise<ExperienceContainer> {
    return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.addExperience(activity.experience.attributes, this.getUserId(), ExperienceTypes.EXP_TYPE_ACTIVITY).then(activity => {

        resolve(activity)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  updateActivity(activity: StudentExperience): Promise<ExperienceContainer> {
    return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.updateExperience(activity, this.getUserId()).then(activity => {

        resolve(activity)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  deleteActivity(activity: StudentExperience): Promise<Activity> {
    return new Promise<Stage>((resolve, reject) => {
      this.webAPIConnector.deleteExperience(activity.experienceId, this.getUserId()).then(stage => {

        resolve(stage)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  getUserCertifications(): Promise<StudentExperience[]> {
    return new Promise<StudentExperience[]>((resolve, reject) => {
      this.webAPIConnector.getExperiences(this.getUserId(), ExperienceTypes.EXP_TYPE_CERT).then(experiences => {
        this.certifications = experiences;
        resolve(this.certifications)

      }).catch((error: any): any => {
        reject()

      })
    })
  }

  addCertification(certification: StudentExperience): Promise<ExperienceContainer> {
    return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.addExperience(certification.experience.attributes, this.getUserId(), ExperienceTypes.EXP_TYPE_CERT).then(certification => {

        resolve(certification)

      }).catch((error: any): any => {
        reject()

      })
    })
  }

  updateCertification(certification: StudentExperience): Promise<ExperienceContainer> {
    return new Promise<ExperienceContainer>((resolve, reject) => {
      this.webAPIConnector.updateExperience(certification, this.getUserId()).then(certification => {

        resolve(certification)

      }).catch((error: any): any => {
        reject()

      })
    })
  }
  
  deleteCertification(certification: StudentExperience): Promise<Certification> {
    return new Promise<Certification>((resolve, reject) => {
      this.webAPIConnector.deleteExperience(certification.experienceId, this.getUserId()).then(certification => {
        resolve(certification)
      }).catch((error: any): any => {
        reject()

      })
    })
  }
  getUserTraining() {
    //get exams
    //get registrations
  }
  getUserExperiences() {

  }
  getUserInfo(): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      this.webAPIConnector.getUserInfo(this.getUserId()).then(student => {
        this.student = student;

        resolve(this.student)
      }).catch((error: any): any => {
        reject()

      })
    })
  }
  saveUserInfo(student: Student): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      this.webAPIConnector.updateUserInfo(student, this.getUserId()).then(student => {
        this.student = student;

        resolve(this.student)
      }).catch((error: any): any => {
        reject()

      })
    })
  }

  createDocument2(experience, item): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      this.webAPIConnector.createDocument2(experience, item, this.getUserId()).then(response => {
        var returnValuse = {
          experienceId: response.experienceId,
          storageId: response.storageId
        }
        resolve(returnValuse)
      }).catch((error: any): any => {
        reject()

      })
    })
  }

  createDocument(experience): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      this.webAPIConnector.createDocument(experience, this.getUserId()).then(response => {
        var returnValuse = {
          experienceId: response.experienceId,
          storageId: response.storageId
        }
        resolve(returnValuse)
      }).catch((error: any): any => {
        reject()

      })
    })
  }

  deleteDocument(experience): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      this.webAPIConnector.deleteDocument(experience, this.getUserId()).then(response => {

        resolve(response.experienceId)
      }).catch((error: any): any => {
        reject()

      })
    })
  }

  getUserImage(): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      this.webAPIConnector.getUserImage(this.getUserId()).then(response => {

        resolve(response)
      }).catch((error: any): any => {
        reject()

      })
    })
  }

  sendUserImage(uploader, image) {
    this.webAPIConnector.sendUserImage(uploader, image, this.getUserId())
  }

  getUserCV(): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      this.webAPIConnector.getUserCV(this.getUserId()).then(response => {
        resolve(response)
      }).catch((error: any): any => {
        reject()

      })
    })
  }

  updateUserCV(curriculum): Promise<Curriculum> {
    return new Promise<Curriculum>((resolve, reject) => {
      this.webAPIConnector.updateUserCV(curriculum, this.getUserId()).then(curriculum => {
        resolve(curriculum);
      }).catch((error: any): any => { reject() })
    })
  }

  addUserCV(curriculum): Promise<any> {
    return new Promise<Curriculum>((resolve, reject) => {
      this.webAPIConnector.addUserCV(curriculum, this.getUserId()).then(curriculum => {
        resolve(curriculum);
      }).catch((error: any): any => { reject() })
    })
  }

  downloadUserCVInODTFormat(): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      this.webAPIConnector.downloadCVInODTFormat(this.getUserId()).then(downloadedCV => { }).catch((error: any): any => { reject() })
    });
    
  }

  uploadDocumentInPromise(uploader, item, experienceContaniner: ExperienceContainer): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      this.createDocument2(experienceContaniner, item).then(exp => {

        this.webAPIConnector.uploadDocument(uploader, this.getUserId(), exp.experienceId, item, exp.storageId);
          
        uploader.onCompleteItem = (item, response, status, headers) => {
          var actualResponse = JSON.parse(item._xhr.response);
          if (status == 200) {
            console.log('upload complete for ' + item.file.name);
            resolve();
          } else {
            console.error(response);
            this.deleteDocument(experienceContaniner).then(exp => {
              reject();
            });
          }
        }
      }).catch(error => {
        console.error(error);
        reject();
        })
    })
    
  }
  

  deleteDocumentInPromise(experienceId: string, storageId:string): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      this.webAPIConnector.deleteStudentDocumentFile(this.getUserId(), experienceId, storageId).then(resp => {
        resolve();
      }).catch(error => {
        return this.handleError;
      })
    }).catch(error => {
      return this.handleError;
    })

  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error);
    return Promise.reject(error);
  }
  
}
