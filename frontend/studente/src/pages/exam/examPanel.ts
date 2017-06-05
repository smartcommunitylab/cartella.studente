import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'

import { StudentExperience } from '../../classes/StudentExperience.class'
import { UserService } from '../../services/user.service'
@Component({
  selector: 'exam-panel',
  templateUrl: './exam.html'

})

export class ExamPanel implements OnInit {
  @Input() exam: StudentExperience;
  @Input() index: number;

  @Output() onDeleted = new EventEmitter<string>();
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService, public loading: LoadingController, private alertCtrl: AlertController, private translate: TranslateService, private utilsService: UtilsService) {

  }
  opened: Boolean = false;
  ngOnInit(): void {
    if (this.index == 0) {
      this.toggle();
    }
  }
  toggle() {
    this.opened = !this.opened;
  }
  open(event) {
    console.log(event);
  }
  getExam(): StudentExperience {
    return this.exam;
  }
}

