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
    { title: "Studente", text: "Se sei uno studente entra per controllare ed aggiornare le tue esperienze formative. Tra i servizi disponibili la possibilità di generare un CV in modo assistito. Vuoi essere coinvolto nello sviluppo di servizi innovativi per la scuola? Proponici la tua idea e ti aiuteremo a realizzarla.",style:"student-login" },
    { title: "Scuola", text: "Attraverso questa sezione gli istituti scolastici possono aggiornare le esperienza scuola-lavoro e vedere le esperienze extra didattiche degli studenti iscritti.",style:"school-login"  },
    { title: "Ente esterno", text: "Se sei un professionista o un rappresentate di un'organizzazione che collabora con il mondo della scuola entra in questa sezione. Da qui potrai inserire opportunità di stage e di lavoro, opportunità formative e certificare corsi ed esperienze fatte dagli studenti di cui sei stato referente.",style:"external-login"  }
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
    window.location = this.config.getConfig('aacUrl') + '/logout?target=' + this.config.getConfig('redirectUrl') + '/#/login';
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
  consent(): Promise<any> {
    // TODO make a call to the consent service
      return new Promise((resolve, reject) => {
       this.connectorService.consent(this.userService.getUserId()).then(result =>{
         resolve(result);
       }
     ,
     err => {
       resolve(false);
     }
       )
     }
     )
  }

  readConsent(): Promise<any> {
    // TODO make a call to the consent service
    return new Promise((resolve, reject) => {
      if (!this.userService.getUserId()) { 
        console.log("studentId not set");
      }
      this.connectorService.readConsent(this.userService.getUserId()).then(result => {
        resolve(result);
      }
        ,
        err => {
          resolve(false);
        }
      )
    }
    )
  }

  /**
   * Return AAC access token if present
   */
  getToken(): string {
    return sessionStorage.getItem('access_token');
  }
 
}
