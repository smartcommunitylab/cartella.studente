import { Component , Output,EventEmitter} from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { UserService } from '../../services/user.service';
import { StudentExperience } from '../../classes/StudentExperience.class';
//import { AddExamPage } from '../addExam/addExam';
import { TranslateService } from 'ng2-translate';
@Component({
  selector: 'page-exam',
  templateUrl: 'exams.html'
})
export class ExamPage {
  exams: StudentExperience[] = [];
  order = true;
  icon = "ios-arrow-down";
  shownExam = null;
  @Output()
  open: EventEmitter<any> = new EventEmitter();
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService, public loading: LoadingController, private alertCtrl: AlertController, private translate: TranslateService) {
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


  //loaded when it is showed
  ionViewWillEnter() {
    let loader = this.loading.create({
      content: this.translate.instant('loading'),
    });
    loader.present().then(() => {
      this.userService.getUserExams().then(exams => {
        this.exams = exams
        this.open.emit(null);
        loader.dismiss();
      })
    })
  }
}

