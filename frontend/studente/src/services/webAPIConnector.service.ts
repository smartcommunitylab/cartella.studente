import {Injectable} from '@angular/core'
import {Http,RequestOptions,Headers} from '@angular/http'
import {ConfigService} from './config.service'
import {Exam} from '../classes/Exam.class'
import {Registration} from '../classes/Registration.class'
import {Experience} from '../classes/Experience.class'
import {Student} from '../classes/Student.class'
import {ExperienceContainer} from '../classes/ExperienceContainer.class'
import {ExperienceSuperContainer} from '../classes/ExperienceSuperContainer.class'
import { ExperienceTypes } from '../assets/conf/expTypes'

@Injectable()
export class WebAPIConnectorService  {

    constructor(private http: Http, private config: ConfigService) {

    }
  //get the host url from configuration
    getHost() {
        let host:string = this.config.getConfig('host');
    }
    getToken() {
      let token:string = this.config.getConfig('token');
    }
  getApiUrl() {
    return this.config.getConfig('apiUrl')
  }
  //get all the institutional exams, they are part of the cv of the students
    getExams(studentId:string):Promise<any[]> {
            let headers = new Headers({ 'Accept': 'application/json' });
      headers.append('x-access-token', ' ');
      let options = new RequestOptions({ headers: headers });
            let url:string=this.getApiUrl()+'student/'+studentId+'/experience/'+ExperienceTypes.EXP_TYPE_EXAM+'?institutional=true';

            return this.http.get(url,options)
               .toPromise()
               .then(response => response.json() )
    }
    getRegistrations(studentId:string):Promise<any[]> {
      let headers = new Headers({ 'Accept': 'application/json' });
      headers.append('x-access-token', ' ');
      let options = new RequestOptions({ headers: headers });
            let url:string=this.getApiUrl()+'student/'+studentId+'/registration';

            return this.http.get(url,options)
               .toPromise()
               .then(response => response.json())
    }
    getExperiences(studentId:string, typeExp:string):Promise<any[]> {
            let headers = new Headers({ 'Accept': 'application/json' });
      headers.append('x-access-token', ' ');
      let options = new RequestOptions({ headers: headers });
            let url:string=this.getApiUrl()+'student/'+studentId+'/experience/'+typeExp;

            return this.http.get(url,options)
               .toPromise()
               .then(response => response.json())
    }
  addExperience(experience: Experience, studentId:string, typeExp:string):Promise<any> {
    let body={
        "attributes": experience,
        "type": typeExp
      }
            let headers = new Headers({ 'Accept': 'application/json' });
      headers.append('x-access-token', ' ');
      let options = new RequestOptions({ headers: headers });
            let url:string=this.getApiUrl()+'student/'+studentId+'/my/experience';

            return this.http.post(url,body, options)
               .toPromise()
               .then(response => response.json()).catch(error=>
                                                       {
                  console.error('An error occurred', error); // for demo purposes only

            })
    }
    updateExperience(experience: ExperienceContainer, studentId:string):Promise<any> {
      let body=experience
      let expId:string=experience.id;
      let headers = new Headers({ 'Accept': 'application/json' });
      headers.append('x-access-token', ' ');
      let options = new RequestOptions({ headers: headers });
            let url:string=this.getApiUrl()+'student/'+studentId+'/my/experience/'+expId;

            return this.http.put(url,body, options)
               .toPromise()
               .then(response => response.json()).catch(error=>
                                                       {
                  console.error('An error occurred', error); // for demo purposes only

            })
    }
  deleteExperience(expId: string, studentId:string):Promise<any> {
       let headers = new Headers({ 'Accept': 'application/json' });
      headers.append('x-access-token', ' ');
      let options = new RequestOptions({ headers: headers });
            let url:string=this.getApiUrl()+'student/'+studentId+'/my/experience/'+expId;

            return this.http.delete(url, options)
               .toPromise()
               .then(response => response.json()).catch(error=>
                                                       {
                  console.error('An error occurred', error); // for demo purposes only

            })
    }
  getUserInfo (studentId:string):Promise<any> {
      let headers = new Headers({ 'Accept': 'application/json' });
      headers.append('x-access-token', ' ');
      let options = new RequestOptions({ headers: headers });
            let url:string=this.getApiUrl()+'student/'+studentId;

            return this.http.get(url,options)
               .toPromise()
               .then(response => response.json())
  }
   updateUserInfo(student: Student, studentId:string):Promise<any> {
      let body=student
      let headers = new Headers({ 'Accept': 'application/json' });
      headers.append('x-access-token', ' ');
      let options = new RequestOptions({ headers: headers });
            let url:string=this.getApiUrl()+'student/'+studentId;

            return this.http.put(url,body, options)
               .toPromise()
               .then(response => response.json()).catch(error=>
                                                       {
                  console.error('An error occurred', error); // for demo purposes only

            })
    }
    createCertificate(experience: ExperienceContainer, studentId:string):Promise<any> {
      let body={}
      let expId:string=experience.id;
      let headers = new Headers({ 'Accept': 'application/json' });
      headers.append('x-access-token', ' ');
      let options = new RequestOptions({ headers: headers });
            let url:string=this.getApiUrl()+'student/'+studentId+'/experience/'+expId+'/certificate';

            return this.http.post(url,body, options)
               .toPromise()
               .then(response => response.json()).catch(error=>
                                                       {
                  console.error('An error occurred', error); // for demo purposes only

            })
    }
    private handleError(error: any): Promise<any> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
