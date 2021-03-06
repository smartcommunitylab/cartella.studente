import { Injectable } from '@angular/core'
import { Http, BaseRequestOptions, RequestOptions, ResponseContentType } from '@angular/http'
import * as FileSaver from 'file-saver';
import { ConfigService } from './config.service'
import { LoginService } from './login.service'
import { Exam } from '../classes/Exam.class'
import { Registration } from '../classes/Registration.class'
import { Experience } from '../classes/Experience.class'
import { Student } from '../classes/Student.class'
import { StudentExperience } from '../classes/StudentExperience.class'
import { ExperienceContainer } from '../classes/ExperienceContainer.class'
import { ExperienceTypes } from '../assets/conf/expTypes'
import { Curriculum } from '../classes/Curriculum.class'
import { FileUploader } from 'ng2-file-upload';
import { AlertController, Backdrop } from 'ionic-angular';

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

  constructor(private http: Http, private config: ConfigService, private alertCtrl: AlertController) {

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
      .timeout(5000)
      .toPromise()
      .then(response => {
        return response.json()
      }
      ).catch(response => {
        return this.handleError(response)
      });
  }
  consent(studentId: string): Promise<any> {
    let url: string = this.getApiUrl() + 'consent/student/' + studentId;
    return this.http.post(url,{})
      .toPromise()
      .then(response => response.json())
  }
  readConsent(studentId: string): Promise<any> {
    let url: string = this.getApiUrl() + 'consent/student/' + studentId;
    return this.http.get(url,{})
      .toPromise()
      .then(response => response.json())
  }
  //get all the institutional exams, they are part of the cv of the students
  getExams(studentId: string): Promise<any[]> {
    let url: string = this.getApiUrl() + 'student/' + studentId + '/experience/' + ExperienceTypes.EXP_TYPE_EXAM + '?institutional=true';

    return this.http.get(url)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError(response));
  }
  getRegistrations(studentId: string): Promise<any[]> {
    let options = new DefaultRequestOptions();
    let url: string = this.getApiUrl() + 'student/' + studentId + '/registration';

    return this.http.get(url, options)
      .timeout(5000)
      .toPromise()
      .then(response => {
        return response.json()
      }
      ).catch(response => {
        return this.handleError(response)
      });
  }
  getSubjectsByRegistration(studentId: string, registrationId: string): Promise<any[]> {
    let options = new DefaultRequestOptions();
    let url: string = this.getApiUrl() + 'student/' + studentId + '/registration/' + registrationId + '/subject';
    return this.http.get(url, options)
      .toPromise()
      .then(response => response.json())
      .catch(response => this.handleError(response));
  }

  getExperiences(studentId: string, typeExp: string): Promise<any[]> {
    let url: string = this.getApiUrl() + 'student/' + studentId + '/experience/' + typeExp + '?page=0&size=50';

    return this.http.get(url)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError(response));
  }
  addExperience(experience: Experience, studentId: string, typeExp: string): Promise<any> {
    let body = {
      "attributes": experience,
      "type": typeExp
    }
    let url: string = this.getApiUrl() + 'student/' + studentId + '/my/experience';

    return this.http.post(url, body)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError(response));
  }
  updateExperience(experience: StudentExperience, studentId: string): Promise<any> {
    let body = experience.experience
    let expId: string = experience.experienceId;
    let url: string = this.getApiUrl() + 'student/' + studentId + '/my/experience/' + expId;

    return this.http.put(url, body, )
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError(response));
  }
  deleteExperience(expId: string, studentId: string): Promise<any> {
    let url: string = this.getApiUrl() + 'student/' + studentId + '/my/experience/' + expId;

    return this.http.delete(url)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError(response));
  }
  getUserInfo(studentId: string): Promise<any> {
    let url: string = this.getApiUrl() + 'student/' + studentId;

    return this.http.get(url)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError(response));
  }
  updateUserInfo(student: Student, studentId: string): Promise<any> {
    let body = student
    let url: string = this.getApiUrl() + 'student/' + studentId;

    return this.http.put(url, body)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError(response));
  }
  createDocument(experience: ExperienceContainer, studentId: string): Promise<any> {
    let body = experience.attributes;

    let expId: string = experience.id;
    let url: string = this.getApiUrl() + 'student/' + studentId + '/experience/' + expId + '/document';

    return this.http.post(url, body)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError(response));
  }
  deleteDocument(experience: StudentExperience, studentId: string): Promise<any> {
    let body = {}
    let expId: string = experience.experienceId;
    let url: string = this.getApiUrl() + 'student/' + studentId + '/experience/' + expId + '/document/file';

    return this.http.delete(url)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError(response));
  }


  deleteStudentDocumentFile(studentId: string, expId: string, storageId: string): Promise<any> {
    let body = {}
    // DELETE /api/student/{studentId}/experience/{experienceId}/document/{storageId}
    let url: string = this.getApiUrl() + 'student/' + studentId + '/experience/' + expId + '/document/' + storageId;

    return this.http.delete(url)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError(response));
  }

  uploadDocument(uploader: FileUploader, userId: string, experienceId: string, item, storageId?: string): void {
    var newUrl = this.config.getConfig('apiUrl') + 'student/' + userId + '/experience/' + experienceId + '/document/' + storageId + '/file';
    console.log(newUrl);
    uploader.setOptions(
      {
        url: newUrl,
        authToken: `Bearer ${sessionStorage.getItem('access_token')}`,
        disableMultipart: false
      }
    );
    item.withCredentials = false;
    uploader.onBuildItemForm = (item, form) => {
      form.append("filename", item.file.name);
    };
    uploader.uploadItem(item);
  }

  // uploadDocumentWithPromise(uploader: FileUploader, userId: string, experienceId: string, item, storageId?: string) { //: Promise<any>

  //   // return new Promise<any>((resolve, reject) => {
  //     var newUrl = this.config.getConfig('apiUrl') + 'student/' + userId + '/experience/' + experienceId + '/document/' + storageId + '/file';
  //     console.log(newUrl);
  //     uploader.setOptions(
  //       {
  //         url: newUrl,
  //         authToken: `Bearer ${sessionStorage.getItem('access_token')}`,
  //         disableMultipart: false
  //       }
  //     );
  //     item.withCredentials = false;

  //     uploader.onBuildItemForm = (item, form) => {
  //       form.append("filename", item.file.name);
  //     };
      
  //     uploader.uploadItem(item);

  //     // uploader.onCompleteItem = (item, response, status, headers) => {

  //     //   var actualResponse = JSON.parse(item._xhr.response);

  //     //   if (status == 200) {
  //     //     console.log('upload complete for ' + item.file.name);
  //     //     resolve();
  //     //   } else {
  //     //     console.log('upload did not complete for ' + item.file.name);
  //     //     reject();
  //     //   }
  //     // }

  //   // });

  // }

  getUrlFile(studentId: string, experienceId: string, storageId: string): Promise<any> {
    let url: string = this.getApiUrl() + 'student/' + studentId + '/experience/' + experienceId + '/document/' + storageId + '/link';

    return this.http.get(url)
      .toPromise()
      .then(response => response.text()
      ).catch(response => this.handleError(response));
  }
  getUserImage(studentId: string): Promise<any> {
    let url: string = this.getApiUrl() + 'student/' + studentId + '/photo';

    return this.http.get(url)
      .toPromise()
      .then(response =>

        response.text()

      ).catch(response => this.handleError(response));
  }
  sendUserImage(uploader: FileUploader, image, studentId: string) {
    var newUrl = this.config.getConfig('apiUrl') + 'student/' + studentId + '/photo/file';
    uploader.setOptions({ url: newUrl, authToken: `Bearer ${sessionStorage.getItem('access_token')}`, disableMultipart: false });
    image.withCredentials = false;
    image.upload();
  }

  getUserCV(studentId: string): Promise<any> {
    let url: string = this.getApiUrl() + 'student/' + studentId + '/cv';

    return this.http.get(url)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError(response));
  }

  updateUserCV(curriculum, studentId): Promise<any> {
    let body = curriculum;

    let url: string = this.getApiUrl() + 'student/' + studentId + '/cv';

    return this.http.put(url, body)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError(response));
  }

  addUserCV(curriculum, studentId): Promise<any> {
    let body = curriculum;

    let url: string = this.getApiUrl() + 'student/' + studentId + '/cv';

    return this.http.post(url, body)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError(response));
  }

  downloadCVInODTFormat(studentId: string): Promise<any> {
    let url: string = this.getApiUrl() + 'student/' + studentId + '/cv/export/odt';

    return this.http.get(url, { responseType: ResponseContentType.Blob }).toPromise().then(downloadedCV => {
      console.log("cv downloaded successfully.")

      FileSaver.saveAs(downloadedCV.blob(), "curriculum.odt");

    }).catch(error => this.handleError(error));

  }

  // downloadDocument(url: string, fileName:string): Promise<any> {

  //   console.log(url);
  //   return this.http.get(url, { responseType: ResponseContentType.Blob }).toPromise().then(downloadedCV => {
  //     console.log("document downloaded successfully.")

  //     FileSaver.saveAs(downloadedCV.blob(), fileName);

  //   }).catch(error => this.handleError);

  // }

  createDocument2(experience: ExperienceContainer, item, studentId: string): Promise<any> {
    let body = {};
    // as per the attributes google sheet, each document must contain attributs->title and filename.
    // var attrs = {};
    // attrs['title'] = item.file.name;
    // body['attributes'] = attrs;
    body['filename'] = item.file.name;

    let expId: string = experience.id;
    let url: string = this.getApiUrl() + 'student/' + studentId + '/experience/' + expId + '/document';

    console.log("create document call ->[" + url + "] body: " + body);

    return this.http.post(url, body)
      .toPromise()
      .then(response => response.json()).catch(response => this.handleError(response));
  }

  private handleError(error: any): Promise<any> {

    return new Promise<string>((resolve, reject) => {
      console.error('An error occurred', error);

      if ((error.status == 401) || (error.status == 403)) {
        // display toast and redirect to logout.
        var errorObj = JSON.parse(error._body)
        var errorMsg = 'Per favore accedi di nuovo.';
        if (errorObj.errorMsg) {
          errorMsg = errorObj.errorMsg;
        }
        let alert = this.alertCtrl.create({
          enableBackdropDismiss: false,
          message: errorMsg,
          buttons: [
            {
              text: 'OK',
              cssClass: 'pop-up-button',
              handler: () => {
                this.logout(errorMsg);
                // Promise.reject(errorMsg);
              }
            }
          ]
        },
        
        );
        alert.present();
      } else {
        Promise.reject(error);
      }
    });

  }

  logout(errMsg) {
    var aacUrl = this.config.getConfig('aacUrl');
    var targetUrl = this.config.getConfig('redirectUrl');
    var logoutUrl = aacUrl + '/logout?target=' + targetUrl + '/#/login?errMsg=' + errMsg;
    window.location.href = logoutUrl;
  }

}
