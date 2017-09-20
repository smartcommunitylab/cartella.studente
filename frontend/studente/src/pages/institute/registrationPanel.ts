import { Component, Output, EventEmitter, Input, OnInit  } from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'
import { TeachingUnit } from '../../classes/TeachingUnit.class';

import { Registration } from '../../classes/Registration.class'
import { UserService } from '../../services/user.service'
@Component({
  selector: 'registration-panel',
  templateUrl: './registration.html'

})

export class RegistrationPanel implements OnInit {
  subjects = [];
  @Input() registration: Registration;
  @Input() teachingUnit: TeachingUnit;
  @Input() index: number;
  @Output() onDeleted = new EventEmitter<string>();

  constructor(public navCtrl: NavController, public params: NavParams, public userService: UserService, public loading: LoadingController, private alertCtrl: AlertController, private translate: TranslateService, private utilsService: UtilsService) {
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
  
  // getRegistration(): Registration {
  //   return this.registration;
  // }
}



/**
 *
 *  @Input() exam: StudentExperience;
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
 */