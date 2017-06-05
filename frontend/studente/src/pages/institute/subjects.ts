import {Component, Output, EventEmitter,Input,OnInit} from '@angular/core';
import { NavController, NavParams, LoadingController, AlertController } from 'ionic-angular';
import { TranslateService } from 'ng2-translate';
import { UtilsService } from '../../services/utils.services'
import { TeachingUnit } from '../../classes/TeachingUnit.class';

import {Registration} from '../../classes/Registration.class'
import {UserService} from '../../services/user.service'
@Component({
  selector: 'subjects',
  templateUrl: './subjects.html'

})

export class Subjects implements OnInit{
    subjects=null;
    @Input() registration: Registration;

      constructor(public navCtrl: NavController, public params: NavParams, public userService: UserService, public loading: LoadingController, private alertCtrl: AlertController, private translate: TranslateService, private utilsService: UtilsService) {
  }
    ngOnInit() {
      this.userService.getUserSubjectByRegistration(this.registration).then(subjects =>
        {
            this.subjects=subjects;
        }
        );
    }
  }

