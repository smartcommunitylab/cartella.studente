import {Component, Output, EventEmitter,Input} from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'

import {StudentExperience} from '../../classes/StudentExperience.class'
import {UserService} from '../../services/user.service'
@Component({
  selector: 'exam-panel',
  templateUrl: './exam.html'

})

export class ExamPanel {
    @Input() exam: StudentExperience;
    @Output() onDeleted = new EventEmitter<string>();
      constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService, public loading: LoadingController, private alertCtrl: AlertController, private translate: TranslateService, private utilsService: UtilsService) {
  }
    opened: Boolean = false;
    toggle () {
      this.opened = !this.opened;
    }
    getExam(): StudentExperience {
      return this.exam;
    }
//      updateExam(): void {
//     this.navCtrl.push(AddExamPage, { exam: JSON.stringify(this.exam) });
//   }

//   deleteExam(): void {
//     //ask confirmation

//     let alert = this.alertCtrl.create({
//       title: this.translate.instant('alert_delete_exam_title'),
//       message: this.translate.instant('alert_delete_exam_message'),
//       buttons: [
//         {
//           text: this.translate.instant('alert_cancel'),
//           cssClass: 'pop-up-button',
//           role: 'cancel'

//         },
//         {
//           text: this.translate.instant('alert_confirm'),
//           cssClass: 'pop-up-button',
//           handler: () => {
//             let loader = this.loading.create({
//               content: this.translate.instant('loading'),
//             });
//             this.userService.deleteExam(this.getExam()).then(exam => {
//               // remove exam from exam
//               this.onDeleted.emit(exam.id);
//               loader.dismiss();
//              this.utilsService.toast( this.translate.instant('toast_delete_stage'),3000,'middle');

//             })
//           }
//         }
//       ]
//     });
//     alert.present();

//   }
  }

