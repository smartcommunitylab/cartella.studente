import { Component } from '@angular/core';
import { NavController, NavParams, LoadingController,AlertController } from 'ionic-angular';
import {UserService } from '../../services/user.service';
import {Student} from '../../classes/Student.class';
import {TranslateService} from 'ng2-translate';
@Component({
  selector: 'page-activities',
  templateUrl: 'profile.html'
})
export class ProfilePage  {
  student:Student=new Student();
  editMode=false;
  constructor(public navCtrl: NavController, public params: NavParams, private userService: UserService,public loading: LoadingController, private translate: TranslateService,private alertCtrl: AlertController){
  }


  openEditMode() {
    this.editMode=true;
  }
  closeEditMode() {
    //sicuro di non voler salvare i dati?
     let alert = this.alertCtrl.create({
    title: this.translate.instant('alert_delete_certification_title'),
    message:  this.translate.instant('alert_delete_certification_message'),
    buttons: [
      {
        text: this.translate.instant('alert_cancel'),
        role: 'cancel',
        cssClass: 'pop-up-button'

      },
      {
        text: this.translate.instant('alert_confirm'),
        cssClass: 'pop-up-button',
        handler: () => {
          //return do read mode
          this.editMode=false;
        }
      }
    ]
  });
  alert.present();
  }
  saveData(){
    this.editMode=false;
    //save data
        let loader = this.loading.create({
    content: this.translate.instant('loading'),
  });
  loader.present().then(() => {
     this.userService.saveUserInfo(this.student).then(student=>{
       this.student=student;
       loader.dismiss();
     });
  })
  }
ionViewWillEnter () {
    let loader = this.loading.create({
    content: this.translate.instant('loading'),
  });
  loader.present().then(() => {
        this.userService.getUserInfo().then(student =>{
          this.student=student;
          loader.dismiss();
  })
  })
}
}
