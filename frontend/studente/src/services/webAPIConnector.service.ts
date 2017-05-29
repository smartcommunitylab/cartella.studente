import { Injectable } from '@angular/core'
import { Http, BaseRequestOptions, RequestOptions } from '@angular/http'
import { ConfigService } from './config.service'
import { LoginService } from './login.service'
import { Exam } from '../classes/Exam.class'
import { Registration } from '../classes/Registration.class'
import { Experience } from '../classes/Experience.class'
import { Student } from '../classes/Student.class'
import { StudentExperience } from '../classes/StudentExperience.class'
import { ExperienceTypes } from '../assets/conf/expTypes'
// import {UserService } from './user.service'
import { FileUploader } from 'ng2-file-upload';

@Injectable()
export class DefaultRequestOptions extends BaseRequestOptions {

  constructor() {
    super();
    this.headers.set('Accept', 'application/json');
    this.headers.set('Content-Type', 'application/json');
    // this.headers.set('x-access-token', ` `);
    this.headers.set('Authorization', `Bearer ${sessionStorage.getItem('access_token')}`);
  }
}

export const requestOptionsProvider = { provide: RequestOptions, useClass: DefaultRequestOptions };

@Injectable()
export class WebAPIConnectorService {

  constructor(private http: Http, private config: ConfigService) {

  }
  //get the host url from configuration
  getHost() {
    let host: string = this.config.getConfig('host');
  }
  getToken() {
    let token: string = this.config.getConfig('token');
  }
  getApiUrl() {
    return this.config.getConfig('apiUrl')
  }
  getProfile(): Promise<any> {
    let url: string = this.getApiUrl() + 'profile';

    return this.http.get(url)
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError);
  }
  consent(studentId: string, subject: string): Promise<any> {
    let url: string = this.getApiUrl() + 'consent';

    return this.http.post(url, {
      "studentId": studentId,
      "subject": subject
    })
      .toPromise()
      .then(response => response.json())
  }
  //get all the institutional exams, they are part of the cv of the students
  getExams(studentId: string): Promise<any[]> {
    let url: string = this.getApiUrl() + 'student/' + studentId + '/experience/' + ExperienceTypes.EXP_TYPE_EXAM + '?institutional=true';

    return this.http.get(url)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError);
  }
  getRegistrations(studentId: string): Promise<any[]> {
    let options = new DefaultRequestOptions();
    let url: string = this.getApiUrl() + 'student/' + studentId + '/registration';

    return this.http.get(url, options)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError);
  }
  getSubjectsByRegistration(studentId: string, registrationId: string): Promise<any[]> {
    let options = new DefaultRequestOptions();
    let url: string = this.getApiUrl() + 'student/' + studentId + '/registration/' + registrationId + '/subject';
    return this.http.get(url, options)
      .toPromise()
      .then(response => response.json())
      .catch(response => this.handleError);
  }

  getExperiences(studentId: string, typeExp: string): Promise<any[]> {
    let url: string = this.getApiUrl() + 'student/' + studentId + '/experience/' + typeExp;

    return this.http.get(url)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError);
  }
  addExperience(experience: Experience, studentId: string, typeExp: string): Promise<any> {
    let body = {
      "attributes": experience,
      "type": typeExp
    }
    let url: string = this.getApiUrl() + 'student/' + studentId + '/my/experience';

    return this.http.post(url, body)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError);
  }
  updateExperience(experience: StudentExperience, studentId: string): Promise<any> {
    let body = experience.experience
    let expId: string = experience.experienceId;
    let url: string = this.getApiUrl() + 'student/' + studentId + '/my/experience/' + expId;

    return this.http.put(url, body, )
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError);
  }
  deleteExperience(expId: string, studentId: string): Promise<any> {
    let url: string = this.getApiUrl() + 'student/' + studentId + '/my/experience/' + expId;

    return this.http.delete(url)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError);
  }
  getUserInfo(studentId: string): Promise<any> {
    let url: string = this.getApiUrl() + 'student/' + studentId;

    return this.http.get(url)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError);
  }
  updateUserInfo(student: Student, studentId: string): Promise<any> {
    let body = student
    let url: string = this.getApiUrl() + 'student/' + studentId;

    return this.http.put(url, body)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError);
  }
  createCertificate(experience: StudentExperience, studentId: string): Promise<any> {
    let body = {}
    let expId: string = experience.id;
    let url: string = this.getApiUrl() + 'student/' + studentId + '/experience/' + expId + '/certificate';

    return this.http.post(url, body)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError);
  }
  deleteCertificate(experience: StudentExperience, studentId: string): Promise<any> {
    let body = {}
    let expId: string = experience.experienceId;
    let url: string = this.getApiUrl() + 'student/' + studentId + '/experience/' + expId + '/certificate/file';

    return this.http.delete(url)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError);
  }
  uploadCertificate(uploader: FileUploader, userId: string, experienceId: string, item): void {
    var newUrl = this.config.getConfig('apiUrl') + 'student/' + userId + '/experience/' + experienceId + '/certificate/file';
    uploader.setOptions({ url: newUrl, authToken: `Bearer ${sessionStorage.getItem('access_token')}`, disableMultipart: false });
    item.withCredentials = false;
    uploader.onBuildItemForm = (item, form) => {
      form.append("filename", item.file.name);
    };
    item.upload();
  }
getUserImage(studentId:string): Promise<any> {
   let url: string = this.getApiUrl() + 'student/' + studentId+'/photo';

    return this.http.get(url)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError);
}
sendUserImage(uploader: FileUploader,image, studentId:string){
   var newUrl = this.config.getConfig('apiUrl') + 'student/' + studentId + '/photo/file';
    uploader.setOptions({ url: newUrl, authToken: `Bearer ${sessionStorage.getItem('access_token')}`, disableMultipart: false });
    image.withCredentials = false;
    // uploader.onBuildItemForm = (item, form) => {
    //   form.append("filename", item.file.name);
    // };
    image.upload();
//    return Promise.resolve();
}
  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error);
    return Promise.reject(error);
  }
}
