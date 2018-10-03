import { Component } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import {UserService } from '../../services/user.service';
import { Registration } from '../../classes/Registration.class';
import { TeachingUnit } from '../../classes/TeachingUnit.class';
//import { AddExamPage } from '../addExam/addExam';
import {TranslateService} from 'ng2-translate';
@Component({
  selector: 'page-institute',
  templateUrl: 'institute.html'
})
export class InstitutePage  {
    registrations: Registration[] = [];
    agreegatedRegistrationMap = {};  
  teachingUnit:TeachingUnit=null;
  order=true;
icon="ios-arrow-down";
  shownRegistration=null;
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService,public loading: LoadingController,private alertCtrl: AlertController, private translate: TranslateService){
  }

toggleDetails(registration) {
    if (this.isDetailsShown(registration)) {
        this.shownRegistration = null;
    } else {
        this.shownRegistration = registration;
    }
};
isDetailsShown(registration) {
    return this.shownRegistration === registration;
};

//   ngOnInit():void {
//       let paramRegistration = this.params.get('paramRegistration');
//       if (paramRegistration!=null){

//         this.registrations = paramRegistration.registrations;
//                 this.teachingUnit = paramRegistration.teachingUnit;

//       }

//   }
////loaded when it is showed
ionViewWillEnter () {
   let loader = this.loading.create({
   content: this.translate.instant('loading'),
 });
   loader.present().then(() => {
    let paramRegistration = this.params.get('paramRegistration');
    if (paramRegistration != null) {
        //    this.userService.getUserExams().then(registrations =>{
        this.registrations = paramRegistration.registrations;
        this.agreegateRegistration(paramRegistration.registrations);
        this.teachingUnit = paramRegistration.teachingUnit;
        loader.dismiss();
    }
 })
//  })
    }

    agreegateRegistration(unAgreegated) {
        for (var r = 0; r < unAgreegated.length; r++) {

                var key = unAgreegated[r].course;

                if (!this.agreegatedRegistrationMap[key]) {
                    this.agreegatedRegistrationMap[key] = [];
                }

                this.agreegatedRegistrationMap[key].push(unAgreegated[r]);
         

        }
    }
}

