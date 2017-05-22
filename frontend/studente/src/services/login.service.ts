import { Injectable }    from '@angular/core';
import { Headers, Http } from '@angular/http';
import 'rxjs/add/operator/toPromise';

@Injectable()
export class LoginService  {
   types: any = [
    { title: " Studente", text: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc id mollis turpis. Mauris rhoncus lobortis erat id egestas. Proin consectetur sem non placerat egestas. Sed mollis nisi non justo ultricies, sit amet varius quam tincidunt. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Phasellus iaculis diam nec placerat aliquet. Ut rhoncus feugiat ipsum non semper. Ut sed ligula suscipit massa ullamcorper porta.",style:"student-login" },
    { title: " Scuola", text: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc id mollis turpis. Mauris rhoncus lobortis erat id egestas. Proin consectetur sem non placerat egestas. Sed mollis nisi non justo ultricies, sit amet varius quam tincidunt. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Phasellus iaculis diam nec placerat aliquet. Ut rhoncus feugiat ipsum non semper. Ut sed ligula suscipit massa ullamcorper porta.",style:"school-login"  },
    { title: " Ente esterno", text: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc id mollis turpis. Mauris rhoncus lobortis erat id egestas. Proin consectetur sem non placerat egestas. Sed mollis nisi non justo ultricies, sit amet varius quam tincidunt. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Phasellus iaculis diam nec placerat aliquet. Ut rhoncus feugiat ipsum non semper. Ut sed ligula suscipit massa ullamcorper porta.",style:"external-login"  }
  ];
  getLoginTypes(): any[] {
    return this.types;
  }
}
