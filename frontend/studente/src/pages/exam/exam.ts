import { Component } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import {UserService } from '../../services/user.service';
import { StudentExperience } from '../../classes/StudentExperience.class';
//import { AddExamPage } from '../addExam/addExam';
import {TranslateService} from 'ng2-translate';
@Component({
  selector: 'page-exam',
  templateUrl: 'exam.html'
})
export class ExamPage  {
  exams:StudentExperience[]=[];
  order=true;
icon="ios-arrow-down";
  shownExam=null;
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService,public loading: LoadingController,private alertCtrl: AlertController, private translate: TranslateService){
  }

toggleDetails(exam) {
    if (this.isDetailsShown(exam)) {
        this.shownExam = null;
    } else {
        this.shownExam = exam;
    }
};
isDetailsShown(exam) {
    return this.shownExam === exam;
};

//  addNewExam(): void {
//    this.navCtrl.push(AddExamPage);
//  }
//
//updateExam(exam): void {
//    this.navCtrl.push(AddExamPage, {stage:JSON.stringify(exam)});
//  }
//
//  deleteExam(exam): void {
//   //ask confirmation
//
//      let alert = this.alertCtrl.create({
//    title: this.translate.instant('alert_delete_exam_title'),
//    message:  this.translate.instant('alert_delete_exam_message'),
//    buttons: [
//      {
//        text: this.translate.instant('alert_cancel'),
//        role: 'cancel'
//
//      },
//      {
//        text: this.translate.instant('alert_confirm'),
//        handler: () => {
//              let loader = this.loading.create({
//    content: this.translate.instant('loading'),
//  });
//              this.userService.deleteStage(exam).then(exam =>{
//       //remove stage from stage
//      for (var i=0; i<this.exams.length;i++)
//        {
//          if (this.exams[i].id==exam.id)
//            {
//                 this.exams.splice(i, 1);
//            }
//          }
//        loader.dismiss();
//        })
//        }
//      }
//    ]
//  });
//  alert.present();
//
//  }
//loaded when it is showed
ionViewWillEnter () {
    let loader = this.loading.create({
    content: this.translate.instant('loading'),
  });
  loader.present().then(() => {
        this.userService.getUserExams().then(exams =>{
        this.exams=exams
          loader.dismiss();
  })
  })
}
}

