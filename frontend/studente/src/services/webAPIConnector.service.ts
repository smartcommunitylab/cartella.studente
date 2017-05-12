import {Injectable} from '@angular/core'
import {Http,RequestOptions,Headers} from '@angular/http'
import {ConfigService} from './config.service'
import {Exam} from '../classes/Exam.class'
import {Registration} from '../classes/Registration.class'
import {Experience} from '../classes/Experience.class'
import {ExperienceContainer} from '../classes/ExperienceContainer.class'

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
    getExams(studentId:string):Promise<Exam[]> {
            let headers = new Headers({ 'Accept': 'application/json' });
      headers.append('x-access-token', ' ');
      let options = new RequestOptions({ headers: headers });
            let url:string=this.getApiUrl()+'student/'+studentId+'/experience/Esame?institutional=true';

            return this.http.get(url,options)
               .toPromise()
               .then(response => response.json() as Exam[])
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


      getCertifications(studentId:string, typeExp:string):Promise<any[]> {
            let headers = new Headers({ 'Accept': 'application/json' });
      headers.append('x-access-token', ' ');
      let options = new RequestOptions({ headers: headers });
            let url:string=this.getApiUrl()+'student/'+studentId+'/experience/'+typeExp+'?institutional=true';

            return this.http.get(url,options)
               .toPromise()
               .then(response => response.json())
    }
    private handleError(error: any): Promise<any> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
