import { Injectable }    from '@angular/core';
import {Http,RequestOptions,Headers} from '@angular/http'
import {ConfigService} from './config.service'
import {WebAPIConnectorService} from './webAPIConnector.service'
import {UserService} from './user.service'
import 'rxjs/add/operator/toPromise';

declare var window: any;

export enum LOGIN_STATUS {
      NOTSIGNEDIN,
      NEW,
      EXISTING  
}

@Injectable()
export class LoginService  {
  
  constructor(private http: Http, private config: ConfigService, private connectorService: WebAPIConnectorService,private userService: UserService) {
  }
  
  // TODO translation
  types: any = [
    { title: "Studente", text: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc id mollis turpis. Maurs rhoncus lobortis erat id egestas. Proin consectetur sem non placerat egestas. Sed mollis nisi non justo ultricies, sit amet varius quam tincidunt. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Phasellus iaculis diam nec placerat aliquet. Ut rhoncus feugiat ipsum non semper. Ut sed ligula suscipit massa ullamcorper porta.",style:"student-login" },
    { title: "Scuola", text: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc id mollis turpis. Mauris rhoncus lobortis erat id egestas. Proin consectetur sem non placerat egestas. Sed mollis nisi non justo ultricies, sit amet varius quam tincidunt. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Phasellus iaculis diam nec placerat aliquet. Ut rhoncus feugiat ipsum non semper. Ut sed ligula suscipit massa ullamcorper porta.",style:"school-login"  },
    { title: "Ente esterno", text: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc id mollis turpis. Mauris rhoncus lobortis erat id egestas. Proin consectetur sem non placerat egestas. Sed mollis nisi non justo ultricies, sit amet varius quam tincidunt. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Phasellus iaculis diam nec placerat aliquet. Ut rhoncus feugiat ipsum non semper. Ut sed ligula suscipit massa ullamcorper porta.",style:"external-login"  }
  ];

  /**
   * LIST OF LOGIN TYPES: STUDENT, SCHOOL, EXTERNAL ENTITY
   */
  getLoginTypes(): any[] {
    return this.types;
  }
  /**
   * Perform login operation using the AAC OAuth2 Implicit flow, depending on the login type
   * @param loginType 
   */
  login(loginType: string): void {
    sessionStorage.login_type = loginType;
    window.location = `${ this.config.getConfig('aacUrl') }/eauth/authorize?client_id=${ this.config.getConfig('aacClientId') }&redirect_uri=${ this.config.getConfig('redirectUrl') }&response_type=token`;
  }

  /**
   * Logout the user from the portal
   */
  logout(): Promise<boolean> {
    // TODO: revoke token and return true
    sessionStorage.clear();
    return Promise.resolve(true);
  }

  serverLogout(): void {
    window.location = this.config.getConfig('aacUrl') + '/logout?target=' + window.location.href;
  }

  /**
   * Check status of the login. Return LOGIN_STATUS value
   */
  checkLoginStatus():Promise<LOGIN_STATUS> {
    if (!sessionStorage.access_token) {
      return Promise.resolve(LOGIN_STATUS.NOTSIGNEDIN);
    }
    return new Promise((resolve, reject) => {
      // TODO make a call to the profile service.
      // if the service return empty profile, resolve NEW
      // if the service return non-empty profile, resolve EXISTING
      // in case of error resolve NOTSIGNEDIN
      this.connectorService.getProfile().then(profile =>{
        //check the case
        this.userService.setUserId(profile.studentId);
        this.userService.setConsentSubject(profile.subject);
        if (profile.authorized){
           resolve(LOGIN_STATUS.EXISTING);
        }else {
           resolve(LOGIN_STATUS.NEW);
        }
      },
      err => {
        // TODO handle error
        resolve(LOGIN_STATUS.NOTSIGNEDIN);
      }); 
      
    });
  }

  /**
   * Perform consent operation for the user using the corresponding API method
   */
  consent(): Promise<boolean> {
    // TODO make a call to the consent service
      return new Promise((resolve, reject) => {
       this.connectorService.consent(this.userService.getUserId(),this.userService.getConsentSubject()).then(result =>{
         resolve(true);
       }
     ,
     err => {
       resolve(false);
     }
       )
     }
     )
    //return Promise.resolve(true);
  }
  
  /**
   * Return AAC access token if present
   */
  getToken(): string {
    return sessionStorage.getItem('access_token');
  }
 
}
