import {Injectable} from '@angular/core'
import {Http,RequestOptions,Headers} from '@angular/http'
import {ConfigService} from './config.service'
import {Exam} from '../classes/Exam.interface'
import {Registration} from '../classes/Registration.interface'


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
    getExams():Promise<Exam[]> {
      let url:string=this.getHost()+'';
      return this.http.get(url)
               .toPromise()
               .then(response => response.json().data as Exam[])
               .catch(this.handleError);
    }
    getRegistrations():Promise<Registration[]> {
      let headers = new Headers({ 'Accept': 'application/json' });
      headers.append('x-access-token', '');
      let options = new RequestOptions({ headers: headers });
            let url:string=this.getApiUrl()+'student/84f01dc1-694d-40eb-9296-01ca5014ef5d/registration';

            return this.http.get(url,options)
               .toPromise()
               .then(response => response.json() as Registration[])
    }

    private handleError(error: any): Promise<any> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
